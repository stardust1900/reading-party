from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from sound.forms import SoundForm
from sound.models import Sound

from django.conf import settings
# Create your views here.
def list(request):
	sounds = Sound.objects.all()
	print(settings.STATIC_ROOT)
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