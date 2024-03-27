<%
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache"); 
response.setHeader ("Expires", "0"); //prevents caching at the proxy server
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registration Success</title>
    <link rel="stylesheet" type="text/css" href="./css/registration_success.css">

</head>
<body>
    <div class="container">
        <h2>Registration Successful</h2>
        <p>Your account has been successfully registered!</p>
        <p>Account Number: <%= request.getParameter("accountNo") %></p>
        <p>Temporary Password: <%= request.getParameter("password") %></p>
        <form action="CustomerLoginServlet" method="post">
            <div class="form-group">
                <label for="accountNo">Account Number:</label>
                <input type="text" id="accountNo" name="accountNo" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <input type="submit" value="Login" class="btn">
        </form>
    </div>
</body>
</html>
