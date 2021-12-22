/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Henri
 */
@Controller
public class SearchPageController {
 
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AccountService accountService;
    

    
    @PostMapping("/mainPage/search")
    public String search(Model model, @RequestParam String name) {
        model.addAttribute("accounts", accountService.findAccounts(name));
        
        // Viedään tieto kirjautuneen käyttäjän nicknamesta
        model.addAttribute("loggedNickname", accountService.getLoggedNickame());
        
        return "search";
    }
   
    
     @PostMapping("/search/follow/{realname}")
     public String follow(Model model, @PathVariable(value = "nickname") String nickname) {
         accountService.startFollow(nickname);
         return "search";
     }

}
