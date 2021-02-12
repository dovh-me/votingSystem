package MainMenu.ballots;

import JavaClass.AlertBox;
import JavaClass.Ballot;
import JavaClass.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class BallotViewStartController implements Initializable {
    @FXML
    Rectangle ballotRect;
    @FXML
    TextField BallotNameTXT, BallotIDTxt;
    @FXML
    Button activity,viewBtn,rmvBallotBtn,addNoteBtn;
    @FXML
    Label blltIDLbl,blltnmLbl;
    @FXML
    TextArea BallotNotesTxt;
    @FXML
    AnchorPane selectedInfo;
    @FXML
    TableView<Ballot>ballot_table;
    @FXML
    TableColumn<Ballot,String>col_id;
    @FXML
    TableColumn<Ballot,String>col_name;

    boolean authenticated = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewBallots();
    }
    /*Populates the table with the ballots already created*/
    public void viewBallots(){
        col_id.setCellValueFactory(new PropertyValueFactory<>("BallotID"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("BallotName"));

        ObservableList<Ballot>obList = FXCollections.observableArrayList();
        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            try {
                String query = "SELECT ballotID, ballotName,isEnded FROM ballots";
                Statement statement= connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                while(rs.next()){
                    String id = rs.getString(1);
                    String name = rs.getString(2);

                    Ballot ballot = new Ballot(id,name);
                    obList.add(ballot);
                }
                statement.close();
                rs.close();
                connection.close();
                ballot_table.setItems(obList);
            } catch (SQLException e) {
                System.out.println("SQL connection Error!");
            }
        }
    }
    /*Enabling the view Ballot button when clicked on the table*/
    public void enableViewBtn() {
        viewBtn.setDisable(false);
    }

    /*Getting ballot info from the database*/
    public void getBallotInfo(){
        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            try{
                Ballot selected = ballot_table.getSelectionModel().getSelectedItem();
                System.out.println(selected.getBallotID());
                String query = "SELECT * FROM ballots WHERE ballotID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1,selected.getBallotID());

                ResultSet rs = statement.executeQuery();
                if (rs.next()){
                    Ballot selectedBallotInfo = new Ballot(rs.getString(1),rs.getString(2),rs.getString(3),rs.getBoolean(4),rs.getBoolean(5));
                    viewBallotInfo(selectedBallotInfo);
                }
                rs.close();
                statement.close();
                connection.close();
            }catch(SQLException e){
                System.out.println("SQL connection Error!");
            }catch (NullPointerException e){
                System.out.println("No ballot selected");
            }
        }
    }


    /*Shows the ballot info in the current stage*/
    //"Ballot selected" is the ballot record that was selected from the table
    public void viewBallotInfo(Ballot selected){
        selectedInfo.setVisible(true);
        BallotNameTXT.setText(selected.getBallotName());
        BallotIDTxt.setText(selected.getBallotID());
        BallotNotesTxt.setText(selected.getBallotNotes());
        if (selected.getEnded()){
            activity.setText("Ballot Already Ended");
            activity.setDisable(true);
            activity.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
        }else {
            if (!selected.getActive()) {
                activity.setDisable(false);
                activity.setText("Start Ballot");
                activity.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                activity.setTextFill(Color.WHITE);
            /*
            ref: https://www.geeksforgeeks.org/javafx-background-class/
             */
            } else {
                activity.setDisable(false);
                activity.setText("End Ballot");
                activity.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                activity.setTextFill(Color.WHITE);
            /*
            ref: https://www.geeksforgeeks.org/javafx-background-class/
             */
            }
        }
    }
    /*
    hiding the selected info elements onClick
     */
    public void hideSelectedInfo(){
        selectedInfo.setVisible(false);
    }

    /*If the ballot meets the requirements and starts the ballot if conditions are met*/
    public void ballotStartPrompt(){
        if (ballotCheck() != null && ballotCheck()) {
            if(adminAuth()) {
                System.out.println("Authenticated");
                Connection connection = DBConnection.getConnection();
                if (connection != null) {
                    try {
                        System.out.println("activating the ballot");
                        String query = "UPDATE ballots SET isActive = 1 WHERE ballotID = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, BallotIDTxt.getText());
                        int rows = statement.executeUpdate();

                        if (rows > 0) {
                            System.out.println("Ballot successfully started");
                            Stage mainStage = (Stage) ballot_table.getScene().getWindow();
                            mainStage.close();
                            votingPanelPrompt();
                        }
                    } catch (SQLException e) {
                        System.out.println("SQL connection Error!");
                    }
                }
            }
        }
    }
    /*Resetting the selected ballot*/
    public void resetBallot(){
        if (adminAuth()){
            Connection connection = DBConnection.getConnection();
            try{
                String query = "UPDATE ballots SET isEnded = 0 WHERE ballotID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1,BallotIDTxt.getText());
                int rows = statement.executeUpdate();

                if (rows > 0){
                    AlertBox.informationAlert("Ballot Reset","Ballot reset success");
                }else{
                    AlertBox.errorAlert("Ballot Reset","Ballot reset failed");
                }
                getBallotInfo();
            }catch (SQLException e){
                System.out.println("DB Error!");
            }
        }
    }

    public boolean adminAuth(){
        AtomicBoolean retVal = new AtomicBoolean(false); //Not sure the what this actually does, IntelliJ suggested this.
        //ref: https://stackoverflow.com/questions/3786825/volatile-boolean-vs-atomicboolean#:~:text=AtomicBoolean%20has%20methods%20that%20perform,so%20within%20a%20synchronized%20block.
        Stage authStage = new Stage();
        authStage.initStyle(StageStyle.UTILITY);
        GridPane root = new GridPane();
        authStage.setTitle("Authentication Required");
        authStage.setScene(new Scene(root,400,300));
        root.setAlignment(Pos.CENTER);
        root.setVgap(10);
        root.setHgap(20);
        root.add(new Label("Username: "),0,0);
        TextField username = new TextField();
        root.add(username,1,0);
        root.add(new Label("Password: "),0,1);
        PasswordField password = new PasswordField();
        root.add(password,1,1);
        Button authBtn = new Button("\uD83D\uDEE1 Authenticate");
        authBtn.setPrefWidth(280);
        root.add(authBtn,0,2);
        root.setColumnSpan(authBtn,GridPane.REMAINING);

        authBtn.setOnAction(e -> retVal.set(authentication(username, password)));
        authStage.showAndWait();

        return retVal.get();
    }

    public boolean authentication(TextField username,TextField password){
        Connection connection = DBConnection.getConnection();

        if (connection!=null){
            try{
                String query = "SELECT userName, pwrd FROM admins WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1,username.getText());
                ResultSet rs = statement.executeQuery();

                if (rs.next()){
                    if(username.getText().equals(rs.getString(1)) && password.getText().equals(rs.getString(2))){
                        Stage authStage = (Stage) username.getScene().getWindow();
                        authStage.close();
                        return true;
                    }else{
                        AlertBox.warningAlert("Incorrect Credentials","The username and password does not match. Please check again.");
                    }
                }else{
                    AlertBox.informationAlert("User not found","No user found with the username you entered.");
                }
                rs.close();
                statement.close();
                connection.close();
            }catch (SQLException e){
                System.out.println("SQL connection Error");
            }
        }
        return false;
    }
    public void votingPanelPrompt(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../voteCast/VoteMainPanel.fxml"));
            Stage votePanelStage = new Stage();
            votePanelStage.setTitle("Ballot Panel");
            votePanelStage.initStyle(StageStyle.UNDECORATED);
            votePanelStage.setScene(new Scene(root,1366,768));
            votePanelStage.show();
        }catch (IOException e){
            System.out.println("VotePanel: File not found");
            e.printStackTrace();
        }
    }
    /*Checks if the registered voters and registered candidates is not 0
    *Also checks if the number of eligible candidates for voting is > 0
    */
    public Boolean ballotCheck(){
        Connection connection = DBConnection.getConnection();
        Boolean ret = null;
        if (connection!=null){
            try {
                String voterQuery = "SELECT COUNT(voterID) FROM voters WHERE voted = 0";
                String candidateQuery = "SELECT COUNT(candidateID) FROM candidates";
                Statement voterStat = connection.createStatement();
                ResultSet voters = voterStat.executeQuery(voterQuery);
                Statement candidateStat = connection.createStatement();
                ResultSet candidates = candidateStat.executeQuery(candidateQuery);
                if (voters.next()){
                    if (candidates.next()){
                        if(voters.getInt(1) > 0 && candidates.getInt(1) > 0){
                            System.out.println(voters.getInt(1) +" "+ candidates.getInt(1));
                            ret = true;
                        }else {
                            AlertBox.informationAlert("Cannot start Ballot","Cannot start the ballot with no registered candidates or voters and no eligible voters");
                            ret = false;
                        }
                    }
                }
                candidates.close(); voters.close(); candidateStat.close(); voterStat.close(); connection.close();
            }catch (SQLException e){
                System.out.println("Error occurred while executing query");;
                return null;
            }
        }
        return ret;
    }
    /*Deleting a ballot from the ballots table*/
    public void removeBallot(){
        Connection connection = DBConnection.getConnection();
        if (connection!=null){
            try{
                String query = "DELETE FROM ballots WHERE ballotID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1,BallotIDTxt.getText());
                int rows = statement.executeUpdate();

                if (rows > 0){
                    System.out.println("Ballot successfully removed!");
                    AlertBox.informationAlert("Successfully Removed","Ballot ID: " + BallotIDTxt.getText() + "\nBallot Name: " + BallotNameTXT.getText() +
                            "\nSuccessfully removed.");
                    hideSelectedInfo();
                    viewBallots();
                }else{
                    System.out.println("Error");
                }
            }catch (SQLException e){
                System.out.println("Error");
            }
        }
    }

    /*Adding note to the ballot for future reference*/
    public void addNote(){
        Connection connection = DBConnection.getConnection();

        try {
            String query = "UPDATE ballots SET ballotNotes = ? WHERE BallotId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,BallotNotesTxt.getText());
            statement.setString(2,BallotIDTxt.getText());
            int rows = statement.executeUpdate();

            if (rows > 0){
                System.out.println("Note successfully added");
                AlertBox.informationAlert("Success","Note successfully added.");
            }else{
                System.out.println("Error executing the query.");
                BallotNotesTxt.setText("");
            }
        }catch (SQLException  e){
            System.out.println("Database Error");
        }
    }

    /*Resetting the candidate votes*/
    public void resetCandidates(){
        if (adminAuth()) {
            Connection connection = DBConnection.getConnection();
            try {
                String query = "UPDATE candidates SET votes = 0";
                Statement statement = connection.createStatement();
                int rows = statement.executeUpdate(query);
                if (rows > 0) {
                    AlertBox.informationAlert("Reset Success", "Successfully updated the votes of all the candidates" +
                            "\nNumber of candidates affected: " + rows);
                } else {
                    AlertBox.errorAlert("Reset Unsuccessful", "An unexpected error occurred while updating the candidates");
                }
            } catch (SQLException e) {
                System.out.println("DB Error!");
            }
        }
    }

/*
--------------------------------------------Navigation methods----------------------------------------------------------
 */
    public void addCandidate(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../candidates/CandidatesAdd.fxml"));
            Stage thisStage = (Stage) ballot_table.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }
    public void viewEditVoters(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../voters/VotersViewEdit.fxml"));
            Stage thisStage = (Stage) ballot_table.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
        }
    }
    public void createBallot(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BallotCreate.fxml"));
            Stage thisStage = (Stage) ballot_table.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
        }
    }

    /*Closing the current window*/
    public void closeWindow(){
        Stage thisStage = (Stage) ballot_table.getScene().getWindow();
        thisStage.close();
    }
}
