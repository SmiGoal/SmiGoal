package smigoal.server.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import smigoal.server.service.crawling.WebDriverUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlingService {

    private WebDriver webDriver;

    public String getURLContent(String url) {
        webDriver = WebDriverUtil.getChromeDriver();
        WebElement webElements=null;
        String query = "body";

        if (!ObjectUtils.isEmpty(webDriver)){
            webDriver.get(url);
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            webElements = webDriver.findElement(By.cssSelector(query));
        }

        return webElements.getText();
    }
}
