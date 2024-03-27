<%
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache"); 
response.setHeader ("Expires", "0"); //prevents caching at the proxy server
%>

<%@ page import="java.sql.*" %>
<%@ page import="bank.DatabaseConnection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String account_no = request.getParameter("account_no");
    String fullName = request.getParameter("full_name");
    String address = request.getParameter("address");
    String mobileNo = request.getParameter("mobile_no");
    String emailId = request.getParameter("email");
    String accountType = request.getParameter("account_type");
    String dob = request.getParameter("dob");
    String idProof = request.getParameter("id_proof");

    try {
    	Connection conn = DatabaseConnection.getConnection();
        String query = "UPDATE customer SET full_name=?, address=?, mobile_no=?, email=?, account_type=?, dob=?, id_proof=? WHERE account_no=?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, fullName);
        pstmt.setString(2, address);
        pstmt.setString(3, mobileNo);
        pstmt.setString(4, emailId);
        pstmt.setString(5, accountType);
        pstmt.setString(6, dob);
        pstmt.setString(7, idProof);
        pstmt.setString(8, account_no);

        int updated = pstmt.executeUpdate();
        
        if (updated > 0) {
            response.sendRedirect("admin_dashboard.jsp?status=updateSuccess");
        } else {
            out.println("<h2>Unable to update customer details. Please try again.</h2>");
        }
        
        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
        out.println("<h2>Error in updating the customer details:</h2> " + e.getMessage());
    }
%>
