# -*- coding: utf-8 -*-
from django.conf.urls import patterns, url
urlpatterns = patterns('rest.views',
    url(r'^query/$', 'query', name='restquery'),
    url(r'^upload/$', 'upload', name='restupload'),
    #url(r'^register/$', 'register', name='register'),
    # url(r'^signin/$', 'signin', name='signin'),
)