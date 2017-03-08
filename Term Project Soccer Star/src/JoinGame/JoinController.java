package JoinGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import startpkg.StartMain;

/**
 * Created by Sifat on 12/3/2015.
 */
public class JoinController {
    private StartMain main;

    @FXML
    private Button exit;
    @FXML
    private Label waitLabel;
    @FXML
    private Label loading;

    @FXML
    public void exitAction(ActionEvent actionEvent){
        System.exit(0);
    }

    public void setJoinMain(StartMain main) {
        this.main = main;
    }

    public void setWaitLabel(String s1, String s2) {
        this.waitLabel.setText(s2);
        this.loading.setText(s1);

    }
}
