package PPAndTOS.PrivacyPoliciesAndTOS.Service;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openqa.selenium.TimeoutException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class WebsiteScrappingService {

    @Autowired
    private WebsiteRepository websiteRepository;


    public String scrapePrivacyPolicy(WebsiteEntity website) {
        String url = website.getUrl();
        String privacyUrl = findPrivacyPolicyUrlWithJsoup(url);

        if (privacyUrl == null) {
            privacyUrl = findPrivacyPolicyUrlWithSelenium(url); // Fallback to Selenium
        }

        if (privacyUrl != null) {
            website.setPrivacypolicyURL(privacyUrl);
            String content = extractPrivacyPolicyContent(privacyUrl);
            if (content != null) {
                updatePrivacyPolicyDetails(website, content);
                log.info("Successfully scraped and saved privacy policy for URL: {}", url);
                return content;
            }
        }

        log.error("Failed to scrape privacy policy for URL: {}", url);
        return null;
    }

    public void updatePrivacyPolicyDetails(WebsiteEntity website, String currentPolicy) {
        // Check if a previous policy exists
        String previousPolicy = website.getCurrentPolicy();

        // Update WebsiteEntity with the new policy details
        website.setCurrentPolicy(currentPolicy);
        website.setPreviousPolicy(previousPolicy);
//        website.setUpdated(previousPolicy == null || !currentPolicy.equals(previousPolicy));
        website.setLastChecked(LocalDateTime.now());
        website.setUpdatedAt(LocalDateTime.now());

        // Save the updated WebsiteEntity
        websiteRepository.save(website);
    }

    private String findPrivacyPolicyUrlWithJsoup(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            // Select potential privacy policy links
            Elements links = doc.select("a:matchesOwn((?i)privacy|data protection|terms)");
            if (!links.isEmpty()) {
                return links.first().absUrl("href");
            }
        } catch (IOException e) {
            log.error("Jsoup failed to fetch URL: {}. Error: {}", url, e.getMessage());
        }
        return null;
    }

    private String extractPrivacyPolicyContent(String privacyUrl) {
        try {
            Document doc = Jsoup.connect(privacyUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            return doc.body().text();
        } catch (IOException e) {
            log.error("Failed to fetch privacy policy content from: {}. Error: {}", privacyUrl, e.getMessage());
        }
        return null;
    }

    private String findPrivacyPolicyUrlWithSelenium(String url) {
        WebDriver driver = setupWebDriver();
        try {
            driver.get(url);
            List<String> possibleTexts = Arrays.asList("Privacy Policy", "Privacy", "Data Protection");

            for (String text : possibleTexts) {
                try {
                    WebElement link = new WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(text)));
                    return link.getAttribute("href");
                } catch (TimeoutException e) {
                    log.warn("Text '{}' not found using Selenium for URL: {}", text, url);
                }
            }
        } finally {
            driver.quit();
        }
        return null;
    }

    private WebDriver setupWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
        return new ChromeDriver(options);
    }
}
