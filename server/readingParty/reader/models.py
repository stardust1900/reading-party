# -*- coding: utf-8 -*-
from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class Reader(models.Model):
	name=models.CharField(max_length=20)
	bio=models.CharField(max_length=140,null=True)

class InviteCode(models.Model):
	owner = models.ForeignKey(User)
	code = models.CharField(max_length=140,unique=True)
	guest = models.IntegerField(null=True)
	flag = models.SmallIntegerField(default=0)