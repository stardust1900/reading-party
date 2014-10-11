# coding=utf-8
from django.shortcuts import render_to_response
from django.shortcuts import render
from django.template import RequestContext
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from reader.forms import LoginForm, RegisterForm
from django.contrib import auth
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from django.core.paginator import Paginator
from django.core.paginator import EmptyPage
from django.core.paginator import PageNotAnInteger
from sound.models import Sound
from reader.models import InviteCode
import hashlib
import time
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

	codes = InviteCode.objects.filter(owner=request.user).filter(flag=0)
	return render_to_response('profile.html',{'sounds': sounds,'codes':codes},context_instance=RequestContext(request))


def register(request):
	if request.method == 'GET':
		if request.GET.get('ivc'):
			ivc = request.GET['ivc']
			errors = []
			try:
				codeObj = InviteCode.objects.get(code=ivc)
			except Exception,e:
				errors.append("无效的邀请码！")
				return render_to_response('ivc.html',context_instance=RequestContext(request,{'errors':errors}))
			if codeObj.flag != 0:
				errors.append("邀请码已被使用！")
				return render_to_response('ivc.html',context_instance=RequestContext(request,{'errors':errors}))
			registerForm = RegisterForm()
			return render_to_response('register.html',context_instance=RequestContext(request,{'ivc':ivc}))
		else:
			return render_to_response('ivc.html',context_instance=RequestContext(request))
	else:
		username = request.POST.get('username','');
		email = request.POST.get('email','');
		password1 = request.POST.get('password1','');
		password2 = request.POST.get('password2','');
		ivc = request.POST.get('ivc','');
		errors = []

		register = RegisterForm({'readername':username,'readermail':email,'password1':password1,'password2':password2,'ivc':ivc})
		if not register.is_valid():
			errors.extend(register.errors.values())
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

		codeObj = InviteCode.objects.get(code=ivc)
		codeObj.guest=user.id
		codeObj.flag = 1
		codeObj.save()

		return render_to_response('login.html',RequestContext(request,{'is_from_register':True}))

@login_required
def showInviteCode(request):
	if not request.user.is_superuser:
		return HttpResponseRedirect("/")

	codes = InviteCode.objects.all().order_by('owner')
	users = User.objects.all().order_by('id')
	return render_to_response('invitecode.html',{'codes':codes,'users':users},context_instance=RequestContext(request))

@login_required
def genIvc(request,ownerId):
	owner = User.objects.get(id=ownerId)

	for i in range(0,5):
		ivc = InviteCode(owner=owner,code=hashlib.md5(owner.email+('%d%d' % (i,time.time()))).hexdigest())
		ivc.save()
	
	return HttpResponseRedirect(reverse('reader.views.showInviteCode'))

@login_required
def changePwd(request):
	if request.method == 'POST':
		oldpassword = request.POST.get('oldpassword','');
		password1 = request.POST.get('password1','');
		password2 = request.POST.get('password2','');

		user = auth.authenticate(username=request.user.email, password=oldpassword)
		if user is not None and user.is_active:
			if password1==password2:
				user.set_password(password1)
				user.save()
				return HttpResponse("success")
			else:
				return HttpResponse("different")
		else:
			return HttpResponse("error")