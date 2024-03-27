package bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CustomerServlet")
public class CustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action != null) {
            switch (action) {
                case "login":
                    login(request, response);
                    break;
                case "changePassword":
                    changePassword(request, response);
                    break;
                case "viewAccountDetails":
                    viewAccountDetails(request, response);
                    break;
                case "viewTransactions":
                    viewTransactions(request, response);
                    break;
                case "deposit":
                    deposit(request, response);
                    break;
                case "withdraw":
                    withdraw(request, response);
                    break;
                case "closeAccount":
                    closeAccount(request, response);
                    break;
                default:
                    response.sendRedirect("customer_login.jsp"); // Redirect to customer login page if action is not recognized
            }
        } else {
            response.sendRedirect("customer_login.jsp"); // Redirect to customer login page if action is not provided
        }
    }

    // Customer login authentication
    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        String password = request.getParameter("password");

        // Perform authentication
        if (authenticateCustomer(accountNumber, password)) {
            // Customer authenticated, redirect to customer dashboard or other page
            response.sendRedirect("customer_dashboard.jsp");
        } else {
            // Authentication failed, redirect back to login page with error message
            request.setAttribute("error", "Invalid account number or password");
            request.getRequestDispatcher("customer_login.jsp").forward(request, response);
        }
    }

    // Authenticate customer
    private boolean authenticateCustomer(String accountNumber, String password) {
        // Connect to database and query for customer credentials
        try {
        	Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM Customer WHERE AccountNumber=? AND TemporaryPassword=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, accountNumber);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true; // Customer authenticated successfully
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Authentication failed
    }

    // Set up a new password for customer
    private void changePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        String newPassword = request.getParameter("newPassword");

        // Update customer's password in the database
        try {
        	Connection conn = DatabaseConnection.getConnection();
            String query = "UPDATE Customer SET TemporaryPassword=? WHERE AccountNumber=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
            conn.close();
            response.sendRedirect("customer_dashboard.jsp"); // Redirect to customer dashboard after password change
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle password change failure
            request.setAttribute("error", "Failed to change password");
            request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
        }
    }

    // View account details and balance
    private void viewAccountDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");

        // Retrieve account details from the database
        try {
        	Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM Account WHERE AccountNumber=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                request.setAttribute("accountDetails", rs);
                request.getRequestDispatcher("account_details.jsp").forward(request, response);
            } else {
                // Account not found
                request.setAttribute("error", "Account not found");
                request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error
            request.setAttribute("error", "Failed to retrieve account details");
            request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
        }
    }

    // View last 10 transactions
    private void viewTransactions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");

        // Retrieve last 10 transactions from the database
        List<String> transactions = new ArrayList<>();
        try {
        	Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM Transaction WHERE AccountNumber=? ORDER BY TransactionDate DESC LIMIT 10";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String transactionDetails = "Type: " + rs.getString("TransactionType") +
                        ", Amount: " + rs.getDouble("Amount") +
                        ", Date: " + rs.getTimestamp("TransactionDate");
                transactions.add(transactionDetails);
            }
            request.setAttribute("transactions", transactions);
            request.getRequestDispatcher("transactions.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error
            request.setAttribute("error", "Failed to retrieve transactions");
            request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
        }
    }

    // Deposit money into the account
    private void deposit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        double amount = Double.parseDouble(request.getParameter("amount"));

        // Update account balance and record transaction in the database
        try {
        	Connection conn = DatabaseConnection.getConnection();
            // Retrieve current balance
            String getBalanceQuery = "SELECT Balance FROM Account WHERE AccountNumber=?";
            PreparedStatement getBalancePs = conn.prepareStatement(getBalanceQuery);
            getBalancePs.setString(1, accountNumber);
            ResultSet rs = getBalancePs.executeQuery();
            if (rs.next()) {
                double currentBalance = rs.getDouble("Balance");
                // Update balance
                double newBalance = currentBalance + amount;
                String updateBalanceQuery = "UPDATE Account SET Balance=? WHERE AccountNumber=?";
                PreparedStatement updateBalancePs = conn.prepareStatement(updateBalanceQuery);
                updateBalancePs.setDouble(1, newBalance);
                updateBalancePs.setString(2, accountNumber);
                updateBalancePs.executeUpdate();
                // Record transaction
                String insertTransactionQuery = "INSERT INTO Transaction (AccountNumber, TransactionType, Amount) VALUES (?, ?, ?)";
                PreparedStatement insertTransactionPs = conn.prepareStatement(insertTransactionQuery);
                insertTransactionPs.setString(1, accountNumber);
                insertTransactionPs.setString(2, "Deposit");
                insertTransactionPs.setDouble(3, amount);
                insertTransactionPs.executeUpdate();
                conn.close();
                response.sendRedirect("customer_dashboard.jsp"); // Redirect to customer dashboard after deposit
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle deposit failure
            request.setAttribute("error", "Failed to deposit money");
            request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
        }
    }

            // Withdraw money from the account
            private void withdraw(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                String accountNumber = request.getParameter("accountNumber");
                double amount = Double.parseDouble(request.getParameter("amount"));

                // Update account balance and record transaction in the database
                try {
                	Connection conn = DatabaseConnection.getConnection();
                    // Retrieve current balance
                    String getBalanceQuery = "SELECT Balance FROM Account WHERE AccountNumber=?";
                    PreparedStatement getBalancePs = conn.prepareStatement(getBalanceQuery);
                    getBalancePs.setString(1, accountNumber);
                    ResultSet rs = getBalancePs.executeQuery();
                    if (rs.next()) {
                        double currentBalance = rs.getDouble("Balance");
                        // Check if sufficient balance
                        if (currentBalance >= amount) {
                            // Update balance
                            double newBalance = currentBalance - amount;
                            String updateBalanceQuery = "UPDATE Account SET Balance=? WHERE AccountNumber=?";
                            PreparedStatement updateBalancePs = conn.prepareStatement(updateBalanceQuery);
                            updateBalancePs.setDouble(1, newBalance);
                            updateBalancePs.setString(2, accountNumber);
                            updateBalancePs.executeUpdate();
                            // Record transaction
                            String insertTransactionQuery = "INSERT INTO Transaction (AccountNumber, TransactionType, Amount) VALUES (?, ?, ?)";
                            PreparedStatement insertTransactionPs = conn.prepareStatement(insertTransactionQuery);
                            insertTransactionPs.setString(1, accountNumber);
                            insertTransactionPs.setString(2, "Withdrawal");
                            insertTransactionPs.setDouble(3, amount);
                            insertTransactionPs.executeUpdate();
                            conn.close();
                            response.sendRedirect("customer_dashboard.jsp"); // Redirect to customer dashboard after withdrawal
                        } else {
                            // Insufficient balance
                            request.setAttribute("error", "Insufficient balance");
                            request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Handle withdrawal failure
                    request.setAttribute("error", "Failed to withdraw money");
                    request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
                }
            }

            // Close the account
            private void closeAccount(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                String accountNumber = request.getParameter("accountNumber");

                // Check if account balance is 0
                try {
                	Connection conn = DatabaseConnection.getConnection();
                    String getBalanceQuery = "SELECT Balance FROM Account WHERE AccountNumber=?";
                    PreparedStatement getBalancePs = conn.prepareStatement(getBalanceQuery);
                    getBalancePs.setString(1, accountNumber);
                    ResultSet rs = getBalancePs.executeQuery();
                    if (rs.next()) {
                        double balance = rs.getDouble("Balance");
                        if (balance == 0) {
                            // Close the account
                            String deleteCustomerQuery = "DELETE FROM Customer WHERE AccountNumber=?";
                            PreparedStatement deleteCustomerPs = conn.prepareStatement(deleteCustomerQuery);
                            deleteCustomerPs.setString(1, accountNumber);
                            deleteCustomerPs.executeUpdate();
                            // Redirect to customer login page after account closure
                            response.sendRedirect("customer_login.jsp");
                        } else {
                            // Cannot close account with balance greater than 0
                            request.setAttribute("error", "Account balance must be 0 to close the account");
                            request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
                        }
                    } else {
                        // Account not found
                        request.setAttribute("error", "Account not found");
                        request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Handle database error
                    request.setAttribute("error", "Failed to close account");
                    request.getRequestDispatcher("customer_dashboard.jsp").forward(request, response);
                }
            }
        }

