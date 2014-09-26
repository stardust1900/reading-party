from django.http import HttpResponse
from django.shortcuts import render_to_response

def page_not_found_view(request):
	# return HttpResponse("404")
	return render_to_response('404.html')