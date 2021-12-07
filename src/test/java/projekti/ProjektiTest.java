package projekti;

import fi.helsinki.cs.tmc.edutestutils.Points;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjektiTest {

    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    private MockMvc mockMvc;
    
    public void noTests() {
        // projektia varten ei ole automaattisia testejä
    }

    @Test
    public void hasAccountTable() {
        jdbcTemplate.execute("SELECT id, username, realname, password, url_address FROM Account");
    }
    
    @Test
    public void hasFollowersTable() {
        jdbcTemplate.execute("SELECT * FROM Followers");
    }
}
