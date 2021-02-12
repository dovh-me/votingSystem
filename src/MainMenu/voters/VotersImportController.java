package MainMenu.voters;

import JavaClass.AlertBox;
import JavaClass.DBConnection;
import JavaClass.Voter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class VotersImportController {
    @FXML
    TextArea importLog;
    @FXML
    TextField pathTxtFld;
    @FXML
    Button importBtn;
    File file;
    @FXML
    /*Selecting  the file to import voters*/
    public void fileSelector(){
        Stage voterImport = (Stage) pathTxtFld.getScene().getWindow();
        try {
            //ref: http://tutorials.jenkov.com/javafx/filechooser.html
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Voter List..");
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", "*.txt"));
            file = fileChooser.showOpenDialog(voterImport);
            pathTxtFld.setText(file.getAbsolutePath());
            try {
                Scanner scan = new Scanner(file);
                String fLine = scan.nextLine();
                System.out.println(fLine);
                if (fLine.equals("/tHiSIsThEfIlE/")) {
                    importBtn.setDisable(false);
                } else {
                    AlertBox.errorAlert("File Error", "File not compatible");
                    System.out.println("File doesn't container the required Signature");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchElementException e) {
                AlertBox.errorAlert("File Error", "Incorrect file type");
            }
        }catch (NullPointerException e){
            System.out.println("No file selected");
        }
    }
    @FXML
    /*Scanning  the selected file*/
    public void voterScanner(){

        Scanner scan = null;

        try {
            scan = new Scanner(file);
            scan.nextLine();
            while(scan.hasNextLine()){
                if (scan.nextLine().equals("/record/")) {
                    String[] tempVoter = scan.nextLine().split(",");
                    System.out.println(Arrays.toString(tempVoter));
                    Voter voter = new Voter(tempVoter[0].trim(), tempVoter[1].trim(), tempVoter[2].trim());
                    System.out.println(tempVoter[0].trim() +" "+ tempVoter[1].trim() +" "+ tempVoter[2].trim());
                    try {
                        if (checkVoter(tempVoter[0].trim())) {
                            importLog.appendText(tempVoter[0] + " already exist.\n");
                            System.out.println(tempVoter[0] + " already exist.");
                        } else {
                            System.out.println("addVoter Calling");
                            addVoter(voter);
                        }
                    }catch (SQLException e){
                        System.out.println("Connection Error!");
                        e.printStackTrace();
                    }
                }else {
                    importLog.appendText("Signature not found\n");
                    System.out.println("Signature not found\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    /*Check if the voter already exists in the database*/
    public boolean checkVoter(String voterID) throws SQLException{
        Connection connection = DBConnection.getConnection();
        String query = "SELECT VoterID FROM voters WHERE VoterID = ?";
        boolean exist;
        assert connection != null;
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,voterID);
        ResultSet rs = statement.executeQuery();
        System.out.println("Query executed!");
        if (rs.next()) {
            exist = true;
        }else{
            exist = false;
        }
        return exist;
    }

    /*Adding the voter into the database*/
    public void addVoter(Voter voter) {
        Connection connection = DBConnection.getConnection();
        String query = "INSERT INTO voters(VoterID, VoterNIC, VoterName, voted) VALUES(?,?,?, false)";
        try {
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,voter.getVoterEID().toUpperCase());
            statement.setString(2,voter.getVoterNIC().toUpperCase());
            statement.setString(3,voter.getVoterName());

            int rows = statement.executeUpdate();
            if(rows > 0){
                System.out.println(">>>" + voter.getVoterEID() + " added");
                importLog.appendText(">>>" + voter.getVoterEID() + " added\n");
            }else{
                System.out.println(">>>" + voter.getVoterEID() + "not added. Database error");
                importLog.appendText(">>>" + voter.getVoterEID() + "not added. Database error");
            }
        }catch (SQLException e){
            System.out.println("Connection Error!");
            Alert ConError = new Alert(Alert.AlertType.ERROR);
            ConError.setContentText("Please check your Connection!");
            ConError.setTitle("Connection Error");
            ConError.show();
        }
    }
    /*Clearing  the importLog TextArea*/
    public void clearLog(){
        System.out.println("Clearing log...");
        importLog.setText("");
    }
    /*
    --------------------------------------------Navigation methods----------------------------------------------------------
     */
    public void addCandidate(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../candidates/CandidatesAdd.fxml"));
            Stage thisStage = (Stage) importBtn.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }
    public void viewEditVoters(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("VotersViewEdit.fxml"));
            Stage thisStage = (Stage) importBtn.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
        }
    }

    public void viewBallot(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../ballots/BallotViewStart.fxml"));
            Stage thisStage = (Stage) importBtn.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
        }
    }

    /*Closing the current window*/
    public void closeWindow(){
        Stage thisStage = (Stage) importBtn.getScene().getWindow();
        thisStage.close();
    }
}
