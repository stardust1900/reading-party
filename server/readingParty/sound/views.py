from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from sound.forms import SoundForm
# Create your views here.
def list(request):
	#return HttpResponse("Hello world")
	return render_to_response('list.html');

def toUpload(request):
	#return HttpResponse("Hello world")
	form = SoundForm()

	return render_to_response('upload.html',{'form': form},context_instance=RequestContext(request));

def upload(request):
	#return HttpResponse("Hello world")
	return render_to_response('list.html');