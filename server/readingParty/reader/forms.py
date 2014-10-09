# -*- coding: utf-8 -*-
from django import forms

class LoginForm(forms.Form):
	readername = forms.CharField(max_length=140)
	password = forms.CharField(max_length=140)


class RegisterForm(forms.Form):
	readermail = forms.CharField(max_length=140,required=True)
	readername = forms.CharField(max_length=140,required=True)
	password1 = forms.CharField(max_length=140,required=True)
	password2 = forms.CharField(max_length=140,required=True)
	ivc = forms.CharField(max_length=140,required=True)