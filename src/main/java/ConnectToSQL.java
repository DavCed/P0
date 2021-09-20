import java.sql.*;
import java.util.*;

public class ConnectToSQL {

    private static Connection connect = null;

    private ConnectToSQL(){}

    public static Connection connectNow() {
        if(connect == null){
            try {
                connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_application","root","thisismysql");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connect;
    }
}

class LoadDatabase {

    public static Connection connect = ConnectToSQL.connectNow();

    public static List<String> loadUsernames() throws SQLException {
        List<String> allUsernames = new ArrayList<>();
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from users");
        while(result.next()){
            allUsernames.add(result.getString(1));
        }
        return allUsernames;
    }

    public static double loadCheckingBalance(String username) throws SQLException {
        double balance = 0;
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from checking_accounts");
        while(result.next()){
            if(result.getString(1).equalsIgnoreCase(username)) {
                balance = result.getDouble(3);
            }
        }
        return balance;
    }

    public static double loadSavingsBalance(String username) throws SQLException {
        double balance = 0;
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from savings_accounts");
        while(result.next()){
            if(result.getString(1).equalsIgnoreCase(username)) {
                balance = result.getDouble(3);
            }
        }
        return balance;
    }

    public static long loadCheckingAccountNumber(String username) throws SQLException {
        long checkingNumber = 0;
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from checking_accounts");
        while(result.next()){
            if(result.getString(1).equalsIgnoreCase(username)){
                checkingNumber = result.getLong(2);
            }
        }
        return checkingNumber;
    }

    public static long loadSavingsAccountNumber(String username) throws SQLException {
        long savingsNumber = 0;
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from savings_accounts");
        while(result.next()){
            if(result.getString(1).equalsIgnoreCase(username)){
                savingsNumber = result.getLong(2);
            }
        }
        return savingsNumber;
    }

    public static List<Float> loadTransactions(long accountNumber) throws SQLException {
        List<Float> allTransactions = new ArrayList<>();
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from transactions");
        while (result.next()) {
            if (result.getLong(1) == accountNumber & result.getString(2).equalsIgnoreCase("post")) {
                allTransactions.add(result.getFloat(3));
            }
        }
        return allTransactions;
    }

    public static List<String[]> loadPendingCheckingAccounts() throws SQLException {
        List<String[]> allPendingAccounts = new ArrayList<>();
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from checking_accounts");
        while(result.next()){
            if(result.getString(4).equalsIgnoreCase("pending")) {
                allPendingAccounts.add(new String[] {result.getString(1),
                        String.valueOf(result.getLong(2)),
                        String.valueOf(result.getDouble(3)),
                        result.getString(4)});
            }
        }
        return allPendingAccounts;
    }

    public static List<String[]> loadPendingSavingsAccounts() throws SQLException {
        List<String[]> allPendingAccounts = new ArrayList<>();
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from savings_accounts");
        while (result.next()) {
            if (result.getString(4).equalsIgnoreCase("pending")) {
                allPendingAccounts.add(new String[] {result.getString(1),
                        String.valueOf(result.getLong(2)),
                        String.valueOf(result.getDouble(3)),
                        result.getString(4)});
            }
        }
        return allPendingAccounts;
    }

    public static String loadCheckingStatus(String username) throws SQLException {
        String status = "";
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from checking_accounts");
        while(result.next()){
            if(result.getString(1).equalsIgnoreCase(username)){
                status = result.getString(4);
            }
        }
        return status;
    }

    public static String loadSavingsStatus(String username) throws SQLException {
        String status = "";
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from savings_accounts");
        while(result.next()){
            if(result.getString(1).equalsIgnoreCase(username)){
                status = result.getString(4);
            }
        }
        return status;
    }
}