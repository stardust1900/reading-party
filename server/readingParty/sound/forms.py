# -*- coding: utf-8 -*-
from django import forms

class SoundForm(forms.Form):
    docfile = forms.FileField(
        label='Select a file'
    )