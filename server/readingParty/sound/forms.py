# -*- coding: utf-8 -*-
from django import forms
from models import *
class SoundForm(forms.Form):
    soundfile = forms.FileField(
        label='select file'
    )
    # memo = forms.CharField(max_length=140,label='memo',widget=forms.TextInput(attrs={'class' : 'form-control'}))
    memo = forms.CharField(max_length=140,label='memo')
    # bookUrl = forms.URLField(label='douban URL',required=False,widget=forms.TextInput(attrs={'class' : 'form-control'}))
    bookUrl = forms.URLField(label='douban URL',required=False)


class CommentForm(forms.Form):
	class Meta:
		model = Sound
		fields =['content']