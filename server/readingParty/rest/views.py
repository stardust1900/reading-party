from django.shortcuts import render
from django.http import HttpResponse
# Create your views here.
def query(request):
	return HttpResponse("Hello world")