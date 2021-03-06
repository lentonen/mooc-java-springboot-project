/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Follers-olio joka tietää kuka seuraa ketäkin. Oliolla tiedossa myös ajankohta, jolloin alettu seurata.
 * Olio tietää myös, jos käyttäjä on estänyt seuraamisen.
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
    
    LocalDate followDate = java.time.LocalDate.now();
    Boolean prevent = false;
    
    Followers(Account from, Account to) {
        this.from = from;
        this.to = to;
    }
    
}
