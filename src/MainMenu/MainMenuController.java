package MainMenu;

import AdminLogin.AdminLoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainMenuController implements Initializable {
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private Button candidatesBtn;
    @FXML
    private Label usernameLabel;
    /*
    --------------------------------------------Navigation methods----------------------------------------------------------
     */
    public void candidates(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("candidates/CandidatesAdd.fxml"));
            Stage thisStage = (Stage) candidatesBtn.getScene().getWindow();
            thisStage.setTitle("US Presidential Election 2020 - Candidates");
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("CandidatesAdd.fxml: File not found");
            e.printStackTrace();
        }

    }
    public  void voters(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../MainMenu/voters/VotersImport.fxml"));
            Stage thisStage = (Stage) candidatesBtn.getScene().getWindow();
            thisStage.setTitle("US Presidential Election 2020 - Voters");
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("VotersImport.fxml: File not found");
            e.printStackTrace();
        }
    }
    public void ballotView(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ballots/BallotViewStart.fxml"));
            Stage thisStage = (Stage) candidatesBtn.getScene().getWindow();
            thisStage.setTitle("US Presidential Election 2020 - Ballots");
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("BallotViewStart.fxml: File not found!");
            e.printStackTrace();
        }
    }

    /*Closing the current window*/
    public void closeWindow(){
       Stage thisStage =  (Stage)candidatesBtn.getScene().getWindow();
       thisStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameLabel.setText(AdminLoginController.getAdmin().getUsername());
    }
}
