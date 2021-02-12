package MainMenu.voteCast;

import JavaClass.AlertBox;
import JavaClass.Candidate;
import JavaClass.DBConnection;
import JavaClass.Voter;
import com.mysql.cj.result.Row;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VoteCastPanelController implements Initializable {
    @FXML
    GridPane GDPaneCandidates;
    @FXML
    ScrollPane scrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getCandidates();
    }
    /*Getting all the candidates from the database*/
    public void getCandidates(){
        ArrayList<Candidate> allCandidates = new ArrayList<>();

        Connection connection = DBConnection.getConnection();

        if (connection!=null){
            try{
                String query = "SELECT candidateID, candidateName,candidateParty FROM candidates";
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                while(rs.next()){
                    Candidate candidate = new Candidate(rs.getString(1), rs.getString(2),rs.getString(3));
                    allCandidates.add(candidate);
                    System.out.println("ID :" + candidate.getCandidateId() + "\nName: " + candidate.getCandidateName() + "\nParty: " +candidate.getCandidateParty());
                }
                showCandidates(allCandidates);
            }catch (SQLException e){
                System.out.println("Error executing the query");
            }
        }
    }
    /*Adding the candidate information into blocks*/
    public void showCandidates(ArrayList<Candidate> allCandidates){
        int row = 0; int col = 0;
        for (Candidate candidate: allCandidates){
            ColumnConstraints columnConstraints = new ColumnConstraints(450,450,450);
            columnConstraints.setHgrow(Priority.NEVER);
            RowConstraints rowConstraints = new RowConstraints(300,300,300);
            rowConstraints.setVgrow(Priority.NEVER);
            GDPaneCandidates.getRowConstraints().add(rowConstraints);
            GDPaneCandidates.getColumnConstraints().add(columnConstraints);
            GridPane candidateGD = new GridPane();
            Label id = new Label(candidate.getCandidateId());
            Label name = new Label(candidate.getCandidateName());
            Label party = new Label(candidate.getCandidateParty());
            Button voteBtn = new Button("Vote");
            voteBtn.setOnAction(e-> voteMenu(candidate,candidateGD));
            candidateGD.setPrefWidth(450);
            candidateGD.setPrefHeight(300);
            candidateGD.add(id,0,0);
            candidateGD.add(name,0,1);
            candidateGD.add(party,0,2);
            candidateGD.add(voteBtn,0,3);
            candidateGD.setAlignment(Pos.CENTER);
            GDPaneCandidates.add(candidateGD,col,row);
            if (col == 3){
                col = 0;
                row++;
            }else {
                col++;
            }
            System.out.println("Candidates successfully loaded");
        }
    }
    /*Creating the vote menu to popup when voter select the respective candidate*/
    public void voteMenu(Candidate candidate,GridPane candidateGD){
        GDPaneCandidates.setDisable(true);
        Stage voteStage = new Stage();
        GridPane root = new GridPane();
        //adding the css file to the new popup window
        String css = this.getClass().getResource("../../CSS/voteCastPanel.css").toExternalForm();
        root.getStylesheets().add(css);
        voteStage.setScene(new Scene(root,400,400));
        root.setAlignment(Pos.CENTER);
        Label id = new Label(candidate.getCandidateId());
        Label name = new Label(candidate.getCandidateName());
        Label party = new Label(candidate.getCandidateParty());
        Button castVote = new Button("Confirm your Vote");
        castVote.setOnAction(e-> vote(candidate,voterIDPromptController.getVoter(),root,candidateGD));
        root.add(id,0,0);
        root.add(name,0,1);
        root.add(party,0,2);
        root.add(castVote,0,3);
        voteStage.setAlwaysOnTop(true);
        voteStage.initStyle(StageStyle.UTILITY);
        voteStage.showAndWait();
        GDPaneCandidates.setDisable(false);
    }
    /*Casting the vote to the selected candidate*/
    public void vote(Candidate candidate, Voter voter,GridPane root,GridPane candidateMenuGD){
        Connection connection = DBConnection.getConnection();
        if (connection!=null){
            try{
                String queryVoter = "UPDATE voters v SET v.voted = 1 WHERE v.voterID = ?";
                String queryCandidate = "UPDATE candidates c SET c.votes = c.votes + 1 WHERE c.candidateID = ?";
                PreparedStatement statementC = connection.prepareStatement(queryCandidate);
                statementC.setString(1,candidate.getCandidateId());
                PreparedStatement statementV = connection.prepareStatement(queryVoter);
                statementV.setString(1,voter.getVoterEID());
                int rowsV = statementV.executeUpdate();
                if (rowsV > 0){
                    System.out.println("Vote casting recorded on voter");
                }
                int rowsC = statementC.executeUpdate();
                if (rowsC > 0){
                    System.out.println("Vote successfully casted to the candidate");
                }
                redirect(candidateMenuGD,root);

            }catch(SQLException e){
                System.out.println("Database error");
                AlertBox.errorAlert("Fatal Error","Fatal Error!\nPlease contact a staff member without closing the Error message");
            }
        }
    }
    /*Showing the thank you window*/
    public void redirect(GridPane candidateMenuGD, GridPane castVoteMenu){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("thankYouWindow.fxml"));
            Stage thankYouStage = new Stage();
            thankYouStage.initStyle(StageStyle.UNDECORATED);
            thankYouStage.setScene(new Scene(root,600,400));
            Stage voteMenu = (Stage) castVoteMenu.getScene().getWindow();
            voteMenu.setAlwaysOnTop(false);

            thankYouStage.showAndWait();

            voteMenu.close();
            Stage candidatesMenu = (Stage) candidateMenuGD.getScene().getWindow();
            candidatesMenu.close();
        }catch (IOException e){
            System.out.println("thankYouWindow.fxml: file not found");
        }
    }
}
