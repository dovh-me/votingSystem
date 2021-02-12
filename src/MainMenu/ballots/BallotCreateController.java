package MainMenu.ballots;

import JavaClass.AlertBox;
import JavaClass.DBConnection;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BallotCreateController {
    @FXML
    TextField blltIdTxt, blltNameTxt;
    @FXML
    TextArea blltNotesTxt;
    /*Creating the ballot*/
    public void createBallot(){
        //Checks if the blltIdTxt and blltNameTxt fields are empty
        if(!blltIdTxt.getText().isEmpty() && !blltNameTxt.getText().isEmpty()) {
            //checks for the length of the ballot ID
            if (blltIdTxt.getText().length() <= 10) {
                Connection connection = DBConnection.getConnection();

                try {
                    //Checks if the ballot already exists
                    String query = "SELECT ballotID FROM ballots WHERE BallotID = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(query);
                    checkStatement.setString(1, blltIdTxt.getText());
                    ResultSet rs = checkStatement.executeQuery();

                    if (rs.next()) {
                        AlertBox.errorAlert("Error: Ballot Exist","Error:\nBallot " + blltIdTxt.getText() + " already exist");
                        System.out.println("Ballot Already exist");
                    } else {
                        try {
                            String notes;
                            if (blltNotesTxt.getText().isEmpty()) {
                                notes = "No Notes";
                            } else {
                                notes = blltNotesTxt.getText();
                            }
                            //Adds the new ballot to the database
                            String addQuery = "INSERT INTO ballots(BallotID, BallotName, BallotNotes) VALUES(?,?,?);";
                            PreparedStatement statement = connection.prepareStatement(addQuery);
                            statement.setString(1, blltIdTxt.getText());
                            statement.setString(2, blltNameTxt.getText());
                            statement.setString(3, notes);
                            int addRows = statement.executeUpdate();

                            if (addRows > 0) {
                                blltIdTxt.setText("");blltNameTxt.setText("");blltNotesTxt.setText("");
                                System.out.println("Ballot successfully created");
                                AlertBox.informationAlert("Ballot Created","Ballot " + blltIdTxt.getText() + " has been successfully created");
                            }
                        } catch (SQLException addException) {
                            System.out.println("An error occurred while adding Ballot");
                            addException.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("SQL connection Error");
                    e.printStackTrace();
                }
            }else{
                Alert errorCharLen = new Alert(Alert.AlertType.INFORMATION);
                AlertBox.informationAlert("Input Error","Ballot ID cannot consist of more than 10 Characters. Please check again");
            }
        }else{
            AlertBox.errorAlert("Fields Error","Ballot ID field and Ballot Name fields required");
        }
    }

    /*
    --------------------------------------------Navigation methods----------------------------------------------------------
     */
    public void addCandidate(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../candidates/CandidatesAdd.fxml"));
            Stage thisStage = (Stage) blltNameTxt.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    public void voters(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../voters/VotersImport.fxml"));
            Stage thisStage = (Stage) blltNameTxt.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
        }
    }
    public void ballotView(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BallotViewStart.fxml"));
            Stage thisStage = (Stage) blltNameTxt.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
        }
    }
    /*Closes the current window*/
    public void closeWindow(){
        Stage thisStage = (Stage) blltIdTxt.getScene().getWindow();
        thisStage.close();
    }

}
