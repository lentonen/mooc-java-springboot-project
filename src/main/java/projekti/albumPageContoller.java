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
    
    @GetMapping("/album/test")
    public String showTest(Model model) {
            String name = accountService.getLoggedNickame();
            Long accountId = accountService.getLoggedId();
            model.addAttribute("message", name);
            model.addAttribute("count", pictureRepository.getPictureCount(accountId));
            return "albumPage";
    }
    
    @GetMapping("/album")
    public String show(Model model) {
        Long firstId = pictureRepository.getNextId(0L, accountService.getLoggedId());
        if (firstId == null) {
            String name = accountService.getLoggedNickame();
            Long accountId = accountService.getLoggedId();
            model.addAttribute("message", name);
            model.addAttribute("count", pictureRepository.getPictureCount(accountId));
            return "albumPage";
        }
            
        return "redirect:/album/"+firstId;
    }
    
    @GetMapping("/album/{id}")
    public String showPicture(Model model, @PathVariable Long id) {
        String name = accountService.getLoggedNickame();
        Long accountId = accountService.getLoggedId();
        
        // Käyttäjä ei näe muiden käyttäjien omaa albumia
        if (pictureRepository.countByAccountIdAndPictureId(accountId, id) == 0)
            return "redirect:/album";
        
        model.addAttribute("message", name);
        model.addAttribute("count", pictureRepository.getPictureCount(accountId));
       
        if (pictureRepository.existsById(id)) {
            model.addAttribute("current", pictureRepository.getOne(id).getId());
        }
        
        Long nextId = pictureRepository.getNextId(id, accountId);
        if (nextId != null){
            FileObject fo = pictureRepository.getOne(nextId);
            model.addAttribute("next", fo.getId()); 
        }
        
        Long previousId = pictureRepository.getPreviousId(id, accountId);
        if (previousId != null) {
            FileObject fo = pictureRepository.getOne(previousId);
            model.addAttribute("previous", fo.getId());
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
        // Käyttäjälle näytetään oma kuva, vaikka yrittääkin hakea suoraan toisen käyttäjän kuvaa.
        if (pictureRepository.countByAccountIdAndPictureId(accountId, id) == 0)
            return pictureRepository.getOne(pictureRepository.getNextId(id, accountId)).getContent();
        
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
        if (pictureRepository.countByAccountIdAndPictureId(accountId, id) == 0)
            return "redirect/album";
        
        accountService.setProfilePicture(id);
   
        return "redirect:/album/"+id;
    }
    
    
    @PostMapping("/album/{id}/deletePicture")
    public String deletePicture(@PathVariable Long id) {
        // Lähdetään pois, jos yritetään poistaa jonkun toisen kuva.
        Long accountId = accountService.getLoggedId();
        if (pictureRepository.countByAccountIdAndPictureId(accountId, id) == 0)
            return "redirect/album";
        pictureRepository.deleteById(id);
        return "redirect:/album/"+id;
    }
}
