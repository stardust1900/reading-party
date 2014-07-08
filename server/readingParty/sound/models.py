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


def delete_file(sender, **kwargs): 
	patch = kwargs['instance'] 
	os.remove(patch.soundfile.path) 

post_delete.connect(delete_file, sender=Sound)