import array
import json
import OpenSSL
import os
import pdb
import requests

from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.template import loader

from .forms import UserLoginForm, UpdateInfoForm
from .models import UserInfo

db_url = "http://127.0.0.1:8101"
ca_url = "http://127.0.0.1:8100"
self_url = "http://127.0.0.1:8000"
legal_renew_referer = "/info_display/display_user_info"

def index(request):
    return render(request, 'info_display/index.html')

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
                login(request, user) #TODO not sure if login is necessary
                return HttpResponseRedirect('/info_display/welcome/')
            else:
                return render(request, 'info_display/user_login.html', {'form': form, 'error_msg': 'Invalid User ID/password combination.'})

    #If not POST request, create empty form
    else:
        form = UserLoginForm()

    return render(request, 'info_display/user_login.html', {'form': form})

def admin_login(request):
    #If POST request, validate input and try to authenticate via DB server
    if request.method == 'POST':
        #Create form instance from submitted data
        form = UserLoginForm(request.POST)
        #Check for validity
        if form.is_valid():
            #Perform authentication
            user = authenticate(username=form.cleaned_data['user_id'], password=form.cleaned_data['password'])
            if user is not None and user.is_staff:
                login(request, user) #TODO not sure if login is necessary
                return HttpResponseRedirect('/info_display/display_admin_info/')
            else:
                return render(request, 'info_display/admin_login.html', {'form': form, 'error_msg': 'Invalid Certificate'})

    #If not POST request, create empty form
    else:
        form = UserLoginForm()

    return render(request, 'info_display/admin_login.html', {'form': form})

#@login_required(login_url='/info_display/user_login')
def welcome(request):
    if request.user.is_authenticated:
        return render(request, 'info_display/welcome.html', {'user_id': request.user.username})
    else:
        return HttpResponseRedirect('/info_display/user_login')

def display_user_info(request):
    if request.user.is_authenticated:
        downloadfile = False
        error_msg = False
        if request.method == 'GET':
            data_response = requests.get(("%s/users/%s" % (db_url, request.user.username)))
            if data_response.ok:
                user_data = data_response.json()
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
            # pdb.set_trace()
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
                    certificate_response = requests.get("%s/certificates/new/%s" % (ca_url, request.user.username))
                    if certificate_response.ok:
                        cert_dict = certificate_response.json()
                        #pkcs12 = OpenSSL.crypto.PKCS12()
                        #pkcs12.set_privatekey(cert_dict['privateKey'])
                        #pkcs12.set_certificate(cert_dict['certificate'])
                        # Create file if it doesn't exist. No other user's data can be leaked, because the
                        # file is written with the data bound to the authenticated user
                        #pdb.set_trace()
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
    if request.user.is_authenticated:
        data_response = None #TODO CA information gathering

        if data_response:
            form = UpdateInfoForm(data_response.json())
        else:
            return HttpResponseRedirect('/info_display/welcome/', {'error_msg': 'User data for %s could not be retrieved.' % request.user.username})

        return render(request, 'info_display/display_user_info.html', {'form': form})
    else:
        return HttpResponseRedirect('/info_display/user_login/')

def all_logout(request):
    if request.user.is_authenticated:
        user_id = request.user.username
        logout(request)
        return render(request, 'info_display/goodbye.html', {'info_msg': 'Logged %s out successfully' % user_id})
    else:
        return render(request, 'info_display/goodbye.html', {'info_msg': 'No user was logged in...'})

def new(request):
    if request.user.is_authenticated:
        if request.method == 'GET':
            return render(request, 'info_display/new.html', {'error_msg': None})
        elif request.method == 'POST':
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
