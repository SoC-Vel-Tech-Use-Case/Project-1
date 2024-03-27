<%@ page import="java.sql.*" %>
<%@ page import="bank.DatabaseConnection" %> 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache"); 
response.setHeader ("Expires", "0"); //prevents caching at the proxy server
%>
<%
    String account_no = request.getParameter("account_no");
    try {
    	Connection con = DatabaseConnection.getConnection();
        String query = "DELETE FROM customer WHERE account_no = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, account_no);
        int result = pstmt.executeUpdate();
        if(result > 0) {
            response.sendRedirect("admin_dashboard.jsp?status=deleteSuccess");
        } else {
            out.println("Error in deleting the customer.");
        }
        con.close();
    } catch(Exception e) {
        e.printStackTrace();
    }
%>
