package MainMenu.voteCast;

import JavaClass.AlertBox;
import JavaClass.DBConnection;
import JavaClass.Voter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class voterIDPromptController implements Initializable {
    private static Voter voter = new Voter();
    @FXML
    TextField VIDtxt, NICtxt;

    public void confirmVoter(){
        Connection connection = DBConnection.getConnection();

        if(connection!=null){
            try{
                String query = "SELECT * FROM voters WHERE VoterID = ?";

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1,VIDtxt.getText().toUpperCase());
                ResultSet rs = statement.executeQuery();
                if (rs.next()){
                    voter.setVoterEID(rs.getString(1));
                    voter.setVoterNIC(rs.getString(2));
                    voter.setVoterName(rs.getString(3));
                    voter.setVoted(rs.getBoolean(4));
                    if(VIDtxt.getText().toUpperCase().equals(voter.getVoterEID()) && NICtxt.getText().toUpperCase().equals(voter.getVoterNIC())){
                        System.out.println("Voter exists");
                        if (voter.getVoted()){
                            System.out.println("Vote already casted");
                            AlertBox.warningAlert("Not eligible for voting", "You have already used your vote.\nPlease contact a staff member if this is FALSE");
                            VIDtxt.setText(""); NICtxt.setText("");
                        }else{
                            getVotePanel();
                        }
                    }
                    else {
                        AlertBox.informationAlert("Invalid Details","The Election ID and NIC number you entered does not match please check your details and try again." +
                                "\nIf the problem persists please contact a staff member");
                        NICtxt.setText("");
                    }
                }else{
                    AlertBox.informationAlert("Invalid Details","The Election ID you entered is invalid please check your details and try again." +
                            "\nIf the problem persists please contact a staff member");
                    System.out.println("No such voter registered");
                    VIDtxt.setText(""); NICtxt.setText("");
                }
            }catch (SQLException e){
                System.out.println("Database Error");
            }
        }
    }
    public static Voter getVoter(){
        return voter;
    }
    public void getVotePanel(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("VoteCastPanel.fxml"));
            Stage voteStage = new Stage();
            voteStage.setScene(new Scene(root,1920,1080));
            voteStage.initStyle(StageStyle.UNDECORATED);
            voteStage.setTitle("US PRESIDENTIAL ELECTION 2020: Cast your Vote");
            voteStage.show();
            Stage currentStage = (Stage) VIDtxt.getScene().getWindow();
            currentStage.close();
        }catch (IOException e){
            System.out.println("VoteCastPanel.fxml file not found");
        }
    }

    public void closeWindow(){
        Stage thisStage = (Stage) VIDtxt.getScene().getWindow();
        thisStage.close();
    }
    public void adminLogin(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../../AdminLogin/AdminLogin.fxml"));
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root,1920,1080));
            stage.show();
            closeWindow();
        }catch (IOException e){
            System.out.println("AdminLogin.fxml: file not found");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VIDtxt.setTooltip(new Tooltip("Click here and\nenter the Election ID number"));
        NICtxt.setTooltip(new Tooltip("Click here and\nenter the National ID number"));
    }
}
