package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(){
        return "loginPage";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        // Dummy authentication logic (replace with real validation)
        if ("user@example.com".equals(email) && "password123".equals(password)) {
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "signUp";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/signUp";
        }

        // Example logic to register the user (you can replace this with actual database operations)
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
        return "redirect:/login";
    }
}
