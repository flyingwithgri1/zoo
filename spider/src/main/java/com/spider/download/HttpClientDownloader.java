package com.spider.download;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;
import com.spider.bean.Page;
import com.spider.bean.Request;
import com.spider.bean.SiteConfig;
import com.spider.bean.SiteConfigManager;
import com.spider.utils.SpiderConfig;
import com.common.util.IPUtils;

public class HttpClientDownloader implements Downloader{

	private Logger logger = Logger.getLogger(getClass());
	
	private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
	
	private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
	
	//暂时只解析状态码为200的响应，后期可在siteConfig里配置状态码
	private final Set<Integer> acceptStatCode = Sets.newHashSet(200, 301, 302);
	
	private CloseableHttpClient getHttpDefaultClient() {
		return httpClientGenerator.getDefaultClient();
	}
	
	private CloseableHttpClient getHttpClient(SiteConfig site) {
        if (site == null ) {
            return httpClientGenerator.getClient(null);
        }
        String siteName = site.getSiteName();
        CloseableHttpClient httpClient = httpClients.get(siteName);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(siteName);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(siteName, httpClient);
                }
            }
        }
        return httpClient;
    }
	
	@Override
	public Page download(Request request) {
		SiteConfig siteConfig = SiteConfigManager.getSiteConfig(request.getSiteName());
		CloseableHttpClient client ;
		String url = request.getUrl();
		HttpUriRequest uriRequest;
		if(url.contains("weibo")){
			uriRequest = RequestBuilder.get(url)//"http://weibo.com/6004281123/FCcq8m6nP" //unfoldUrl = "";
					.addHeader("Cookie",siteConfig.getCookies().get("Cookie")).build();
			/*if(request.getExtra("isUnfoldText") != null && (Boolean)request.getExtra("isUnfoldText")){
				uriRequest.addHeader("Referer", "https://weibo.com/pearvideo?refer_flag=1001030101_&is_hot=1");
//				uriRequest.addHeader("Host","weibo.com");
			}//
			uriRequest.addHeader("Referer", "https://weibo.com/pearvideo?refer_flag=1001030101_&is_hot=1");*/
			client = getHttpDefaultClient();
		} else if(url.contains("weixin")){
			uriRequest = RequestBuilder.get(url)
					.addHeader("Cookie", siteConfig.getCookies().get("Cookie")).build();
			client = getHttpDefaultClient();
		}else{
			uriRequest = getHttpUriRequest(request, siteConfig);
			
			client = getHttpClient(siteConfig);
		}
		
		HttpClientContext context = HttpClientContext.create();
		int statusCode = 0;
		CloseableHttpResponse response = null;
		try {
			//在webmagic中，httpclient没有关闭
			response = client.execute(uriRequest, context);
			statusCode = response.getStatusLine().getStatusCode();
			request.setStatusCode(statusCode);
			if(statusAccept(acceptStatCode, statusCode)){
				if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY
						|| statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
					String redirectUrl = response.getFirstHeader("Location").getValue();
					if (redirectUrl != null && !redirectUrl.isEmpty()) {
						EntityUtils.consume(response.getEntity());
						request.setUrl(redirectUrl);
						uriRequest = getHttpUriRequest(request, siteConfig);
						response = getHttpClient(siteConfig).execute(uriRequest);
						statusCode = response.getStatusLine()
								.getStatusCode();
					}
				}
				Page page = hadnlerResponse(request, response); 
				return page;
			}else{
				logger.warn("status code error: " + statusCode + "; url is:" + request.getUrl());
				logger.error(request);
				return null;
			}
		} catch (IOException e) {
			logger.warn("download page " + request.getUrl() + " error", e);
            if (siteConfig.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, siteConfig);
            }
            return null;
		}finally {
        	request.setStatusCode(statusCode);
            try {
                if (response != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
	}

	private HttpUriRequest getHttpUriRequest(Request request,SiteConfig siteConfig){
		RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
		/*Map<String, String> headers = new HashMap<>();
		if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }*/
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(siteConfig.getTimeOut())//从连接池取连接的超时时间
                .setSocketTimeout(siteConfig.getTimeOut())//建立连接的超时时间
                .setConnectTimeout(siteConfig.getTimeOut())//获取数据的连接时间
                .setCookieSpec(CookieSpecs.DEFAULT);
//        		.setCookieSpec(CookieSpecs.BEST_MATCH);	//因BEST_MATCH被提倡禁用，故使用DEFAULT
        requestBuilder.setConfig(requestConfigBuilder.build());
        if(siteConfig.getCookies() != null){
        	Iterator<Map.Entry<String, String>> iter = siteConfig.getCookies().entrySet().iterator();
        	while(iter.hasNext()){
        		Map.Entry<String, String> eneity = iter.next();
        		if(eneity.getValue() !=null && eneity.getKey() != null){
        			requestBuilder.addHeader(eneity.getKey(), eneity.getValue());
        		}
        	}
        }
        return requestBuilder.build();
	}
	
	protected RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePair != null && nameValuePair.length > 0) {
                requestBuilder.addParameters(nameValuePair);
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }
	
	protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
		return acceptStatCode.contains(statusCode);
	}
	
	private Page hadnlerResponse(Request request,HttpResponse response) throws UnsupportedOperationException, IOException{
		String charset = SiteConfigManager.getSiteConfig(request.getSiteName()).getCharset();
		//这里可对下载下来的content进行校验，如包含某些关键字即为被反爬
		String content = getContent(charset, response);
		Page page = new Page();
		page.setRequest(request);
		page.setContent(content);
		page.setStatusCode(response.getStatusLine().getStatusCode());
		return page;
	}

	private String getContent(String charset, HttpResponse response) throws UnsupportedOperationException, IOException {
		if (StringUtils.isBlank(charset)) {
            byte[] contentBytes = IOUtils.toByteArray(response.getEntity().getContent());
            String htmlCharset = getHtmlCharset(response, contentBytes);
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()" + Charset.defaultCharset() );
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(response.getEntity().getContent(), charset);
        }
	}
	
	protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset;
        // charset
        // 1:encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            logger.debug("Auto get charset: " + charset);
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2:charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2:html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        logger.debug("Auto get charset: {}" + charset);
        // 3:todo use tools as cpdetector for content decode
        return charset;
    }
	
	protected Page addToCycleRetry(Request request, SiteConfig site) {
        Page page = new Page();
        int cycleTriedTimes = request.getCycleTriedTimes();
        cycleTriedTimes++;
        if (cycleTriedTimes >= site.getCycleRetryTimes()) {
            return null;
        }
        page.addTargetRequest(request.setPriority(0).setCycleTriedTimes(cycleTriedTimes));
        page.setNeedCycleRetry(true);
        return page;
    }
}
