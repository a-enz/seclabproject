from django import forms

class UserLoginForm(forms.Form):
    user_id = forms.CharField(label='User ID', max_length=10)
    password = forms.CharField(label='Password', max_length=256, widget=forms.PasswordInput)

class GetInfoForm(forms.Form):
    user_id = forms.CharField(max_length=10, widget=forms.HiddenInput())
