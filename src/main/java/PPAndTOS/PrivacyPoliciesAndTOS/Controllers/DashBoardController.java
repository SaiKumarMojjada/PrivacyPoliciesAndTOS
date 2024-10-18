package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.WebsiteEntity;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashBoardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("websiteCount", dashboardService.getMonitoredWebsiteCount());
        model.addAttribute("policyChangeCount", dashboardService.getPolicyChangeCount());
        model.addAttribute("pendingAlertsCount", dashboardService.getPendingAlertsCount());

        List<WebsiteEntity> websites = dashboardService.getAllWebsites();
        model.addAttribute("websites", websites);
        return "dashboard";
    }
}
