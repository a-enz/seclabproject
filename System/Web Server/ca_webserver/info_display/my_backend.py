import hashlib
import json
import requests

import pdb

from django.contrib.auth.models import User

base_url = "http://127.0.0.1:8101"

class RemoteBackend(object):

    def authenticate(self, request, username=None, password=None):
        #TODO could save users?
        digester = hashlib.sha1()
        digester.update(password.encode('utf-8'))

        #TODO REST API call
        pwd_json = {'userPasswordHash': digester.hexdigest()} # {'userPasswordHash': digester.digest().decode('utf-8')}
        login_response = requests.post(("%s/users/verify/%s" % (base_url, username)), data=json.dumps(pwd_json))
        if login_response.ok:
            auth_status = login_response.json()['correctCredentials']
            pdb.set_trace()
            if auth_status == 'true':
                try:
                    user = User.objects.get(username=username)
                except User.DoesNotExist:
                    user = User(username=username)
                    user.save()
                return user
        return None

    # def authenticate(self, request, username=None, password=None):
    #     if username == 'none':
    #         return None
    #     else:
    #         try:
    #             user = User.objects.get(username=username)
    #         except User.DoesNotExist:
    #             user = User(username=username)
    #             user.save()
    #         return user

    def get_user(self, user_id):
        try:
            return User.objects.get(pk=user_id)
        except User.DoesNotExist:
            return None
