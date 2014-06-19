# -*- coding: utf-8 -*-
from django.conf.urls import patterns, url

urlpatterns = patterns('sound.views',
    url(r'^list/$', 'list', name='list'),
    url(r'^toUpload/$', 'toUpload', name='toUpload'),
    url(r'^upload/$', 'upload', name='upload'),
    url(r'^edit/(\d+)/$', 'edit', name='edit'),
    url(r'^remove/(\d+)/$', 'remove', name='remove'),
)
