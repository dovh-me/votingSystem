package AdminLogin;

import JavaClass.Admin;
import JavaClass.AlertBox;
import JavaClass.Ballot;
import JavaClass.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminLoginController implements Initializable {
    Ballot activeBallot = new Ballot();
    @FXML
    private Label activeBallotLabel;
    @FXML
    private TextField username,password;

    @FXML
    private AnchorPane voteAPane;

    static Admin admin = new Admin();
    @FXML
    /*Admin login function*/
    public void loginFunc(){
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            System.out.println("connection check passed");
            try {
                //Checks if both the username and password fields are not empty
                if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
                    System.out.println("Field check passed");
                    String query = "SELECT userName,pwrd FROM admins WHERE userName = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, username.getText());
                    ResultSet rs = statement.executeQuery();
                    System.out.println("Query execution successful");
                    System.out.println("User Exists");
                    //Checks if the user exists in the database
                    if (rs.next()) {
                        admin = new Admin(rs.getString(1), rs.getString(2));

                        //Checks if the username and password matches to that in the database
                        if (!admin.getUsername().equals(username.getText()) || !admin.getPassword().equals(password.getText())) {
                            System.out.println("Incorrect Login Credentials");
                            AlertBox.warningAlert("Login Fail", "Incorrect login credentials.");
                        } else {
                            System.out.println("Login success!");
                            if (!ballotActive(connection)) {
                                //Redirects to the MainMenu.fxml if the login is success and no ballot is active
                                Parent root = FXMLLoader.load(getClass().getResource("../MainMenu/MainMenu.fxml"));
                                System.out.println("File found");
                                Stage stage = new Stage();
                                stage.setTitle("Main Menu");
                                stage.setScene(new Scene(root, 1366, 768));
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.show();

                            }else{
                                //Redirects to VoteMainPanel.fxml if a ballot is active
                                System.out.println("ballot active!");
                                votePanel();
                            }
                           closeWindow();
                        }
                    }else{
                        AlertBox.errorAlert("Login Fail", "User not found");
                    }
                    rs.close(); statement.close(); connection.close();
                } else {
                    AlertBox.informationAlert("Login fail", "Please fill in all the fields");
                }
            } catch (SQLException | IOException exception) {
                AlertBox.errorAlert("Fatal Error", "Fatal Error!\nPlease check your connection");
                exception.printStackTrace();
            }
        }
    }
    /*logged in admin*/
    public static Admin getAdmin(){
        return admin;
    }

    public void votePanel() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../MainMenu/voteCast/VoteMainPanel.fxml"));
        System.out.println("File found");
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 1366, 768));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }
    /*
    --------------------------------------------Navigation methods----------------------------------------------------------
     */
    /*Redirects to the voterIDPrompt.fxml if Proceed to voter panel is clicked*/
    public void votePanelLogin(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../MainMenu/voteCast/voterIDPrompt.fxml"));
            System.out.println("File found");
            Stage stage = new Stage();
            stage.setTitle("Active Ballot");
            stage.setScene(new Scene(root, 1920, 1080));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
            closeWindow();
        }catch (IOException e){
            System.out.println("voterIDPrompt.fxml: file not found");
        }
    }

    /*Checks if there are any active ballots*/
    public boolean ballotActive(Connection connection){
        try {
            String queryBallot = "SELECT ballotID FROM ballots WHERE isActive = 1";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(queryBallot);
            boolean active = rs.next();
            if (active){
                activeBallot.setBallotID(rs.getString(1));
            }
            rs.close(); statement.close(); connection.close();
            return active;
        }catch (SQLException e){
            System.out.println("Database Error");
            e.printStackTrace();
            return false;
        }
    }

    /*Closing the current window*/
    public void closeWindow(){
        Stage thisStage = (Stage) voteAPane.getScene().getWindow();
        thisStage.close();
    }

    @Override
    /*Checks if the any ballot is active at the beginning of the program*/
    public void initialize(URL location, ResourceBundle resources) {
        Connection connection = DBConnection.getConnection();
        if(ballotActive(connection)){
            //Sets the visibility of the ballotPanel buttons in the login screen
            activeBallotLabel.setText("Currently active ballot: " + activeBallot.getBallotID());
            voteAPane.setVisible(true);
        }else{
            voteAPane.setVisible(false);
        }
    }
}

