package projekti;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Henri
 */
@Service
public class MessageService {
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    FollowersRepository followersRepository;
    @Autowired
    MessageRepository messageRepository;
    
    
    /**
     * Palauttaa kaikki yhden käyttäjän seinäviestien kommenttien entityId:t
     * @param accountId käyttäjä, jonka viestien kommentteja etsitään
     * @return seinäviestien kommenttien entityId:t
     */
    public List<Long> findAllCommentEntityIds(List<Long> accountId) {
        // Etsitään käyttäjän seinäviestien id:t
        List<Message> wallMessages = messageRepository.findByAccountIdInAndEntityIdNull(accountId);
        List<Long> entityId = new ArrayList<>();
        
        for (Message msg : wallMessages){
            entityId.add(msg.getId());
        }    
        return entityId;
    }
    
    
    /**
     * Palauttaa tiettyihin kuviin tai seinäviesteihinn liittyvät kommentit. Palautetaan maksimissaan 10 kommenttia. Tämä rajoite oli annettu harkkatyön ohjeessa.
     * @param entityId lista id:stä, joiden kommentteja etsitään
     * @return viestit joiden entityId löytyy parametrina viedystä listasta
     */
    public List<Message> findAllComments(List<Long> entityId) {
        List<Message> comments = new ArrayList<Message>();
        for (Long id : entityId) {
            comments.addAll(messageRepository.findTop10ByEntityIdOrderByMessageDateAscMessageTimeAsc(id));
        }
        return comments;
    }
    
    
    /**
     * Palauttaa tiettyyn entiteettiin (kuva tai seinäviesti) liittyvät kommentit. Palauttaa maksimissaan 10 kommenttia. tämä rajoite oli annettu 
     * harkkatyön ohjeessa.
     * @param entityId entiteetin Id
     * @return Kuvan tai seinäviestin kommentit
     */
    public List<Message> findComments(Long entityId) {
        return messageRepository.findTop10ByEntityIdOrderByMessageDateAscMessageTimeAsc(entityId);
    }
    
    
    /**
     * Palauttaa kaikki listassa ennettujen käyttäjien seinäviestien kommentit
     * @param accountId käyttäjät joiden viestien kommentteja etsitään
     * @return käyttäjän seinäviestien kommentit
     */
    public List<Message> findAllCommentsByUser(List<Long> accountId) {
        List <Message> msg =findAllComments(findAllCommentEntityIds(accountId));
        return msg;
    }
    
    
    /**
     * Luo ja tallentaa seinäviestin tietokantaan
     * @param content viestin sisältö
     * @param account käyttäjä joka viestin lähettää
     */
    public void createWallMessage(String content, Account account) {
        Message msg = new Message();
        msg.setContent(content);
        msg.setAccount(account);
        messageRepository.save(msg);
    }
    
    
    /**
     * Luodaan kommentti seinäviestille
     * @param content kommentin sisältö
     * @param account kommentin tekijän tili
     * @param messageId mihin viestiin kommentti liittyy
     */
    public void createWallMessageComment(String content, Account account, Long messageId) {
        Message msg = new Message();
        msg.setContent(content);
        msg.setAccount(account);
        msg.setEntityId(messageId);
        messageRepository.save(msg);
    } 
}

