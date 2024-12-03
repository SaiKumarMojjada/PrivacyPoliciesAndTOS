package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.User;
import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.UserRepository;
import PPAndTOS.PrivacyPoliciesAndTOS.Repository.WebsiteRepository;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.EmailService;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.WebsiteScrappingService;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.WebsiteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private WebsiteService websiteService;;

    @GetMapping("/addWebsite")
    public String addNewWebsite() {
        return "addNewWebsite";
    }

    @GetMapping("/public/websites")
    public String getWebsiteLinksPage(Model model) {
        List<WebsiteEntity> websites = websiteService.getAllWebsites();
        websites.forEach(website -> {
            System.out.println("Website ID: " + website.getId());
            System.out.println("Website URL: " + website.getUrl());
            System.out.println("Current Policy: " + website.getCurrentPolicy());
            System.out.println("Current ToS: " + website.getCurrentTos());
        });
        model.addAttribute("websites", websites);
        return "monitorWebsites";
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

    @GetMapping("/websites/editWebsite/{id}")
    public String editWebsite(@PathVariable Long id, Model model) {
        WebsiteEntity website = websiteService.findById(id);
        model.addAttribute("website", website);
        return "editWebsite";
    }

    // Handle website update
    @PostMapping("/websites/update/{id}")
    public String updateWebsite(@PathVariable Long id, @ModelAttribute("website") WebsiteEntity website) {
        websiteService.updateWebsite(id, website);
        return "redirect:/dashboard";
    }

    // Handle website deletion
    @GetMapping("/websites/deleteWebsite/{id}")
    public String deleteWebsite(@PathVariable Long id) {
        websiteService.deleteById(id);
        return "redirect:/dashboard";
    }
}

