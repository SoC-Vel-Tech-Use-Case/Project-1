package bank;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/CustomerRegistrationServlet")
public class CustomerRegistrationServlet extends HttpServlet {
    

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String mobileNo = request.getParameter("mobileNo");
        String email = request.getParameter("email");
        String accountType = request.getParameter("accountType");
        double initialBalance = Double.parseDouble(request.getParameter("initialBalance"));
        Date dob = Date.valueOf(request.getParameter("dob"));
        String idProof = request.getParameter("idProof");
        
        // Generate account number and temporary password
        int accountNo = generateAccountNumber();
        String temporaryPassword = generateTemporaryPassword();
        
        try {
            // Create database connection
        	Connection conn = DatabaseConnection.getConnection();
            
            // Insert customer details into the database
            String insertQuery = "INSERT INTO Customer (full_name, address, mobile_no, email, account_type, initial_balance, dob, id_proof, account_no, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, fullName);
            pstmt.setString(2, address);
            pstmt.setString(3, mobileNo);
            pstmt.setString(4, email);
            pstmt.setString(5, accountType);
            pstmt.setDouble(6, initialBalance);
            pstmt.setDate(7, dob);
            pstmt.setString(8, idProof);
            pstmt.setInt(9, accountNo);
            pstmt.setString(10, temporaryPassword);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Insert initial transaction record
                insertInitialTransaction(conn, accountNo, initialBalance);
                
                // Store account number in session
                HttpSession session = request.getSession();
                session.setAttribute("account_no", accountNo);
                
                // Redirect to registration success page
                response.sendRedirect("registration_success.jsp?accountNo=" + accountNo + "&password=" + temporaryPassword);
            } else {
                // Redirect to registration error page
                response.sendRedirect("registration_error.jsp");
            }
            
            // Close database connection
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Redirect to error page
            response.sendRedirect("error.jsp");
        }
    }
    
    private void insertInitialTransaction(Connection conn, int accountNo, double initialBalance) throws SQLException {
        // Insert initial transaction record for account creation
        String initialTransactionQuery = "INSERT INTO transaction (account_no, transaction_type, amount, transaction_date) VALUES (?, ?, ?, ?)";
        PreparedStatement initialTransactionStmt = conn.prepareStatement(initialTransactionQuery);
        initialTransactionStmt.setInt(1, accountNo);
        initialTransactionStmt.setString(2, "Deposit"); // Assuming the creation of an account is considered a deposit
        initialTransactionStmt.setDouble(3, initialBalance); // Initial balance
        initialTransactionStmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Current timestamp
        initialTransactionStmt.executeUpdate();
        initialTransactionStmt.close();
    }
    
    private int generateAccountNumber() {
        // Generate random account number
        return (int) (Math.random() * 900000) + 100000; // Generates a 6-digit number
    }
    
    private String generateTemporaryPassword() {
        // Generate random temporary password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
