package PPAndTOS.PrivacyPoliciesAndTOS.Service;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WebsiteScrappingService {

    @Autowired
    private WebsiteRepository websiteRepository;


    public String scrapeTheContent(WebsiteEntity website, String linkToFind) {
        String url = website.getUrl();
        String linkUrl = findLinkUrl(url, linkToFind);

        if (linkUrl != null) {
            website.setPrivacypolicyURL(linkUrl);
            String content = extractPrivacyPolicyContent(linkUrl);
            String isSaved;
            if (content != null) {
                if(linkToFind == "privacy"){
                    isSaved = updatePPDetails(website, content);
                    log.info("Successfully scraped and saved privacy policy for URL: {}", url);
                    return content;
                }else{
                    isSaved = updateTosDetails(website, content);
                    log.info("Successfully scraped and saved tos for URL: {}", url);
                    return content;
                }

            }
        }

        log.error("Failed to scrape privacy policy for URL: {}", url);
        return null;
    }

    public String updatePPDetails(WebsiteEntity website, String currentPolicy) {
        try{
            String previousPolicy = website.getCurrentPolicy();
            website.setCurrentPolicy(currentPolicy);
            website.setPreviousPolicy(previousPolicy);
            website.setLastChecked(LocalDateTime.now());
            website.setUpdatedAt(LocalDateTime.now());
            websiteRepository.save(website);
            return "saved";
        }catch (Exception e){
            log.error("Error occurred while saving Privacy Policy data for website: {}", website.getWebsiteName(), e);
            return "failed";
        }
    }

    public String updateTosDetails(WebsiteEntity website, String currentPolicy) {
        try {
            String previousPolicy = website.getCurrentTos();
            website.setCurrentTos(currentPolicy);
            website.setPreviousTos(previousPolicy);
            websiteRepository.save(website);
            return "saved";
        } catch (Exception e) {
            log.error("Error occurred while saving Terms of Service data for website: {}", website.getWebsiteName(), e);
            return "failed";
        }
    }

    private String findLinkUrl(String url, String linkToFind) {
        String link = findLinkUrlWithJsoup(url,linkToFind);
        if (link == null) {
            link = findLinkUrlWithSelenium(url, linkToFind);
        }
        return link;
    }

    private String findLinkUrlWithJsoup(String url, String linkToFind) {
        try {
            log.info("Finding Content Link URL using Jsoup for: {}", url);
            Connection.Response initialResponse = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .method(Connection.Method.GET)
                    .execute();

            Document homepage = initialResponse.parse();
            Elements links = homepage.select("footer a:matchesOwn((?i)privacy|privacy policy|privacy notice|privacy statement|data protection)");

            if (links.isEmpty()) {
                List<String> orderedPrivacyTexts;
                if(linkToFind != null && linkToFind == "privacy") {
                    orderedPrivacyTexts = Arrays.asList(
                            "privacy", "privacy policy", "privacy notice", "privacy statement", "data protection"
                    );
                }else{
                    orderedPrivacyTexts = Arrays.asList(
                            "terms of service","terms and conditions","terms", "user agreement",
                            "service agreement","conditions of use","terms of use","legal terms",
                            "legal agreement","acceptable use policy"
                    );
                }


                for (String text : orderedPrivacyTexts) {
                    links = homepage.select(String.format("footer a:containsOwn(%s), div.footer a:containsOwn(%s)", text, text));
                    if (!links.isEmpty()) {
                        return links.first().absUrl("href");
                    }
                }

                for (String text : orderedPrivacyTexts) {
                    links = homepage.select(String.format("a:containsOwn(%s)", text));
                    if (!links.isEmpty()) {
                        return links.first().absUrl("href");
                    }
                }

                // Broader fallback
                Elements broadMatchLinks = homepage.select("a[href~=(?i)\\b(privacy|policy|protection)\\b]");
                if (!broadMatchLinks.isEmpty()) {
                    return broadMatchLinks.first().absUrl("href");
                }
            } else {
                return links.first().absUrl("href");
            }

        } catch (HttpStatusException e) {
            log.error("HTTP error fetching URL. Status={}, URL={}", e.getStatusCode(), url, e);
            if (e.getStatusCode() == 403) {
                return findLinkUrlWithSelenium(url, linkToFind);
            }
        } catch (IOException e) {
            log.error("IOException occurred while fetching URL using Jsoup: {}", url, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching URL using Jsoup: {}", url, e);
        }

        return findLinkUrlWithSelenium(url, linkToFind);
    }


    private String findLinkUrlWithSelenium(String url, String  linkToFind) {
        WebDriver webDriver = setupWebDriver();

        try {
            log.info("Starting Selenium to find privacy policy link for URL: {}", url);
            webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            webDriver.get(url);

            try {
                WebDriverWait alertWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
                alertWait.until(ExpectedConditions.alertIsPresent());
                webDriver.switchTo().alert().accept();
                log.info("Alert found and accepted.");
            } catch (NoAlertPresentException e) {
                log.info("No alert was present, proceeding with Selenium.");
            } catch (TimeoutException e) {
                log.warn("No alert present within the time frame.");
            }

            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20)); // Generous timeout
            List<String> possibleTexts;
            if(linkToFind == "privacy") {
                possibleTexts = Arrays.asList(
                        "Privacy Policy", "Privacy Notice", "Privacy",
                        "Privacy and Cookies", "Data Protection", "Privacy Statement"
                );
            }else{
                possibleTexts = Arrays.asList(
                        "terms of service","terms and conditions","terms", "user agreement",
                        "service agreement","conditions of use","terms of use","legal terms",
                        "legal agreement","acceptable use policy"
                );
            }


            for (String text : possibleTexts) {
                try {
                    WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(text)));
                    ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", link); // Scroll into view
                    wait.until(ExpectedConditions.elementToBeClickable(link));

                    if (link.isDisplayed()) {
                        String privacyLink = link.getAttribute("href");
                        log.info("Found privacy policy link with Selenium: {}", privacyLink);
                        return privacyLink;
                    }
                } catch (TimeoutException ex) {
                    log.warn("Timeout while searching for text '{}': {}", text, ex.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            log.error("Privacy policy link not found on the page using Selenium for URL: {}", url, e);
        } catch (WebDriverException e) {
            log.error("WebDriver exception occurred for URL: {}. Error: {}", url, e);
            e.printStackTrace();
        } finally {
            webDriver.quit();
            log.info("Selenium WebDriver session closed.");
        }
        log.warn("Selenium could not find a privacy policy link for URL: {}", url);
        return null;
    }


    private String extractPrivacyPolicyContent(String privacyUrl) {
        String content = extractContentWithJsoup(privacyUrl);
        if (content != null) {
            log.info("Successfully extracted privacy policy content using Jsoup for: {}", privacyUrl);
            return content;
        }

        content = extractContentWithSelenium(privacyUrl);
        if (content != null) {
            log.info("Successfully extracted privacy policy content using Selenium for: {}", privacyUrl);
        } else {
            log.error("Failed to extract privacy policy content using both Jsoup and Selenium for: {}", privacyUrl);
        }

        return content;
    }

    private String extractContentWithJsoup(String privacyUrl) {
        try {
            log.info("Attempting to extract privacy policy content with Jsoup for: {}", privacyUrl);
            Document document = Jsoup.connect(privacyUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            String[] selectors = {"article", "section", "div#privacy", "div.policy", "main", "div.content"};
            for (String selector : selectors) {
                Elements elements = document.select(selector);
                if (!elements.isEmpty()) {
                    return elements.text();
                }
            }

            return document.body().text();
        } catch (IOException e) {
            log.error("Jsoup failed to extract content from: {}. Error: {}", privacyUrl, e.getMessage());
        }
        return null;
    }

    private String extractContentWithSelenium(String privacyUrl) {
        WebDriver webDriver = setupWebDriver();
        try {
            log.info("Attempting to extract privacy policy content with Selenium for: {}", privacyUrl);
            webDriver.get(privacyUrl);

            new WebDriverWait(webDriver, Duration.ofSeconds(10))
                    .until(webDriver1 -> ((JavascriptExecutor) webDriver1).executeScript("return document.readyState").equals("complete"));

            return webDriver.findElement(By.tagName("body")).getText();
        } catch (WebDriverException e) {
            log.error("Selenium failed to extract content from: {}. Error: {}", privacyUrl, e.getMessage());
        } finally {
            webDriver.quit();
        }
        return null;
    }

    private WebDriver setupWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        return new ChromeDriver(options);
    }

}
