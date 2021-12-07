/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Henri
 */
@Controller
public class OtherUsersPageController {
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AccountService accountService;
    

    @GetMapping("/user/{url}")
    public String showOtherUser(Model model, @PathVariable String url) {
        String nickname = accountService.getLoggedNickame();
        model.addAttribute("message", nickname);
        String otherUserNickname = accountRepository.findByUrlAddress(url).getNickname();
        model.addAttribute("nickname", otherUserNickname);
        return "OtherUsersPage";
    }
 
}
