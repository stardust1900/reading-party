# -*- coding: utf-8 -*-
from django.db import models

# Create your models here.
class Reader(models.Model):
	name=models.CharField(max_length=20)
	bio=models.CharField(max_length=140,null=True)