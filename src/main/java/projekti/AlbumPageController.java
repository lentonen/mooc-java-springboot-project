package projekti;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AlbumPageController {

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    PictureService pictureService;
    
    @Autowired
    PictureLikeRepository pictureLikeRepository;
    
    @Autowired
    MessageService messageService;
    
    @Autowired
    MessageLikeRepository messageLikeRepository;
    
    @Autowired
    MessageRepository messageRepository;
    
    
    @GetMapping("/album")
    public String showOwnAlbum() {
        String url = accountService.getLoggedUrl();
        return "redirect:/mainPage/" + url +"/album";
    }
    
    
    @GetMapping("/mainPage/{url}/album")
    public String show(Model model, @PathVariable String url) {
        String loggedNickname = accountService.getLoggedNickame();
        Long userId = accountService.getIdByUsingUrl(url);
        Long loggedUserId = accountService.getLoggedId();
        String name = accountService.getNickname(url);
        boolean ownAlbum = userId.equals(loggedUserId);
        // Tieto kirjautuneesta käyttäjästä
        model.addAttribute("loggedNickname", loggedNickname);
        // Viedään tieto, ollaanko omassa albumissa
        model.addAttribute("ownAlbum", ownAlbum);
        
        // Viedään tieto henkilökohtaisesta url, jotta saadaan ohjattua ensimmäisen kuvan tallennuksen jälkeen oikeaan paikkaan.
        model.addAttribute("url", url);
        
        try {
            Long firstId = pictureRepository.getNext(0L, userId).getId();
            return "redirect:/mainPage/"+url+"/album/"+firstId;
        } catch (NullPointerException e) {
            model.addAttribute("nickname", name);
            model.addAttribute("count", pictureRepository.countByOwnerId(userId));
            return "albumPage";
        } 
    }
    
    
    @GetMapping("/mainPage/{url}/album/{id}")
    public String showPicture(Model model,@PathVariable String url, @PathVariable Long id) {
        Long userId = accountService.getIdByUsingUrl(url);
        Long loggedUserId = accountService.getLoggedId();
        boolean ownAlbum = userId.equals(loggedUserId);
        
        // Viedään tieto, ollaanko omassa albumissa
        model.addAttribute("ownAlbum", ownAlbum);
        
        // Viedään henkilön url takaisin, jotta saadaan next ja previous toimimaan
        model.addAttribute("url", url);
        
        String name = accountService.getNickname(url);
        model.addAttribute("nickname", name);
        model.addAttribute("count", pictureRepository.countByOwnerId(userId));
        
        // Viedään tieto montako tykkäystä kuvassa on
        model.addAttribute("likes", pictureService.getLikes(id));
        
        // Viedään tieto kuka on kirjautuneena
        model.addAttribute("loggedUserId", accountService.getLoggedId());
        
        // Viedään tieto kuka omistaa albumin
        model.addAttribute("albumOwnerId", accountService.getIdByUsingUrl(url));
        
        // Viedään tieto kirjautuneen käyttäjän nicknamesta
        model.addAttribute("loggedNickname", accountService.getLoggedNickame());
        
        // Tarkastellaan ketä kirjautunut käyttäjä seuraa. Viedään tieto kommentointia varten    
        List<Long> loggedUserFollows = accountService.isFollowingId(accountService.getLoggedId());
        loggedUserFollows.add(loggedUserId);
        model.addAttribute("followList", loggedUserFollows);
        
        // HUOM! tässä versiossa kaikki käyttäjät voivat tarkastella toisten kuvia
        // Käyttäjä ei näe muiden käyttäjien omaa albumia
        //if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            // TODO: Tee tähän boolean-toiminnallisuus siten, että jos katsotaan muiden albumia, niin tällöin ei näytetä
            // ollenkaan kuvien lisäämiseen tarkoitettua osiota. Tällöin samaa album-sivua voidaan käyttää myös muiden albumien tarkasteluun.
        //    return "redirect:/album";
        
        // Asetetaan id:tä vastaava kuva albumiin
        if (pictureRepository.existsById(id)) {
            model.addAttribute("current", id);
            model.addAttribute("description", pictureRepository.getOne(id).getDescription());
            model.addAttribute("comments", messageService.findComments(id));
        }
        
        // Viedään tieto seuraavan kuvan id:stä.
        try {
            Long nextId = pictureRepository.getNext(id, userId).getId();
            model.addAttribute("next", nextId); 
        } catch (Exception e) {
            // Do nothing
        }
        
        // viedään tieto edellisen kuvan id:stä.
        try {
            Long previousId = pictureRepository.getPrevious(id, userId).getId();
            model.addAttribute("previous", previousId);
        } catch (Exception e) {
            // Do nothing
        }

        // Viedään tieto siitä, onko nykyinen kuva profiilikuva
        boolean isProfilePic = false;
        if (Objects.equals(accountService.getLoggedAccount().getProfilePictureId(), id)){
            isProfilePic = true;
        }     
        model.addAttribute("booleanProfilePic", isProfilePic);
        return "albumPage";
    }
    
    
    @GetMapping(path = "/album/{id}/content", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] showOnePicture(@PathVariable Long id){
        //Long accountId = accountService.getLoggedId();
        // Käyttäjälle näytetään oma kuva, vaikka yrittääkin hakea suoraan toisen käyttäjän kuvaa. Jos omaa ei ole, näytetään tyhjä kuva. HUOM! TÄmä poistettu käytöstä.
        // Tällä hetkellä kaikki kuvat tulevat näkyviin kaikille
        /*if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            try {
                return pictureRepository.getOne(pictureRepository.getNext(id, accountId).getId()).getContent();
            } catch (NullPointerException e) {
                byte[] emptyData = {};
                return emptyData;
            }*/
        return pictureRepository.getOne(id).getContent();
    }
    
    @PostMapping("/mainPage/{url}/album/{id}/save")
    public String save(@PathVariable String url, @PathVariable Long id, @RequestParam("file") MultipartFile file, @RequestParam("description") String description) throws IOException {
        Account loggedAccount = accountService.getLoggedAccount();
        if (file.isEmpty() || !file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) || pictureService.pictureCount(loggedAccount.getId()) > 10)
            return "redirect:/mainPage/" + url +"/album/" +id; 
        FileObject fo = new FileObject();
        fo.setContent(file.getBytes());
        fo.setOwner(loggedAccount);
        fo.setDescription(description);
        pictureRepository.save(fo);
        Long nextId = pictureRepository.getNext(id, accountService.getId(url)).getId();
        return "redirect:/mainPage/" + url +"/album/" +nextId; 
    }
    
    
    @PostMapping("/mainPage/{url}/album/{id}/setProfilePicture")
    public String setProfilePicture(@PathVariable String url, @PathVariable Long id) {
        // Lähdetään pois, jos yritetään asettaa profiilikuvaksi jonkun toisen kuva.
        Long accountId = accountService.getLoggedId();
        if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            return "redirect:/mainPage/" + url +"/album/";
        
        accountService.setProfilePicture(id);
   
        return "redirect:/mainPage/" + url +"/album/" +id;
    }
    
    
    @PostMapping("/mainPage/{url}/album/{id}/deletePicture")
    public String deletePicture(@PathVariable String url, @PathVariable Long id) {
        // Lähdetään pois, jos yritetään poistaa jonkun toisen kuva.
        Long accountId = accountService.getLoggedId();
        if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            return "redirect:/mainPage/" + url +"/album/" +id;
        pictureRepository.deleteById(id);
        try {
            Long previousId = pictureRepository.getPrevious(id, accountId).getId();
            return "redirect:/mainPage/" + url +"/album/" +previousId;
        } catch (NullPointerException e) {
            return "redirect:/mainPage/" + url +"/album/";
        }
        
    }
    
    
    @PostMapping("mainPage/{url}/album/like/{accountId}/picture/{pictureId}")
    public String likePicture(@PathVariable String url, @PathVariable Long accountId, @PathVariable Long pictureId) {
         if (pictureLikeRepository.existsByAccountIdAndPictureId(accountId, pictureId))
            return "redirect:/mainPage/"+url+"/album/"+pictureId;
        pictureLikeRepository.save(new PictureLike(accountService.getLoggedAccount(), pictureRepository.getById(pictureId)));
        return "redirect:/mainPage/"+url+"/album/"+pictureId;
    }
    
    
    @PostMapping("mainPage/{url}/album/{pictureId}/pictureComment")
    public String createComment(@PathVariable String url, @RequestParam String comment, @PathVariable Long pictureId) {
        messageService.createWallMessageComment(comment, accountService.getLoggedAccount(), pictureId);
        return "redirect:/mainPage/"+url+"/album/"+pictureId;
    }
    
    
    @PostMapping("mainPage/{url}/album/{pictureId}/like/{accountId}/pictureComment/{commentId}")
    public String likeComment(@PathVariable String url, @PathVariable Long pictureId, @PathVariable Long accountId, @PathVariable Long commentId) {
         if (messageLikeRepository.existsByAccountIdAndMessageId(accountId, commentId))
            return "redirect:/mainPage/"+url+"/album/"+pictureId;
        messageLikeRepository.save(new MessageLike(accountService.getLoggedAccount(), messageRepository.getById(commentId)));
        return "redirect:/mainPage/"+url+"/album/"+pictureId;
    }
    
    
}

