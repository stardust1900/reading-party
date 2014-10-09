#coding=utf-8  
from django.conf.urls import patterns, include, url
urlpatterns = patterns('reader.views',
    url(r'^login/$', 'login', name='login'),
    url(r'^logout/$', 'logout', name='logout'),
    url(r'^register/$', 'register', name='register'),
    url(r'^profile/$', 'showProfile', name='profile'),
    url(r'^showInviteCode/$', 'showInviteCode', name='showInviteCode'),
    url(r'^genIvc/(\d+)/$', 'genIvc', name='genIvc'),
    # url(r'^signin/$', 'signin', name='signin'),
)