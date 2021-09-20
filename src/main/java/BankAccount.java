import java.sql.*;
import java.text.DecimalFormat;

abstract class AccountType {

    public Connection connect = ConnectToSQL.connectNow();

    protected double balance;
    protected long accountNumber, routingNumber;

    public abstract void withdraw(double amount, String accountType) throws SQLException;
    public abstract void deposit(double amount, String accountType) throws SQLException;
    public abstract void postTransfer(double amount, long accountNumber, String accountType) throws SQLException;
    public abstract void acceptTransfer(String accepted, double amount, long accountNumber, String accountType) throws SQLException;
    public abstract void changeTransfer(String decision, double amount, long accountNumber, String accountType) throws SQLException;
}

public class BankAccount extends AccountType {

    DecimalFormat df = new DecimalFormat("0.00");

    public BankAccount(long accountNumber, double balance){
        this.balance = balance;
        this.routingNumber = 997000111;
        this.accountNumber = accountNumber;
    }

    @Override
    public void withdraw(double amount, String accountType) throws SQLException {
        if(amount > this.balance | amount <= 0){
            System.out.println("Withdraw Rejected. :(");
        } else {
            this.balance -= amount;
            System.out.println("Withdraw Accepted! :)");
            if(accountType.equalsIgnoreCase("checking")) {
                PreparedStatement preparedStatement1 = connect.prepareStatement("update checking_accounts set " +
                        "checking_account_balance = ? where checking_account_number = ?");
                preparedStatement1.setDouble(1, this.balance);
                preparedStatement1.setLong(2, this.accountNumber);
                preparedStatement1.executeUpdate();

                PreparedStatement preparedStatement2 = connect.prepareStatement("insert into transactions " +
                        "(account_number, type, amount, account_type) values (?, ?, ?, ?)");
                preparedStatement2.setLong(1, this.accountNumber);
                preparedStatement2.setString(2, "withdraw");
                preparedStatement2.setDouble(3, amount);
                preparedStatement2.setString(4, accountType);
                preparedStatement2.executeUpdate();
            } else if(accountType.equalsIgnoreCase("savings")){
                PreparedStatement preparedStatement1 = connect.prepareStatement("update savings_accounts set " +
                        "savings_account_balance = ? where savings_account_number = ?");
                preparedStatement1.setDouble(1, this.balance);
                preparedStatement1.setLong(2, this.accountNumber);
                preparedStatement1.executeUpdate();

                PreparedStatement preparedStatement2 = connect.prepareStatement("insert into transactions " +
                        "(account_number, type, amount, account_type) values (?, ?, ?, ?)");
                preparedStatement2.setLong(1, this.accountNumber);
                preparedStatement2.setString(2, "withdraw");
                preparedStatement2.setDouble(3, amount);
                preparedStatement2.setString(4, accountType);
                preparedStatement2.executeUpdate();
            }
        }
    }

    @Override
    public void deposit(double amount, String accountType) throws SQLException {
        if(amount <= 0){
            System.out.println("Deposit Rejected. :(");
        } else {
            this.balance += amount;
            System.out.println("Deposit Accepted! :)");
            if(accountType.equalsIgnoreCase("checking")) {
                PreparedStatement preparedStatement1 = connect.prepareStatement("update checking_accounts set " +
                        "checking_account_balance = ? where checking_account_number = ?");
                preparedStatement1.setDouble(1, this.balance);
                preparedStatement1.setLong(2, this.accountNumber);
                preparedStatement1.executeUpdate();

                PreparedStatement preparedStatement2 = connect.prepareStatement("insert into transactions " +
                        "(account_number, type, amount, account_type) values (?, ?, ?, ?)");
                preparedStatement2.setLong(1, this.accountNumber);
                preparedStatement2.setString(2, "deposit");
                preparedStatement2.setDouble(3, amount);
                preparedStatement2.setString(4, accountType);
                preparedStatement2.executeUpdate();
            } else if(accountType.equalsIgnoreCase("savings")){
                PreparedStatement preparedStatement1 = connect.prepareStatement("update savings_accounts set " +
                        "savings_account_balance = ? where savings_account_number = ?");
                preparedStatement1.setDouble(1, this.balance);
                preparedStatement1.setLong(2, this.accountNumber);
                preparedStatement1.executeUpdate();

                PreparedStatement preparedStatement2 = connect.prepareStatement("insert into transactions " +
                        "(account_number, type, amount, account_type) values (?, ?, ?, ?)");
                preparedStatement2.setLong(1, this.accountNumber);
                preparedStatement2.setString(2, "deposit");
                preparedStatement2.setDouble(3, amount);
                preparedStatement2.setString(4, accountType);
                preparedStatement2.executeUpdate();
            }
        }
    }

    @Override
    public void postTransfer(double amount, long accountNumber, String accountType) throws SQLException {
        if(amount > this.balance | amount < 0){
            System.out.println("Transfer Rejected. :|");
        } else {
            System.out.println("Posted a Transfer of $" + df.format(amount) + "! :)");
            PreparedStatement preparedStatement1 = connect.prepareStatement("insert into transactions " +
                    "(account_number, type, amount, account_type) values (?, ?, ?, ?)");
            preparedStatement1.setLong(1, accountNumber);
            preparedStatement1.setString(2, "post");
            preparedStatement1.setDouble(3, amount);
            preparedStatement1.setString(4, accountType);
            preparedStatement1.executeUpdate();
        }
    }

    @Override
    public void acceptTransfer(String accepted, double amount, long accountNumber, String accountType) throws SQLException {

        double withdrawBalance = 0;
        double depositBalance = 0;

        if(accepted.equalsIgnoreCase("yes")){
            System.out.println("Accepted a Transfer of $" + df.format(amount) + "! :)");
            if(accountType.equalsIgnoreCase("checking")) {
                Statement statement1 = connect.createStatement();
                ResultSet result1 = statement1.executeQuery("select * from savings_accounts");
                while (result1.next()) {
                    if (result1.getLong(2) == accountNumber) {
                        withdrawBalance = (result1.getDouble(3) - amount);
                    }
                }

                PreparedStatement preparedStatement1 = connect.prepareStatement("update savings_accounts set " +
                        "savings_account_balance = ? where savings_account_number = ?");
                preparedStatement1.setDouble(1, withdrawBalance);
                preparedStatement1.setLong(2, accountNumber);
                preparedStatement1.executeUpdate();



                Statement statement2 = connect.createStatement();
                ResultSet result2 = statement2.executeQuery("select * from checking_accounts");
                while (result2.next()) {
                    if (result2.getLong(2) == this.accountNumber) {
                        depositBalance = (result2.getDouble(3) + amount);
                    }
                }

                PreparedStatement preparedStatement2 = connect.prepareStatement("update checking_accounts set " +
                        "checking_account_balance = ? where checking_account_number = ?");
                preparedStatement2.setDouble(1, depositBalance);
                preparedStatement2.setLong(2, this.accountNumber);
                preparedStatement2.executeUpdate();
            } else if(accountType.equalsIgnoreCase("savings")){
                Statement statement1 = connect.createStatement();
                ResultSet result1 = statement1.executeQuery("select * from checking_accounts");
                while (result1.next()) {
                    if (result1.getLong(2) == accountNumber) {
                        withdrawBalance = (result1.getDouble(3) - amount);
                    }
                }

                PreparedStatement preparedStatement1 = connect.prepareStatement("update checking_accounts set " +
                        "checking_account_balance = ? where checking_account_number = ?");
                preparedStatement1.setDouble(1, withdrawBalance);
                preparedStatement1.setLong(2, accountNumber);
                preparedStatement1.executeUpdate();

                Statement statement2 = connect.createStatement();
                ResultSet result2 = statement2.executeQuery("select * from savings_accounts");
                while (result2.next()) {
                    if (result2.getLong(2) == this.accountNumber) {
                        depositBalance = (result2.getDouble(3) + amount);
                    }
                }

                PreparedStatement preparedStatement2 = connect.prepareStatement("update savings_accounts set " +
                        "savings_account_balance = ? where savings_account_number = ?");
                preparedStatement2.setDouble(1, depositBalance);
                preparedStatement2.setLong(2, this.accountNumber);
                preparedStatement2.executeUpdate();
            }
        } else if(accepted.equalsIgnoreCase("no")){
            System.out.println("Transfer Not Accepted. :(");
        }
    }

    @Override
    public void changeTransfer(String decision, double amount, long accountNumber, String accountType) throws SQLException {
        if(decision.equalsIgnoreCase("accepted")) {
            PreparedStatement preparedStatement1 = connect.prepareStatement("update transactions set " +
                    "type = ? where account_number = ? AND account_type = ? AND amount = ? AND type = ?");
            preparedStatement1.setString(1, "accepted");
            preparedStatement1.setLong(2, accountNumber);
            preparedStatement1.setString(3, accountType);
            preparedStatement1.setDouble(4,amount);
            preparedStatement1.setString(5, "post");
            preparedStatement1.executeUpdate();
        } else if(decision.equalsIgnoreCase("rejected")){
            PreparedStatement preparedStatement1 = connect.prepareStatement("update transactions set " +
                    "type = ? where account_number = ? AND account_type = ? AND amount = ? AND type = ?");
            preparedStatement1.setString(1, "rejected");
            preparedStatement1.setLong(2, accountNumber);
            preparedStatement1.setString(3, accountType);
            preparedStatement1.setDouble(4,amount);
            preparedStatement1.setString(5, "post");
            preparedStatement1.executeUpdate();
        }
    }

    public double getBalance(){
        return this.balance;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public long getAccountNumber(){
        return this.accountNumber;
    }

    public void setAccountNumber(long accountNumber){
        this.accountNumber = accountNumber;
    }

    public void getBankActivity(long accountNumber) throws SQLException {
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery("select * from transactions");
        while(result.next()){
            if(result.getLong(1) == accountNumber) {
                System.out.println("Transaction: " + result.getString(2) + "\tAmount: $" + df.format(result.getDouble(3)));
            }
        }
    }
}