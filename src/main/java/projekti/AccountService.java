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
import org.springframework.stereotype.Service;

/**
 *
 * @author Henri
 */
@Service
public class AccountService {
    
    @Autowired
    AccountRepository accountRepository;
    
    public List<Account> findAccounts(String searchString) {
        return accountRepository.findByRealnameStartingWith(searchString);
    }
    
    
    
}
