# -*- coding:utf-8 -*-
# backends.py

from django.contrib.auth.backends import ModelBackend
from django.contrib.auth.models import User

class EmailCheckModelBackend(ModelBackend):
    def authenticate(self, username = None, password = None, is_staff = None):
        try:
            user = User.objects.get(email = username)
            if user.check_password(password):
                if is_staff is not None:
                    if user.is_staff == is_staff:
                        return user
                    else:
                        return None
                return user
            
        except User.DoesNotExist:
            return None