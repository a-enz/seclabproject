import pdb
import re

from django.http import HttpResponseRedirect

def get_data_from_cert(cert):
    user_data = {}
    matching = re.search(r'.*CN=(?P<username>[^,]*).*', cert)
    if matching:
        user_data['username'] = matching.group('username')
    else:
        user_data = None
    return user_data

def is_ca_admin(user):
    if user.username == 'ca_admin':
        return True
    else:
        return False

def user_access(user):
    if user.is_authenticated and not is_ca_admin(user):
        return True
    else:
        return False

def admin_access_decorator(func):

    def wrapper(*args, **kwargs):
        user = args[0].user
        if is_ca_admin(user):
            return HttpResponseRedirect('/info_display/display_admin_info')
        else:
            return func(*args, **kwargs)

    return wrapper
