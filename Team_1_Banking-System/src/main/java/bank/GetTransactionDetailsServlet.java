package bank;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/GetTransactionDetailsServlet")
public class GetTransactionDetailsServlet extends HttpServlet {
    private static final String SELECT_TRANSACTION_QUERY = "SELECT * FROM transaction WHERE account_no = ?";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNo = request.getSession().getAttribute("accountNo").toString();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_TRANSACTION_QUERY)) {
            
            selectStatement.setString(1, accountNo);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                List<String> jsonTransactions = new ArrayList<>();
                while (resultSet.next()) {
                    String jsonTransaction = "{"
                            + "\"id\": " + resultSet.getInt("id") + ","
                            + "\"accountNo\": \"" + resultSet.getString("account_no") + "\","
                            + "\"transactionType\": \"" + resultSet.getString("transaction_type") + "\","
                            + "\"amount\": " + resultSet.getDouble("amount") + ","
                            + "\"transactionDate\": \"" + resultSet.getTimestamp("transaction_date") + "\""
                            + "}";
                    jsonTransactions.add(jsonTransaction);
                }
                
                // Construct JSON array and send response
                StringBuilder json = new StringBuilder("[");
                for (String jsonTransaction : jsonTransactions) {
                    json.append(jsonTransaction).append(",");
                }
                if (!jsonTransactions.isEmpty()) {
                    json.deleteCharAt(json.length() - 1); // Remove trailing comma
                }
                json.append("]");
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(json.toString());
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred while fetching transaction details.");
        }
    }
}
