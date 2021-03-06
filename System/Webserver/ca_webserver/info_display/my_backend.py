import hashlib
import json
import requests

import pdb

from django.contrib.auth.models import User

base_url = "https://192.168.50.33:8100"

class RemoteBackend(object):
    def authenticate(self, request, username=None, password=None):
        #TODO could save users?
        if username is None or password is None:
            return None
        digester = hashlib.sha1()
        digester.update(password.encode('utf-8'))

        #TODO REST API call
        pwd_json = {'userPasswordHash': digester.hexdigest()} # {'userPasswordHash': digester.digest().decode('utf-8')}
        login_response = requests.post(("%s/users/verify/%s" % (base_url, username)), data=json.dumps(pwd_json), verify='/home/webserver/virtual_environment/ca_webserver/ssl/cacert.pem')
        # pdb.set_trace()
        if login_response.ok:
            auth_status = login_response.json()['correctCredentials']
            #pdb.set_trace()
            if auth_status:
                try:
                    user = User.objects.get(username=username)
                    # Dirty fix
                    user.backend = 'RemoteBackend'
                except User.DoesNotExist:
                    user = User(username=username)
                    user.backend = 'RemoteBackend'
                    user.save()
                return user
        return None

    def get_user(self, user_id):
        try:
            return User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return None
