package bank;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {
    

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int account_no = (int) session.getAttribute("account_no"); // Assuming you store customerId in session after login
        
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        try {
            // Create database connection
            try (Connection conn = DatabaseConnection.getConnection()) {
                
                // Verify current password
                String getPasswordQuery = "SELECT password FROM Customer WHERE account_no = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(getPasswordQuery)) {
                    pstmt.setInt(1, account_no);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String storedPassword = rs.getString("password");
                            if (!currentPassword.equals(storedPassword)) {
                                response.sendRedirect("change_password.jsp?error=incorrect");
                                return;
                            }
                        } else {
                            response.sendRedirect("error.jsp");
                            return;
                        }
                    }
                }
                
                // Update password
                String updatePasswordQuery = "UPDATE Customer SET password = ? WHERE account_no = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updatePasswordQuery)) {
                    pstmt.setString(1, newPassword);
                    pstmt.setInt(2, account_no);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        response.sendRedirect("customerlogin.jsp");
                    } else {
                        response.sendRedirect("change_password.jsp?error=update_failed");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
