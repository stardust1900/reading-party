# -*- coding: utf-8 -*-
from django.db import models

# Create your models here.
class Sound(models.Model):
    soundfile = models.FileField(upload_to='sounds/%Y/%m/%d')
    memo = models.CharField(max_length=140)
    bookUrl = models.URLField(null=True)
