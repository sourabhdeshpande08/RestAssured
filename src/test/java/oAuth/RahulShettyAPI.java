package oAuth;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.*;

public class RahulShettyAPI {

	String code;
	String accessToken;

	@Test
	public void getCode() throws InterruptedException {

		//This method will get the auth code using Google authorization server.
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--user-data-dir=C:/temp/automation_profile");
		options.addArguments("--remote-allow-origins=*");
		options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
		options.addArguments("--disable-blink-features=AutomationControlled");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();

		driver.get(
				"https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/userinfo.email&auth_url=https://accounts.google.com/o/oauth2/v2/auth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php");

		Thread.sleep(2000);
		String url = driver.getCurrentUrl();
		System.out.println(url);
		code = url.split("code=")[1].split("&scope")[0];

		driver.close();

	}

	@Test(dependsOnMethods = "getCode")
	public void getAccessToken() {

		
		String response = given().log().all().urlEncodingEnabled(false).baseUri("https://www.googleapis.com")
				.queryParam("code", code)
				.queryParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
				.queryParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
				.queryParam("redirect_uri", "https://rahulshettyacademy.com/getCourse.php")
				.queryParam("grant_type", "authorization_code").when().post("/oauth2/v4/token").then().log().all()
				.extract().response().asPrettyString();
		
		JsonPath js = new JsonPath(response);
		accessToken = js.getString("access_token");

	}
	
	@Test(dependsOnMethods="getAccessToken")
	public void getInfo() {
		
		
		RequestSpecification getInfoBaseReq = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com").addQueryParam("access_token", accessToken).build();
		String info = given().log().all().spec(getInfoBaseReq).when().get("/getCourse.php").then().log().all().extract().response().asPrettyString();
		
		
	}

}
