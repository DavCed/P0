import org.apache.log4j.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

public class Driver {

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(Driver.class);
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setThreshold(Level.INFO);
        consoleAppender.setLayout(new EnhancedPatternLayout("<- [%p] %m - %d [%p] ->"));
        consoleAppender.activateOptions();
        LogManager.getRootLogger().addAppender(consoleAppender);
        logger.info("RUNNING APPLICATION");
        System.out.println();
        try (Scanner scan = new Scanner(System.in)) {
            int opt1;
            int opt2;
            int opt3;
            int opt4;
            User user = new Customer();
            Employee employee = new EmployeeActions();
            Customer customer;
            double amount;
            int transaction;
            String accept;
            String username;
            String password;
            String passcode;
            String response;
            Random rand = new Random();
            BankAccount checking;
            BankAccount savings;
            DecimalFormat df = new DecimalFormat("0.00");
            long newAccountNumber;
            long accountNumber;
            long checkingNumber;
            long savingsNumber;
            double checkingBal;
            double savingsBal;
            double startBal;
            String accountType;
            List<Float> allTransactions;
            List<String[]> allPendingCheckingAccounts;
            List<String[]> allPendingSavingsAccounts;
            List<String> allUsernames;
            System.out.println("Welcome to the Bank Application! ;)\n");
            System.out.println("Select an Option:\n1 - Customer Portal\n2 - Employee Portal\n3 - Register a Customer Account\n4 - Exit Application");
            System.out.print("Option: ");
            opt1 = scan.nextInt();
            while (opt1 != 4) {
                switch (opt1) {
                    case 1:
                        System.out.print("\nUsername: ");
                        username = scan.next();
                        System.out.print("Password: ");
                        password = scan.next();
                        if (user.verifyUser(username, password)) {
                            customer = new Customer(username, password);
                            checkingNumber = LoadDatabase.loadCheckingAccountNumber(username);
                            savingsNumber = LoadDatabase.loadSavingsAccountNumber(username);
                            checkingBal = LoadDatabase.loadCheckingBalance(username);
                            savingsBal = LoadDatabase.loadSavingsBalance(username);
                            checking = new BankAccount(checkingNumber, checkingBal);
                            savings = new BankAccount(savingsNumber, savingsBal);
                            customer.setChecking(checking);
                            customer.setSavings(savings);
                            System.out.println("-----------------------------------------------------------------------");
                            employee.viewCustomerBalance(customer, "both");
                            System.out.println("\nSelect an Option:\n1 - Access Checking Account\n" +
                                    "2 - Access Savings Account\n3 - Open Bank Account\n4 - Log Out");
                            System.out.print("Option: ");
                            opt2 = scan.nextInt();
                            while (opt2 != 4) {
                                switch (opt2) {
                                    case 1:
                                        if (customer.getChecking().getAccountNumber() == 0
                                                & LoadDatabase.loadCheckingStatus(customer.getUserName()).equalsIgnoreCase("none")) {
                                            System.out.println("\n" + username
                                                    + " Does Not Have a Checking Account Open. :(");
                                        } else if(customer.getChecking().getAccountNumber() > 0
                                                & LoadDatabase.loadCheckingStatus(customer.getUserName()).equalsIgnoreCase("pending")){
                                            System.out.println("\n" + username + " Checking Account is Awaiting Approval. :|");
                                        } else if(customer.getChecking().getAccountNumber() > 0 & customer.getChecking().getBalance() > 0
                                                & LoadDatabase.loadCheckingStatus(customer.getUserName()).equalsIgnoreCase("approved")) {
                                            System.out.println("-----------------------------------------------------------------------");
                                            System.out.println("CHECKING ACCOUNT "
                                                    + customer.getChecking().getAccountNumber() + "\n");
                                            System.out.println("Select an Option:\n" +
                                                    "1 - View Balance\n" +
                                                    "2 - Make a Transaction\n" +
                                                    "3 - Post a Money Transfer To Savings Account\n" +
                                                    "4 - Accept Money Transfers From Savings Account\n" +
                                                    "5 - Go Back");
                                            System.out.print("Option: ");
                                            opt3 = scan.nextInt();
                                            while (opt3 != 5) {
                                                switch (opt3) {
                                                    case 1:
                                                        System.out.println("\nCurrent Balance: $"
                                                                + df.format(customer.getChecking().getBalance()));
                                                        break;
                                                    case 2:
                                                        System.out.println("\nChoose a Transaction:\n1 - Withdraw Money\n" +
                                                                "2 - Deposit Money");
                                                        System.out.print("Option: ");
                                                        transaction = scan.nextInt();
                                                        if (transaction == 1) {
                                                            System.out.print("\nEnter an Amount to Withdraw: $");
                                                            amount = scan.nextDouble();
                                                            customer.getChecking().withdraw(amount, "checking");
                                                            System.out.println();
                                                            employee.viewCustomerBalance(customer, "checking");
                                                        } else if (transaction == 2) {
                                                            System.out.print("\nEnter an Amount to Deposit: $");
                                                            amount = scan.nextDouble();
                                                            customer.getChecking().deposit(amount, "checking");
                                                            System.out.println();
                                                            employee.viewCustomerBalance(customer, "checking");
                                                        } else {
                                                            System.out.println("\nInvalid Transaction. :| ");
                                                        }
                                                        break;
                                                    case 3:
                                                        if (customer.getSavings().getAccountNumber() == 0) {
                                                            System.out.println("\n" + username
                                                                    + " Does Not Have a Savings Account Open. :(");
                                                        } else {
                                                            System.out.print("\nEnter Amount to Transfer " +
                                                                    "to Savings Account: $");
                                                            amount = scan.nextDouble();
                                                            accountNumber = customer.getSavings().getAccountNumber();
                                                            customer.getChecking().postTransfer(amount, accountNumber, "savings");
                                                            employee.viewBankActivity(customer, "checking");
                                                        }
                                                        break;
                                                    case 4:
                                                        allTransactions = LoadDatabase.loadTransactions(
                                                                customer.getChecking().getAccountNumber());
                                                        if (allTransactions.size() > 0) {
                                                            for (int i = 0; i <= allTransactions.size() - 1; i++) {
                                                                System.out.print("\nEnter Yes To Accept Amount of $"
                                                                        + df.format(allTransactions.get(i)) + ": ");
                                                                accept = scan.next();
                                                                accountNumber = customer.getSavings().getAccountNumber();
                                                                customer.getChecking().acceptTransfer(accept,
                                                                        allTransactions.get(i), accountNumber, "checking");
                                                                if (accept.equalsIgnoreCase("yes")) {
                                                                    customer.getSavings().setBalance(
                                                                            customer.getSavings().getBalance() - allTransactions.get(i));
                                                                    customer.getChecking().setBalance(
                                                                            customer.getChecking().getBalance() + allTransactions.get(i));
                                                                    customer.getChecking().changeTransfer("accepted", allTransactions.get(i),
                                                                            customer.getChecking().getAccountNumber(), "checking");
                                                                } else if (accept.equalsIgnoreCase("no")) {
                                                                    customer.getChecking().changeTransfer("rejected", allTransactions.get(i),
                                                                            customer.getChecking().getAccountNumber(), "checking");
                                                                } else {
                                                                    System.out.println("Invalid. :|");
                                                                }
                                                            }
                                                            employee.viewBankActivity(customer, "checking");
                                                        } else {
                                                            System.out.println("\nNo Post Transfers to Checking Account. :(");
                                                        }
                                                        break;
                                                    default:
                                                        System.out.println("\nTry Again. :|");
                                                        break;
                                                }
                                                System.out.println("-----------------------------------------------------------------------");
                                                System.out.println("CHECKING ACCOUNT "
                                                        + customer.getChecking().getAccountNumber() + "\n");
                                                System.out.println("Select an Option:\n" +
                                                        "1 - View Balance\n" +
                                                        "2 - Make a Transaction\n" +
                                                        "3 - Post a Money Transfer To Savings Account\n" +
                                                        "4 - Accept Money Transfers From Savings Account\n" +
                                                        "5 - Go Back");
                                                System.out.print("Option: ");
                                                opt3 = scan.nextInt();
                                            }
                                        }
                                        break;
                                    case 2:
                                        if (customer.getSavings().getAccountNumber() == 0
                                                & LoadDatabase.loadSavingsStatus(customer.getUserName()).equalsIgnoreCase("none")) {
                                            System.out.println("\n" + username
                                                    + " Does Not Have a Savings Account Open. :(");
                                        } else if(customer.getSavings().getAccountNumber() > 0
                                                & LoadDatabase.loadSavingsStatus(customer.getUserName()).equalsIgnoreCase("pending")){
                                            System.out.println("\n" + username + " Savings Account is Awaiting Approval. :|");
                                        } else if(customer.getSavings().getAccountNumber() > 0 & customer.getSavings().getBalance() > 0
                                                & LoadDatabase.loadSavingsStatus(customer.getUserName()).equalsIgnoreCase("approved")) {
                                            System.out.println("-----------------------------------------------------------------------");
                                            System.out.println("SAVINGS ACCOUNT "
                                                    + customer.getSavings().getAccountNumber() + "\n");
                                            System.out.println("Select an Option:\n" +
                                                    "1 - View Balance\n" +
                                                    "2 - Make a Transaction\n" +
                                                    "3 - Post a Money Transfer To Checking Account\n" +
                                                    "4 - Accept Money Transfers From Checking Account\n" +
                                                    "5 - Go Back");
                                            System.out.print("Option: ");
                                            opt3 = scan.nextInt();
                                            while (opt3 != 5) {
                                                switch (opt3) {
                                                    case 1:
                                                        System.out.println("\nCurrent Balance: $"
                                                                + df.format(customer.getSavings().getBalance()));
                                                        break;
                                                    case 2:
                                                        System.out.println("\nChoose a Transaction:\n1 - Withdraw Money\n" +
                                                                "2 - Deposit Money");
                                                        System.out.print("Option: ");
                                                        transaction = scan.nextInt();
                                                        if (transaction == 1) {
                                                            System.out.print("\nEnter an Amount to Withdraw: $");
                                                            amount = scan.nextDouble();
                                                            customer.getSavings().withdraw(amount, "savings");
                                                            System.out.println();
                                                            employee.viewCustomerBalance(customer, "savings");
                                                        } else if (transaction == 2) {
                                                            System.out.print("\nEnter an Amount to Deposit: $");
                                                            amount = scan.nextDouble();
                                                            customer.getSavings().deposit(amount, "savings");
                                                            System.out.println();
                                                            employee.viewCustomerBalance(customer, "savings");
                                                        } else {
                                                            System.out.println("\nInvalid Transaction. :| ");
                                                        }
                                                        break;
                                                    case 3:
                                                        if (customer.getChecking().getAccountNumber() == 0) {
                                                            System.out.println("\n" + username
                                                                    + " Does Not Have a Checking Account Open. :(");
                                                        } else {
                                                            System.out.print("\nEnter Amount to Transfer " +
                                                                    "to Checking Account: $");
                                                            amount = scan.nextDouble();
                                                            accountNumber = customer.getChecking().getAccountNumber();
                                                            customer.getSavings().postTransfer(amount, accountNumber, "checking");
                                                            employee.viewBankActivity(customer, "savings");
                                                        }
                                                        break;
                                                    case 4:
                                                        allTransactions = LoadDatabase.loadTransactions(
                                                                customer.getSavings().getAccountNumber());
                                                        if (allTransactions.size() > 0) {
                                                            for (int i = 0; i <= allTransactions.size() - 1; i++) {
                                                                System.out.print("\nEnter Yes To Accept Amount of $"
                                                                        + df.format(allTransactions.get(i)) + ": ");
                                                                accept = scan.next();
                                                                accountNumber = customer.getChecking().getAccountNumber();
                                                                customer.getSavings().acceptTransfer(accept,
                                                                        allTransactions.get(i), accountNumber, "savings");
                                                                if (accept.equalsIgnoreCase("yes")) {
                                                                    customer.getChecking().setBalance(
                                                                            customer.getChecking().getBalance() - allTransactions.get(i));
                                                                    customer.getSavings().setBalance(
                                                                            customer.getSavings().getBalance() + allTransactions.get(i));
                                                                    customer.getSavings().changeTransfer("accepted", allTransactions.get(i),
                                                                            customer.getSavings().getAccountNumber(), "savings");
                                                                } else if (accept.equalsIgnoreCase("no")) {
                                                                    customer.getSavings().changeTransfer("rejected", allTransactions.get(i),
                                                                            customer.getSavings().getAccountNumber(), "savings");
                                                                } else {
                                                                    System.out.println("Invalid. :|");
                                                                }
                                                            }
                                                            employee.viewBankActivity(customer, "savings");
                                                        } else {
                                                            System.out.println("\nNo Post Transfers to Savings Account. :(");
                                                        }
                                                        break;
                                                    default:
                                                        System.out.println("\nTry Again. :|");
                                                        break;
                                                }
                                                System.out.println("-----------------------------------------------------------------------");
                                                System.out.println("SAVINGS ACCOUNT "
                                                        + customer.getSavings().getAccountNumber() + "\n");
                                                System.out.println("Select an Option:\n" +
                                                        "1 - View Balance\n" +
                                                        "2 - Make a Transaction\n" +
                                                        "3 - Post a Money Transfer To Checking Account\n" +
                                                        "4 - Accept Money Transfers From Checking Account\n" +
                                                        "5 - Go Back");
                                                System.out.print("Option: ");
                                                opt3 = scan.nextInt();
                                            }
                                        }
                                        break;
                                    case 3:
                                        System.out.print("\nEnter Checking or Savings to open account: ");
                                        accountType = scan.next();
                                        System.out.print("Enter a Starting Balance: $");
                                        startBal = scan.nextDouble();
                                        if(startBal < 0){
                                            System.out.println("\nInvalid. Negative Balance. :|");
                                        } else if(startBal == 0) {
                                            System.out.println("\nInvalid. Zero Balance. :|");
                                        } else {
                                            if (customer.getChecking().getAccountNumber() == 0
                                                    & accountType.equalsIgnoreCase("checking")) {
                                                newAccountNumber = rand.nextInt(299999999) + 211111111;
                                                customer.getChecking().setAccountNumber(newAccountNumber);
                                                customer.getChecking().setBalance(startBal);
                                                user.openBankAccount(customer, "checking");
                                            } else if (customer.getSavings().getAccountNumber() == 0
                                                    & accountType.equalsIgnoreCase("savings")) {
                                                newAccountNumber = rand.nextInt(299999999) + 211111111;
                                                customer.getSavings().setAccountNumber(newAccountNumber);
                                                customer.getSavings().setBalance(startBal);
                                                user.openBankAccount(customer, "savings");
                                            } else if (!(accountType.equalsIgnoreCase("checking") |
                                                    accountType.equalsIgnoreCase("savings"))) {
                                                System.out.println("\n" + accountType + " Does Not Exist Here. :|");
                                            } else {
                                                System.out.println("\n" + username + " Already Has a " + accountType
                                                        + " Account. :|");
                                            }
                                        }
                                        break;
                                    default:
                                        System.out.println("\nTry Again. :|");
                                        break;
                                }
                                System.out.println("-----------------------------------------------------------------------");
                                System.out.println("Select an Option:\n1 - Access Checking Account\n" +
                                        "2 - Access Savings Account\n3 - Open Bank Account\n4 - Log Out");
                                System.out.print("Option: ");
                                opt2 = scan.nextInt();
                            }
                        } else {
                            System.out.println("\n" + username + " Not Found in Database. :(");
                        }
                        break;
                    case 2:
                        System.out.print("\nEmployee Passcode: ");
                        passcode = scan.next();
                        if(passcode.equalsIgnoreCase("0987654321")){
                            System.out.println("-----------------------------------------------------------------------");
                            logger.info("DATABASE CONNECTED (BANK_APPLICATION)");
                            System.out.println("\nSelect an Option:\n1 - View Customers Balances" +
                                    "\n2 - View Customers Bank Activity\n3 - Approve Pending Checking Accounts" +
                                    "\n4 - Approve Pending Savings Accounts\n5 - Exit Portal");
                            System.out.print("Option: ");
                            opt4 = scan.nextInt();
                            while(opt4 != 5){
                                switch(opt4){
                                    case 1:
                                        employee.accessCustomersBalance();
                                        break;
                                    case 2:
                                        employee.accessCustomersBankActivity();
                                        break;
                                    case 3:
                                        System.out.println("\nCHECKING ACCOUNTS:");
                                        allPendingCheckingAccounts = LoadDatabase.loadPendingCheckingAccounts();
                                        allUsernames = LoadDatabase.loadUsernames();
                                        username = "";
                                        if(allPendingCheckingAccounts.size() > 0) {
                                            for (int k = 0; k <= allPendingCheckingAccounts.size() - 1; k++) {
                                                for (int m = 0; m <= allPendingCheckingAccounts.get(k).length - 1; m++) {
                                                    for (int n = 0; n <= allUsernames.size() - 1; n++) {
                                                        if (allPendingCheckingAccounts.get(k)[m].equalsIgnoreCase(
                                                                allUsernames.get(n))) {
                                                            username = allUsernames.get(n);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            for (int p = 0; p <= allPendingCheckingAccounts.size() - 1; p++) {
                                                float balance = Float.parseFloat(allPendingCheckingAccounts.get(p)[2]);
                                                System.out.println("Customer Username: "
                                                        + allPendingCheckingAccounts.get(p)[0]
                                                        + "\tAccount Number: " + allPendingCheckingAccounts.get(p)[1]
                                                        + "\tAccount Balance: $" + df.format(balance)
                                                        + "\tStatus: " + allPendingCheckingAccounts.get(p)[3]);
                                                System.out.print("Enter YES to Approve or NO to Decline: ");
                                                response = scan.next().toLowerCase();
                                                switch (response) {
                                                    case "yes":
                                                        employee.approveBankAccount(username, "checking");
                                                        break;
                                                    case "no":
                                                        employee.rejectBankAccount(username, "checking");
                                                        break;
                                                    default:
                                                        System.out.println("Invalid. :|");
                                                        break;
                                                }
                                            }
                                        } else {
                                            System.out.println("No Pending Checking Accounts. :(");
                                        }
                                        break;
                                    case 4:
                                        System.out.println("\nSAVINGS ACCOUNTS:");
                                        allPendingSavingsAccounts = LoadDatabase.loadPendingSavingsAccounts();
                                        allUsernames = LoadDatabase.loadUsernames();
                                        username = "";
                                        if(allPendingSavingsAccounts.size() > 0) {
                                            for (int k = 0; k <= allPendingSavingsAccounts.size() - 1; k++) {
                                                for (int m = 0; m <= allPendingSavingsAccounts.get(k).length - 1; m++) {
                                                    for (int n = 0; n <= allUsernames.size() - 1; n++) {
                                                        if (allPendingSavingsAccounts.get(k)[m].equalsIgnoreCase(
                                                                allUsernames.get(n))) {
                                                            username = allUsernames.get(n);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            for (int p = 0; p <= allPendingSavingsAccounts.size() - 1; p++) {
                                                float balance = Float.parseFloat(allPendingSavingsAccounts.get(p)[2]);
                                                System.out.println("Customer Username: " + allPendingSavingsAccounts.get(p)[0]
                                                        + "\tAccount Number: " + allPendingSavingsAccounts.get(p)[1]
                                                        + "\tAccount Balance: $" + df.format(balance)
                                                        + "\tStatus: " + allPendingSavingsAccounts.get(p)[3]);
                                                System.out.print("Enter Yes to approve or No to Decline: ");
                                                response = scan.next().toLowerCase();
                                                switch (response) {
                                                    case "yes":
                                                        employee.approveBankAccount(username, "savings");
                                                        break;
                                                    case "no":
                                                        employee.rejectBankAccount(username, "savings");
                                                        break;
                                                    default:
                                                        System.out.println("Invalid. :|");
                                                        break;
                                                }
                                            }
                                        } else {
                                            System.out.println("No Pending Savings Accounts. :(");
                                        }
                                        break;
                                    default:
                                        System.out.println("Try Again. :|");
                                        break;
                                }
                                System.out.println("-----------------------------------------------------------------------");
                                System.out.println("Select an Option:\n1 - View Customers Balances" +
                                        "\n2 - View Customers Bank Activity\n3 - Approve Pending Checking Accounts" +
                                        "\n4 - Approve Pending Savings Accounts\n5 - Exit Portal");
                                System.out.print("Option: ");
                                opt4 = scan.nextInt();
                            }
                        } else {
                            System.out.println("Wrong Passcode. :|\n");
                            logger.warn("INTRUDER ALERT");
                            System.out.println();
                        }
                        break;
                    case 3:
                        System.out.print("\nEnter Username: ");
                        username = scan.next();
                        System.out.print("Enter Password: ");
                        password = scan.next();
                        if (!user.verifyDuplicateUsername(username)) {
                            user.registerAccount(username, password);
                        } else {
                            System.out.println("\n" + username + " Already Exists in Database. :|");
                        }
                        break;
                    default:
                        System.out.println("\nTry Again. :|");
                        break;
                }
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("Select an Option:\n1 - Customer Portal\n2 - Employee Portal\n3 - Register a Customer Account\n4 - Exit Application");
                System.out.print("Option: ");
                opt1 = scan.nextInt();
            }
            System.out.println();
            logger.info("EXITING APPLICATION");
        } catch (InputMismatchException | SQLException e) {
            System.out.println();
            logger.error("APPLICATION CRASHED");
        }
    }
}