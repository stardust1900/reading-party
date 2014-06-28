# -*- coding: utf-8 -*-
from django import forms

class SoundForm(forms.Form):
    soundfile = forms.FileField(
        label='Select a file'
    )
    memo = forms.CharField(max_length=140,label='memo',widget=forms.TextInput(attrs={'class' : 'form-control'}))
    bookUrl = forms.URLField(label='douban URL',required=False,widget=forms.TextInput(attrs={'class' : 'form-control'}))