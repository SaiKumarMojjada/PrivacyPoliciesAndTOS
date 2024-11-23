package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.User;
import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.UserRepository;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.EmailService;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.WebsiteScrappingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class WebsiteController {

    @Autowired
    private WebsiteScrappingService websiteScrappingService;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/addWebsite")
    public String addNewWebsite() {
        return "addNewWebsite";
    }

    @PostMapping("/addNewWebsite")
    public String addWebsite(@ModelAttribute WebsiteEntity website, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        website.setUser(sessionUser);
        website.setCreatedAt(LocalDateTime.now());
        website.setUpdatedAt(LocalDateTime.now());
        website.setPolicyChanged(false);
        website.setMonitoringEnabled(true);
        website.setAlertStatus("Pending");
        website.setAlertCount(0);

        websiteScrappingService.scrapeTheContent(website, "privacy");
        websiteScrappingService.scrapeTheContent(website, "tos");
        websiteRepository.save(website);

        String subject = "New Website Added: " + website.getWebsiteName();
        String body = "Hello,\n\nYou have successfully added a new website to your monitoring list:\n"
                + "Website Name: " + website.getWebsiteName() + "\n"
                + "Website URL: " + website.getUrl() + "\n\n"
                + "Thank you for using our service.\n\nBest regards,\nPrivacy Policies and TOS Team";

        emailService.sendEmail(sessionUser.getUserEmail(), subject, body);

        return "redirect:/dashboard";
    }
}

