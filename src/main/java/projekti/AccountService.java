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
        List<Account> lista = accountRepository.findByNicknameContainingIgnoreCase(searchString);
        
        // Poistetaan kirjautunut käyttäjä listasta
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        lista.remove(accountRepository.findByUsername(name));
        
        return lista;
    }
    
    
    /**
     * Aloittaa seuraamaan käyttäjää
     * @param nickname minkä nimistä käyttäjää aletaan seurata
     */
    public void startFollow(String nickname) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        followersRepository.save(new Followers(accountRepository.findByUsername(username), accountRepository.findByNickname(nickname)));
        // TODO: ei voi seurata jo seurattua käyttäjää
        

        //List<Followers> following = accountRepository.findByUsername(name).getFollowing();
        //Account newFollowed = accountRepository.findByUsername(realname);
        //followed.add(newFollowed);
        //accountRepository.findByUsername(name).setFollow(followed);  
    }
    
    
    public void startFollowUrl(String urlAddress) {
        Account from = accountRepository.findByNickname(getLoggedNickame());
        Account to = accountRepository.findByUrlAddress(urlAddress);
        if (followersRepository.findByFromIdAndToId(from.getId(), to.getId()) == null) 
            followersRepository.save(new Followers(from, to));
    }
    
    
    public void stopFollowUrl(String urlAddress) {
        Long from = getLoggedId();
        Long to = accountRepository.findByUrlAddress(urlAddress).getId();
        Followers f = followersRepository.findByFromIdAndToId(from, to);
        if (f != null)
            followersRepository.delete(f);
    }
    
    
    public String getUrl(String nickname) {
        return accountRepository.findByNickname(nickname).getUrlAddress();
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
     * Palauttaa listan seuraajista
     * @param listFollowers lista followers-olioita, joiden joukosta seuraajat haetaan
     * @return lista seuraajista
     */
    public List<Account> findFollowers(ArrayList<Followers> listFollowers) {
        List<Account> accounts = new ArrayList<>();
        for (Followers followers : listFollowers) {
            Account follower  = followers.getFrom();
            if (!accounts.contains(follower)) {
                accounts.add(follower);
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
     * @param userId kenen käyttäjän seuraamia tilejä etsitään
     * @return lista seuratuista tileistä
     */
    public List<Account> isFollowing(Long userId) {
     ArrayList<Followers> follow = followersRepository.findByFromId(userId);
     return findFollowed(follow);
    }
    
    /**
     * Palauttaa listassa käyttäjän seuraamien tilien ID:t
     * @param userId Kenen käyttäjän seuraajia etsitään
     * @return Lista seurattujen henkilöiden ID
     */
    public List<Long> isFollowingId(Long userId) {
        List<Account> follow = isFollowing(userId);
        List<Long> followId = new ArrayList<Long>();
        for (Account a : follow) {
            followId.add(a.getId());
        }
        return followId;
    }
    
    /**
     * Palauttaa listan seuraajista
     * @param userId kenen käyttäjän seuraajia etsitään
     * @return lista seuraajista
     */
    public List<Account> followers(Long userId) {
        ArrayList<Followers> followers = followersRepository.findByToId(userId);
        return findFollowers(followers);
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
    
    
    /**
     * Palauttaa kirjautuneen käyttäjän usernamen
     * @return kirjautuneen käyttäjän id
     */
    public String getLoggedName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
    
    
    /**
     * Palauttaa kirjautuneen käyttäjän nicknamen
     * @return kirjautuneen käyttäjän id
     */
    public String getLoggedNickame() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username =  auth.getName();
        return accountRepository.findByUsername(username).getNickname();
    }
    
    
    /**
     * Palauttaa kirjautuneen käyttäjän ID:n
     * @return kirjautuneen käyttäjän id
     */
    public Long getLoggedId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username =  auth.getName();
        return accountRepository.findByUsername(username).getId();
    }
    
    /**
     * Palauttaa kirjautuneen käyttäjän
     * @return kirjautuneen käyttäjän Accountin
     */
    public Account getLoggedAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username =  auth.getName();
        return accountRepository.findByUsername(username);
    }
    
    
    /**
     * Kertoo seuraako käyttäjä toista käyttäjää
     * @param from kuka seuraa
     * @param to ketä seurataan
     * @return true jos seurataan, false jo ei seurata
     */
    public boolean follows(Long from, Long to) {
        if (followersRepository.findByFromIdAndToId(from, to) == null)
            return false;
        return true;     
    }
    
    
    /**
     * Estetään käyttäjän seuraaminen
     * @param from kenen käyttäjän seuraaminen estetään
     */
    public void preventFollow(Long from) {
        Long to = getLoggedId();
        Followers f = followersRepository.findByFromIdAndToId(from, to);
        f.setPrevent(true);
        followersRepository.saveAndFlush(f);
    }
    
    
    /**
     * Sallitaan käyttäjän seuraaminen
     * @param from kenen käyttäjän seuraaminen sallitaan
     */
    public void removePrevent(Long from) {
        Long to = getLoggedId();
        Followers f = followersRepository.findByFromIdAndToId(from, to);
        f.setPrevent(false);
        followersRepository.saveAndFlush(f);
    }
    
    /**
     * Asettaa annetun ID:n käyttäjän profiilikuvan ID:ksi
     * @param pictureId Kuva joka halutaan profiilikuvaksi
     */
    public void setProfilePicture(Long pictureId) {
        String username = getLoggedName();
        accountRepository.findByUsername(username).setProfilePictureId(pictureId);
        accountRepository.flush();
    }
    
    
    /**
     * Palauttaa url vastaavan nicknamen
     * @param url sivu jonka omistajaa etsitään
     * @return sivun omistajan nickname
     */
    public String getNickname(String url) {
        return accountRepository.findByUrlAddress(url).getNickname();
    }
    
    
    /**
     * Palauttaa sivun omistajan ID:n
     * @param url sivu jonka omistajaa etsitään
     * @return sivun omistajan ID
     */
    public Long getId(String url) {
        return accountRepository.findByUrlAddress(url).getId();
    }
    
    
    /**
     * Kertoo onko käyttäjä omalla sivulla
     * @param url sivu jolla ollaan
     * @return palauttaa true jos sivu on oma, false jos jonkun muun sivu
     */
    public Boolean ownPage(String url) {
        if (getLoggedAccount().getUrlAddress().equals(url))
            return true;
        return false;
    }
    
    /**
     * Poistetaan seurattujen henkilöiden listasta ne, jotka ovat estäneet seuraamisen
     * @param userId
     * @param follow 
     */
    public void deleteUsersWhoPrevented(Long userId, List<Long> follow) {
        Iterator<Long> i = follow.iterator(); //TODO: korjaa tämä toimivaksi
        while (i.hasNext()) {
            Long followId = i.next();
            try {
                if (followersRepository.findByFromIdAndToId(userId, followId).getPrevent())
                    i.remove();
            } catch (NullPointerException e) {
                // Älä tee mitään. Heittää poikkeuksen, jos follow-listassa ei ole oman ID:n lisäksi mitään muuta
            } 
        }
    }
    
    
    /**
     * Palauttaa url vastaavan ID:n
     * @param url nimi jota vastaava ID palautetaan
     * @return nicknamea vastaava ID
     */
    public Long getIdByUsingUrl(String url) {
        return accountRepository.findByUrlAddress(url).getId();
    }
    
    
    /**
     * Palauttaa kirjautuneen käyttäjnä url-tunnisteen
     * @return kirjautuneen käyttäjän url
     */
    public String getLoggedUrl() {
        return getUrl(getLoggedNickame());
    }
    
    
    /**
     * Palauttaa tiedon onko seuraaminen estetty.
     * @param from seuraaja
     * @param to ketä seurataan
     * @return true jos seuraaminen estetty, false jos ei. Palauttaa false, jos tarkastellaan onko käyttäjä estänyt itsensä.
     * Jos käyttäjä ei seuraa toista käyttäjää, niin palautetaan false.
     */
    public Boolean isPrevented(Long from, Long to) {
        if (from == to)
            return false;
        if (followersRepository.existsByFromIdAndToId(from, to))
            return followersRepository.findByFromIdAndToId(from, to).getPrevent();
        return false;
    } 
}
