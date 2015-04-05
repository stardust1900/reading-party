#coding=utf-8  
from django.shortcuts import render_to_response
from django.shortcuts import render

# Create your views here.
def login(request):
	return render_to_response('login.html')