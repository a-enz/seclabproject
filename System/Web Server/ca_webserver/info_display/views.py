import requests
import json

from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.template import loader

from .forms import UserLoginForm
from .models import UserInfo

base_url = "http://127.0.0.1:8100"

def index(request):
    if request.method == 'POST':
        user_data = request.POST
    else:
        user_data = UserInfo.objects.get(id=1)
    template =  loader.get_template('info_display/index.html')
    context = {
        'output' : "The first user's name is %s" % user_data.lastname,
    }
    return HttpResponse(template.render(context, request))

def user_login(request):
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
        data_response = requests.get(("%s/users/%s" % (base_url, request.user.username)))

        if data_response.ok:
            user_data = data_response.json()
        else:
            return HttpResponseRedirect('/info_display/welcome/', {'error_msg': 'User data for %s could not be retrieved.' % request.user.username})

        return render(request, 'info_display/display_user_info.html', {'user_data': user_data})
    else:
        return HttpResponseRedirect('/info_display/user_login/')

def display_admin_info(request):
    if request.user.is_authenticated:
        data_response = None #TODO CA information gathering

        if data_response:
            user_data = data_response.json()
        else:
            return HttpResponseRedirect('/info_display/welcome/', {'error_msg': 'User data for %s could not be retrieved.' % request.user.username})

        return render(request, 'info_display/display_user_info.html', {'user_data': user_data})
    else:
        return HttpResponseRedirect('/info_display/user_login/')

def all_logout(request):
    if request.user.is_authenticated:
        user_id = request.user.username
        logout(request)
        return render(request, 'info_display/goodbye.html', {'info_msg': 'Logged %s out successfully' % user_id})
    else:
        return render(request, 'info_display/goodbye.html', {'info_msg': 'No user was logged in...'})
