# -*- coding: utf-8 -*-
from django import forms

class SoundForm(forms.Form):
    soundfile = forms.FileField(
        label='Select a file'
    )
    memo = forms.CharField(max_length=140,label='memo')
    bookUrl = forms.URLField(label='douban URL',required=False)