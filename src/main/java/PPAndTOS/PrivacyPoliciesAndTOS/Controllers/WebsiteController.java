package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.User;
import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.UserRepository;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
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

    @GetMapping("/addWebsite")
    public String addNewWebsite() {
        return "addNewWebsite";
    }

    @PostMapping("/addNewWebsite")
    public String addWebsite(@ModelAttribute WebsiteEntity website, HttpSession session) {
        // Fetch the logged-in user
        User sessionUser = (User) session.getAttribute("user");
        website.setUser(sessionUser);
        website.setCreatedAt(LocalDateTime.now());
        website.setUpdatedAt(LocalDateTime.now());
        website.setPolicyChanged(false);
        website.setMonitoringEnabled(true);
        website.setAlertStatus("Pending");
        website.setAlertCount(0);

        websiteScrappingService.scrapePrivacyPolicy(website);
        // Save the website entity to the database
        websiteRepository.save(website);

        // Redirect to the dashboard
        return "redirect:/dashboard";
    }
}

