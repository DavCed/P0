import java.sql.*;

interface User {

    Connection connect = ConnectToSQL.connectNow();
    boolean verifyUser(String username, String password) throws SQLException;
    boolean verifyDuplicateUsername(String username) throws SQLException;
    void registerAccount(String username, String password) throws SQLException;
    void openBankAccount(Customer customer, String accountType) throws SQLException;
}

public class Customer implements User {

    private String userName;
    private String password;
    private BankAccount checking;
    private BankAccount savings;

    public Customer() {}

    public Customer(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.checking = null;
        this.savings = null;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public BankAccount getChecking() {
        return this.checking;
    }

    public void setChecking(BankAccount checking){
        this.checking = checking;
    }

    public BankAccount getSavings(){
        return this.savings;
    }

    public void setSavings(BankAccount savings){
        this.savings = savings;
    }

    @Override
    public boolean verifyUser(String username, String password) throws SQLException {
        boolean log = false;
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from users");
        while (result.next()) {
            if (result.getString(1).equalsIgnoreCase(username) & result.getString(2).equalsIgnoreCase(password)) {
                log = true;
                break;
            }
        }
        return log;
    }

    @Override
    public boolean verifyDuplicateUsername(String username) throws SQLException {
        boolean log = false;
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from users");
        while(result.next()){
            if(result.getString(1).equalsIgnoreCase(username)){
                log = true;
                break;
            }
        }
        return log;
    }

    @Override
    public void registerAccount(String username, String password) throws SQLException {

        boolean validUsername = true;
        boolean validPassword = true;

        if(username.length() >= 8) {
            if(username.charAt(0) < 'A' & username.charAt(0) > 'Z'
                    | username.charAt(0) < 'a' & username.charAt(0) > 'z') {
                System.out.println("\nUsername can only start with a character. :|");
                validUsername = false;
            } else {
                for (int i = 0; i <= username.length() - 1; i++) {
                    char c = username.charAt(i);
                    if (!(c >= 'A' & c <= 'Z' | c >= 'a' & c <= 'z' | c > '0' & c < '9')) {
                        System.out.println("\nPlease enter a username with characters and numbers only. :|");
                        validUsername = false;
                        break;
                    }
                }
            }
        } else {
            System.out.println("\nPlease enter a username of 8 characters or more. :|");
            validUsername = false;
        }

        if(password.length() < 8){
            System.out.println("Please enter a password of 8 or more characters. :|");
            validPassword = false;
        }

        if(!validUsername | !validPassword){
            System.out.println("Invalid Account. :(");
        } else {
            System.out.println("Valid Account! Login Now! :)");

            PreparedStatement preparedStatement1 = connect.prepareStatement("insert into users " +
                    "(cust_username, cust_password, checking_account_number, savings_account_number, " +
                    "checking_account_status, savings_account_status) values (?, ?, ?, ?, ?, ?)");
            preparedStatement1.setString(1,username);
            preparedStatement1.setString(2,password);
            preparedStatement1.setLong(3,0);
            preparedStatement1.setLong(4,0);
            preparedStatement1.setString(5,"none");
            preparedStatement1.setString(6,"none");
            preparedStatement1.executeUpdate();

            PreparedStatement preparedStatement2 = connect.prepareStatement("insert into checking_accounts " +
                    "(cust_username, checking_account_number, checking_account_balance, status) values (?, ?, ?, ?)");
            preparedStatement2.setString(1,username);
            preparedStatement2.setLong(2,0);
            preparedStatement2.setDouble(3,0);
            preparedStatement2.setString(4,"none");
            preparedStatement2.executeUpdate();

            PreparedStatement preparedStatement3 = connect.prepareStatement("insert into savings_accounts " +
                    "(cust_username, savings_account_number, savings_account_balance, status) values (?, ?, ?, ?)");
            preparedStatement3.setString(1,username);
            preparedStatement3.setLong(2,0);
            preparedStatement3.setDouble(3,0);
            preparedStatement3.setString(4,"none");
            preparedStatement3.executeUpdate();
        }
    }

    @Override
    public void openBankAccount(Customer customer, String accountType) throws SQLException {

        boolean validAccountBalance = false;

        if(accountType.equalsIgnoreCase("checking")){
            validAccountBalance = customer.getChecking().getBalance() > 0;
        } else if(accountType.equalsIgnoreCase("savings")){
            validAccountBalance = customer.getSavings().getBalance() > 0;
        }

        if (!validAccountBalance) {
            System.out.println("Invalid Bank Account. :(");
        } else {
            System.out.println("Valid Bank Account! Awaiting Approval! :)");

            if(accountType.equalsIgnoreCase("checking")) {
                PreparedStatement preparedStatement1 = connect.prepareStatement("update users set " +
                        "checking_account_number = ?, checking_account_status = ? where cust_username = ?");
                preparedStatement1.setLong(1, customer.getChecking().getAccountNumber());
                preparedStatement1.setString(2,"pending");
                preparedStatement1.setString(3, customer.getUserName());
                preparedStatement1.executeUpdate();

                PreparedStatement preparedStatement2 = connect.prepareStatement("update checking_accounts set " +
                        "checking_account_number = ?, checking_account_balance = ?, status = ? where cust_username = ?");
                preparedStatement2.setLong(1,customer.getChecking().getAccountNumber());
                preparedStatement2.setDouble(2,customer.getChecking().getBalance());
                preparedStatement2.setString(3,"pending");
                preparedStatement2.setString(4,customer.getUserName());
                preparedStatement2.executeUpdate();

            } else if (accountType.equalsIgnoreCase("savings")) {
                PreparedStatement preparedStatement1 = connect.prepareStatement("update users set " +
                        "savings_account_number = ?, savings_account_status = ? where cust_username = ?");
                preparedStatement1.setLong(1, customer.getSavings().getAccountNumber());
                preparedStatement1.setString(2,"pending");
                preparedStatement1.setString(3, customer.getUserName());
                preparedStatement1.executeUpdate();

                PreparedStatement preparedStatement2 = connect.prepareStatement("update savings_accounts set " +
                        "savings_account_number = ?, savings_account_balance = ?, status = ? where cust_username = ?");
                preparedStatement2.setLong(1, customer.getSavings().getAccountNumber());
                preparedStatement2.setDouble(2, customer.getSavings().getBalance());
                preparedStatement2.setString(3,"pending");
                preparedStatement2.setString(4, customer.getUserName());
                preparedStatement2.executeUpdate();
            }
        }
    }
}