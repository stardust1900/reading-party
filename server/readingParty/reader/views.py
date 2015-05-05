#coding=utf-8  
from django.shortcuts import render_to_response
from django.shortcuts import render
from django.template import RequestContext
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from reader.forms import LoginForm
from django.contrib import auth
# Create your views here.
def login(request):
	print('111111')
	if request.method == 'GET':
		loginForm = LoginForm()
		if 'next' in request.GET:
			loginForm.next = request.GET['next']
		else:
			loginForm.next = "/"
		return render_to_response('login.html',{'form': loginForm},context_instance=RequestContext(request))
	else:
		email = request.POST['email']
		password = request.POST['password']
		next = request.POST['next']
		user = auth.authenticate(username=email, password=password)
		print(email)
		print(password)
		print(user)
		if user is not None and user.is_active:
			auth.login(request, user)
			return HttpResponseRedirect(next)
		else:
			loginForm = LoginForm()
			if 'next' in request.GET:
				loginForm.next = request.GET['next']
			else:
				loginForm.next = "/"
			return render_to_response('login.html',{'form': loginForm,'password_is_wrong':True},context_instance=RequestContext(request))
