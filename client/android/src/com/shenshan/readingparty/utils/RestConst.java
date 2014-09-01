package com.shenshan.readingparty.utils;

public interface RestConst {
	String GETURL = "http://192.168.137.1:8000/rest/query";
	/**
	 * django 中 settings.APPEND_SLASH is True post的url必须以“/”结尾 
	 */
	String POSTURL = "http://192.168.137.1:8000/rest/upload/";
	String DOMAIN = "http://192.168.137.1:8000";
}
