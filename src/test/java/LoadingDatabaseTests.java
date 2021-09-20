import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class LoadingDatabaseTests {

    @Test
    public void testingLoadingAccountNumbers() throws SQLException {
        assertFalse(LoadDatabase.loadCheckingAccountNumber("newuser1") <= 0);
        assertFalse(LoadDatabase.loadSavingsAccountNumber("newuser1") <= 0);
    }

    @Test
    public void testingLoadingAccountBalances() throws SQLException {
        assertFalse(LoadDatabase.loadCheckingBalance("newuser1") <= 0);
        assertFalse(LoadDatabase.loadSavingsBalance("newuser1") <= 0);
    }

    @AfterAll
    static void printTestOutputs() throws SQLException {
        System.out.println("Checking Account Number: " + LoadDatabase.loadCheckingAccountNumber("newuser1"));
        System.out.println("Balance: " + LoadDatabase.loadCheckingBalance("newuser1"));
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Savings Account Number: " + LoadDatabase.loadSavingsAccountNumber("newuser1"));
        System.out.println("Balance: " + LoadDatabase.loadSavingsBalance("newuser1"));
    }
}