package PPAndTOS.PrivacyPoliciesAndTOS.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebsiteScrappingService {


   /* public String scrapePrivacyPolicy(PrivacyOfWeb privacyOfWeb, String url, boolean store) {
        String privacyUrl = findPrivacyLink(url);
        if (privacyUrl != null) {
            // Scrape the content from the found privacy policy URL
            privacyOfWeb.setWebsitePrivacyLink(privacyUrl);
            String privacy =  scrapePrivacyPolicyContent(privacyOfWeb, privacyUrl, store);

            return privacy;

        } else {
            log.error("No privacy policy link found for URL: {}", url);
            return null;
        }
    }*/

}
