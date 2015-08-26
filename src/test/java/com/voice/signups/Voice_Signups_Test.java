package com.voice.signups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


import utilities.*;


@Listeners(MyEmailableReporterListener.class)
public class Voice_Signups_Test {	
	WebDriver wd;
	Properties envconf,uiconf;
	String voice_url;
	String ss_dir=System.getProperty("user.dir")+"/src/Execution Reports/Screenshots/";
	String ss_name;
	Wait<WebDriver> wait;
	WebElement we;
	ExcelManipulator xlrdr;
	Logger logger;
	File log4jfile;
	Map<String,String> inputDataFromExcel;
	Map<String,String> outputData=new HashMap<String,String>(); 

	public void readProps() throws FileNotFoundException, IOException {
		envconf=new Properties();
		envconf.load(new FileInputStream(System.getProperty("user.dir")
				+ "/src/test/resources/Config File/conf.properties"));
		uiconf=new Properties();
		uiconf.load(new FileInputStream(System.getProperty("user.dir")
				+ "/src/test/resources/UI Map/UI_Locators.properties"));
	}

	public void configureLog4j()  {
		logger=Logger.getLogger(Voice_Signups_Test.class.getName());
	}

	@BeforeTest
	public void setUp() throws Exception{
		readProps();
		configureLog4j();
		inputDataFromExcel=new ExcelManipulator().getDataFromExcel();
		voice_url="http://voice."+inputDataFromExcel.get("env")+".netzero.net";	
		initBrowserObject(envconf.getProperty("browser"));
		wd.manage().window().maximize();
		wd.get(voice_url);
		wd.manage().timeouts().implicitlyWait(60,TimeUnit.SECONDS);
		wait = new FluentWait<WebDriver>( wd )
				.withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS)
				.ignoring( StaleElementReferenceException.class);	
		logger.info("Browser Opened Voice Site");
		logger.warn("Warning");
		logger.error("Error");
		logger.debug("Debug::: Opened Voice Site");
	}


	public void initBrowserObject(String br) throws Exception {	
		
		switch(br){
		case "ff": case "firefox":
			wd=new FirefoxDriver(new ProfilesIni().getProfile("selenium"));
			break;
		case "gc": case "chrome":
			System.getProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"/src/chromedriver.exe");
			wd=new ChromeDriver();
			break;
		case "ie": case "internet explorer":
			System.getProperty("webdriver.ie.driver",System.getProperty("user.dir")+"/src/IEDriverServer.exe");
			wd=new InternetExplorerDriver();
			break;
		}
	}

	@Test
	public void Signup() throws Exception {		
		System.out.println("********Test Started********");
		doSignup(inputDataFromExcel.get("dname"),inputDataFromExcel.get("pname"));
	}

	public void doSignup(String dname, String pname) throws Exception {	
		wd.findElement(By.xpath(getDeviceXpath(dname))).click();
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(getPlanXpath(pname)))).click();
			wd.findElement(By.xpath(getPlanXpath(pname))).click();

		}
		catch ( StaleElementReferenceException e ) {      
			Reporter.log("Stale element found, trying again... : \n" + e.getMessage() + "\n");
			wd.findElement(By.xpath(getPlanXpath(pname))).click();
		}
		catch (UnhandledAlertException e){
			wd.switchTo().alert().accept();
			wd.findElement(By.xpath(getPlanXpath(pname))).click();
		}
		
		wd.findElement(By.xpath(uiconf.getProperty("cart_page_chkout"))).click();
		wd.findElement(By.xpath(uiconf.getProperty("assignMe"))).click();
		submitShippingInfo();
		submitPaymentInfo();
		submitReviewOrderPage();
		Thread.sleep(5000);
		takeScreenshot();
		writeMemberInfoToExcel();
		Thread.sleep(5000);		
		
	}

	public void submitReviewOrderPage() {			
		wd.findElement(By.xpath(uiconf.getProperty("checkout_button"))).click();
	}

	public void writeMemberInfoToExcel() throws Exception {
		outputData.put("mid",wd.findElement(By.xpath(uiconf.getProperty("mid"))).getText());
		outputData.put("pwd",wd.findElement(By.xpath(uiconf.getProperty("pwd"))).getText());
		outputData.put("mdn",wd.findElement(By.xpath(uiconf.getProperty("mdn"))).getText());
		outputData.put("date",new SimpleDateFormat("dd-mm-yyyy").format(new Date()));
		new ExcelManipulator().setDataIntoExcel(outputData);
	}

	public void submitPaymentInfo() {
		if(inputDataFromExcel.get("paytype").equalsIgnoreCase("pp")){
			submitPPInfo();
		}
		else if(inputDataFromExcel.get("paytype").equalsIgnoreCase("cc")){
			submitCCInfo();
		}
	}

	public void submitCCInfo() {
		wd.findElement(By.xpath(uiconf.getProperty("ccnum"))).sendKeys("5412345678901190");
		wd.findElement(By.xpath(uiconf.getProperty("cvc"))).sendKeys("1234");
		//wd.findElement(By.xpath(uiconf.getProperty("ccname"))).sendKeys("UOL");
		wd.findElement(By.xpath(uiconf.getProperty("ccexpiry"))).sendKeys("12/20");
		wd.findElement(By.xpath(uiconf.getProperty("addresscheck"))).click();
		wd.findElement(By.xpath(uiconf.getProperty("cctermsaccept"))).click();
		wd.findElement(By.xpath(uiconf.getProperty("ccdetailssave"))).click();
	}
	public void submitPPInfo() {
		wd.findElement(By.xpath(uiconf.getProperty("pp_rb"))).click();
		wd.findElement(By.xpath(uiconf.getProperty("pp_terms"))).click();
		//wd.findElement(By.xpath(uiconf.getProperty("ccname"))).sendKeys("UOL");
		wd.findElement(By.xpath(uiconf.getProperty("pp_checkout"))).click();
		wd.findElement(By.xpath(uiconf.getProperty("pp_site_login_link"))).click();

		if(inputDataFromExcel.get("env").equalsIgnoreCase("prod")){
			wd.findElement(By.xpath(uiconf.getProperty("pp_site_login_email"))).
			sendKeys(envconf.getProperty("pp_prod_uname"));
			wd.findElement(By.xpath(uiconf.getProperty("pp_site_pwd"))).
			sendKeys(envconf.getProperty("pp_prod_pwd"));
		}
		else {
			wd.findElement(By.xpath(uiconf.getProperty("pp_site_login_email"))).
			sendKeys(envconf.getProperty("pp_sandbox_uname"));
			wd.findElement(By.xpath(uiconf.getProperty("pp_site_pwd"))).
			sendKeys(envconf.getProperty("pp_sandbox_pwd"));
		}
		wd.findElement(By.xpath(uiconf.getProperty("pp_site_login_button"))).click();
		wd.findElement(By.xpath(uiconf.getProperty("pp_site_continue_button"))).click();
	}


	public void submitShippingInfo () {
		wd.findElement(By.xpath(uiconf.getProperty("fname"))).sendKeys("jfaux");
		wd.findElement(By.xpath(uiconf.getProperty("lname"))).sendKeys("webqa");
		wd.findElement(By.xpath(uiconf.getProperty("street"))).sendKeys("6655 Reseda");
		wd.findElement(By.xpath(uiconf.getProperty("city"))).sendKeys("Reseda");
		wd.findElement(By.xpath(uiconf.getProperty("state"))).sendKeys("California");
		wd.findElement(By.xpath(uiconf.getProperty("zip"))).sendKeys("91356");
		wd.findElement(By.xpath(uiconf.getProperty("email"))).sendKeys("snakka@corp.untd.com");
		wd.findElement(By.xpath(uiconf.getProperty("shipping_continue"))).click();
	}

	public String getDeviceXpath(String dname){
		String dxpath;
		switch(dname){
		case "fury": dxpath=uiconf.getProperty("fury_xpath");break;
		case "warp": dxpath=uiconf.getProperty("warp_xpath");break;
		case "ip4s": dxpath=uiconf.getProperty("ip4s_xpath");break;
		case "sgs3": dxpath=uiconf.getProperty("sgs3_xpath");break;
		default: dxpath="";
		}		
		return dxpath;
	}

	public String getPlanXpath(String pname){
		String pxpath=""; 
		switch(pname){
		case "basic": pxpath=uiconf.getProperty("basic_xpath");break;
		case "pro": pxpath=uiconf.getProperty("pro_xpath");break;
		case "plat": pxpath=uiconf.getProperty("plat_xpath");break;
		case "talktext": pxpath=uiconf.getProperty("talktext_xpath");break;
		default: pxpath="";	
		}
		return pxpath;
	}	

	@AfterTest
	public void tearDown() throws Exception{
		takeScreenshot();
		wd.quit();
		//SendEmail.sendEmail();
		Thread.sleep(2000);	
	}

	public void takeScreenshot() throws Exception {
		File ss=((TakesScreenshot)wd).getScreenshotAs(OutputType.FILE);
		ss_name=ss_dir+getTimeStamp("ddMMyyyy_hhmmss")+".png";
		FileUtils.copyFile(ss, new File(ss_name));
		logger.info("Screenshot "+ss_name+" taken");
	}

	public String getTimeStamp(String str) {
		return  new SimpleDateFormat(str).format(new Date());
	}	

	
	public void takeScreenshotOnFailure(ITestResult testResult) throws Exception {
		if(testResult.getStatus()==ITestResult.FAILURE){
			System.out.println(testResult.getStatus());
		takeScreenshot();
		logger.info("Screenshot taken on Test Failure");
		}
	}	
	MyEmailableReporterListener merl;
	
	public void sendReport(){
	merl=new MyEmailableReporterListener();
	TestNG testng=new TestNG();
	testng.addListener(merl);
	testng.run();
	}
}
