package com.spider.download;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import com.spider.bean.SiteConfig;

/**
 * 参考：http://blog.csdn.net/u014704496/article/details/40863045
 *     http://www.yeetrack.com/?p=782#Connection-persistence
 *     http://blog.csdn.net/heyutao007/article/details/49275253
 * 另：http://www.yeetrack.com/?p=923 webdriver
 * 
 * Location 302 Moved Temporarily (from disk cache)
 * */
public class HttpClientGenerator {

	protected Logger logger = Logger.getLogger(getClass());
	private PoolingHttpClientConnectionManager connectionManager;
	
	public HttpClientGenerator() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        // 将每个路由基础的连接增加到20
        connectionManager.setDefaultMaxPerRoute(20);
        // 将最大连接数增加到200,这个设置要根据线程池的数量来设置
        connectionManager.setMaxTotal(200);
        
//        HttpHost host = new HttpHost("webservice.webxml.com.cn");//针对的主机  
//        connectionManager.setMaxPerRoute(new HttpRoute(host), 5);//每个路由器对每个服务器允许最大5个并发访问
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
		new Runnable() {
			@Override
			public void run() {
				// 关闭过期的连接  
				connectionManager.closeExpiredConnections();  
				// 关闭空闲时间超过1分钟的链接
				connectionManager.closeIdleConnections(1, TimeUnit.MINUTES);  
			}
		},5, 5, TimeUnit.MINUTES);
	}
	
	public CloseableHttpClient getDefaultClient() {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(connectionManager);
		return httpClientBuilder.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36").build();
	}
	
	public CloseableHttpClient getClient(SiteConfig config) {
        return generatorClient(config);
    }
	
	public CloseableHttpClient generatorClient(SiteConfig config){
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(connectionManager);
        /*
         * 失败后立刻重试
         * SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        if (config != null) {
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(config.getRetryTimes(), true));
        }
        generateCookie(httpClientBuilder, config);
        return httpClientBuilder.build();*/
        httpClientBuilder.setRetryHandler(new HttpRequestRetryHandler() {
        	@Override
            public boolean retryRequest(
                    IOException exception,
                    int executionCount,
                    HttpContext context) {
        		logger.info("触发自定义异常机制");
                if (executionCount >= config.getRetryTimes()) {
                    // 如果已经重试了RetryTimes次，就放弃
                	logger.info("超过非队列重试次数：" + config.getSiteName());
                    return false;
                }
                
                if(exception instanceof SocketTimeoutException){
                	logger.info("socket超时：" + config.getSiteName());
                	return false;
                }
                if (exception instanceof InterruptedIOException) {
                	logger.info("链接被打扰：" + config.getSiteName());
                    // 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                	logger.info("目标服务器不可达：" + config.getSiteName());
                    // 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                	logger.info("连接被拒绝：" + config.getSiteName());
                    // 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {
                	logger.info("ssl握手异常：" + config.getSiteName());
                    // ssl握手异常
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果请求是幂等的，就再次尝试
                	logger.info("请求是幂等的，再次尝试：" + config.getSiteName());
                    return true;
                }
                return false;
            }

        });
        
        /*
         * 一般说来，HttpClient实际上就是一系列特殊的handler或者说策略接口的实现，这些handler（测试接口）负责着处理Http协议的某一方面，
         * 比如重定向、认证处理、有关连接持久性和keep alive持续时间的决策。这样就允许用户使用自定义的参数来代替默认配置，实现个性化的功能
         * */
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(
                HttpResponse response,
                HttpContext context) {
                    long keepAlive = super.getKeepAliveDuration(response, context);
                    if (keepAlive == -1) {
                        //如果服务器没有设置keep-alive这个参数，我们就把它设置成20秒设置连接的过期时间，
                    	//这样就可以配合closeExpiredConnections()方法解决连接池中失效的连接
                        keepAlive = 20000;
                    }
                    return keepAlive;
            }

        });
        // 自定义gzip压缩 //addInterceptorLast, BasicHeaderElementIterator拦截器
        httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {

            public void process(
                    final HttpRequest request,
                    final HttpContext context) throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
                
            }
        });
        // 自定义重定向
        httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
        
        /*// 默认代理
        DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(new HttpHost("8.8.8.8",8080));
        
        // jre代理
        SystemDefaultRoutePlanner systemDefaultRoutePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        
        // 自定义代理
        HttpRoutePlanner httpRoutePlanner = new HttpRoutePlanner() {
			
			@Override
			public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
				return new HttpRoute(  
                        target,  
                        null,  
                        new HttpHost("8.8.8.8", 8080),  
                        "https".equalsIgnoreCase(target.getSchemeName())  
                        );
			}
		};
		httpClientBuilder.setRoutePlanner(httpRoutePlanner);*/
        //设置缓存没找到怎么写
        
        if (config != null && config.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(config.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }
        return httpClientBuilder.build();
	}
	
}

//定时关闭无用的连接
class IdleConnectionMonitorThread extends Thread{
	protected Logger logger = Logger.getLogger(getClass());
	private final PoolingHttpClientConnectionManager connectionManager;  
	private volatile boolean shutdown = false;  
	public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connectionManager) {  
		super();  
		this.connectionManager = connectionManager;  
	}  
	@Override
	public void run() {
		try {  
			while (!shutdown) {  
				synchronized (this) {  
					wait(10000);  
					// 关闭过期的连接  
					connectionManager.closeExpiredConnections();  
					// 关闭空闲时间超过60秒的连接  
					connectionManager.closeIdleConnections(60, TimeUnit.SECONDS);  
				}  
			}  
		} catch (InterruptedException e) {  
		}  
	}
}
