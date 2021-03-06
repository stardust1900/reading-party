# -*- coding: utf-8 -*-
from django.conf.urls import patterns, url

urlpatterns = patterns('sound.views',
    url(r'^list/$', 'list', name='list'),
    url(r'^toUpload/$', 'toUpload', name='toUpload'),
    url(r'^upload/$', 'upload', name='upload'),
    url(r'^edit/$', 'edit', name='edit'),
    url(r'^remove/(\d+)/$', 'remove', name='remove'),
    url(r'^toEdit/(\d+)/$', 'toEdit', name='toEdit'),
    url(r'^play/(\d+)/$', 'play', name='play'),
    url(r'^addComment/$', 'addComment', name='addComment'),
    url(r'^removeComment/(\d+)/$', 'removeComment', name='removeComment'),
)
