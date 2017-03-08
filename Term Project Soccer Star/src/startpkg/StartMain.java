package startpkg;

import JoinGame.ConnectClientController;
import JoinGame.JoinController;
import additional.Sound;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import CreateGame.ServerController;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Created by Sifat on 12/4/2015.
 */
public class StartMain extends Application {

    Stage stage;
    private JoinController jc;
    private Scene mainScene, faqScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        //stage.setFullScreen(true);
        showStartPage();
        Sound.sound1.loop();

    }


    //@Override
    public void showServerPlayPage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/CreateGame/Server.fxml"));
        Parent root = loader.load();

        ServerController controller = loader.getController();
        controller.initializeColumns();
        controller.setStartMain(this);
        stage.setTitle("Server");
        Scene scene = new Scene(root,1366,768);
        stage.setScene(scene);
        scene.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.F10)) {
                controller.enablePortEdit();
            }
        });
        //stage.show();

    }
    public void showStartPage() throws Exception {

        //Sound.sound2.loop();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Start.fxml"));
        Parent root = loader.load();


        StartController controller = loader.getController();
        controller.setMain(this);

        stage.setTitle("Start");
        mainScene = new Scene(root,1366,768);
        stage.setScene(mainScene);
        stage.setMaximized(true);
        stage.setMinHeight(760);
        stage.setMinWidth(1366);
        stage.show();

        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("faq.fxml"));
        root = loader.load();


        faqController faq = loader.getController();
        faq.setMain(this);
        faqScene = new Scene(root,1366,768);
    }

    public void showConnectClientPage() throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/JoinGame/ConnectClient.fxml"));
        Parent root = loader.load();


        ConnectClientController controller = loader.getController();
        controller.setConnectClientMain(this);

        stage.setTitle("Client");
        stage.setScene(new Scene(root, 1366, 768));
        //stage.show();
    }

    public void showJoinClientPage() throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/JoinGame/Join.fxml"));
        Parent root = loader.load();
        JoinController controller = loader.getController();
        jc = controller;
        controller.setJoinMain(this);

       // stage.setTitle("Mode");
        stage.setScene(new Scene(root, 1366, 768));

    }

    public JoinController getJc() {
        return jc;
    }

    public Stage getStage() {
        return stage;
    }
    public void showMainMenu(){
        stage.setScene(mainScene);
    }
    public void showFaqPage(){
        stage.setScene(faqScene);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}












