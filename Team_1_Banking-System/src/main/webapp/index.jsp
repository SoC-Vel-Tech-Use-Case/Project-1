<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Cache-Control" content="no-store" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to SecureBank</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
	<link rel="stylesheet" type="text/css" href="./css/index.css">
    <style>
            </style>
</head>
<body>
    <div class="container">
    <div class="logo">
            <img src="./img/logo.jpg" alt="SecureBank Logo">
        </div>
        <h1>BANKING SYSTEM</h1>
        <p class="tagline">Safe . Reliable . Secure . Fast</p>
        
        <p class="tagline">Innovating banking, securing futures</p>
        <a href="customerlogin.jsp" class="btn">User Login</a>
        <a href="AdminLogin.jsp" class="btn">Admin Login</a>
        <div class="security-info">
            <p>Your security is our top priority. Experience banking with peace of mind.</p>
        </div>
    </div>
    <script>
        history.pushState(null, null, document.URL);
        window.addEventListener('popstate', function () {
            history.pushState(null, null, document.URL);
        });
    </script>
</body>
</html>
