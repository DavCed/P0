import java.sql.*;
import java.text.DecimalFormat;

interface Employee {

    Connection conn = ConnectToSQL.connectNow();
    void approveBankAccount(String username, String accountType) throws SQLException;
    void rejectBankAccount(String username, String accountType) throws SQLException;
    void viewCustomerBalance(Customer customer, String accountType) throws SQLException;
    void viewBankActivity(Customer customer, String accountType) throws SQLException;
    void accessCustomersBalance() throws SQLException;
    void accessCustomersBankActivity() throws SQLException;
}

public class EmployeeActions implements Employee {

    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void approveBankAccount(String username, String accountType) throws SQLException {

        PreparedStatement preparedStatement1;
        PreparedStatement preparedStatement2;

        switch (accountType) {
            case "checking":
                preparedStatement1 = conn.prepareStatement("update users set " +
                        "checking_account_status = ? where cust_username = ?");
                preparedStatement1.setString(1, "approved");
                preparedStatement1.setString(2, username);
                preparedStatement1.executeUpdate();

                preparedStatement2 = conn.prepareStatement("update checking_accounts set " +
                        "status = ? where cust_username = ?");
                preparedStatement2.setString(1, "approved");
                preparedStatement2.setString(2, username);
                preparedStatement2.executeUpdate();
                break;
            case "savings":
                preparedStatement1 = conn.prepareStatement("update users set " +
                        "savings_account_status = ? where cust_username = ?");
                preparedStatement1.setString(1, "approved");
                preparedStatement1.setString(2, username);
                preparedStatement1.executeUpdate();

                preparedStatement2 = conn.prepareStatement("update savings_accounts set " +
                        "status = ? where cust_username = ?");
                preparedStatement2.setString(1, "approved");
                preparedStatement2.setString(2, username);
                preparedStatement2.executeUpdate();
                break;
            }
    }

    @Override
    public void rejectBankAccount(String username, String accountType) throws SQLException {

        PreparedStatement preparedStatement1;
        PreparedStatement preparedStatement2;

        switch (accountType) {
            case "checking":
                preparedStatement1 = conn.prepareStatement("update users set " +
                        "checking_account_number = ?, checking_account_status = ? where cust_username = ?");
                preparedStatement1.setLong(1, 0);
                preparedStatement1.setString(2, "none");
                preparedStatement1.setString(3, username);
                preparedStatement1.executeUpdate();

                preparedStatement2 = conn.prepareStatement("update checking_accounts set " +
                        "checking_account_number = ?, checking_account_balance = ?, status = ? where cust_username = ?");
                preparedStatement2.setDouble(1,0);
                preparedStatement2.setDouble(2,0);
                preparedStatement2.setString(3, "none");
                preparedStatement2.setString(4, username);
                preparedStatement2.executeUpdate();
                break;
            case "savings":
                preparedStatement1 = conn.prepareStatement("update users set " +
                        "savings_account_number = ?, savings_account_status = ? where cust_username = ?");
                preparedStatement1.setLong(1,0);
                preparedStatement1.setString(2, "none");
                preparedStatement1.setString(3, username);
                preparedStatement1.executeUpdate();

                preparedStatement2 = conn.prepareStatement("update savings_accounts set " +
                        "savings_account_number = ?, savings_account_balance = ?, status = ? where cust_username = ?");
                preparedStatement2.setLong(1,0);
                preparedStatement2.setDouble(2,0);
                preparedStatement2.setString(3, "none");
                preparedStatement2.setString(4, username);
                preparedStatement2.executeUpdate();
                break;
        }
    }

    @Override
    public synchronized void viewCustomerBalance(Customer customer, String accountType) throws SQLException {

        System.out.println("Employee Doing Daily Balance Check-Ups for Unusual Activity...");

        String checkingStatus = LoadDatabase.loadCheckingStatus(customer.getUserName());
        String savingsStatus = LoadDatabase.loadSavingsStatus(customer.getUserName());

        switch (accountType) {
            case "checking":
                if(checkingStatus.equalsIgnoreCase("approved")) {
                    System.out.println("Employee is Viewing " + customer.getUserName()
                            + " Checking Account Balance of $" + df.format(customer.getChecking().getBalance()));
                } else {
                    System.out.println("Employee Notices " + customer.getUserName() + " Does Not Have a Checking Account.");
                }
                break;
            case "savings":
                if (savingsStatus.equalsIgnoreCase("approved")) {
                    System.out.println("Employee is Viewing " + customer.getUserName()
                            + " Savings Account Balance of $" + df.format(customer.getSavings().getBalance()));
                } else {
                    System.out.println("Employee Notices " + customer.getUserName() + " Does Not Have a Savings Account.");
                }
                break;
            default:
                if (checkingStatus.equalsIgnoreCase("approved")) {
                    System.out.println("Employee is Viewing " + customer.getUserName()
                            + " Checking Account Balance of $" + df.format(customer.getChecking().getBalance()));
                } else {
                    System.out.println("Employee Notices " + customer.getUserName() + " Does Not Have a Checking Account.");
                }

                if (savingsStatus.equalsIgnoreCase("approved")) {
                    System.out.println("Employee is Viewing " + customer.getUserName()
                            + " Savings Account Balance of $" + df.format(customer.getSavings().getBalance()));
                } else {
                    System.out.println("Employee Notices " + customer.getUserName() + " Does Not Have a Savings Account.");
                }
                break;
        }
    }

    @Override
    public synchronized void viewBankActivity(Customer customer, String accountType) throws SQLException {

        System.out.println("\nEmployee is Viewing " + customer.getUserName() + " Bank Activity...");

        if(accountType.equalsIgnoreCase("checking") & customer.getChecking().getAccountNumber() != 0) {
            customer.getChecking().getBankActivity(customer.getChecking().getAccountNumber());
        } else if(accountType.equalsIgnoreCase("savings") & customer.getSavings().getAccountNumber() != 0) {
            customer.getSavings().getBankActivity(customer.getSavings().getAccountNumber());
        } else {
            System.out.println("No Bank Activity. :(");
        }
    }

    @Override
    public void accessCustomersBalance() throws SQLException {
        System.out.println("\nCHECKING ACCOUNTS:");
        Statement statement1 = conn.createStatement();
        ResultSet result1 = statement1.executeQuery("select * from checking_accounts");
        while (result1.next()) {
            if (result1.getLong(2) > 0 & result1.getDouble(3) > 0
                    & result1.getString(4).equalsIgnoreCase("approved")) {
                System.out.println("Customer Username: " + result1.getString(1)
                        + "\tChecking Account Number: " + result1.getLong(2)
                        + "\tBalance: $" + df.format(result1.getDouble(3)));
            }
        }

        System.out.println("\nSAVINGS ACCOUNTS:");
        Statement statement2 = conn.createStatement();
        ResultSet result2 = statement2.executeQuery("select * from savings_accounts");
        while (result2.next()) {
            if (result2.getLong(2) > 0 & result2.getDouble(3) > 0
                    & result2.getString(4).equalsIgnoreCase("approved")) {
                System.out.println("Customer Username: " + result2.getString(1)
                        + "\tSavings Account Number: " + result2.getLong(2)
                        + "\tBalance: $" + df.format(result2.getDouble(3)));
            }
        }
    }

    @Override
    public void accessCustomersBankActivity() throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery("select * from transactions");
        System.out.println("\nACTIVITY:");
        while (result.next()) {
            System.out.println("Account Type: " + result.getString(4)
                    + "\tAccount Number: " + result.getLong(1)
                    + "\tTransaction: " + result.getString(2) +
                    "\tAmount: $" + df.format(result.getDouble(3)));
        }
    }
}