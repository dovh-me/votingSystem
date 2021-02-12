package JavaClass;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection(){
        String url  = "jdbc:mysql://localhost:3306/javatest";
        String username = "root";
        String password = "";
        try {
            System.out.println("Trying SQL connection");
            Connection connection = DriverManager.getConnection(url,username,password);
            System.out.println("SQL connection Success!");
            return connection;

        } catch (SQLException e) {
            System.out.println("SQL connection Error!");
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Fatal Error!\nPlease check your connection");
            a.show();
        }
        return null;
    }
}
