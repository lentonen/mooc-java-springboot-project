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
    
    @GetMapping("/search")
    public String searchView(Model model) {
        return "search";
    }
    
    
    
}
