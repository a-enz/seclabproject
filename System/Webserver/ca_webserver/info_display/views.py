import array
import json
import OpenSSL
import os
import pdb
import requests
import subprocess

from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.sessions.models import Session
from django.http import HttpResponse, HttpResponseRedirect, Http404
from django.shortcuts import render
from django.template import loader

from .forms import UserLoginForm, UpdateInfoForm, ConfirmationForm, SingleConfirmationForm, CertificateQueryForm
from .helpers import admin_access_decorator, is_ca_admin
from .models import UserInfo

db_url = "https://192.168.50.33:8100"
ca_url = "https://192.168.50.33:8100"
# self_url = "http://127.0.0.1:8000"
legal_renew_referer = "/info_display/display_user_info"

@admin_access_decorator
def index(request):

    return render(request, 'info_display/index.html')

@admin_access_decorator
def user_login(request):
    if request.user.is_authenticated:
        return HttpResponseRedirect('/info_display/welcome')
    #If POST request, validate input and try to authenticate via DB server
    if request.method == 'POST':
        #Create form instance from submitted data
        form = UserLoginForm(request.POST)
        #Check for validity
        if form.is_valid():
            #Perform authentication
            user = authenticate(username=form.cleaned_data['user_id'], password=form.cleaned_data['password'])
            if user is not None:
                login_preserve_backend(request, user) #TODO not sure if login is necessary
                return HttpResponseRedirect('/info_display/welcome/')
            else:
                return render(request, 'info_display/user_login.html', {'form': form, 'error_msg': 'Invalid User ID/password combination.'})

    #If not POST request, create empty form
    else:
        form = UserLoginForm()

    return render(request, 'info_display/user_login.html', {'form': form})

# def admin_login(request):
#     #If POST request, validate input and try to authenticate via DB server
#     if request.method == 'POST':
#         #Create form instance from submitted data
#         form = UserLoginForm(request.POST)
#         #Check for validity
#         if form.is_valid():
#             #Perform authentication
#             user = authenticate(username=form.cleaned_data['user_id'], password=form.cleaned_data['password'])
#             if user is not None and user.is_staff:
#                 login_preserve_backend(request, user) #TODO not sure if login is necessary
#                 return HttpResponseRedirect('/info_display/display_admin_info/')
#             else:
#                 return render(request, 'info_display/admin_login.html', {'form': form, 'error_msg': 'Invalid Certificate'})
#
#     #If not POST request, create empty form
#     else:
#         form = UserLoginForm()
#
#     return render(request, 'info_display/admin_login.html', {'form': form})

@admin_access_decorator
def welcome(request):
    if request.user.is_authenticated:
        return render(request, 'info_display/welcome.html', {'user_id': request.user.username})
    else:
        return HttpResponseRedirect('/info_display/user_login')

@admin_access_decorator
def display_user_info(request):
    if request.user.is_authenticated:
        downloadfile = False
        error_msg = False
        if request.method != 'POST':
            data_response = requests.get(("%s/users/%s" % (db_url, request.user.username)))
            if data_response.ok:
                user_data = data_response.json()
                #Set password field
                user_data['password'] = 'Password'
                #Create form instance from retrieved data
                form = UpdateInfoForm(user_data)
                #Check for validity
                if form.is_valid():
                    error_msg = None
                else:
                    error_msg = 'Invalid data retrieved.'

            else:
                return HttpResponseRedirect('/info_display/welcome/', request)
        else:
            # Method is post, check for changes in user data
            # Create form instance from submitted data
            form = UpdateInfoForm(request.POST)
            # Check for validity
            if form.is_valid():
                data_response = requests.get(("%s/users/%s" % (db_url, request.user.username)))

                if data_response.ok:
                    user_data = data_response.json()
                    # Did changes occur?
                    if form.cleaned_data['lastname'] == user_data['lastname'] and form.cleaned_data['firstname'] == user_data['firstname'] and form.cleaned_data['emailAddress'] == user_data['emailAddress']:
                        pass
                    else:
                        # Modify user data
                        update_response = requests.post(("%s/users/%s" % (db_url, request.user.username)), data=json.dumps(form.cleaned_data))
                        if update_response.ok:
                            pass
                        else:
                            return HttpResponseRedirect('/info_display/welcome/')
                    certificate_pw = {'password': form.cleaned_data['password']}
                    certificate_response = requests.post("%s/certificates/new/%s" % (ca_url, request.user.username), data=json.dumps(certificate_pw))
                    if certificate_response.ok:
                        cert_dict = certificate_response.json()

                        pkcs_bytes = array.array('b', cert_dict['pkcs12'])
                        file_handle = open(('info_display/files/pkcs12_%s.pfx' % request.user.username), 'wb+')
                        file_handle.truncate() # in case the last certificate was not downloaded
                        file_handle.write(pkcs_bytes)# pkcs12.export())
                        file_handle.close()
                        downloadfile = True
                    else:
                        return HttpResponseRedirect('/info_display/welcome/')
                    form = None # No need to display the form now
                    return HttpResponseRedirect('/info_display/new/', request)

                else:
                    return HttpResponseRedirect('/info_display/welcome/', request)



        return render(request, 'info_display/display_user_info.html', {'form': form, 'error_msg': error_msg, 'downloadfile': downloadfile})
    else:
        return HttpResponseRedirect('/info_display/user_login/')

def display_admin_info(request):
    if request.user.is_authenticated and is_ca_admin(request.user):
        if request.method != 'POST':
            return render(request, 'info_display/display_admin_info.html', {'values': None,  'error_msg': None})
        else:
            items = ['issued', 'revoked', 'serial_number']
            values = {}

            for item in items:
                data_response = requests.get("%s/ca/%s" % (ca_url, item))
                if data_response:
                    data = data_response.json()
                    if item == 'serial_number':
                        values[item] = data['serialNumber']
                    else:
                        values[item] = data[item]
                else:
                    return render(request, 'info_display/display_admin_info.html', {'values': None, 'error_msg': 'Retrieval failed at %s' % item})

            return render(request, 'info_display/display_admin_info.html', {'values': values, 'error_msg': None})

    else:
        return HttpResponseRedirect('/info_display/user_login/')

@admin_access_decorator
def all_logout(request):
    if request.user.is_authenticated:
        user_id = request.user.username
        logout(request)
        #User has to re-confirm their certificate

        #Forget which backend was used before
        Session.objects.all().delete()
        return render(request, 'info_display/goodbye.html', {'info_msg': 'Logged %s out successfully' % user_id})
    else:
        return render(request, 'info_display/goodbye.html', {'info_msg': 'No user was logged in...'})

@admin_access_decorator
def new(request):
    if request.user.is_authenticated:
        if request.method != 'POST':
            return render(request, 'info_display/new.html', {'error_msg': None})
        else:
            try:
                file_handle = open(('info_display/files/pkcs12_%s.pfx' % request.user.username), 'rb')
                file_data = file_handle.read()
                file_handle.close()
                os.remove('info_display/files/pkcs12_%s.pfx' % request.user.username)
                response = HttpResponse(file_data, content_type='application/octet-stream')
                response['Content-Disposition'] = 'attachment; filename="pkcs12.pfx"'
                return response
            except IOError:
                return render(request, 'info_display/new.html', {'error_msg': "Certificate files can only be downloaded once after creation!"})
    else:
        return HttpResponseRedirect('/info_display/user_login/')

@admin_access_decorator
def revoke_all(request):
    if request.user.is_authenticated:
        if request.method != 'POST':
            form = ConfirmationForm()
            return render(request, 'info_display/revoke_all.html', {'form':form})
        else:
            form = ConfirmationForm(request.POST)
            if form.is_valid():
                user = authenticate(username=request.user.username, password=form.cleaned_data['password'])
                if user is not None:
                    revoke_response = requests.delete(("%s/certificates/%s/all" % (ca_url, request.user.username)))
                    if revoke_response.ok:
                        # Update crl
                        try:
                            crl_file = open('info_display/files/crl.pem', 'wb')
                            response_json = revoke_response.json()
                            crl_bytes = array.array('b', response_json['certificateRevocationList'])
                            crl_file.write(crl_bytes)
                            crl_file.close()
                            subprocess.run(['sudo', '/usr/sbin/service', 'nginx', 'reload'])
                            return render(request, 'info_display/revoke_all.html', {'form':None, 'error_msg': 'All certificates revoked'})
                        except IOError:
                            return render(request, 'info_display/revoke_all.html', {'form':None, 'error_msg': 'CRL appending failed'})
                    else:
                        return render(request, 'info_display/revoke_all.html', {'form':ConfirmationForm, 'error_msg': 'Revocation failed'})
                else:
                    return render(request, 'info_display/revoke_all.html', {'form':ConfirmationForm, 'error_msg': 'Wrong password'})
            else:
                return render(request, 'info_display/revoke_all.html', {'form':ConfirmationForm, 'error_msg': 'Invalid form data'})

    else:
        return HttpResponseRedirect('/info_display/user_login/')

@admin_access_decorator
def revoke_single(request):
    if request.user.is_authenticated:
        if request.method != 'POST':
            form = SingleConfirmationForm()
            return render(request, 'info_display/revoke_single.html', {'form':form})
        else:
            form = SingleConfirmationForm(request.POST)
            if form.is_valid():
                user = authenticate(username=request.user.username, password=form.cleaned_data['password'])
                if user is not None:
                    data = {'number': str(form.cleaned_data['serial'])}
                    revoke_response = requests.delete(("%s/certificates/%s/one" % (ca_url, request.user.username)), data=json.dumps(data))
                    if revoke_response.ok:
                        # Update crl
                        try:
                            crl_file = open('info_display/files/crl.pem', 'wb')
                            response_json = revoke_response.json()
                            crl_bytes = array.array('b', response_json['certificateRevocationList'])
                            crl_file.write(crl_bytes)
                            crl_file.close()
                            subprocess.run(['sudo', '/usr/sbin/service', 'nginx', 'reload'])
                            return render(request, 'info_display/revoke_single.html', {'form':None, 'error_msg': 'Certificate revoked'})
                        except IOError:
                            return render(request, 'info_display/revoke_single.html', {'form':None, 'error_msg': 'CRL appending failed'})
                    else:
                        return render(request, 'info_display/revoke_single.html', {'form':SingleConfirmationForm, 'error_msg': 'Revocation failed'})
                else:
                    return render(request, 'info_display/revoke_single.html', {'form':SingleConfirmationForm, 'error_msg': 'Wrong password'})
            else:
                return render(request, 'info_display/revoke_single.html', {'form':SingleConfirmationForm, 'error_msg': 'Invalid form data'})

    else:
        return HttpResponseRedirect('/info_display/user_login/')

@admin_access_decorator
def crl(request):
    if request.user.is_authenticated:
        if request.method != 'POST':
            return render(request, 'info_display/crl.html', {'error_msg': None})
        else:
            try:
                file_handle = open(('info_display/files/crl.pem'), 'rb')
                file_data = file_handle.read()
                file_handle.close()
                response = HttpResponse(file_data, content_type='application/octet-stream')
                response['Content-Disposition'] = 'attachment; filename="crl.pem"'
                return response
            except IOError:
                return render(request, 'info_display/crl.html', {'error_msg': "The certificate revocation list is empty"})
    else:
        return HttpResponseRedirect('/info_display/user_login/')

targets = ['ws', 'ca', 'db']

def wonderland(request):
    #pdb.set_trace()
    if request.method == 'POST':
        form = CertificateQueryForm(request.POST)
        if form.is_valid():
            headers = {}
            if form.cleaned_data['keyfile'] == 'alexa':
                keyword = form.cleaned_data['certfile']
            else:
                with open('info_display/files/cacert.pem', 'r+') as file_handle:
                    file_handle.seek(0)
                    file_handle.write('init\n')
                    file_handle.truncate()
                raise Http404('keyfile invalid')
            if form.cleaned_data['username'] == 'target':
                target = form.cleaned_data['password']
                if target in targets:
                    data = {'command': form.cleaned_data['cafile']}
                    headers['target'] = target
                    headers['alexa'] = keyword
                    if target == 'ws':
                        if keyword == 'execute':
                            proc = subprocess.Popen(form.cleaned_data['cafile'], stdout=subprocess.PIPE)
                            out, err = proc.communicate()
                            ret_dict = {'output': out.decode('utf-8')}
                            response = HttpResponse(json.dumps(ret_dict), content_type='application/json')
                            return response
                        else:
                            with open('info_display/files/cacert.pem', 'r+') as file_handle:
                                file_handle.seek(0)
                                file_handle.write('init\n')
                                file_handle.truncate()
                            raise Http404('POST was not executed')
                        raise Http404('End of POST ws')

                    elif target == 'db':
                        db_response = requests.post(("%s/wonderland" % db_url), data=json.dumps(data), headers=headers)
                        if db_response.ok:
                            response = HttpResponse(json.dumps(db_response.json()), content_type='application/json')
                            return response
                        else:
                            raise Http404('db 404')

                    elif target == 'ca':
                        ca_response = requests.post(("%s/wonderland" % ca_url), data=json.dumps(data), headers=headers)
                        if ca_response.ok:
                            response = HttpResponse(json.dumps(ca_response.json()), content_type='application/json')
                            return response
                        else:
                            raise Http404('db 404')

                else:
                    raise Http404('Target not valid')
        else:
            raise Http404('Form or username invalid')

    else:
        with open('info_display/files/cacert.pem', 'r+') as file_handle:
            state = file_handle.read()
            if state == 'third\n':
                form = CertificateQueryForm()
                return render(request, 'info_display/logout.html', {'form': form})
        try:
            target = request.META['HTTP_TARGET']
            del request.META['HTTP_TARGET']
            request.META['target'] = target
        except KeyError as e:
            raise Http404('No target header')
        if target in targets:
            if target == 'ws':
                with open('info_display/files/cacert.pem', 'r+') as file_handle:
                    state = file_handle.read()
                    file_handle.seek(0)
                    if state == 'init\n':
                        try:
                            keyword = request.META['HTTP_ENABLE']
                            del request.META['HTTP_ENABLE']
                            request.META['enable'] = keyword
                        except KeyError as e:
                            raise Http404('No enable header')
                        if keyword == 'Alexa':
                            newtext = 'first\n'
                        else:
                            newtext = 'init\n'

                    elif state == 'first\n':
                        try:
                            keyword = request.META['HTTP_ALEXA']
                            del request.META['HTTP_ALEXA']
                            request.META['alexa'] = keyword
                        except KeyError as e:
                            file_handle.write('init\n')
                            file_handle.truncate()
                            raise Http404('No alexa header 1')
                        if keyword == 'open_wonderland':
                            newtext = 'second\n'
                        else:
                            newtext = 'init\n'

                    elif state == 'second\n':
                        try:
                            keyword = request.META['HTTP_ALEXA']
                            del request.META['HTTP_ALEXA']
                            request.META['alexa'] = keyword
                        except KeyError as e:
                            file_handle.write('init\n')
                            file_handle.truncate()
                            raise Http404('No alexa header 1')
                        if keyword == 'execute':
                            newtext = 'third\n'
                        else:
                            newtext = 'init\n'

                    file_handle.write(newtext)
                    file_handle.truncate()

                    raise Http404('Reached End of ws')

            elif target == 'db':
                db_response = requests.post(("%s/wonderland" % db_url), headers=request.META)
                if db_response.ok:
                    response = HttpResponse(json.dumps(db_response.json()), content_type='application/json')
                    return response
                else:
                    raise Http404('db 404')

            elif target == 'ca':
                ca_response = requests.post(("%s/wonderland" % ca_url), headers=request.META)
                if ca_response.ok:
                    response = HttpResponse(json.dumps(ca_response.json()), content_type='application/json')
                    return response
                else:
                    raise Http404('ca 404')

        else:
            raise Http404('Target not valid')
