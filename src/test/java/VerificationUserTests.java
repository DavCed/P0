import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class VerificationUserTests {

   static User user;

    @BeforeAll
    static void createObjectFirst() {
        user = new Customer("newuser1", "newuser1");
    }

    @Test
    public void verifyUserCredentialsLoggingIn() throws SQLException {
        assertTrue(user.verifyUser("newuser1","newuser1"));
        assertFalse(user.verifyUser("newuser","newuser1"));
        assertFalse(user.verifyUser("newuser1","newuser"));
    }

    @Test
    public void verifyDuplicateUsernameForRegistering() throws SQLException {
        assertTrue(user.verifyDuplicateUsername("newuser1"));
    }
}
