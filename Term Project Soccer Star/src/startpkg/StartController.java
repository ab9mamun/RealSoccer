package startpkg;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by Sifat on 12/3/2015.
 */
public class StartController {
    private StartMain main;
    @FXML
    private Button createGame;
    @FXML
    private Button joinGame;
    @FXML
    private Button exit;

    @FXML
    public void CreateGameAction(ActionEvent actionEvent) {
        try {
            main.showServerPlayPage();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void JoinGameAction(ActionEvent event){
        try{
            main.showConnectClientPage();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    public void showFaqPage(ActionEvent event){
        main.showFaqPage();
    }

    @FXML
    public void exitAction(ActionEvent actionEvent){
        System.exit(0);
    }

    public void setMain(StartMain main) {
        this.main = main;
    }
}
