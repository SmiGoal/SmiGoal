package smigoal.server.service.crawling;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;

@Service
public class WebDriverUtil {

    private static String WEB_DRIVER_PATH;  // webdriver 경로

    public static WebDriver getChromeDriver(){
        if (ObjectUtils.isEmpty(System.getProperty("webdriver.chrome.driver"))){
            System.setProperty("webdriver.chrome.driver", WEB_DRIVER_PATH);
        }

        // webDriver 옵션 설정
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("--lang=ko");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.setCapability("ignoreProtectedModeSettings", true);

        // 배포 환경용 코드
        chromeOptions.setBinary("/opt/chrome/chrome-linux64/chrome");

        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        return driver;
    }

    @Value("${driver_path}")
    public void initDriver(String path){
        WEB_DRIVER_PATH = path;
    }

    public static void quit(WebDriver driver){
        if (!ObjectUtils.isEmpty(driver)){
            driver.quit();
        }
    }
}
