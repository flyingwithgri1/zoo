package com.spider.download;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.spider.bean.Page;
import com.spider.bean.Request;
import com.spider.bean.SiteConfig;
import com.spider.bean.SiteConfigManager;
import com.spider.utils.SpiderConfig;

public class PhantomJSDownloader implements Downloader {
	// private ConcurrentLinkedQueue<PhantomJSDriver> queue = new
	// ConcurrentLinkedQueue<>();

	//登录调用层级
	int stackLevel = 0;
	private Logger logger = Logger.getLogger(getClass());

	private Map<String, WebDriver> drivers = new HashMap<>();

	public WebDriver loginWeixin(WebDriver driver,String currentUrl,int stackLevel,SiteConfig config){
		logger.info("登录堆栈调用深度：" + stackLevel);
		if(stackLevel > 5){
			//重新创建driver
			logger.info("登录堆栈调用深度超过5级，重新创建driver");
			driver = genterDriver(config);
		}
		if(stackLevel>10){
			logger.error("登录堆栈调用深度超过10级，程序退出");
		}
		driver.get("https://account.wxb.com/");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(By.cssSelector("#email")).sendKeys("17888811680");
					driver.findElement(By.cssSelector("#password")).sendKeys("flying123456");
					driver.findElement(By.xpath("//input[@type='checkbox']")).click();
					logger.info(driver.findElement(By.xpath("//input[@type='checkbox']")).isSelected());
					driver.findElement(By.cssSelector("#submit")).submit();
					return true;
				} catch (NoSuchElementException e) {
					return false;
				}
			}
		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		File scrFile = ((PhantomJSDriver) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File("login.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = driver.getPageSource();
		if(str.contains("请输入正确的验证码")){
			try {
				logger.error("微信需要输入验证码，休眠60分钟");
				Thread.sleep(60*60*1000);
				logger.warn("重新开始登录");
				return loginWeixin(driver, currentUrl,stackLevel++,config);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		wait = new WebDriverWait(driver, 60);
		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(By.cssSelector("#root"));
					logger.warn("微信登录成功");
					return true;
				} catch (NoSuchElementException e) {
					return false;
				}
			}
		});
		driver.get(currentUrl);
		return driver;
	}

	private void closeDriver(WebDriver driver) {
		try {
			if (driver != null) {
				driver.close();
				driver.quit();
				driver = null;
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WebDriver genterDriver(SiteConfig config) {
		DesiredCapabilities dcaps = DesiredCapabilities.phantomjs();
		// ssl证书支持
		dcaps.setCapability("acceptSslCerts", true);
		// 截屏支持
		dcaps.setCapability("takesScreenshot", true);
		
		// css搜索支持
		dcaps.setCapability("cssSelectorsEnabled", true);
		// 禁止加载图片
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "loadImages", false);
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent",
				config.getUserAgent() == null
						? "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36"
						: config.getUserAgent());
		// dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX+"userAgent",
		// "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like
		// Gecko) Chrome/54.0.2840.99 Safari/537.36");
		// js支持
		dcaps.setJavascriptEnabled(true);
		// 驱动支持
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, SpiderConfig.phantomjsPath);

		// dcaps.setBrowserName("");
		// dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
		// "--com=testtttttttttt");

		dcaps.setCapability("takesScreenshot", false);
//		dcaps.setCapability(BROWSER_NAME, "chrome");
		dcaps.setBrowserName("chrome");
		WebDriver driver = new PhantomJSDriver(dcaps);
		logger.info(dcaps.getBrowserName());
		return driver;
	}

	@Override
	public Page download(Request request) {
		SiteConfig config = SiteConfigManager.getSiteConfig(request.getSiteName());
		WebDriver driver = null;
		try {
			logger.info("download url:" + request.getUrl());
			if (request.getSiteName().startsWith("微信-")) {
				driver = drivers.get("微信");
				if(driver == null){
					logger.error("微信驱动为空，需要登录");
					driver = genterDriver(config);
				}
				driver.get(request.getUrl());
				Thread.sleep(3000);
				//判断是否需要登录
				WebDriverWait wait = new WebDriverWait(driver, 60);
				AtomicInteger i = new AtomicInteger(0);
				Boolean flag = wait.until(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver driver) {
						try {
							driver.findElement(By.cssSelector("div.not-login"));
							return true;
						} catch (NoSuchElementException e) {
							if(i.get() == 2){//尝试2次，如果2次没有找到登录按钮，即为登录成功
								return true;
							}
							logger.warn("登录尝试："  + i.getAndIncrement());
							return false;
						}
					}
				});
				logger.info("是否需要登录：" + flag);
				if(flag && i.get() != 2){
					driver = loginWeixin(driver,request.getUrl(),0,config);
				}
				drivers.put("微信", driver);
			} else {
				driver = genterDriver(config);
				driver.get(request.getUrl());
			}
			Thread.sleep(1000);
			File scrFile = ((PhantomJSDriver) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File("home.png"));
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					try {
						JavascriptExecutor je = (JavascriptExecutor) driver;
						String temp = "0";
						String lastScrolle = "";
						while (true) {
							// scrollTo(x,y)
							temp = je.executeScript("scrollTo(0," + Integer.parseInt(temp) + 3000
									+ "); return document.body.clientHeight;").toString();
							if (lastScrolle.equals(temp)) {
								break;
							}
							lastScrolle = temp;
							Thread.sleep(200);
						}
						return true;
					} catch (NoSuchElementException e) {
						return false;
					} catch (InterruptedException e) {
						return false;
					}
				}
			});
			Page page = new Page();
			page.setRequest(request);
			page.setContent(driver.getPageSource());
			page.setStatusCode(200);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			return addToCycleRetry(request, config);
		} finally {
			if(!config.getSiteName().startsWith("微信-")){
				closeDriver(driver);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
