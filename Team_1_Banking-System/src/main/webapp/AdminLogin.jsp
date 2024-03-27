<%
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache"); 
response.setHeader ("Expires", "0"); //prevents caching at the proxy server
%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Login</title>
    <link rel="stylesheet" type="text/css" href="./css/AdminLogin.css">

</head>
<body>

    <img src="./img/Privacy policy-rafiki.png"  style="max-height: 100vh; max-width: 100vw; position: relative">


<div style="
    background-color: white;
    height: 58vh;
    width: 26vw;
    vertical-align: middle;
    margin-top: 20vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    border-radius: 6px;
">
<h2 style="
    margin-top: 2vh;
    margin-left: 5vw;
    font-size: 38px;
    color: #0010f0;
    font-style: italic;
    font-size: 42px;
">Admin Login</h2>

    <form action="AdminOperations.jsp" method="post">
        <label for="adminUsername">Username:</label>
        <input type="text" id="adminUsername" name="adminUsername" required=""><br>
        
        <label for="adminPassword">Password:</label>
        <input type="password" id="adminPassword" name="adminPassword" required=""><br>

        <input type="submit" value="Login">
    </form>
    	
</div>

    <script>
        history.pushState(null, null, document.URL);
        window.addEventListener('popstate', function () {
            history.pushState(null, null, document.URL);
        });
    </script>
</body>
</html>
