package com.common.util;

import org.apache.commons.lang3.StringEscapeUtils;

public class HTMLEscapeUtil {
	/**
	 * html格式转义字符转换为String类型
	 * @param htmlStr
	 * @return
	 */
	public static String htmlEscape2Str(String htmlStr) {
		try {
			return StringEscapeUtils.unescapeHtml4(htmlStr);
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * String格式转义字符转换为html类型
	 * @param str
	 * @return
	 */
	public static String strEscape2Html(String str) {
		try {
			return StringEscapeUtils.escapeHtml4(str);
		} catch (Exception e) {
			return "";
		}
	}
	
}
