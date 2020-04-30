//This screen shot & Exception capture code in Extent report is working fine
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class FreeCRMTest {

	public WebDriver driver;
	public ExtentReports extent;
	public ExtentTest extenttest;

	@BeforeMethod
	public void setup() {

		System.setProperty("webdriver.chrome.driver", "D:\\ChromeDriver\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://classic.freecrm.com/");

	}

	@BeforeTest
	public void setExtent() {

		extent = new ExtentReports(System.getProperty("user.dir") + "/test-output/ExtentReport.html", true);
		extent.addSystemInfo("HostName", "Yogesh Window");
		extent.addSystemInfo("UserName", "Yogesh Thakur");
		extent.addSystemInfo("Enviornment", "QA staging");
	}

	@AfterTest
	public void endReport() {
		extent.flush();
		extent.close();

	}

	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {
		String dateName = new SimpleDateFormat("yyyymmddhhmmss").format(new Date());

		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		// after execution, you could see a folder "FailedTestsScreenshots"
		// under src folder
		String destination = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + screenshotName + dateName
				+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;

	}

	@Test
	public void freeCRMtitleTest() {
		extenttest = extent.startTest("freeCRMtitleTest");
		String title = driver.getTitle();
		System.out.println(title);
		Assert.assertEquals(title, "CRMPRO - CRM software for customer relationship management, sales, and  support.");
	
	}
	
	//Deliberately failing below case to capture the screen shot and exception
	@Test
	public void freeCRMLoginTest() {
		extenttest = extent.startTest("freeCRMLoginTest");
		driver.findElement(By.xpath("abc")).click();
	}
	
	
//	@Test
//	public void freeCRMLogoTest() {
//		boolean b = driver.findElement(By.xpath("//img[@class='1456789==23img-responsive123']")).isDisplayed();
//		Assert.assertTrue(b);
//	}

	@AfterMethod
	// To capture failed test cases screen shot
	public void tearDown(ITestResult result) throws IOException {

		if (result.getStatus() == ITestResult.FAILURE) {
			extenttest.log(LogStatus.FAIL, "TEST CASE FAILED IS " + result.getName()); // To add name in extent report
			extenttest.log(LogStatus.FAIL, "TEST CASE FAILED IS " + result.getThrowable()); // To add failed test cases error and exception both in extent report

			String screenshotPath = FreeCRMTest.getScreenshot(driver, result.getName());
			extenttest.log(LogStatus.FAIL, extenttest.addScreenCapture(screenshotPath)); // To add screenshot in extent report
			
		} else if (result.getStatus() == ITestResult.SKIP) {
			extenttest.log(LogStatus.SKIP, "TEST CASE SKIP IS " + result.getName()); // For Skip Test cases

		} else if (result.getStatus()==ITestResult.SUCCESS) {
			extenttest.log(LogStatus.PASS, "TEST CASE PASS IS " + result.getName());
		}
		
		
		extent.endTest(extenttest); // ending test and ends the current test and prepare the HTML report

		driver.quit();
	}
}
