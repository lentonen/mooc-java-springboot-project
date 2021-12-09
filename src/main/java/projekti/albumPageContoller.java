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
public class albumPageContoller {

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    AccountService accountService;
    
    @GetMapping("/album")
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
    }
    
    @GetMapping("/album/{id}")
    public String showPicture(Model model, @PathVariable Long id) {
        String name = accountService.getLoggedNickame();
        Long accountId = accountService.getLoggedId();
        model.addAttribute("message", name);
        model.addAttribute("count", pictureRepository.countByOwnerId(accountId));
        
        // Käyttäjä ei näe muiden käyttäjien omaa albumia
        if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            // TODO: Tee tähän boolean-toiminnallisuus siten, että jos katsotaan muiden albumia, niin tällöin ei näytetä
            // ollenkaan kuvien lisäämiseen tarkoitettua osiota. Tällöin samaa album-sivua voidaan käyttää myös muiden albumien tarkasteluun.
            return "redirect:/album";
        
        // Asetetaan id:tä vastaava kuva albumiin
        if (pictureRepository.existsById(id)) {
            model.addAttribute("current", pictureRepository.getOne(id).getId());
        }
        
        // Viedään tieto seuraavan kuvan id:stä.
        try {
            Long nextId = pictureRepository.getNext(id, accountId).getId();
            FileObject fo = pictureRepository.getOne(nextId);
            model.addAttribute("next", fo.getId()); 
        } catch (Exception e) {
            // Do nothing
        }
        
        // viedään tieto edellisen kuvan id:stä.
        try {
            Long previousId = pictureRepository.getPrevious(id, accountId).getId();
            FileObject fo = pictureRepository.getOne(previousId);
            model.addAttribute("previous", fo.getId());
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
        Long accountId = accountService.getLoggedId();
        // Käyttäjälle näytetään oma kuva, vaikka yrittääkin hakea suoraan toisen käyttäjän kuvaa. Jos omaa ei ole, näytetään tyhjä kuva.
        if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            try {
                return pictureRepository.getOne(pictureRepository.getNext(id, accountId).getId()).getContent();
            } catch (NullPointerException e) {
                byte[] emptyData = {};
                return emptyData;
            }
        return pictureRepository.getOne(id).getContent();
    }
    
    @PostMapping("/album")
    public String save(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || !file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE))
            return "redirect:/album";  
        FileObject fo = new FileObject();
        fo.setContent(file.getBytes());
        fo.setOwner(accountService.getLoggedAccount());
        pictureRepository.save(fo);
        return "redirect:/album";
    }
    
    
    @PostMapping("/album/{id}/setProfilePicture")
    public String setProfilePicture(@PathVariable Long id) {
        // Lähdetään pois, jos yritetään asettaa profiilikuvaksi jonkun toisen kuva.
        Long accountId = accountService.getLoggedId();
        if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            return "redirect/album";
        
        accountService.setProfilePicture(id);
   
        return "redirect:/album/"+id;
    }
    
    
    @PostMapping("/album/{id}/deletePicture")
    public String deletePicture(@PathVariable Long id) {
        // Lähdetään pois, jos yritetään poistaa jonkun toisen kuva.
        Long accountId = accountService.getLoggedId();
        if (!pictureRepository.existsByOwnerIdAndId(accountId, id))
            return "redirect/album";
        pictureRepository.deleteById(id);
        return "redirect:/album/"+id;
    }
}
