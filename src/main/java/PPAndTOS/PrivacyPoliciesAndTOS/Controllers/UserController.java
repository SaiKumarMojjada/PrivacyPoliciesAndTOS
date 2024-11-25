package PPAndTOS.PrivacyPoliciesAndTOS.Controllers;

import PPAndTOS.PrivacyPoliciesAndTOS.Model.User;
import PPAndTOS.PrivacyPoliciesAndTOS.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/updateUser/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "updateUser";
    }

//    @PostMapping("/updateUser")
//    public String updateUser(@ModelAttribute User user) {
//        userService.updateUser(user);
//        return "redirect:/dashboard";
//    }
}
