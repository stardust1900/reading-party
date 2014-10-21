# coding=utf-8
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from sound.forms import SoundForm
from sound.forms import CommentForm
from sound.models import Sound
from sound.models import Comment
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.conf import settings
from django.contrib.auth.decorators import login_required

from django.core.paginator import Paginator
from django.core.paginator import EmptyPage
from django.core.paginator import PageNotAnInteger
from django.conf import settings

from django.db.models.fields.files import FieldFile
import os
import sys
import subprocess
from time import gmtime, strftime
import logging

# Get an instance of a logger
logger = logging.getLogger("readingparty")

def list(request):
    # print(logger)
    # logger.error("error,error")
    # logger.info("info,info")
    # logger.debug("debug,debug")
    # logger.warn("warn,warn")
    limit = 10  # 每页显示的记录数
    sounds = Sound.objects.all().order_by('-pubTime')
    paginator = Paginator(sounds, limit)  # 实例化一个分页对象

    page = request.GET.get('page')  # 获取页码
    try:
        sounds = paginator.page(page)  # 获取某页对应的记录
    except PageNotAnInteger:  # 如果页码不是个整数
        sounds = paginator.page(1)  # 取第一页的记录
    except EmptyPage:  # 如果页码太大，没有相应的记录
        sounds = paginator.page(paginator.num_pages)  # 取最后一页的记录
    return render_to_response('list2.html', {'sounds': sounds},context_instance=RequestContext(request))

@login_required
def toUpload(request):
    form = SoundForm()
    return render_to_response('upload.html', {'form': form}, context_instance=RequestContext(request))


@login_required
def upload(request):
    if request.method == 'POST':
        form = SoundForm(request.POST, request.FILES)
        if form.is_valid():
            fileName = request.FILES['soundfile'].name
            if(fileName.endswith('amr') or fileName.endswith('mp3')):
                newSound = Sound(soundfile=request.FILES['soundfile'], memo=request.POST[
                                 'memo'], bookUrl=request.POST['bookUrl'], reader=request.user)
                newSound.save()

                if(fileName.endswith('amr')):
                    # mediaRoot = settings.MEDIA_ROOT[0] if isinstance(settings.MEDIA_ROOT,
                    # __builtins__.['list']) else settings.MEDIA_ROOT
                    mediaRoot = settings.MEDIA_ROOT[0] if isinstance(
                        settings.MEDIA_ROOT, type([])) else settings.MEDIA_ROOT
                    # soundsDir = strftime("/sounds/%Y/%m/%d/", gmtime())
                    amrfileName = newSound.soundfile.name
                    filePath = mediaRoot + '/' + amrfileName
                    amr2mp3(filePath)
                    newSound.soundfile.name = amrfileName[0:amrfileName.index(".amr")] + '.mp3'
                    newSound.save()
            else:
                form.field_error = '请上传mp3或amr格式的文件！'
                return render_to_response('upload.html', {'form': form}, context_instance=RequestContext(request))

    return HttpResponseRedirect(reverse('sound.views.list'))


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

@login_required
def toEdit(request, soundId):
    s = Sound.objects.get(id=soundId)
    data = {'': None, 'memo': s.memo, 'bookUrl': s.bookUrl}
    form = SoundForm(data)
    form.soundId = s.id

    return render_to_response('edit.html', {'form': form}, context_instance=RequestContext(request))

@login_required
def edit(request):
    # s = Sound.objects.get(id=)
    if request.method == 'POST':
        soundId = request.POST['soundId']
        # print(request.POST['soundId'])
        s = Sound.objects.get(id=soundId)
        if s.reader == request.user:
            s.memo = request.POST['memo']
            s.bookUrl = request.POST['bookUrl']
            s.save()
    else:
        print("get")
    # sounds = Sound.objects.all()
    # return render_to_response('list.html',{'sounds':sounds},context_instance=RequestContext(request))
    # 使用url转向 更改url到list
    return HttpResponseRedirect(reverse('sound.views.list'))

@login_required
def remove(request, soundId):
    s = Sound.objects.get(id=soundId)
    if s.reader == request.user:
        s.delete()
        return HttpResponseRedirect(reverse('sound.views.list'))


def play(request, soundId):
    s = Sound.objects.get(id=soundId)
    comments = s.comment_set.all()
    form = CommentForm()
    return render_to_response('play.html', {'form': form,'sound':s,'comments':comments}, context_instance=RequestContext(request))

@login_required
def addComment(request):
    if request.method == 'POST':
        form = CommentForm(request.POST)
        if form.is_valid():
            content = request.POST['content']
            soundId = request.POST['soundId']
            sound = Sound.objects.get(id=soundId)
            comment = Comment(sound=sound,content=content,commenter=request.user)
            comment.save()
    return HttpResponseRedirect(reverse('sound.views.play',args=(soundId,)))

@login_required
def removeComment(request, commentId):
    c = Comment.objects.get(id=commentId)
    if c.commenter == request.user:
        c.delete()
    return HttpResponseRedirect(reverse('sound.views.play',args=(c.sound.id,)))
