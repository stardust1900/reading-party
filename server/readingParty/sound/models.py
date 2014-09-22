# -*- coding: utf-8 -*-
from django.db import models
from django.db.models.signals import post_delete 
from django.contrib.auth.models import User
import os 

# Create your models here.
class Sound(models.Model):
	reader = models.ForeignKey(User)
	soundfile = models.FileField(upload_to='sounds/%Y/%m/%d')
	memo = models.CharField(max_length=140)
	bookUrl = models.URLField(null=True)
	pubTime = models.DateTimeField(auto_now=True, auto_now_add=True)


def delete_file(sender, **kwargs): 
	patch = kwargs['instance'] 
	try:
		os.remove(patch.soundfile.path)
	except Exception, e:
		print(e)
	
post_delete.connect(delete_file, sender=Sound)

class Comment(models.Model):
	sound = models.ForeignKey(Sound)
	content = models.TextField()
	commenter = models.ForeignKey(User)
	pubTime = models.DateTimeField(auto_now=True, auto_now_add=True)