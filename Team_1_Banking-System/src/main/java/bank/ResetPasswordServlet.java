package bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {

    private static final String UPDATE_PASSWORD_QUERY = "UPDATE customer SET password = ? WHERE account_no = ?";

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
        String newPassword = request.getParameter("newPassword");
        String accountNo = request.getSession().getAttribute("accountNo").toString();

        Connection connection = null;
        PreparedStatement updateStatement = null;
        try {
            // Establish database connection
        	connection = DatabaseConnection.getConnection();

            // Update password in the customer table
            updateStatement = connection.prepareStatement(UPDATE_PASSWORD_QUERY);
            updateStatement.setString(1, newPassword);
            updateStatement.setString(2, accountNo);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Password reset successful
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Password reset failed
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to reset password.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred while processing the request.");
        } finally {
            // Close resources
            try {
                if (updateStatement != null) updateStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
