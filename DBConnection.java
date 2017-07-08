import java.sql.*;

public class DBConnection {

	public static Connection getConnection() throws Exception {

		Connection connection = null;
		try {
			System.out.println("Begin Connection!\n");
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost/postgres";
			String user = "ricedb";
			String pw = "wy13";
			connection = DriverManager.getConnection(url, user, pw);
			// connection = DriverManager.getConnection(url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Connection Built!");
		return connection;
	}
}