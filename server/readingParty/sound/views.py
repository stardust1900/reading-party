#coding=utf-8  
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from sound.forms import SoundForm
from sound.models import Sound
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.conf import settings
# Create your views here.
def list(request):
	sounds = Sound.objects.all()
	return render_to_response('list.html',{'sounds':sounds})

def toUpload(request):
	#return HttpResponse("Hello world")
	form = SoundForm()
	return render_to_response('upload.html',{'form': form},context_instance=RequestContext(request));

def upload(request):
	if request.method == 'POST':
	    form = SoundForm(request.POST, request.FILES)
	    if form.is_valid():
		    newSound = Sound(soundfile = request.FILES['soundfile'],memo = request.POST['memo'],bookUrl = request.POST['bookUrl'])
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


def remove(request,soundId):
	s = Sound.objects.get(id=soundId)
	s.delete()
	return  HttpResponseRedirect(reverse('sound.views.list'))