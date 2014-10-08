# coding=utf-8
from django.shortcuts import render_to_response
from django.shortcuts import render
from django.template import RequestContext
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from reader.forms import LoginForm, RegisterForm
from django.contrib import auth
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from django.core.paginator import Paginator
from django.core.paginator import EmptyPage
from django.core.paginator import PageNotAnInteger
from sound.models import Sound
# Create your views here.


def login(request):
	if request.method == 'GET':
		loginForm = LoginForm()
		if 'next' in request.GET:
			loginForm.next = request.GET['next']
		else:
			loginForm.next = "/"
		return render_to_response('login.html', {'form': loginForm}, context_instance=RequestContext(request))
	else:
		email = request.POST['email']
		password = request.POST['password']
		next = request.POST['next']
		if next.strip()=='':
			next = "/"
		user = auth.authenticate(username=email, password=password)

		if user is not None and user.is_active:
			auth.login(request, user)
			return HttpResponseRedirect(next)
		else:
			loginForm = LoginForm()
			if 'next' in request.GET:
				loginForm.next = request.GET['next']
			else:
				loginForm.next = "/"
			return render_to_response('login.html', {'form': loginForm, 'password_is_wrong': True}, context_instance=RequestContext(request))


def logout(request):
	auth.logout(request)
	return HttpResponseRedirect("/")


@login_required
def showProfile(request):
	limit = 10
	sounds = Sound.objects.filter(reader=request.user).order_by('-pubTime')
	paginator = Paginator(sounds, limit)
	page = request.GET.get('page')
	try:
		sounds = paginator.page(page)
	except PageNotAnInteger:
		sounds = paginator.page(1)
	except EmptyPage:
		sounds = paginator.page(paginator.num_pages)
	return render_to_response('profile.html',{'sounds': sounds},context_instance=RequestContext(request))


def register(request):
	if request.method == 'GET':
		registerForm = RegisterForm()
		return render_to_response('register.html',context_instance=RequestContext(request))
	else:
		username = request.POST.get('username','');
		email = request.POST.get('email','');
		password1 = request.POST.get('password1','');
		password2 = request.POST.get('password2','');
		errors = []

		register = RegisterForm({'readername':username,'readermail':email,'password1':password1,'password2':password2})
		if not register.is_valid():
			errors.extend(registerForm.errors.values())
			return render_to_response('register.html',RequestContext(request,{'username':username,'email':email,'errors':errors}))

		if password1!=password2:
			errors.append("两次输入的密码不一致!")
			return render_to_response('register.html',RequestContext(request,{'username':username,'email':email,'errors':errors}))
			# return render_to_response('register.html',{'errors':errors},context_instance=RequestContext(request))

		filterResult=User.objects.filter(username=username)
		if len(filterResult)>0:
			errors.append("用户名已存在")
			return render_to_response('register.html',RequestContext(request,{'username':username,'email':email,'errors':errors}))

		filterResult=User.objects.filter(email=email)
		if len(filterResult)>0:
			errors.append("电邮已注册")
			return render_to_response('register.html',RequestContext(request,{'username':username,'email':email,'errors':errors}))

		user=User()
		user.username=username
		user.email=email
		user.set_password(password1)
		user.save()

		return render_to_response('login.html',RequestContext(request,{'is_from_register':True}))
