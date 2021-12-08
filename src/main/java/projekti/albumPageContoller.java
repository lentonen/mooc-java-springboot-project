package projekti;

import java.io.IOException;
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
    public String show() {
        return "redirect:/album/1";
    }
    
    @GetMapping("/album/{id}")
    public String showPicture(Model model, @PathVariable Long id) {
        model.addAttribute("count", pictureRepository.count());
       
        if (pictureRepository.existsById(id)) {
            model.addAttribute("current", pictureRepository.getOne(id).getId());
        }
        
        for (Long i = id-1; i >= 0; i--) {
            if (pictureRepository.existsById(i)){
                FileObject fo = pictureRepository.getOne(i);
                model.addAttribute("previous", fo.getId());
                break;  
            }
        }
            
        for (Long i = id+1; i <= id+1000; i++) {
            if (pictureRepository.existsById(i)){
                FileObject fo = pictureRepository.getOne(i);
                model.addAttribute("next", fo.getId());
                break;  
            }
        }        
        return "albumPage";
    }
    
    @GetMapping(path = "/album/{id}/content", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] showOnePicture(@PathVariable Long id){
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
}
