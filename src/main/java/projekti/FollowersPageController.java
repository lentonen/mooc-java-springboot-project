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
public class FollowersPageController {
    
    @Autowired
    FollowersRepository followersRepository;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AccountService accountService; 
    
    
    @GetMapping("/followersPage")
    public String show(Model model) {
        
// Etsitään sisällä olevan käyttäjän ID ja se, ketä käyttäjä seuraa
        String nickname = accountService.getLoggedNickame();
        List<Followers> followers = accountRepository.findByNickname(nickname).getFollowers();


        model.addAttribute("followers", followers);
        model.addAttribute("message", accountService.getLoggedNickame());
        return "followersPage";
    }
    
    @PostMapping("/followersPage/{id}/prevent")
    public String preventFollow(Model model, @PathVariable(value = "id") Long id) {
        accountService.preventFollow(id);
        return "redirect:/followersPage";
    }
     
    @PostMapping("/followersPage/{id}/removePrevent")
    public String removePrevent(Model model, @PathVariable(value = "id") Long id) {
        accountService.removePrevent(id);
        return "redirect:/followersPage";
    } 
}
