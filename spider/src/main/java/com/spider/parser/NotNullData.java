package com.spider.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.joda.time.DateTime;

import com.spider.utils.CommonUtil;
import com.spider.utils.IPUtils;

public class NotNullData {
	
	private static String[] string = {"66","67","68"};
	private static Set<String> set = new HashSet<String>();
	
	
	static {
		set.addAll(Arrays.asList(string));
	}

	public static void setNotNullData(Document document,Map<String, Object> extra){
		String siteId = extra.get("siteId").toString();
		if(set.contains(siteId)){
			document.put("siteId", "17");
		}else{
			document.put("siteId", extra.get("siteId"));
		}
		document.put("siteName", extra.get("siteName"));
		document.put("siteTypeId", extra.get("siteTypeId"));
		document.put("siteTypeName", extra.get("siteTypeName"));
		document.put("typeId", extra.get("typeId"));
		document.put("typeName", extra.get("typeName"));
		document.put("categoryName", extra.get("categoryName"));
		document.put("categoryId", extra.get("categoryId"));
		document.put("isNew", extra.get("isNew"));
		document.put("crawlrepeat", extra.get("crawlrepeat"));
		document.put("getDateTime",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
		document.put("entityId",StringUtils.join(IPUtils.getLocalMachineAddress(), CommonUtil.generateShortUuid()));
	}
}
