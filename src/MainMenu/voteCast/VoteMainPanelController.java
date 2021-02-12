package MainMenu.voteCast;

import JavaClass.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class VoteMainPanelController implements Initializable {

    @FXML
    Label ballotInfoLbl,totalVoters,votedVoters,votedVotersPrcntg;
    @Override

    public void initialize(URL location, ResourceBundle resources) {
        activeBallot();
        voterStats();
    }

    /*Shows the number of total voters registered and how many of the voters have used their votes*/
    public void voterStats(){
        Connection connection = DBConnection.getConnection();
        try{
            //Gets the total number of voters registered in the system as TotalVoters and number of voter who has used their votes as VotedVoters
            String query = "SELECT (SELECT COUNT(voted) FROM voters WHERE voted = 1) AS VotedVoters,COUNT(VoterID) AS TotalVoters FROM voters";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                votedVoters.setText(rs.getString(1));
                totalVoters.setText(rs.getString(2));
                double total = rs.getDouble(2); double voted = rs.getDouble(1);
                //calculates the number of voted voters out of total voters as a percentage
                double asPercentage = voted/total*100;
                votedVotersPrcntg.setText(Double.toString(asPercentage) + "%");
            }
            rs.close(); statement.close(); connection.close();
        }catch (SQLException e){
            System.out.println("DB error!");
        }
    }
    /*Gets the information about the active ballot from the database*/
    public void activeBallot(){
        Connection connection = DBConnection.getConnection();

        if (connection!=null){
            try{
                String query  = "SELECT ballotID, ballotName FROM ballots WHERE isActive = 1";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                if (rs.next()){
                    ballotInfoLbl.setText("Currently active ballot: " + rs.getString(1) + " " + rs.getString(2));
                }else{
                    ballotInfoLbl.setText("No active Ballots");
                }
                rs.close(); statement.close(); connection.close();
            }catch (SQLException e){
                ballotInfoLbl.setText("Error. Please check your Connection status before proceeding any further");
                System.out.println("Query Execution Error");
            }
        }
    }
    /*Ending the currently active ballot*/
    public void endBallot(){
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try{
                String query = "UPDATE ballots SET isActive = 0, isEnded = 1 WHERE isActive = 1";
                Statement statement = connection.createStatement();
                int rows = statement.executeUpdate(query);
                if (rows > 0){
                    System.out.println("ballot ended successfully");
                    Stage currentStage = (Stage) ballotInfoLbl.getScene().getWindow();
                    currentStage.close();
                    winnerPrint(); graphPrint();
                    try{
                        //Redirecting the ballotStats.fxml when the ballot has successfully ended
                        Parent root = FXMLLoader.load(getClass().getResource("../ballots/ballotStats.fxml"));
                        Stage mainStage = new Stage();
                        mainStage.initStyle(StageStyle.UNDECORATED);
                        mainStage.setScene(new Scene(root,1360, 768));
                        mainStage.show();
                    }catch (IOException e){
                        System.out.println("MainMenu.fxml file not found");
                    }
                }
            }catch (SQLException e){
                System.out.println("Database Error");
            }
        }
    }
    /*Redirecting the voterIDPrompt.fxml for users to cast votes*/
    public void getCandidates(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("voterIDPrompt.fxml"));
            Stage voteCastStage = new Stage();
            voteCastStage.initStyle(StageStyle.UNDECORATED);
            voteCastStage.setScene(new Scene(root,1920,1080));
            voteCastStage.setTitle("US Presidential Election : Vote Casting Panel");
            voteCastStage.show();

            Stage thisStage = (Stage) ballotInfoLbl.getScene().getWindow();
            thisStage.close();
        }catch (IOException e){
            System.out.println("voterIDPrompt.fxml not found");
        }
    }
    /*Closing the current window*/
    public void closeWindow(){
       Stage thisStage = (Stage) ballotInfoLbl.getScene().getWindow();
       thisStage.close();
    }
    /*Prints the winner of the ballot to the system console*/
    public void winnerPrint(){
        Connection connection = DBConnection.getConnection();
        try {
            String queryWin = "SELECT * FROM candidates WHERE votes = (SELECT MAX(votes) FROM candidates)";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(queryWin);

            while(rs.next()){
                System.out.println("--------!Winner!--------");
                System.out.println("Candidate ID: " + rs.getString(1));
                System.out.println("Candidate Name: " + rs.getString(2));
                System.out.println("Candidate Party: " + rs.getString(3));
                System.out.println("Votes: " + rs.getInt(4));
                System.out.println("------------------------");
            }
            rs.close(); statement.close(); connection.close();
        }catch (SQLException e){
            System.out.println("DB error");
        }
    }
    /*Printing the ballot results in the system console when the ballot ends*/
    public void graphPrint(){
        Connection connection = DBConnection.getConnection();
        try {
            String queryGraph = "SELECT candidateID, votes FROM candidates";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(queryGraph);
            System.out.println("-----!Ballot Stats!-----\n");
            while(rs.next()){
                System.out.print(rs.getString(1) + " : ");
                graphDataPrint(rs.getInt(2));
            }
            System.out.println("---------------------------End of Ballot----------------------------");
            statement.close(); rs.close(); connection.close();
        }catch (SQLException e){
            System.out.println("DB error");
        }
    }
    /*Printing the number of astrix for each vote*/
    public void graphDataPrint(int count){
        for (int i = 0; i < count; i++){
            System.out.print("*");
        }
        System.out.print("\n");
    }
    /*----------------------------------------------------------------------------------------------------*/
}
