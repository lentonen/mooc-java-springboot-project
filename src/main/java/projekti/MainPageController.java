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
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class MainPageController {
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    WallMessageRepository wallMessageRepository;
    
    


    @GetMapping("/mainPage")
    public String list(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String url= accountRepository.findByUsername(name).getUrlAddress();
        return "redirect:mainPage/"+url;
    }
    
    
    @GetMapping("/mainPage/{url}")
    public String getOne(Model model, @PathVariable String url) {
        
        // Jos url löytyy joltakin käyttäjältä, palautetaan käyttäjän sivu
        try {
            String name = accountService.getLoggedNickame();
            Long loggedId = accountService.getLoggedId();
            model.addAttribute("message", name);
            model.addAttribute("profilePictureId", accountRepository.findByUrlAddress(url).getProfilePictureId());
            // Ladataan wallMessaget
            model.addAttribute("wallMessages", wallMessageRepository.findByAccountId(loggedId));
            
            return "mainPage";
            
        
        // Jos yrl ei löydy, niin edellinen heittää poikkeuksen. Käsitellään poikkeus siten, että ohjataan käyttäjä takaisin omalle sivulle.
        } catch (NullPointerException e) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String urlException = accountRepository.findByUsername(auth.getName()).getUrlAddress();
            return "redirect:"+urlException;
        }
        
        
    }
    
    
    @PostMapping("/wallMessage")
    public String create(@RequestParam String message) {
        WallMessage msg = new WallMessage();
        msg.setContent(message);
        msg.setAccount(accountService.getLoggedAccount());
        wallMessageRepository.save(msg);

        return "redirect:/mainPage";
    }
}
