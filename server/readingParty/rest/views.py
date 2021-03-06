from django.shortcuts import render
from django.http import HttpResponse
from sound.models import Sound
from sound.forms import SoundForm
from django.contrib.auth.models import User
from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from django.contrib import auth
import os
import sys
import subprocess
import json
import pytz
import hashlib
# Create your views here.


def toJson(obj):
    d = {}
    d['soundId'] = obj.id
    d['memo'] = obj.memo
    d['readerName'] = obj.reader.username
    d['pubtime'] = obj.pubTime.replace(tzinfo=pytz.utc).astimezone(
        pytz.timezone('Asia/Shanghai')).strftime('%Y-%m-%d %H:%M:%S')
    d['soundUrl'] = obj.soundfile.url
    return d


def soundsToJson(sounds):
    jsonResult = {}
    jsonList = []
    for sound in sounds:
        jsonList.append(toJson(sound))
    jsonResult['sounds'] = jsonList
    return jsonResult


def query(request):
    since_id = request.GET.get('since_id')
    max_id = request.GET.get('max_id')
    if max_id != None:
        sounds = Sound.objects.filter(id__gt=max_id).order_by('-pubTime')
    elif since_id != None:
        sounds = Sound.objects.filter(
            id__lt=since_id).order_by('-pubTime')[:10]
    else:
        sounds = Sound.objects.all().order_by('-pubTime')[:10]

    encodedjson = json.dumps(sounds, default=soundsToJson)

    return HttpResponse(encodedjson)


@csrf_exempt
def upload(request):
    if request.method == 'POST':
        # print(request.FILES)
        form = SoundForm(request.POST, request.FILES)
        # print(request.POST['bookUrl'])
        user = User.objects.all()[0]
        # print(user)
        if form.is_valid():
            fileName = request.FILES['soundfile'].name
            if(fileName.endswith('amr')):
                newSound = Sound(soundfile=request.FILES['soundfile'], memo=request.POST[
                                 'memo'], bookUrl=request.POST['bookUrl'], reader=user)
                newSound.save()
                mediaRoot = settings.MEDIA_ROOT[0] if isinstance(
                    settings.MEDIA_ROOT, type([])) else settings.MEDIA_ROOT
            amrfileName = newSound.soundfile.name
            # print(amrfileName)
            # soundsDir = strftime("/sounds/%Y/%m/%d/", gmtime())
            filePath = mediaRoot + '/' + amrfileName
            # print(filePath)
            amr2mp3(filePath)
            newSound.soundfile.name = amrfileName[
                0:amrfileName.index(".amr")] + '.mp3'
            newSound.save()
            return HttpResponse("success")
    return HttpResponse("failed")


def amr2mp3(f):
    if f.endswith(".amr"):
        # print(f.index(".amr"))
        name = f[0:f.index(".amr")]
        print(name)
        params = []
        params.append('ffmpeg')
        params.append('-i')
        params.append(f)
        params.append(name + '.mp3')
        subprocess.call(params)

@csrf_exempt
def doAuth(request):
    if request.method == 'POST':
        email = request.POST['email']
        password = request.POST['password']
        user = auth.authenticate(username=email, password=password)
        if user is not None:
            md5 = hashlib.md5(email).hexdigest()
            return HttpResponse("success:"+md5)

    return HttpResponse("failed")
