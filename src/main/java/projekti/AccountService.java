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
    
    @Autowired
    FollowersRepository followersRepository;
    
    
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
    
    
    /**
     * Aloittaa seuraamaan käyttäjää
     * @param realname minkä nimistä käyttäjää aletaan seurata
     */
    public void startFollow(String realname) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        
        followersRepository.save(new Followers(accountRepository.findByUsername(name), accountRepository.findByRealname(realname)));
        // TODO: ei voi seurata jo seurattua käyttäjää
        

        //List<Followers> following = accountRepository.findByUsername(name).getFollowing();
        //Account newFollowed = accountRepository.findByUsername(realname);
        //followed.add(newFollowed);
        //accountRepository.findByUsername(name).setFollow(followed);  
    }
    
    
    public String getUrl(String realname) {
        return accountRepository.findByRealname(realname).getUrlAddress();
    }
    
    /**
     * Palauttaa listan seuratuista henkilöistä.
     * @param follow lista followers-olioita, joiden joukosta seurattuja henkilöitä etsitään
     * @return lista seuratuista henkilöistä
     */
    public List<Account> findFollowed(ArrayList<Followers> follow) {
        List<Account> accounts = new ArrayList<>();
        
        for (Followers followers : follow) {
            Account followed  = followers.getTo();
            if (!accounts.contains(followed)) {
                accounts.add(followed);
            }
        }
        return accounts;
    }
    
    
    /**
     * Lopetaan käyttäjän seuraaminen
     * @param id kenen seuraaminen lopetetaan
     */
    public void stopFollowing(Long id) {
        Long userId = whoIsLogged();
        Followers toBeDeleted = followersRepository.findByFromIdAndToId(userId, id);
        followersRepository.delete(toBeDeleted);
    }
    
    
    /**
     * Palauttaa listan käyttäjän seuraamista tileistä.
     * @param userId
     * @return 
     */
    public List<Account> isFollowing(Long userId) {
     ArrayList<Followers> follow = followersRepository.findByFromId(userId);
     return findFollowed(follow);
    }
    
    
    /**
     * Palauttaa kirjautuneen käyttäjän id:n
     * @return kirjautuneen käyttäjän id
     */
    public Long whoIsLogged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Long userId = accountRepository.findByUsername(name).getId();
        return userId;
    }
}
