<!DOCTYPE html>
<html>
<head>
    <title>Customer Login</title>
    <link rel="stylesheet" type="text/css" href="./css/customerlogin.css">

    <script>
        function saveAccountNumber() {
            // Get the account number from the input field
            var accountNo = document.getElementById("accountNo").value;
            
            // Check if the account number is not empty
            if (accountNo.trim() !== "") {
                // Save the account number to local storage
                localStorage.setItem("accountNo", accountNo);
            } else {
                // Handle the case when the account number is empty
                alert("Please enter your account number.");
            }
        }
    </script>
</head>
<body>
    
    
    <img src="./img/Secure login-rafiki.png"  style="max-height: 100vh; max-width: 100vw; position: relative">
    
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
    <h1>Customer Login</h1>
    <form action="CustomerLoginServlet" method="post" onsubmit="return saveAccountNumber()" style="">
        <label for="accountNo">Account Number:</label>
        <input type="text" id="accountNo" name="accountNo" required=""><br>
        
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required=""><br>
        
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
