/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Henri
 */
@Service
public class AccountService {
    
    @Autowired
    AccountRepository accountRepository;
    
    
    /**
     * Haetaan hakusanaa vastaavat käyttäjät listaan. Listassa ei näytetä kirjautunutta käyttäjää.
     * @param searchString hakusana
     * @return lista joka sisältää hakusanalla alkavat käyttäjät
     */
    public List<Account> findAccounts(String searchString) {
        // Haetaan hakusanaa vastaava lista
        List<Account> lista = accountRepository.findByRealnameStartingWith(searchString);
        
        // Poistetaan kirjautunut käyttäjä listasta
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        lista.remove(accountRepository.findByUsername(name));
        
        return lista;
    }
    
    
    
}
