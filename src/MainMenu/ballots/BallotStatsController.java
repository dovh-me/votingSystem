package MainMenu.ballots;

import JavaClass.Candidate;
import JavaClass.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class BallotStatsController implements Initializable {

    @FXML
    PieChart pieChart;
    @FXML
    TableView<Candidate>candidateTable;
    @FXML
    TableColumn<Candidate, String> colID;
    @FXML
    TableColumn<Candidate, String> colName;
    @FXML
    TableColumn<Candidate, String> colParty;
    @FXML
    TableColumn<Candidate, Integer> votes;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewPieChart();
        viewBallotStatsTable();
    }
    /*Creating the pie chart for the ballot and adding the data*/
    public void viewPieChart(){
        Connection connection = DBConnection.getConnection();
        try {
            //Retrieving the ballot data from the database (Only the ballotId and number of votes are retrieved)
            String query = "SELECT candidateID, votes FROM candidates";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ObservableList<PieChart.Data> candidateStat = FXCollections.observableArrayList();
            while(rs.next()){
                candidateStat.add(new PieChart.Data(rs.getString(1),rs.getInt(2)));
            }
            //Adds the data to the pie chart
            pieChart.setData(candidateStat);
        }catch (SQLException e){
            System.out.println("DB error!");
        }
    }
    /*Showing the number of votes each candidates have received in the ballot in a table*/
    public void viewBallotStatsTable(){
        colID.setCellValueFactory(new PropertyValueFactory<>("candidateId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        colParty.setCellValueFactory(new PropertyValueFactory<>("candidateParty"));
        votes.setCellValueFactory(new PropertyValueFactory<>("votes"));

        ObservableList<Candidate> allCandidates = FXCollections.observableArrayList();

        Connection connection = DBConnection.getConnection();
        String query = "SELECT * FROM candidates";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                allCandidates.add(new Candidate(rs.getString(1),rs.getString(2),rs.getString(3),rs.getInt(4)));
            }
            candidateTable.setItems(allCandidates);
        }catch (SQLException e){
            System.out.println("DB Error!");
        }
    }

    /*Navigating to the Main Menu*/
    public void mainMenu(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("../MainMenu.fxml"));
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root,1366,768));
            stage.show();
            closeWindow();
        }catch (IOException e){
            System.out.println();
        }
    }
    /*Closes the current window*/
    public void closeWindow(){
        Stage stage = (Stage) pieChart.getScene().getWindow();
        stage.close();
    }
}
