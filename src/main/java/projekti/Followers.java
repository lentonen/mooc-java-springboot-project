/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author Henri
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Followers extends AbstractPersistable<Long> {
    
    @ManyToOne
    private Account from;
    
    @ManyToOne
    private Account to;
    
}