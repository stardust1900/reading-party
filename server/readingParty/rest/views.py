from django.shortcuts import render
from django.http import HttpResponse
from sound.models import Sound
from sound.forms import SoundForm
from django.contrib.auth.models import User
from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
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
	jsonResult={}
	jsonList=[]
	for sound in sounds:
		jsonList.append(toJson(sound))
	jsonResult['sounds'] = jsonList
	return jsonResult
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

@csrf_exempt
def upload(request):
	print('wwwwwwwwwwwwwwwww')
	if request.method == 'POST':
		print(request.FILES)
		form = SoundForm(request.POST, request.FILES)
		print(request.POST['bookUrl'])
		user = User.objects.all()[0]
		print(user)
		if form.is_valid():
			newSound = Sound(soundfile = request.FILES['soundfile'],memo = request.POST['memo'],bookUrl = request.POST['bookUrl'],reader = user)
			newSound.save()
			mediaRoot = settings.MEDIA_ROOT[0] if isinstance(settings.MEDIA_ROOT, type([])) else settings.MEDIA_ROOT
			print(mediaRoot)
			print(newSound.soundfile)
			return HttpResponse("success")
	return HttpResponse("failed")