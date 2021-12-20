/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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
    MessageRepository messageRepository;
    
    @Autowired
    MessageService messageService;
    
    @Autowired
    MessageLikeRepository messageLikeRepository;
    

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
            String name = accountService.getNickname(url);
            Long userId = accountService.getId(url);
            Long loggedUserId = accountService.getLoggedId();
            
            // Viedään tieto, minkä nimisen käyttäjän sivulla ollaan
            model.addAttribute("nickname", name);
            
            // Sivun omistajan profiilikuvan ID.
            model.addAttribute("profilePictureId", accountRepository.findByUrlAddress(url).getProfilePictureId());  
            
            // Tieto onko kyseessä oma sivu
            model.addAttribute("ownPageBoolean", accountService.ownPage(url));
            
            // Tieto kenen käyttäjän sivulla ollaan
            model.addAttribute("user", accountRepository.findByUrlAddress(url));
            
            // Viedään tieto seuraako käyttäjä sivun omistajaa
            boolean follows = accountService.follows(accountService.getLoggedId(), accountRepository.findByUrlAddress(url).getId());
            model.addAttribute("followBoolean", follows);
            
            // Haetaan käyttäjän seuraamat tilit ja lisätään oma tili listaan seinäkommenttien hakemista varten. 
            List<Long> follow = accountService.isFollowingId(userId);
            follow.add(userId);
            // Poistetaan seurattujen listasta ne ID:t, jotka ovat estäneet seuraamisen
            accountService.deleteUsersWhoPrevented(userId, follow);
            
            // Tarkastellaan ketä kirjautunut käyttäjä seuraa. Viedään tieto kommentointia varten
            List<Long> loggedUserFollows = accountService.isFollowingId(accountService.getLoggedId());
            loggedUserFollows.add(loggedUserId);
            model.addAttribute("followList", loggedUserFollows);
            
            // Ladataan wallMessaget
            model.addAttribute("wallMessages", messageRepository.findTop25ByAccountIdInAndEntityIdOrderByMessageDateDescMessageTimeDesc(follow, null));
            List<Message> msg = messageService.findAllCommentsByUser(userId);
            model.addAttribute("comments", messageService.findAllCommentsByUser(userId));
            
            // Viedään sisään kirjautuneen käyttäjän profiilikuvan ID
            model.addAttribute("loggedUserPicId", accountService.getLoggedAccount().getProfilePictureId());
            
            // Viedään sisään kirjautuneen käyttäjän ID
            model.addAttribute("loggedUserId", loggedUserId);
   
            return "mainPage";
            
        
        // Jos yrl ei löydy, niin edellinen heittää poikkeuksen. Käsitellään poikkeus siten, että ohjataan käyttäjä takaisin omalle sivulle.
        } catch (NullPointerException e) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String urlException = accountRepository.findByUsername(auth.getName()).getUrlAddress();
            return "redirect:"+urlException;
        }
        
        
    }
    
    
    @PostMapping("/wallMessage")
    public String createWallMessage(@RequestParam String message) {
        messageService.createWallMessage(message, accountService.getLoggedAccount());
        return "redirect:/mainPage";
    }
    
    
    @PostMapping("/messageComment/{id}")
    public String createComment(@RequestParam String comment, @PathVariable Long id) {
        messageService.createWallMessageComment(comment, accountService.getLoggedAccount(), id);
        return "redirect:/mainPage";
    }
    
    
    @PostMapping("/mainPage/{url}/like/{userId}/wallMessage/{id}")
    public String like (@PathVariable String url, @PathVariable Long userId, @PathVariable Long id, HttpServletRequest httpServletRequest) {
        if (messageLikeRepository.existsByAccountIdAndMessageId(userId, id))
            return "redirect:/mainPage/"+url;
        messageLikeRepository.save(new MessageLike(accountService.getLoggedAccount(), messageRepository.getById(id)));
        return "redirect:/mainPage/"+url;
    }
}
