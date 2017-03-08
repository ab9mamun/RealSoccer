package serverpkg;


import additional.NetworkUtil;
import additional.Sound;
import common.GameState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    Stage window;
    PlayBoard play;
    ServerThread st;
    static String serverName="";
    static String homeColor = "Blue";
    static String awayColor = "Red";
    static int portCount = 15;


    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(serverName+" (Home)");

        play = new PlayBoard(this);
        st = new ServerThread(this);
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        play.showPlayBoard(homeColor, awayColor);
        play.showMessage("connecting...",500);



        //Thread.sleep(1000);
        window.setOnCloseRequest(e -> {
            try {
                stop();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        window.show();
    }

   /* public static void main(String[] args) {
        launch(args);
    }*/
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
        System.out.println("closing");
        st.closeConnections();
        System.exit(0);
    }

    public Stage getWindow() {
        return window;
    }

    public PlayBoard getPlay() {
        return play;
    }


    public static void setServerName(String serverName) {
        Main.serverName = serverName;
    }

    public static void setAwayColor(String awayColor) {
        Main.awayColor = awayColor;
    }

    public static void setHomeColor(String homeColor) {
        Main.homeColor = homeColor;
    }

    public static String getAwayColor() {
        return awayColor;
    }

    public static String getHomeColor() {
        return homeColor;
    }

    public static void setPortCount(int portCount) {
        Main.portCount = portCount;
    }

    public static int getPortCount() {
        return portCount;
    }
}

class ServerThread implements Runnable{
    Thread t;
    Socket sock;
    ServerSocket servSock;
    NetworkUtil nc;
    NetworkUtil[] ncs;
    static final int PORT_COUNT = Main.getPortCount();
    static final int START_PORT = 33334;
    int plus = 0;
   // Double[] receive;
    Main main;
    ServerThread(Main main){
        this.main = main;
        t = new Thread(this);
        ncs = new NetworkUtil[PORT_COUNT];
        //receive = new Double[4];
        t.start();
    }
    public void closeConnections(){
        //if(nc!=null)
         //   nc.closeConnection();
       // if(ncs!=null){
          //  for(NetworkUtil x: ncs){
             //   x.closeConnection();
           // }
        //}
    }

    @Override
    public void run() {

        try {


            nc = new NetworkUtil();
            System.out.println("here i am");
            int condition = makeSocket(nc,START_PORT-1+plus,null);
            System.out.println("i passed");
            try {
                nc.write("Port: " + PORT_COUNT);
                System.out.println("Port: "+ PORT_COUNT);
            }catch (Exception e){
                System.out.println(e+" while sending port");
            }

            for(int i=0; i<PORT_COUNT; i++) {
                ncs[i] = new NetworkUtil();
                condition = makeSocket(ncs[i],START_PORT+i+plus,"write");
            }


            if(condition<0) main.getPlay().triggerTerminate();
            System.out.println("Connected" + "  at " + System.currentTimeMillis());
            try {
                nc.write("Hi Client, my time is: " + System.currentTimeMillis());
            }
            catch (Exception e){
                System.out.println(e+ "In hi");
            }
            //Thread.sleep(100);
            main.getPlay().setNc(nc,ncs);

        }
        catch(Exception e){
            GameState.addErrorCount();
            System.out.println(e+" in main try");
        }
        while(!GameState.errorTooMuch()){

            try {
                String s = (String ) nc.read();
                if(s==null) continue;
                System.out.println("Data received at "+System.currentTimeMillis());

                if(s.contains("Chat:")){
                    main.getPlay().showMessage(s,4000);

                }
                else if(s.contains("Ready")){
                    main.getPlay().showMessage("Connection established",4000);
                    try {
                        nc.write("Ready: "+Main.getHomeColor()+" "+Main.getAwayColor());
                    }catch (Exception e){
                        System.out.println(e+" while ready");
                        GameState.addErrorCount();
                    }
                    GameState.setConnectionEstablished(true);
                }

                else main.getPlay().setValues(s);

            } catch(Exception e){
                GameState.addErrorCount();
                System.out.println(e+" while receiving data");
            }
        }
        main.getPlay().triggerTerminate();

    }
    private int makeSocket(NetworkUtil nc, int port, String s){

        if(GameState.errorTooMuch()) return -1;

        try {
            ServerSocket servSock = new ServerSocket(port);
            Socket sock = servSock.accept();
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