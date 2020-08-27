package funcScripts;

import java.sql.*;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class UpdateSecurePasswordEmployee {

    /*
     * 
     * This program updates your existing moviedb customers table to change the
     * plain text passwords to encrypted passwords.
     * 
     * You should only run this program **once**, because this program uses the
     * existing passwords as real passwords, then replace them. If you run it more
     * than once, it will treat the encrypted passwords as real passwords and
     * generate wrong values.
     * 
     */
    public static void main(String[] args) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        // change the customers table password column from VARCHAR(20) to VARCHAR(128)
        String alterQuery = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
        PreparedStatement statement = connection.prepareStatement(alterQuery);
        int alterResult = statement.executeUpdate();
        HelperFunc.printToConsole("altering employees table schema completed, " + alterResult + " rows affected");

        // get the ID and password for each customer
        String query = "SELECT email, password from employees";
        statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        // we use the StrongPasswordEncryptor from jasypt library (Java Simplified Encryption) 
        //  it internally use SHA-256 algorithm and 10,000 iterations to calculate the encrypted password
        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

        ArrayList<String> updateQueryList = new ArrayList<>();

        HelperFunc.printToConsole("encrypting password (this might take a while)");
        while (rs.next()) {
            // get the ID and plain text password from current table
            String email = rs.getString("email");
            String password = rs.getString("password");
            
            // encrypt the password using StrongPasswordEncryptor
            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            // generate the update query
            String updateQuery = String.format("UPDATE employees SET password='%s' WHERE email='%s'", encryptedPassword,
                    email);
            updateQueryList.add(updateQuery);
        }
        rs.close();

        // execute the update queries to update the password
        HelperFunc.printToConsole("updating password");
        int count = 0;
        for (String updateQuery : updateQueryList) {
            statement = connection.prepareStatement(updateQuery);
            int updateResult = statement.executeUpdate();
            count += updateResult;
        }
        HelperFunc.printToConsole("updating password completed, " + count + " rows affected");

        statement.close();
        connection.close();

        HelperFunc.printToConsole("finished");

    }

}
