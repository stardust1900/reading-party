package com.shenshan.readingparty.utils;

public interface RestConst {
	String DOMAIN = "http://192.168.137.1:8000";

	String GETURL = DOMAIN + "/rest/query";
	/**
	 * django 中 settings.APPEND_SLASH is True post的url必须以“/”结尾
	 */
	String POSTURL = DOMAIN + "/rest/upload/";

	String AUTHURL = DOMAIN + "/rest/doAuth/";

}
