package MainMenu.candidates;

import JavaClass.AlertBox;
import JavaClass.Candidate;
import JavaClass.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CandidatesAddController implements Initializable {
    @FXML
    private TextField NID,name,party;
    @FXML
    private AnchorPane rootAnchor;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    /*Adding a new candidate*/
    public void addCandidate(){
        //checks if all the required fields are filled
        if(!NID.getText().isEmpty() && !name.getText().isEmpty() && !party.getText().isEmpty()){
            Candidate candidate = new Candidate(NID.getText(), name.getText(), party.getText());
            Connection connection = DBConnection.getConnection();
            if (connection != null) {
                try {
                    //adding the input information to the database creating the candidate
                    String query = "INSERT candidates(candidateID,candidateName,candidateParty) VALUES(?,?,?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, candidate.getCandidateId());
                    statement.setString(2, candidate.getCandidateName());
                    statement.setString(3, candidate.getCandidateParty());

                    int rows = statement.executeUpdate();
                    if (rows > 0) {
                        NID.setText("");name.setText("");party.setText("");
                        AlertBox.informationAlert("Candidate Added", "Candidate successfully added to the database");
                    }
                    NID.setText(""); name.setText(""); party.setText("");
                    statement.close(); connection.close();
                } catch (SQLException e) {
                    System.out.println("Error! Query not executed.");
                    e.printStackTrace();
                }
            }
        }else{
            System.out.println("* All fields required");
            AlertBox.errorAlert("Candidate not Added", "All fields required");
        }
    }

    /*resetting the input fields*/
    public void resetFields(){
        NID.setText("");
        name.setText("");
        party.setText("");
    }
    /*
    --------------------------------------------Navigation methods----------------------------------------------------------
     */
    public void viewCandidates(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("CandidatesViewEdit.fxml"));
            Stage thisStage = (Stage) NID.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("CandidatesViewEdit.fxml: File not found!");
        }
    }
    public void voters(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../voters/VotersImport.fxml"));
            Stage thisStage = (Stage) NID.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("VotersImport.fxml: File not found!");
        }
    }
    public void ballotView(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../ballots/BallotViewStart.fxml"));
            Stage thisStage = (Stage) NID.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("BallotViewStart.fxml: File not found!");
        }
    }
    /*Closing the current window*/
    public void closeWindow(){
        Stage thisStage = (Stage) NID.getScene().getWindow();
        thisStage.close();
    }
}
