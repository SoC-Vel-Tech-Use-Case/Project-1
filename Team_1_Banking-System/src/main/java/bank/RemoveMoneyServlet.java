package bank;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RemoveMoneyServlet")
public class RemoveMoneyServlet extends HttpServlet {
    private static final String UPDATE_CUSTOMER_QUERY = "UPDATE customer SET initial_balance = initial_balance - ? WHERE account_no = ?";
    private static final String INSERT_TRANSACTION_QUERY = "INSERT INTO transaction (account_no, transaction_type, amount, transaction_date) VALUES (?, ?, ?, ?)";

    // Load the JDBC driver when the servlet is initialized
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get parameters from the request
        String amountToRemove = request.getParameter("amountToRemove");
        String accountNo = request.getSession().getAttribute("accountNo").toString();

        Connection connection = null;
        PreparedStatement updateStatement = null;
        PreparedStatement insertStatement = null;
        try {
            // Establish database connection
        	connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Update balance in the customer table
            updateStatement = connection.prepareStatement(UPDATE_CUSTOMER_QUERY);
            updateStatement.setDouble(1, Double.parseDouble(amountToRemove));
            updateStatement.setString(2, accountNo);
            updateStatement.executeUpdate();

            // Insert a new transaction record
            insertStatement = connection.prepareStatement(INSERT_TRANSACTION_QUERY);
            insertStatement.setString(1, accountNo);
            insertStatement.setString(2, "Withdrawal"); // Transaction type: Withdrawal
            insertStatement.setDouble(3, Double.parseDouble(amountToRemove));
            insertStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            insertStatement.executeUpdate();

            // Commit the transaction
            connection.commit();

            // Send a success response back to the client
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            // Rollback the transaction if an error occurs
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred while processing the request.");
        } finally {
            // Close resources and reset auto-commit
            try {
                if (updateStatement != null) updateStatement.close();
                if (insertStatement != null) insertStatement.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
