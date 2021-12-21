package projekti;

import java.io.IOException;
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
public class AlbumPageContoller {

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    PictureService pictureService;
    
    /*@GetMapping("/album")
    public String show(Model model) {
        try {
            Long firstId = pictureRepository.getNext(0L, accountService.getLoggedId()).getId();
            return "redirect:/album/"+firstId;
        } catch (NullPointerException e) {
            String name = accountService.getLoggedNickame();
            Long accountId = accountService.getLoggedId();
            model.addAttribute("message", name);
            model.addAttribute("count", pictureRepository.countByOwnerId(accountId));
            return "albumPage";
        } 
    }*/
    
    
    @GetMapping("/mainPage/{url}/album")
    public String show(Model model, @PathVariable String url) {
        Long userId = accountService.getIdByUsingUrl(url);
        Long loggedUserId = accountService.getLoggedId();
        String name = accountService.getNickname(url);
        boolean ownAlbum = userId.equals(loggedUserId);
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
        
        // HUOM! tässä versiossa kaikki käyttäjät voivat tarkastella toisten kuvia
        // Käyttäjä ei näe muiden käyttäjien omaa albumia
        //if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            // TODO: Tee tähän boolean-toiminnallisuus siten, että jos katsotaan muiden albumia, niin tällöin ei näytetä
            // ollenkaan kuvien lisäämiseen tarkoitettua osiota. Tällöin samaa album-sivua voidaan käyttää myös muiden albumien tarkasteluun.
        //    return "redirect:/album";
        
        // Asetetaan id:tä vastaava kuva albumiin
        if (pictureRepository.existsById(id)) {
            model.addAttribute("current", id);
            String description = pictureRepository.getOne(id).getDescription();
            model.addAttribute("description", pictureRepository.getOne(id).getDescription());
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
}
