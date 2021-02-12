package MainMenu.voteCast;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class thankYouWindowController implements Initializable {
    @FXML
    Label thankYou;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Timer initialized");
        //Creating a time interval before redirecting the voterIDPrompt.fxml
        Timeline interval = new Timeline(new KeyFrame(Duration.seconds(3),e-> redirect()));
        interval.setCycleCount(1);
        interval.play();
        System.out.println("Timer has passed");
    }
    @FXML
    /*Redirecting to voterIDPrompt.fxml*/
    public void redirect(){
        try {
            Stage thisStage = (Stage) thankYou.getScene().getWindow();
            thisStage.close();
            System.out.println("Thank you window closed");
            Parent root = FXMLLoader.load(getClass().getResource("voterIDPrompt.fxml"));
            Stage newVote = new Stage();
            newVote.initStyle(StageStyle.UNDECORATED);
            newVote.setTitle("US PRESIDENTIAL ELECTION 2020: Cast your Vote");
            newVote.setScene(new Scene(root,1920,1080));
            newVote.show();

        }catch (IOException e){
            System.out.println();
        }
    }

}
