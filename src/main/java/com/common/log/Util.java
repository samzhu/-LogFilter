package com.common.log;

import javax.servlet.http.HttpServletRequest;

public class Util {
	public static boolean isFormUrlEncoded(HttpServletRequest request) {

	    String contentTypeStr = request.getContentType();
	    if ("POST".equalsIgnoreCase(request.getMethod())
	            && contentTypeStr != null
	            && contentTypeStr.startsWith(AccessConstants.X_WWW_FORM_URLECODED)) {
	      return true;
	    } else {
	      return false;
	    }
	  }
}
