import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.SQLException;
public class SEProject extends JFrame {
private JFrame loginFrame;
private JFrame mainFrame;
private Connection conn;
private int loggedInUserID;
public SEProject() {
try {
// Establish database connection
conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/seproject",
"root", "@yukti789");
} catch (SQLException e) {
e.printStackTrace();
JOptionPane.showMessageDialog(null, "Failed to connect to database.");
System.exit(1);
}
// Login window
loginFrame = new JFrame("Library Management System - Login");
loginFrame.setSize(300, 150);
loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
loginFrame.setLocationRelativeTo(null);
JPanel loginPanel = new JPanel();
JLabel userLabel = new JLabel("Username:");
JTextField userField = new JTextField(20);
JLabel passLabel = new JLabel("Password:");
JPasswordField passField = new JPasswordField(20);
JButton loginButton = new JButton("Login");
loginButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
String username = userField.getText();
String password = new String(passField.getPassword());
if (authenticate(username, password)) {
loginFrame.dispose();
showMainFrame();
} else {
JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.");
}
}
});
loginPanel.add(userLabel);
loginPanel.add(userField);
loginPanel.add(passLabel);
loginPanel.add(passField);
loginPanel.add(loginButton);
loginFrame.add(loginPanel);
loginFrame.setVisible(true);
}
private boolean authenticate(String username, String password) {
try (PreparedStatement pstmt = conn.prepareStatement("SELECT UserID FROM Users WHERE Username = ? AND Password = ?")) {
pstmt.setString(1, username);
pstmt.setString(2, password);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
loggedInUserID = rs.getInt("UserID"); // Set loggedInUserID if authentication is successful
return true; // Return true if authentication is successful
} else {
JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.");
return false; // Return false if authentication fails
}
} catch (SQLException e) {
e.printStackTrace();
JOptionPane.showMessageDialog(loginFrame, "An error occurred during authentication.");
return false; // Return false in case of an error
}
}
private void showMainFrame() {
mainFrame = new JFrame("Library Management System");
mainFrame.setSize(400, 300);
mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
mainFrame.setLayout(new GridLayout(5, 1));
JButton searchBookButton = new JButton("Search Book");
searchBookButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
searchBook();
}
});
mainFrame.add(searchBookButton);
JButton issueBookButton = new JButton("Issue Book");
issueBookButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
issueBook();
}
});
mainFrame.add(issueBookButton);
JButton returnBookButton = new JButton("Return Book");
returnBookButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
returnBook();
}
});
mainFrame.add(returnBookButton);
JButton viewLogButton = new JButton("View History Log");
viewLogButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
viewHistoryLog();
}
});
mainFrame.add(viewLogButton);
JButton checkOverdueButton = new JButton("Check Overdue Payment");
checkOverdueButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
checkOverduePayment();
}
});
mainFrame.add(checkOverdueButton);
mainFrame.setLocationRelativeTo(null);
mainFrame.setVisible(true);
}
private void searchBook() {
String bookId = JOptionPane.showInputDialog(mainFrame, "Enter Book ID to Search:");
if (bookId != null && !bookId.isEmpty()) {
try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Books WHERE BookID = ?")) {
pstmt.setString(1, bookId);
ResultSet rs = pstmt.executeQuery();
if (rs.next()) {
String title = rs.getString("Title");
String author = rs.getString("Author");
String genre = rs.getString("Genre");
String isbn = rs.getString("ISBN");
int totalCopies = rs.getInt("TotalCopies");
int availableCopies = rs.getInt("AvailableCopies");
JOptionPane.showMessageDialog(mainFrame, "Book ID: " + bookId +
"\nTitle: " + title + "\nAuthor: " + author +
"\nGenre: " + genre + "\nISBN: " + isbn + "\nTotal Copies: " +
totalCopies + "\nAvailable Copies: " + availableCopies);
} else {
JOptionPane.showMessageDialog(mainFrame, "Book with ID: " +
bookId + " not found.");
}
} catch (SQLException ex) {
ex.printStackTrace();
JOptionPane.showMessageDialog(mainFrame, "An error occurred while searching for the book.");
}
}
}
private void issueBook() {
String bookId = JOptionPane.showInputDialog(mainFrame, "Enter Book ID to Issue:");
if (bookId != null && !bookId.isEmpty()) {
    try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Books SET AvailableCopies = AvailableCopies - 1 WHERE BookID = ?")) {
    pstmt.setString(1, bookId);
    int updatedRows = pstmt.executeUpdate();
    if (updatedRows > 0) {
    JOptionPane.showMessageDialog(mainFrame, "Book successfully issued.");
    } else {
    JOptionPane.showMessageDialog(mainFrame, "Book with ID: " +
    bookId + " is not available.");
    }
    } catch (SQLException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(mainFrame, "An error occurred while issuing the book.");
    }
    }
    }
    private void returnBook() {
    String bookId = JOptionPane.showInputDialog(mainFrame, "Enter Book ID to Return:");
    if (bookId != null && !bookId.isEmpty()) {
    try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Books SET AvailableCopies = AvailableCopies + 1 WHERE BookID = ?")) {
    pstmt.setString(1, bookId);
    int updatedRows = pstmt.executeUpdate();
    if (updatedRows > 0) {
    JOptionPane.showMessageDialog(mainFrame, "Book successfully returned.");
    } else {
    JOptionPane.showMessageDialog(mainFrame, "Book with ID: " +
    bookId + " is not available for return.");
    }
    } catch (SQLException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(mainFrame, "An error occurred while returning the book.");
    }
    }
    }
    private void viewHistoryLog() {
    try {
    PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM IssuedBooks WHERE UserID = ?");
    pstmt.setInt(1, loggedInUserID); // Assuming loggedInUserID is the ID of the currently logged-in user
ResultSet rs = pstmt.executeQuery();
StringBuilder history = new StringBuilder();
while (rs.next()) {
int bookID = rs.getInt("BookID");
String dateOfIssue = rs.getString("DateOfIssue");
String dueDate = rs.getString("DueDate");
String returnDate = rs.getString("ReturnDate");
double fineAmount = rs.getDouble("FineAmount");
String status;
if (returnDate != null) {
status = "Returned on " + returnDate + " with fine of Rs. " + fineAmount;
} else {
status = "Not returned yet";
}
history.append("Book ID: ").append(bookID).append(", ")
.append("Issued on: ").append(dateOfIssue).append(", ")
.append("Due date: ").append(dueDate).append(", ")
.append("Status: ").append(status).append("\n");
}
if (history.length() == 0) {
JOptionPane.showMessageDialog(mainFrame, "No history found.");
} else {
JTextArea historyTextArea = new JTextArea(history.toString());
JScrollPane scrollPane = new JScrollPane(historyTextArea);
JOptionPane.showMessageDialog(mainFrame, scrollPane, "History Log",
JOptionPane.INFORMATION_MESSAGE);
}
} catch (SQLException e) {
e.printStackTrace();
JOptionPane.showMessageDialog(mainFrame, "An error occurred while fetching the history log.");
}
}
private void checkOverduePayment() {
try {
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM OverduePayments WHERE UserID = ?");
pstmt.setInt(1, loggedInUserID); // Use loggedInUserID to fetch overdue payments for the logged-in user
ResultSet rs = pstmt.executeQuery();
StringBuilder overdueInfo = new StringBuilder();
while (rs.next()) {
double amount = rs.getDouble("Amount");
String datePaid = rs.getString("DatePaid");
overdueInfo.append("Amount: ").append(amount).append(", Date Paid: ").append(datePaid).append("\n");
}
if (overdueInfo.length() == 0) {
JOptionPane.showMessageDialog(mainFrame, "No overdue payments found for this user.");
} else {
JOptionPane.showMessageDialog(mainFrame, "Overdue Payments:\n" +
overdueInfo.toString());
}
} catch (SQLException e) {
e.printStackTrace();
JOptionPane.showMessageDialog(mainFrame, "An error occurred while fetching overdue payments.");
}
}
public static void main(String[] args) {
SwingUtilities.invokeLater(() -> {
new SEProject();
});
}
}