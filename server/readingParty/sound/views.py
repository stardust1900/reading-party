from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from sound.forms import SoundForm
from sound.models import Sound

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
	else:
		form = SoundForm()
	sounds = Sound.objects.all()
	return render_to_response('list.html',{'sounds':sounds, 'form': form},context_instance=RequestContext(request))

def toEdit(request,soundId):
	s = Sound.objects.get(id=soundId)
	data = {'':None,'memo':s.memo,'bookUrl':s.bookUrl}
	form = SoundForm(data)
	form.soundId = s.id
	# form.memo = s.memo
	# form.bookUrl = s.bookUrl
	# form.id = s.id
	print(form)
	return render_to_response('edit.html',{'form': form},context_instance=RequestContext(request));

def edit(request):
	# s = Sound.objects.get(id=)
	return HttpResponse("Edit ")

def remove(request,soundId):
	s = Sound.objects.get(id=soundId)
	s.delete()
	return  list(request)