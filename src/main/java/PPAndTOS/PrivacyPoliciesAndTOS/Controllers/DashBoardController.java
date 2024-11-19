package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.User;
import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.DashboardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashBoardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Retrieve the user from the principal (assuming it is already loaded)
                Object principal = authentication.getPrincipal();
                if (principal instanceof User) {
                    sessionUser = (User) principal;
                    session.setAttribute("user", sessionUser);
                }
            }
            if (sessionUser == null) {
                System.out.println("User not found in session or authentication. Redirecting to login.");
                return "redirect:/login";
            }
        }
        Long userId = sessionUser.getId();
//        List<WebsiteEntity> userWebsites = dashboardService.loginDashboard(userId);
//        model.addAttribute("listOfValues",userWebsites);
        model.addAttribute("user",sessionUser);
        model.addAttribute("websiteCount", dashboardService.getMonitoredWebsiteCount());
        model.addAttribute("policyChangeCount", dashboardService.getPolicyChangeCount());
        model.addAttribute("pendingAlertsCount", dashboardService.getPendingAlertsCount());

        List<WebsiteEntity> websites = dashboardService.getAllWebsites();
        model.addAttribute("websites", websites);
        return "dashboard";
    }

    @GetMapping("/dashboard/{websiteId}")
    public String showWebsiteDashboard(@PathVariable("websiteId") Long websiteId, Model model) {
        WebsiteEntity website = dashboardService.getWebsiteById(websiteId);
        model.addAttribute("website", website);
        return "websiteDashboard";
    }


}
