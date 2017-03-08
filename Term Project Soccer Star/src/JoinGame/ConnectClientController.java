package JoinGame;

import clientpkg.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import startpkg.StartMain;
import additional.NetworkUtil;

import java.net.Socket;

/**
 * Created by Sifat on 12/3/2015.
 */
public class ConnectClientController {
    private StartMain main;

    @FXML
    private TextField clientName;

    @FXML
    private TextField serverIP;

    private String serverAddress = "127.0.0.1";
    private int serverPort = 30000;

    @FXML
    public void connectAction(ActionEvent actionEvent) {
        try {
            NetworkUtil nc = new NetworkUtil(new Socket(serverIP.getText(), serverPort));
            clientpkg.Main.setServerIP(serverIP.getText());
            nc.write(clientName.getText());
            main.getStage().setTitle(clientName.getText());
            clientpkg.Main.setClientName(clientName.getText());
            main.showJoinClientPage();
            clientpkg.Main.setServerIP(serverIP.getText());


            new Thread(){
                @Override
                public void run() {

                    while (true) {
                        try {
                            String s = (String) nc.read();
                            if (s == null) continue;
                            if (s.equals("start")) {
                                Thread.sleep(1600);
                                Platform.runLater(()->Main.Launch(main.getStage()));

                                break;
                            }
                            else if(s.equals("refuse")){
                                Platform.runLater(()->main.getJc().setWaitLabel("Sorry..", " Server is playing with someone else"));
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }.start();


            System.out.println("hi");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setConnectClientMain(StartMain main) {
        this.main = main;
    }
}
