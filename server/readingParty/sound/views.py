#coding=utf-8  
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from sound.forms import SoundForm
from sound.models import Sound
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.conf import settings
from django.contrib.auth.decorators import login_required

from django.core.paginator import Paginator
from django.core.paginator import EmptyPage
from django.core.paginator import PageNotAnInteger

# Create your views here.
def list(request):
	limit = 10  # 每页显示的记录数
	sounds = Sound.objects.all()
	paginator = Paginator(sounds, limit)  # 实例化一个分页对象

	page = request.GET.get('page')  # 获取页码
	try:
	    sounds = paginator.page(page)  # 获取某页对应的记录
	except PageNotAnInteger:  # 如果页码不是个整数
	    sounds = paginator.page(1)  # 取第一页的记录
	except EmptyPage:  # 如果页码太大，没有相应的记录
	    sounds = paginator.page(paginator.num_pages)  # 取最后一页的记录

	return render_to_response('list.html',{'sounds':sounds})

@login_required
def toUpload(request):
	#return HttpResponse("Hello world")
	form = SoundForm()
	return render_to_response('upload.html',{'form': form},context_instance=RequestContext(request));

@login_required
def upload(request):
	if request.method == 'POST':
	    form = SoundForm(request.POST, request.FILES)

	    if form.is_valid():
		    newSound = Sound(soundfile = request.FILES['soundfile'],memo = request.POST['memo'],bookUrl = request.POST['bookUrl'],reader = request.user)
		    newSound.save()
	    else:
		    return render_to_response('upload.html',{'form': form},context_instance=RequestContext(request));
	# else:
	# 	form = SoundForm()
	# sounds = Sound.objects.all()
	# return render_to_response('list.html',{'sounds':sounds, 'form': form},context_instance=RequestContext(request))
	#使用url转向 更改url到list
	return HttpResponseRedirect(reverse('sound.views.list'))


def toEdit(request,soundId):
	s = Sound.objects.get(id=soundId)
	data = {'':None,'memo':s.memo,'bookUrl':s.bookUrl}
	form = SoundForm(data)
	form.soundId = s.id

	return render_to_response('edit.html',{'form': form},context_instance=RequestContext(request));


def edit(request):
	# s = Sound.objects.get(id=)
	if request.method == 'POST':
		soundId = request.POST['soundId']
		print(request.POST['soundId'])
		s = Sound.objects.get(id=soundId)
		s.memo = request.POST['memo']
		s.bookUrl = request.POST['bookUrl']
		s.save()
	else:
		print("get")
	# sounds = Sound.objects.all()
	# return render_to_response('list.html',{'sounds':sounds},context_instance=RequestContext(request))
	#使用url转向 更改url到list
	return HttpResponseRedirect(reverse('sound.views.list'))

@login_required
def remove(request,soundId):
	s = Sound.objects.get(id=soundId)
	s.delete()
	return  HttpResponseRedirect(reverse('sound.views.list'))