package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
