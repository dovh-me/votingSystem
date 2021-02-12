package MainMenu.voters;

import JavaClass.AlertBox;
import JavaClass.DBConnection;
import JavaClass.Voter;
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
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class VotersViewEditController implements Initializable {
    @FXML
    AnchorPane rootAnchPane;
    @FXML
    private TableView<Voter> voterViewTable;
    @FXML
    private TableColumn<Voter, Integer> col_id;
    @FXML
    private TableColumn<Voter, String> col_NIC;
    @FXML
    private TableColumn<Voter, String> col_name;
    @FXML
    private TableColumn<Voter, Boolean> col_voted;

    ObservableList<Voter> obList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewVoters();
    }

    /*Adding existing voter details into the table*/
    public void viewVoters(){
        col_id.setCellValueFactory(new PropertyValueFactory<>("voterEID"));
        col_NIC.setCellValueFactory(new PropertyValueFactory<>("voterNIC"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("voterName"));
        col_voted.setCellValueFactory(new PropertyValueFactory<>("voted"));

        Connection connection = DBConnection.getConnection();

        try{
            String query = "SELECT VoterID, VoterNIC, VoterName, voted FROM voters";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                Voter voter = new Voter(rs.getString(1), rs.getString(2),rs.getString(3),rs.getBoolean(4));
                obList.add(voter);
            }

            voterViewTable.setItems(obList);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*Edit voter popup Menu [ADDITIONAL]*/
    public void editVoterMenu(){
        try {
            System.out.println("editVoterMenu executed");
            Voter selectedVoter = voterViewTable.getSelectionModel().getSelectedItem();
            System.out.println("selection assigned");
            Stage editStage = new Stage();
            GridPane root = new GridPane();
            editStage.setTitle("Edit Voter: " + selectedVoter.getVoterEID());
            editStage.setScene(new Scene(root, 600, 400));
            root.setAlignment(Pos.CENTER);
            root.setHgap(20);
            root.setVgap(10);
            root.add(new Label("Voter Election ID"), 0, 0);
            root.add(new Label("Voter NIC Number"), 0, 1);
            root.add(new Label("Voter Name"), 0, 2);

            TextField voterEIDTxt = new TextField(selectedVoter.getVoterEID());
            voterEIDTxt.setDisable(true);
            voterEIDTxt.setFocusTraversable(false);
            TextField voterNIC = new TextField(selectedVoter.getVoterNIC());
            voterNIC.setFocusTraversable(false);
            voterNIC.setDisable(true);
            TextField name = new TextField(selectedVoter.getVoterName());
            name.setFocusTraversable(false);

            root.add(voterEIDTxt, 1, 0);
            root.add(voterNIC, 1, 1);
            root.add(name, 1, 2);

            Button updateBtn = new Button("Update Voter");
            Button deleteBtn = new Button("Delete Voter");

            updateBtn.setOnAction(event -> updateVoter(name,voterNIC,selectedVoter));
            deleteBtn.setOnAction(event -> deleteVoter(selectedVoter,name));
            root.add(updateBtn,1,3);
            root.add(deleteBtn,0,3);
            rootAnchPane.setDisable(true);
            editStage.showAndWait();
            rootAnchPane.setDisable(false);
            voterViewTable.getItems().clear();
            viewVoters();

        }catch (NullPointerException e){
            AlertBox.informationAlert("Voter not selected","Please select a voter to update");
        }
    }

    /*Updating the selected voter [ADDITIONAL]*/
    public void updateVoter(TextField name, TextField party, Voter selected){
        Connection connection = DBConnection.getConnection();
        try {
            System.out.println(selected.getVoterEID());
            String query = "UPDATE voters SET VoterName = ? WHERE VoterID = ?";
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,name.getText());
            statement.setString(2,selected.getVoterEID());
            int rows = statement.executeUpdate();
            obList.clear();
            viewVoters();
            if (rows > 0){
                System.out.println("Voter successfully Updated!");
                AlertBox.informationAlert("Update Success","Voter Successfully updated");

                Stage editStage = (Stage) name.getScene().getWindow();
                editStage.close();
            }else{
                AlertBox.errorAlert("Update Failed","Update unsuccessful");
            }
        }catch (SQLException e){
            System.out.println("Query not executed!");
            Alert queryError = new Alert(Alert.AlertType.ERROR);
            queryError.setContentText("Please check your connection!");
            queryError.setTitle("Connection Problem");
            queryError.show();
        }
    }

    /*Delete the selected voter from the database*/
    public void deleteVoter(Voter selected, TextField name){
        Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        deleteConfirm.setTitle("Confirm Deletion");
        deleteConfirm.setContentText("Voter to delete: " + selected.getVoterEID() + " " + selected.getVoterName()+
                "\nOnce deleted the information will be unrecoverable. Please confirm if you wish to proceed."
        );
        Optional<ButtonType> confirmation = deleteConfirm.showAndWait();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            Connection connection = DBConnection.getConnection();
            try {
                String query = "DELETE FROM voters WHERE VoterID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, selected.getVoterEID());
                int rows = statement.executeUpdate();

                if (rows > 0) {
                    System.out.println("Voter successfully removed!");
                    AlertBox.informationAlert("Voter deleted","Voter Successfully removed");
                    Stage editStage = (Stage) name.getScene().getWindow();
                    editStage.close();
                }else{
                    System.out.println("Voter not removed!");
                    AlertBox.errorAlert("Deletion Failure","Voter not deleted");
                }
            } catch (SQLException e) {
                System.out.println("Query not executed");

            }
        }
    }

    /*Delete all voters in the database*/
    public void deleteAllVoters(){
        Connection connection = DBConnection.getConnection();
        try{
            String queryCount = "SELECT COUNT(VoterID) FROM voters";
            assert connection != null;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(queryCount);

            if (rs.next()){
                //Delete the voters only if the admin confirms the operation
                Alert PromptDeleteAll = new Alert(Alert.AlertType.CONFIRMATION);
                PromptDeleteAll.setTitle("Delete All Voters");
                PromptDeleteAll.setContentText("Do you want to delete all the Voters in the database?\nOnce deleted this data is unrecoverable" +
                        "\nNumber of Voters being deleted: " +rs.getInt(1));
                Optional<ButtonType>confirmation = PromptDeleteAll.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK){
                    String queryDel = "DELETE FROM voters";
                    try {
                        Statement statementDel = connection.createStatement();
                        int rows = statementDel.executeUpdate(queryDel);
                        if (rows == rs.getInt(1)) {
                            AlertBox.informationAlert("Successful deletion","Successfully deleted all voters.\nRows Deleted: " + rows);
                        }else{
                            AlertBox.informationAlert("Deletion Error","An error occurred while deleting voters\nRows Deleted: " +rows);
                        }
                        voterViewTable.getItems().clear();
                        viewVoters();
                    }catch (SQLException e){
                        System.out.println("Connection Error!");
                    }
                }else{
                    System.out.println("No Permission Granted");
                }
            }
        }catch (SQLException e){
            System.out.println("Connection Error!");
        }
    }
    @FXML
    /*Resetting the voted status of voters [Added to enable future improvements to the system]*/
    public void resetVoters(){
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "UPDATE voters SET voted = 0";
                Statement statement = connection.createStatement();
                int rows = statement.executeUpdate(query);

                if (rows > 0) {
                    AlertBox.informationAlert("Reset Success", "Voter voting status successfully updated. " +
                            "\nNumber of voters affected: " + rows);
                }else {
                    AlertBox.errorAlert("Reset Fail", "Unable to reset the voters");
                }
                voterViewTable.getItems().clear();
                viewVoters();
                statement.close(); connection.close();
            } catch (SQLException e) {
                System.out.println("DB Error!");
            }
        }
    }

    /*
    --------------------------------------------Navigation methods----------------------------------------------------------
     */
    public void importVoters(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../voters/VotersImport.fxml"));
            Stage thisStage = (Stage) voterViewTable.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
        }
    }
    public void addCandidate(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../candidates/CandidatesAdd.fxml"));
            Stage thisStage = (Stage) voterViewTable.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }
    public void ballotView(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../ballots/BallotViewStart.fxml"));
            Stage thisStage = (Stage) voterViewTable.getScene().getWindow();
            thisStage.setScene(new Scene(root,1366,768));
        }catch (IOException e){
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    /*Closing the current window*/
    public void closeWindow(){
        Stage thisStage = (Stage) voterViewTable.getScene().getWindow();
        thisStage.close();
    }
}
