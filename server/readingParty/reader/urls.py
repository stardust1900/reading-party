#coding=utf-8  
from django.conf.urls import patterns, include, url
urlpatterns = patterns('reader.views',
    url(r'^login/$', 'login', name='login'),
)