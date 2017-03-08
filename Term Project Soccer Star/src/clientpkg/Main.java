package clientpkg;


import additional.NetworkUtil;
import additional.Sound;
import javafx.application.Application;
import common.*;
import javafx.application.Platform;
import javafx.stage.Stage;
import sun.nio.ch.Net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;


public class Main {
    Stage window;
    PlayBoard play;
    ClientThread ct;
    static String serverIP = "127.0.0.1";
    static String clientName = "";

    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(clientName+" (Away)");
        play = new PlayBoard(this,false);
        ct = new ClientThread(this);
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        play.showPlayBoard();
        play.showMessage("connecting...",500);


        window.setOnCloseRequest(e->{
            try {
                stop();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        window.show();
    }

    public static void Launch(Stage stage){
        try {
            new Main().start(stage);
            Sound.sound1.stop();
            //Sound.sound2.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() throws Exception {
        //super.stop();
        ct.closeConnections();
        System.exit(0);
    }
    public PlayBoard getPlay() {
        return play;
    }
    public Stage getWindow() {
        return window;
    }

    public static String getServerIP() {
        return serverIP;
    }

    public static void setServerIP(String serverIP) {
        Main.serverIP = serverIP;
    }

    public static void setClientName(String clientName) {
        Main.clientName = clientName;
    }
}

class ClientThread implements Runnable{
    Thread t;
    Socket sock;
    NetworkUtil nc;
    Main main;
    int plus = 0;
    int PORT_COUNT = 15;
    final int START_PORT = 33334;
    String serverIP;
    NetworkUtil[] ncs;
   // Double[] receive;
    ClientThread(Main main){
        this.main = main;

        //receive = new Double[4];
        t = new Thread(this);
        serverIP = Main.getServerIP();
        t.start();
    }

    public void closeConnections(){
       // if(nc!=null)
        //nc.closeConnection();
        //if(ncs!=null){
          //  for(NetworkUtil x: ncs){
              //  x.closeConnection();
            //}
        //}
    }

    @Override
    public void run() {

        try {

            nc = new NetworkUtil();
           int decision =  makeSocket(nc,START_PORT-1+plus,null);
            try{
                String s = nc.read();
                if (s.contains("Port:")) {
                    StringTokenizer st = new StringTokenizer(s);
                    s = st.nextToken();
                    s = st.nextToken();
                    PORT_COUNT = Integer.parseInt(s);

                    System.out.println(PORT_COUNT);
                }
            }catch (Exception e){
                System.out.println(e+ " while receiving port");
                PORT_COUNT = 15;
            }
            ncs = new NetworkUtil[PORT_COUNT];
            for(int i=0; i<PORT_COUNT; i++){

                ncs[i] = new NetworkUtil();
               decision = makeSocket(ncs[i],START_PORT+i+plus,"read");
            }
            if(decision<0) main.getPlay().triggerTerminate();
            String s = (String) nc.read();
            main.getPlay().setNc(nc);
            System.out.println("Server: "+s+"  at "+System.currentTimeMillis());
            for(int i=0; i<PORT_COUNT; i++){
               new ReadThread(main, ncs[i]);
            }
            while(!GameState.errorTooMuch()){

                try {
                    //modify here
                    String goal=(String)nc.read();
                    if(goal==null){
                        continue;
                    }

                    if(goal.equals("Server")){
                        GameState.addServerGoal();
                        main.getPlay().updateScore();
                    }
                   else if(goal.equals("Client")){
                        GameState.addClientGoal();
                        main.getPlay().updateScore();
                    }
                    else if(goal.contains("Time")){
                        main.getPlay().updateTime(goal);
                    }
                    else if(goal.contains("rematch")){
                        main.getPlay().initializeAfterRematch();
                    }
                    else if(goal.equals("end")){
                      Platform.runLater(()->main.getPlay().showSummary());
                    }
                    else if(goal.contains("Ready")){
                        main.getPlay().showMessage("Connection established",4000);
                        main.getPlay().colorize(goal);
                        GameState.setConnectionEstablished(true);
                    }
                    //System.out.println("Data received at "+System.currentTimeMillis());
                   // main.getPlay().setValues(d);
                } catch(Exception e){
                    System.out.println(e+" while receiving data");
                }
            }


        }
        catch(Exception e){
            System.out.println(e);
        }

        Platform.runLater(()->main.getPlay().triggerTerminate());

    }

    private int makeSocket(NetworkUtil nc, int port, String s){

        if(GameState.errorTooMuch()) return -1;

        try {
            Thread.sleep(10);
            Socket sock = new Socket(serverIP,port);
            if (s == null) {
                nc.setSocket(sock);
            } else nc.setSocket(sock, s);
        }
        catch (Exception e){
            GameState.addErrorCount();
            System.out.println(e+" making socket");
            plus++;
            System.out.println("plus: "+plus);
            return makeSocket(nc,port+1,s);
        }
        System.out.println("success");
        return 0;

    }

}
class ReadThread implements Runnable{
    Thread t;
    Main main;
    NetworkUtil nc;
    PlayBoard play;

    public ReadThread(Main main, NetworkUtil nc) {
        this.main = main;
        this.nc = nc;
        t = new Thread(this);
        play = main.getPlay();
        t.start();
    }

    @Override
    public void run() {
        while(!GameState.errorTooMuch()){
            try{
                String s = (String) nc.read();
                if(s==null) continue;
                play.setValues(s);

            }catch (Exception e){
                GameState.addErrorCount();
                System.out.println(e+" in read thread");
            }
        }
        Platform.runLater(()->main.getPlay().triggerTerminate());
    }
}