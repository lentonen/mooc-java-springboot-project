/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Henri
 */
@Controller
public class FollowPageController {
    
    @Autowired
    FollowersRepository followersRepository;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AccountService accountService; 
    
    
    @GetMapping("/followPage")
    public String show(Model model) {
        // Etsitään sisällä olevan käyttäjän ID ja se, ketä käyttäjä seuraa

        Long userId = accountService.whoIsLogged();
        List<Account> accounts = accountService.isFollowing(userId);

        model.addAttribute("accounts", accounts);
        model.addAttribute("message", accountService.getLoggedNickame());
        
        // Viedään tieto kirjautuneen käyttäjän nicknamesta
        model.addAttribute("loggedNickname", accountService.getLoggedNickame());
        
        return "followPage";
    }
    
    @PostMapping("/followPage/{id}")
     public String follow(Model model, @PathVariable(value = "id") Long id) {
         accountService.stopFollowing(id);
         return "redirect:/followPage";
     }
    
}
