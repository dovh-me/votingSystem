package MainMenu.candidates;

import JavaClass.AlertBox;
import JavaClass.Candidate;
import JavaClass.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class CandidatesViewEditController implements Initializable {
    @FXML
    AnchorPane rootAnchPane;
    @FXML
    TextField searchCandidateTxt;
    @FXML
    private TableView<Candidate> table;
    @FXML
    private TableColumn<Candidate, Integer>col_NID;
    @FXML
    private TableColumn<Candidate, String>col_name;
    @FXML
    private TableColumn<Candidate, String>col_party;

    ObservableList<Candidate>obList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewCandidates();
    }

    /*Showing the currently existing candidates in the database in a table*/
    public void viewCandidates(){
        col_NID.setCellValueFactory(new PropertyValueFactory<>("candidateId"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        col_party.setCellValueFactory(new PropertyValueFactory<>("candidateParty"));

        Connection connection = DBConnection.getConnection();

        try{
            //Getting the required information to populate the table from the database
            if (!obList.isEmpty()){
                obList.clear();
            }
            String query = "SELECT candidateID, candidateName, candidateParty FROM candidates";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                Candidate candidate = new Candidate(rs.getString(1), rs.getString(2),rs.getString(3));
                obList.add(candidate);
            }
            rs.close(); statement.close(); connection.close();
            table.setItems(obList);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*Searching for the candidate By ID*/
    public void searchCandidate(){
        Connection connection = DBConnection.getConnection();
        try {
            String query = "SELECT candidateID, candidateName, candidateParty FROM candidates WHERE candidateID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,searchCandidateTxt.getText());
            searchCandidateTxt.setText("");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                obList.clear();
                obList.add(new Candidate(rs.getString(1),rs.getString(2),rs.getString(3)));
            }
            rs.close(); statement.close(); connection.close();
        }catch (SQLException e){
            System.out.println("DB error!");
        }
    }

    /*Editing the selected candidate*/
    public void editCandidateMenu(){
        try {
            //obtaining the selected candidate from the table
            Candidate selectedCandidate = table.getSelectionModel().getSelectedItem();

            //creating a new stage to display the selected candidate
            Stage editStage = new Stage();
            GridPane root = new GridPane();
            editStage.setTitle("Edit Candidate: " + selectedCandidate.getCandidateName());
            editStage.setScene(new Scene(root, 600, 400));
            root.setAlignment(Pos.CENTER);
            root.setHgap(20);
            root.setVgap(10);
            root.add(new Label("Candidate Nomination ID"), 0, 0);
            root.add(new Label("Candidate Name"), 0, 1);
            root.add(new Label("Candidate Party"), 0, 2);

            TextField NID = new TextField(selectedCandidate.getCandidateId());
            NID.setDisable(true);
            NID.setFocusTraversable(false);
            TextField name = new TextField(selectedCandidate.getCandidateName());
            name.setFocusTraversable(false);
            TextField party = new TextField(selectedCandidate.getCandidateParty());
            party.setFocusTraversable(false);

            root.add(NID, 1, 0);
            root.add(name, 1, 1);
            root.add(party, 1, 2);

            Button updateBtn = new Button("Update Candidate");
            Button deleteBtn = new Button("Delete Candidate");

            updateBtn.setOnAction(event -> updateCandidate(name,party,selectedCandidate));
            deleteBtn.setOnAction(event -> deleteCandidate(selectedCandidate,name));
            root.add(updateBtn,1,3);
            root.add(deleteBtn,0,3);
            rootAnchPane.setDisable(true);
            editStage.showAndWait();
            rootAnchPane.setDisable(false);
            table.getItems().clear();
            viewCandidates();

        }catch (NullPointerException e){
            AlertBox.informationAlert("Candidate Not Selected","Please select a candidate to update");
        }
    }

    /*Updating the candidate information*/
    public void updateCandidate(TextField name, TextField party, Candidate selected){
        Connection connection = DBConnection.getConnection();
        try {
            System.out.println(selected.getCandidateId());
            String query = "UPDATE candidates SET candidateName = ?, candidateParty = ? WHERE candidateID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,name.getText());
            statement.setString(2,party.getText());
            statement.setString(3,selected.getCandidateId());
            int rows = statement.executeUpdate();
            if (rows > 0){
                System.out.println("Candidate successfully Updated!");
                Alert updateCandidate = new Alert(Alert.AlertType.INFORMATION);
                updateCandidate.setTitle("Update Success");
                updateCandidate.setContentText("Candidate Successfully updated");
                updateCandidate.showAndWait();

                Stage editStage = (Stage) name.getScene().getWindow();
                editStage.close();
            }else{
                AlertBox.errorAlert("Update Failure","Update unsuccessful");
            }
            statement.close(); connection.close();
        }catch (SQLException e){
            System.out.println("Query not executed!");
            AlertBox.errorAlert("Connection Problem","Please check your connection!");
        }
    }

    /*Deleting the selected candidate*/
    public void deleteCandidate(Candidate selected,TextField name){
        Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        deleteConfirm.setTitle("Confirm Deletion");
        deleteConfirm.setContentText("Candidate to delete: " + selected.getCandidateId() + " " + selected.getCandidateName()+
                "\nOnce deleted the information will be unrecoverable. Please confirm if you want to proceed."
        );
        Optional<ButtonType> confirmation = deleteConfirm.showAndWait();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            Connection connection = DBConnection.getConnection();
            try {
                String query = "DELETE FROM candidates WHERE candidateID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, selected.getCandidateId());
                int rows = statement.executeUpdate();

                if (rows > 0) {
                    System.out.println("Candidate successfully removed!");
                    Alert removeSuccess = new Alert(Alert.AlertType.INFORMATION);
                    removeSuccess.setTitle("Candidate deleted");
                    removeSuccess.setContentText("Candidate Successfully removed");
                    removeSuccess.showAndWait();
                    Stage editStage = (Stage) name.getScene().getWindow();
                    editStage.close();
                }else{
                    System.out.println("Candidate not removed!");
                    AlertBox.errorAlert("Deletion Failure","Candidate not deleted");
                }
                statement.close(); connection.close();
            } catch (SQLException e) {
                System.out.println("Query not executed");

            }
        }

    }

    /*
        --------------------------------------------Navigation methods----------------------------------------------------------
         */
    public void voters(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../voters/VotersImport.fxml"));
            Stage thisStage = (Stage) table.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("VotersImport.fxml: File not found!");
        }
    }
    public void addCandidate(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("CandidatesAdd.fxml"));
            Stage thisStage = (Stage) table.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("CandidatesAdd.fxml: File not found!");
            e.printStackTrace();
        }
    }
    public void ballotView(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../ballots/BallotViewStart.fxml"));
            Stage thisStage = (Stage) table.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("BallotViewStart.fxml: File not found!");
            e.printStackTrace();
        }
    }

    /*Closing the current window*/
    public void closeWindow(){
        Stage thisStage = (Stage) table.getScene().getWindow();
        thisStage.close();
    }
}
