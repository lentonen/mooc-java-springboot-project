/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public List<Long> findAllCommentEntityIds(Long accountId) {
        List<Message> messages = messageRepository.findByAccountIdAndEntityIdNotNull(accountId);
        List<Long> commentIds = new ArrayList<>();
        
        for (Message msg : messages) {
            commentIds.add(msg.getEntityId());
        }
        return commentIds;
    }
    
    /**
     * Palauttaa entityId:tä vastaavat viestit
     * @param entityId lista jota vastaavia viestejä etsitään
     * @return viestit joiden entityId löytyy parametrina viedystä listasta
     */
    public List<Message> findAllComments(List<Long> entityId) {
        return messageRepository.findByEntityIdInOrderByMessageDateDescMessageTimeDesc(entityId);
    }
    
    
    /**
     * Palauttaa kaikki käyttäjän seinäviestien kommentit
     * @param accountId käyttäjä jonka viestien kommentteja etsitään
     * @return käyttäjän seinäviestien kommentit
     */
    public List<Message> findAllCommentsByUser(Long accountId) {
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
    
    
    public void createWallMessageComment(String content, Account account, Long messageId) {
        Message msg = new Message();
        msg.setContent(content);
        msg.setAccount(account);
        msg.setEntityId(messageId);
        messageRepository.save(msg);
    }
}
