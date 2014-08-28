from django.shortcuts import render
from django.http import HttpResponse
from sound.models import Sound
from django.conf import settings
import json
import pytz 
# Create your views here.
def toJson(obj):
	d={}
	d['soundId']=obj.id
	d['memo']=obj.memo
	d['readerName']=obj.reader.username
	d['pubtime']=obj.pubTime.replace(tzinfo=pytz.utc).astimezone(pytz.timezone('Asia/Shanghai')).strftime('%Y-%m-%d %H:%M:%S')
	d['soundUrl']=obj.soundfile.url
	return d
def soundsToJson(sounds):
	jsonList=[]
	for sound in sounds:
		jsonList.append(toJson(sound))
	return jsonList
def query(request):
	since_id = request.GET.get('since_id')
	max_id = request.GET.get('max_id')
	if since_id != None:
		sounds = Sound.objects.filter(id__gt=since_id).order_by('-pubTime')
	elif max_id != None:
		sounds = Sound.objects.filter(id__lt=max_id).order_by('-pubTime')[:10]
	else:
		sounds = Sound.objects.all().order_by('-pubTime')[:10]

	encodedjson = json.dumps(sounds,default=soundsToJson)

	return HttpResponse(encodedjson)