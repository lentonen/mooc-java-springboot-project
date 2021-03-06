/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Henri
 */
@Service
public class PictureService {
    
    @Autowired
    PictureRepository pictureRepository;
    
    @Autowired
    AccountService accountService;
    
    
    /**
     * Palauttaa kuvan omistajan ID:n
     * @param pictureId kuva jonka omistajaa etsitään
     * @return kuvan omistajan ID
     */
    public Long getPictureOwner(Long pictureId) {
        Optional<FileObject> fo = pictureRepository.findById(pictureId);
        return fo.get().getOwner().getId();
    }
    
    
    /**
     * Palauttaa tiedon siitä, saako käyttäjä nähdä toisen käyttäjän kuvan
     * @param loggedId
     * @param pictureId
     * @return 
     */
    public Boolean canSeeThisPic(Long loggedId, Long pictureId) {
        Long pictureOwner = getPictureOwner(pictureId);
        return accountService.follows(loggedId, pictureOwner);
    }
    
    
    /**
     * Kertoo paljonko käyttäjällä on kuvia
     * @param accountId käyttäjän id
     * @return kuvien lukumäärän
     */
    public Long pictureCount(Long accountId) {
        return pictureRepository.countByOwnerId(accountId);
    }
    
    
    /**
     * Kertoo paljonko kuvalla on tykkäyksiä
     * @param pictureId Kuva jonka tykkäyksiä etsiään
     * @return  tykkäysten lukumäärä
     */
    public int getLikes(Long pictureId) {
        return pictureRepository.getOne(pictureId).getLikes().size();
    }
   
    
}
