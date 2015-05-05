# -*- coding: utf-8 -*-
from django import forms

class LoginForm(forms.Form):
	readername = forms.CharField(max_length=140)
	password = forms.CharField(max_length=140)