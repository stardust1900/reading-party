---
layout: post
title: "django使用mysql.connector的问题"
description: ""
category: 
tags: []
---
django中是默认使用MySqldb作为mysql链接模块的。但是安装Mysqldb很不方便，不像mysql-connector，直接pip install就可以了。但是用mysql-connector的时候遇到中文入库的问题。总是unicodeError。

当数据库配置为下面这样的时候
{% highlight python %}
DATABASES = {
    # 'default': {
    #     'ENGINE': 'django.db.backends.sqlite3',
    #     'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
    # }
    'default': {
        'ENGINE': 'mysql.connector.django',
        # 'ENGINE': 'django.db.backends.mysql',
        'NAME': 'readingparty',
        'USER':'readingparty',
        'PASSWORD':'readingparty',
        'HOST':'127.0.0.1',
        'PORT':'3306',
    }
}
{% endhighlight %}

![error](/pic/snapshot/20140919/error1.PNG)

![error](/pic/snapshot/20140919/error2.PNG)

使用python manager.py dbshell 也报错

![error](/pic/snapshot/20140919/error3.PNG)

但是当配置文件是下面这样的时候，一切正常 

{% highlight python %}
DATABASES = {
    # 'default': {
    #     'ENGINE': 'django.db.backends.sqlite3',
    #     'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
    # }
    'default': {
        # 'ENGINE': 'mysql.connector.django',
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'readingparty',
        'USER':'readingparty',
        'PASSWORD':'readingparty',
        'HOST':'127.0.0.1',
        'PORT':'3306',
    }
}
{% endhighlight %}

显然不是数据库配置或者是python编码格式的问题，应该是mysql-connector模块的问题，但是我找不到问题在哪。。。