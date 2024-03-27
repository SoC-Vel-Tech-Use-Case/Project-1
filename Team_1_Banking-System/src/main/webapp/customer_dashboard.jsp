<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bank.DatabaseConnection" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.Objects" %> <!-- Import for Objects class -->
<%
    // Check if session exists
    if (Objects.isNull(session.getAttribute("accountNo"))) {
        // Redirect to customerlogin.jsp if session does not exist
        response.sendRedirect("customerlogin.jsp");
        return; // Stop further execution of the JSP
    }

    // Check if the refresh flag is not set in the session
    if (session.getAttribute("refreshed") == null) {
        // Set the refresh flag in the session
        session.setAttribute("refreshed", true);
        // Refresh the page after 1 second
        response.setHeader("Refresh", "1");
    }
%>

<%
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragma","no-cache"); 
response.setHeader ("Expires", "0"); //prevents caching at the proxy server
%>

<!DOCTYPE html>
<html>
<head>


    <meta charset="UTF-8">
    <title>Customer Dashboard</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/2.11.338/pdf.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.9.3/html2pdf.bundle.min.js"></script>
    
    <script type="text/javascript">
    // Disable browser back button
    history.pushState(null, null, document.URL);
    window.addEventListener('popstate', function () {
        history.pushState(null, null, document.URL);
    });
</script>
    <link rel="stylesheet" type="text/css" href="dashboard.css">
    <link rel="stylesheet" type="text/css" href="./css/customer_dashboard.css">

</head>
<body style="display: flex; flex-direction: column; justify-content:center;">
<div class = "container"> 
    <h1>Welcome <%= session.getAttribute("accountNo") %> to Your Dashboard</h1>

    <%
        // Initialize variables for account balance
        double accountBalance = 0;

        try {
            // Establish database connection
             Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT initial_balance FROM customer WHERE account_no = ?");
            stmt.setString(1, session.getAttribute("accountNo").toString());
            ResultSet rs = stmt.executeQuery();
            
            // Retrieve account balance
            if (rs.next()) {
                accountBalance = rs.getDouble("initial_balance");
            }

            // Close resources
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    %>

    <h2>Account Balance: $<%= accountBalance %></h2>
    <div style="display: flex; flex-direction: row; width: 80vw; justify-content: center;">
    
    <button onclick="openAddMoneyAlert()">Add Money</button>
    <button onclick="openRemoveMoneyAlert()">Remove Money</button>
    <button onclick="downloadTable()">Download PDF</button>
        <button onclick="resetPassAlert()">Reset Password</button>
    

	<button onclick="deleteAcc()">Delete Account</button>
	    <button class="logout-button" style="background-color= red" onclick="logout()">Logout</button>
	
    
   
	<script>
	/* FOR CSV
	function downloadTable() {
		  // Get the table element
		  var table = document.getElementById("transactions");
		  var rows = table.rows;

		  // Initialize a variable to store CSV content
		  var csvContent = "data:text/csv;charset=utf-8,";

		  // Loop through rows and columns to build CSV content
		  for (var i = 0; i < rows.length; i++) {
		    var rowData = [];
		    var cells = rows[i].cells;
		    for (var j = 0; j < cells.length; j++) {
		      rowData.push(cells[j].innerText);
		    }
		    csvContent += rowData.join(",") + "\n";
		  }

		  // Create a download link and trigger click event to start download
		  var encodedUri = encodeURI(csvContent);
		  var link = document.createElement("a");
		  link.setAttribute("href", encodedUri);
		  link.setAttribute("download", "table_data.csv");
		  document.body.appendChild(link);
		  link.click();
		}

		*/
		//FOR PDF
		function downloadTable() {
	    // Get the table element
	    var table = document.getElementById("transactions");

	    // Styling adjustments for the table
	    table.style.width = '100%'; // Ensure the table fills the container width
	    table.style.borderCollapse = 'collapse'; // Collapse borders for consistent rendering

	    // Create configuration object for html2pdf
	    var opt = {
	        margin: 10, // Adjust margin as needed
	        filename: 'bank_statement.pdf',
	        image: { type: 'jpeg', quality: 1 }, // Adjust image quality
	        html2canvas: { scale: 2 }, // Adjust html2canvas scale for better resolution
	        jsPDF: { unit: 'pt', format: 'a4', orientation: 'portrait' } // Adjust PDF format and orientation
	    };

	    // Use html2pdf to generate and download PDF
	    html2pdf().from(table).set(opt).save();
	}

    var accountNo = '<%= session.getAttribute("accountNo") %>';

    function deleteAcc() {
        if (confirm("Are you sure you want to delete your account?")) {
            if (<%= accountBalance %> === 0) {
                var xhr = new XMLHttpRequest();
                xhr.open("POST", "deleteAccount");
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === XMLHttpRequest.DONE) {
                        if (xhr.status === 200) {
                            alert("Account deleted successfully!");
                            location.reload();
                            window.location.href = "customerlogin.jsp";
                        } else {
                            alert("Error: " + xhr.responseText);
                        }
                    }
                };
                xhr.send("accountNo=" + encodeURIComponent(accountNo));
            } else {
                alert("Cannot delete account. Please ensure your account balance is 0.");
            }
        }
    }
</script>

<%@ page import="java.sql.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>

<%
// Transaction class definition
// Transaction class definition
class Transaction {
    private int transactionId;
    private String accountNo;
    private String transactionType;
    private double amount;
    private Timestamp transactionDate;

    public Transaction(int transactionId, String accountNo, String transactionType, double amount, Timestamp transactionDate) {
        this.transactionId = transactionId;
        this.accountNo = accountNo;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    // Getter methods
    public int getTransactionId() {
        return transactionId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }
}

%>


   
    
    
    </div>
    
    <h2>Recent Transactions:</h2>
<label for="sortOrder" style="margin-right: 10px;">Sort Order:</label>
<select id="sortOrder" onchange="sortTable()" style="padding: 5px; border-radius: 3px;">
    <option value="asc">Ascending</option>
    <option value="desc">Descending</option>
</select>



<script>
    function sortTable() {
        var table = document.getElementById("transactions");
        var sortOrder = document.getElementById("sortOrder").value;
        var rows, switching, i, x, y, shouldSwitch;
        switching = true;
        while (switching) {
            switching = false;
            rows = table.rows;
            for (i = 1; i < (rows.length - 1); i++) {
                shouldSwitch = false;
                if (sortBy == 4) { // Check if sorting by transaction date
                    x = new Date(rows[i].getElementsByTagName("TD")[sortBy].innerText); // Parse the transaction date
                    y = new Date(rows[i + 1].getElementsByTagName("TD")[sortBy].innerText); // Parse the transaction date
                } else {
                    x = parseFloat(rows[i].getElementsByTagName("TD")[sortBy].innerText.replace(/[^\d.-]/g, '')); // Parse the amount
                    y = parseFloat(rows[i + 1].getElementsByTagName("TD")[sortBy].innerText.replace(/[^\d.-]/g, '')); // Parse the amount
                }
                if (sortOrder === "asc") {
                    if (x > y) {
                        shouldSwitch = true;
                        break;
                    }
                } else if (sortOrder === "desc") {
                    if (x < y) {
                        shouldSwitch = true;
                        break;
                    }
                }
            }
            if (shouldSwitch) {
                rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
                switching = true;
            }
        }
    }
</script>


    
    <div style="display: flex; justify-content-center; min-width: 100vw">
   <div style="max-width: 70vw;">
    <div class="transactions-table">
    <table border="1" id="transactions">
        <thead>
            <tr>
                <th>Transaction ID</th>
                <th>Account No</th>
                <th>Transaction Type</th>
                <th>Amount</th>
                <th>Transaction Date</th>
            </tr>
        </thead>
        <tbody>
            <% 
            try {
            	 Connection con = DatabaseConnection.getConnection();
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM (SELECT * FROM transaction WHERE account_no = ? ORDER BY transaction_date DESC LIMIT 10) AS recent_transactions ORDER BY transaction_date ASC");
                stmt.setString(1, session.getAttribute("accountNo").toString());
                ResultSet rs = stmt.executeQuery();

                // Create a list to store transactions
                List<Transaction> transactions = new ArrayList<>();
				int counter = 0;	
                while (rs.next()) {
                    // Create a Transaction object for each row and add it to the list
                    Transaction transaction = new Transaction(
                            rs.getInt("transaction_id"),
                            rs.getString("account_no"),
                            rs.getString("transaction_type"),
                            rs.getDouble("amount"),
                            rs.getTimestamp("transaction_date")
                    );
                    transactions.add(transaction);
                }

                // Sort the transactions by transaction date in ascending order
                Collections.sort(transactions, (t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()));

                // Display the transactions in the table
                for (Transaction transaction : transactions) {
            %>
                <tr>
                    <td><%= counter += 1 %></td>
                    <td><%= transaction.getAccountNo() %></td>
                    <td><%= transaction.getTransactionType() %></td>
                    <td><%= transaction.getAmount() %></td>
                    <td><%= transaction.getTransactionDate() %></td>
                </tr>
            <% 
                }

                rs.close();
                stmt.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            %>
        </tbody>
    </table>
    </div>
    </div>
    </div>

    <!-- Modal for adding money -->
    <div id="addMoneyModal" style="display: none;">
        <input type="number" id="addMoneyAmount" placeholder="Enter amount to add">
        <button onclick="addMoney()">Add</button>
        <button onclick="closeAddMoneyModal()">Cancel</button>
    </div>

    <!-- Modal for removing money -->
    <div id="removeMoneyModal" style="display: none;">
        <input type="number" id="removeMoneyAmount" placeholder="Enter amount to remove">
        <button onclick="removeMoney()">Remove</button>
        <button onclick="closeRemoveMoneyModal()">Cancel</button>
    </div>

 <script>
 
 function sortTable() {
     var table = document.getElementById("transactions");
     var sortOrder = document.getElementById("sortOrder").value;
     var rows, switching, i, x, y, shouldSwitch;
     switching = true;
     while (switching) {
         switching = false;
         rows = table.rows;
         for (i = 1; i < (rows.length - 1); i++) {
             shouldSwitch = false;
             x = rows[i].getElementsByTagName("TD")[4].innerText;
             y = rows[i + 1].getElementsByTagName("TD")[4].innerText;
             if (sortOrder === "asc") {
                 if (x > y) {
                     shouldSwitch = true;
                     break;
                 }
             } else if (sortOrder === "desc") {
                 if (x < y) {
                     shouldSwitch = true;
                     break;
                 }
             }
         }
         if (shouldSwitch) {
             rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
             switching = true;
         }
     }
 }
 	function resetPassAlert(){
        var newPass = prompt("Enter new Password:");
        if (newPass !== null) {
            resetPassword(newPass);
        }
    } 
    function openAddMoneyAlert() {
        var amountToAdd = prompt("Enter amount to add:");
        if (amountToAdd !== null) {
            addMoney(amountToAdd);
        }
    }

    function openRemoveMoneyAlert() {
        var amountToRemove = prompt("Enter amount to remove:");
        if (amountToRemove !== null) {
            removeMoney(amountToRemove);
        }
    }
    
    function resetPassword(newPassword) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "ResetPasswordServlet"); // URL to the ResetPasswordServlet
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    // Handle success response here
                    alert("Password reset successful!");
                } else {
                    // Handle error response here
                    alert("Error: " + xhr.responseText);
                }
            }
        };
        xhr.send("newPassword=" + encodeURIComponent(newPassword));
    }


    function addMoney(amountToAdd) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "AddMoneyServlet");
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    // Handle success response here
                    location.reload(); // Reload the page after successful update
                } else {
                    // Handle error response here
                    alert("Error: " + xhr.responseText);
                }
            }
        };
        xhr.send("amountToAdd=" + encodeURIComponent(amountToAdd));
    }

    function removeMoney(amountToRemove) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "RemoveMoneyServlet");
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    // Handle success response here
                    location.reload(); // Reload the page after successful update
                } else {
                    // Handle error response here
                    alert("Error: Transaction could not be processed due to insufficient balance. ");
                }
            }
        };
        xhr.send("amountToRemove=" + encodeURIComponent(amountToRemove));
    }

    // Adjust logout function as before


        function logout() {
            window.location.href = "CustomerLogoutServlet"; // Adjust the URL if needed
        }
    </script>
    </div>
    
     <script src="noback.js"></script>
</body>
</html>