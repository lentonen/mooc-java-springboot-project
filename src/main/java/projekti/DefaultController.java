package projekti;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
//muutetaan jotakin
    @GetMapping("*")
    public String helloWorld(Model model) {
        return "redirect:/mainPage";
    }
}
