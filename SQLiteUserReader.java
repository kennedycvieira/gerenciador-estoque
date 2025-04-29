import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A Java application that connects to a SQLite database,
 * queries the "usuarios" table, and prints all usernames and passwords.
 */
public class SQLiteUserReader {
    
    public static void main(String[] args) {
        // Database path - you can change this to your SQLite DB file path
        String dbPath = "estoque.db";
        
        // JDBC URL for SQLite
        String url = "jdbc:sqlite:" + dbPath;
        
        // Create connection and statement objects
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to SQLite database successfully.");
            
            // Create the table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                    "username TEXT UNIQUE NOT NULL," +
                                    "password TEXT NOT NULL)";
            stmt.execute(createTableSQL);
            
            // Query to get all users
            String query = "SELECT id, username, password FROM usuarios";
            ResultSet rs = stmt.executeQuery(query);
            
            System.out.println("\n--- All Users ---");
            System.out.println("ID\tUsername");
            System.out.println("--------------------------------");
            
            boolean hasUsers = false;
            
            // Process each row in the result set
            while (rs.next()) {
                hasUsers = true;
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                
                System.out.printf("%d\t%s\t\t%n", id, username);
            }
            
            if (!hasUsers) {
                System.out.println("No users found in the database.");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}