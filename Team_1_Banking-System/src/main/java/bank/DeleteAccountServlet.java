package bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/deleteAccount")
public class DeleteAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNo = request.getParameter("accountNo");

        if (accountNo == null || accountNo.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Account number is required.");
            return;
        }

        HttpSession session = request.getSession(false); // Get the session without creating a new one
        if (session != null) {
            session.invalidate(); // Invalidate the current session
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement balanceStmt = con.prepareStatement("SELECT initial_balance FROM customer WHERE account_no = ?");
            balanceStmt.setString(1, accountNo);
            ResultSet balanceResult = balanceStmt.executeQuery();

            if (balanceResult.next()) {
                double balance = balanceResult.getDouble("initial_balance");
                if (balance == 0) {
                    PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM customer WHERE account_no = ?");
                    deleteStmt.setString(1, accountNo);
                    int rowsAffected = deleteStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().println("Failed to delete account.");
                    }
                    deleteStmt.close();
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().println("Cannot delete account. Account balance is not zero.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("Account not found.");
            }
        } catch (SQLException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Error occurred while processing the request.");
        }
    }
}
