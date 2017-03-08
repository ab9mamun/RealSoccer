package serverpkg;
import additional.NetworkUtil;
import common.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.text.DecimalFormat;
import java.util.StringTokenizer;


public class PlayBoard {
    Main main;
    Group root;
    AnchorPane pane;
    Scene scene;
    long lastFrame = 0;
    long lastFrame2 = 0;
    int frameCount = 0;
    int frameCount2 = 0;
    Football ball;
    Field field;
    int count = 0;
    Player[] players;
    NetworkUtil nc;
    NetworkUtil[] ncs;
    double total = 0;
    boolean server;
    Collide collide;
    long prev;
    int currentNC =0;
    private final long FRAME_INTERVAL = 15;
    private final long REFRESH_INTERVAL = 5;
    private final int  REFRESH_INTERVAL_NANO = 200000;
    private Label score;
    private Label goal;
    private Label lost;
    private double timeFactor;
    private int min;
    private double sec;
    Label time;
    DecimalFormat formatter;
    long prev2;
    long prev3;
    boolean justSent;
    Paint homeColor, awayColor;

    public PlayBoard(Main main) {
        this.main = main;
        root = new Group();
        pane = new AnchorPane();
        scene = new Scene(pane, 1366, 768);

    }



    public void showPlayBoard(String home, String away) {
        Rectangle r = new Rectangle(0,0,1400, 800);
        r.setFill(Color.GRAY);
        pane.getChildren().add(r);
        field = new Field(pane);
        main.getWindow().setMinHeight(760);
        main.getWindow().setMinWidth(1360);
        main.getWindow().setMaximized(true);
        main.getWindow().setScene(scene);

        initializeBoard();
        colorize(home,away);


        ball.setBall(3);
        ball.setCenter(field.getCenterX(), field.getCenterY());

        pane.getChildren().add(root);

        scene.setOnKeyPressed(e->{
            if(e.getCode().equals(KeyCode.F4))
                main.getWindow().setFullScreen(true);
        });

        new AnimationTimer(){
            @Override
            public void handle (long now) {
                    if(min<=90) {
                        updateGameState(now);
                        updateGameState2(now);
                    }
                }
        }.start();
        /*new AnimationTimer(){
            @Override
            public void handle(long now){
                updateGameState2(now);
            }
        }.start();*/

        ///thread to refresh the client window
       /*new Thread(){
            //**** the commented out statements do same as the uncommented, but consumes processor speed 4X than the other
            //                                                                                                      one :p
            @Override
            public void run(){
                while(!GameState.errorTooMuch()){

                    try {
                        Thread.sleep(REFRESH_INTERVAL, REFRESH_INTERVAL_NANO);
                        if(frameCount>0)
                            sendValues();

                    } catch (Exception e) {
                        System.out.println(e+" interrupted");
                    }

                }
                Platform.runLater(()->triggerTerminate());
            }
        }.start();
                */
    }



     private void sendValues(long now) {
        if(ncs==null) return;
         if(now-prev2<2*REFRESH_INTERVAL*1000000 || min>90 ||GameState.errorTooMuch())
             return;

         prev2 = now;
        String s = "";
         s+= ball.getCenterX()+" ";
         s+= ball.getCenterY()+" ";
         for(int i=0; i<10; i++){
             s+= players[i].getCenterX()+" ";
             s+= players[i].getCenterY()+" ";
         }

        try {
            ncs[currentNC].write(s);
            currentNC= (currentNC+1)%ServerThread.PORT_COUNT;
        }catch(Exception e){
            GameState.addErrorCount();
            System.out.println(e+" while sending values");
        }

    }


    private void updateGameState(long now) {

        double elapsedSec = (now-lastFrame) / 1000000000.0;
        if(elapsedSec>FRAME_INTERVAL*1.25/1000.0)
            elapsedSec = FRAME_INTERVAL*1.25/1000.0;

        lastFrame = now;
        if (frameCount > 0) {
            ball.updatePosition(elapsedSec, field);
            for (Player x : players) {
                x.updatePosition(elapsedSec, field);
            }
            collide.detectCollision();
            updateTime(elapsedSec, now);
            sendValues(now);
        }
        frameCount = frameCount % 10000000 + 1;

    }
    private void updateGameState2(long now) {
        double elapsedSec = (now-lastFrame2) / 1000000000.0;
        if(elapsedSec>FRAME_INTERVAL*1.25/1000.0)
            elapsedSec = FRAME_INTERVAL*1.25/1000.0;

        lastFrame2 = now;
        if (frameCount2 > 0) {
            ball.updatePosition(elapsedSec, field);
            for (Player x : players) {
                x.updatePosition(elapsedSec, field);
            }
            collide.detectCollision();
            //sendValues2(now);
        }
        frameCount2 = frameCount2 % 10000000 + 1;

    }

    public void setNc(NetworkUtil nc, NetworkUtil[] ncs) {
        this.nc = nc;
        this.ncs = ncs;
      // GameState.setConnectionEstablished(true);
    }

    public void setValues(String s) {
        try {
            StringTokenizer st = new StringTokenizer(s);
            players[Integer.parseInt(st.nextToken())].setVelocity(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));
        } catch (Exception e) {
            GameState.addErrorCount();
            System.out.println(e+ " while setting values");
        }

    }





    private void initializeBoard() {
        Label logo = new Label("Real Soccer");
        logo.setFont(Font.font("AR Destine", 34));
        logo.setLayoutX(scene.getX()+scene.getWidth()/2-scene.getWidth()*0.08);
        logo.setLayoutY(1.5);
        pane.getChildren().addAll(logo);

        timeFactor = 90.0/GameState.getDuration();

        time = new Label("Time: 00:00");
        time.setTextFill(Color.WHITE);
        time.setFont(Font.font("Brush Script MT", field.getHeight()*0.075));
        time.setLayoutX(field.getStartX()+field.getWidth()-field.getWidth()*0.2);
        time.setLayoutY(field.getStartY()- field.getHeight()*0.1);
        root.getChildren().add(time);


        score= new Label("0-0");
        score.setFont(Font.font("AR Destine", field.getHeight()*0.09));
        score.setTextFill(Color.WHITE);
        goal = new Label("G O A L !!!");
        goal.setFont(Font.font("Informal Roman", field.getHeight()*0.125));
        goal.setTextFill(Color.HOTPINK);

        root.getChildren().add(score);
        score.setLayoutX(field.getCenterX()-field.getWidth()*0.043);
        score.setLayoutY(field.getStartY()-field.getHeight()*0.1);
        goal.setLayoutX(field.getCenterX()-field.getWidth()*0.4);
        goal.setLayoutY(field.getCenterY()-field.getHeight()*0.46);

        players = new Player[10];
        double[] x = new double[10];
        double[] y = new double[5];
        x[0] = field.getStartX() + field.getWidth() * 0.2;
        y[0] = field.getCenterY();
        x[1] = x[2] = x[0];
        y[1] = y[0] + field.getHeight() * 0.15;
        y[2] = y[0] - field.getHeight() * 0.15;
        y[4] = y[0] - field.getHeight() * 0.1;
        y[3] = y[0] + field.getHeight() * 0.1;
        x[3] = x[0] + field.getWidth() * 0.15;
        x[4] = x[3];
        for (int i = 0; i < 5; i++) {
            x[5 + i] = 2 * field.getStartX() + field.getWidth() - x[i];
        }

        for (int i = 0; i < 5; i++) {
            players[i] = new Player(root, Color.BLUE,i, true);
            players[i].setCenter(x[i], y[i]);
            players[5 + i] = new Player(root, Color.RED,5+i, false);
            players[5 + i].setCenter(x[5 + i], y[i]);
        }
        ball = new Football(root);
        ball.setPlay(this);


        collide = new Collide(players, ball);
        Player.addDummy(root);
        root.getChildren().add(goal);
        goal.setVisible(false);
        field.addBars(root);
        lost = new Label("Connection Lost!!");
        lost.setFont(Font.font("Veranda", field.getHeight()*0.045));
        lost.setTextFill(Color.RED);
        lost.setLayoutX(field.getStartX());
        lost.setLayoutY(field.getStartY()-field.getHeight()*0.08);
        root.getChildren().add(lost);
        lost.setVisible(false);

        prev = 0;
        prev2 = 0;
        prev3 = 0;

        formatter = new DecimalFormat("00");
        justSent = false;



    }

    public void initializeAfterGoal() {
        score.setText(GameState.getServerGoal()+"-"+GameState.getClientGoal());
        double[] x = new double[10];
        double[] y = new double[5];
        x[0] = field.getStartX() + field.getWidth() * 0.2;
        y[0] = field.getCenterY();
        x[1] = x[2] = x[0];
        y[1] = y[0] + field.getHeight() * 0.15;
        y[2] = y[0] - field.getHeight() * 0.15;
        y[4] = y[0] - field.getHeight() * 0.1;
        y[3] = y[0] + field.getHeight() * 0.1;
        x[3] = x[0] + field.getWidth() * 0.15;
        x[4] = x[3];
        for (int i = 0; i < 5; i++) {
            x[5 + i] = 2 * field.getStartX() + field.getWidth() - x[i];
        }

        for (int i = 0; i < 5; i++) {
            players[i].stop();
            players[i].setCenter(x[i], y[i]);
            players[5+i].stop();
            players[5 + i].setCenter(x[5 + i], y[i]);
        }
        ball.stop();
        ball.setGoaling(false);
        ball.setCenter(field.getCenterX(),field.getCenterY());
        ball.setGoalTriggered(false);
        goal.setVisible(false);
    }

    public void notifyClient(String s){
        Platform.runLater(()->score.setText(GameState.getServerGoal()+"-"+GameState.getClientGoal()));
        if(s!=null){
            try{
                nc.write(s);
            }catch (Exception e){
                GameState.addErrorCount();
                System.out.println(e+ "notifyClient");
            }
        }
    }

    public void showGoalLabel(){
        Platform.runLater(()->goal.setVisible(true));
    }

    public void updateTime(double sec2, long now){
        if(!GameState.isConnectionEstablished()) return;
        if(min>90) return;

        sec+= sec2*timeFactor;
        min+= sec/60;
        sec%= 60;

        String sMin = formatter.format(min);
        String sSec = formatter.format((int)sec);
        //System.out.println(sMin+" "+sSec);
        String s = "Time: "+sMin+":"+sSec;
        time.setText(s);
        if(min>90){
            showSummary();
        }

        if(now-prev>=100000000) {
            prev = now;
            if(nc==null) return;
            if(!GameState.errorTooMuch()) try {
                nc.write(s);
            } catch (Exception e) {
                GameState.addErrorCount();
                System.out.println(e +" while sending time");
            }
        }
    }

    public void triggerTerminate(){
        lost.setVisible(true);
    }


    public void showSummary(){
        try {
            nc.write("end");
        } catch (Exception e) {
            GameState.addErrorCount();
            System.out.println(e+" showSummary");
        }

        int server = GameState.getServerGoal();
        int client = GameState.getClientGoal();
        String decide = "drawn";
        if(server>client) decide = "won";
        else if(server<client) decide = "lost";

        double fw = field.getWidth(), fh = field.getHeight();
        Rectangle table = new Rectangle(field.getStartX(), field.getStartY(), fw, fh);
        table.setFill(Color.GRAY);

        Label summaryTitle = new Label("Match Summary");
        summaryTitle.setFont(Font.font("Aharoni", fw*0.1));

        Label decision = new Label ("Match "+ decide+ " by  "+server+"-"+client);
        decision.setFont(Font.font("Agency FB", fw*0.08));


        summaryTitle.setLayoutX(table.getX()+table.getWidth()*0.115);
        summaryTitle.setLayoutY(table.getY()+table.getHeight()*0.05);

        Rectangle separator = new Rectangle(table.getX()+fw*0.08, table.getY()+table.getHeight()*0.25, table.getWidth()*0.84, table.getHeight()*0.075);
        decision.setLayoutX(table.getX()+fw*0.12);
        decision.setLayoutY(table.getY()+fh*0.4);

        Button rematch = new Button("PLAY AGAIN!");
        rematch.setPrefSize(fw*0.15, fh*0.12);
        rematch.setLayoutX(field.getImageView().getX()-fw*0.15);
        rematch.setLayoutY(field.getCenterY()-fh*0.12/2);
        rematch.setStyle("-fx-base: #009900;");
        rematch.setFont(Font.font("Veranda", fh*0.033));
        rematch.setTextFill(Color.BLACK);

        Button exit = new Button("EXIT");
        exit.setPrefSize(fw*0.15, fh*0.12);
        exit.setLayoutX(field.getImageView().getX()+field.getImageView().getImage().getWidth());
        exit.setLayoutY(field.getCenterY()-fh*0.12/2);
        exit.setStyle("-fx-base: #f90000;");
        exit.setFont(Font.font("Veranda", fh*0.045));
        exit.setTextFill(Color.BLACK);

        root.getChildren().addAll(table, summaryTitle,separator, decision);
        pane.getChildren().addAll(rematch, exit);

        exit.setOnAction(e->System.exit(0));
        rematch.setOnAction(e->{
            pane.getChildren().removeAll(rematch,exit);
            root.getChildren().removeAll(table, summaryTitle, separator,decision);
            
            initializeForRematch();

        });
    }

    public void  showMessage(String s, long durationInMili){
        new Thread(){
            @Override
            public  void  run(){

                Label l=new Label(s);
                Platform.runLater(()->{
                    l.setLayoutX(field.getStartX());
                    l.setLayoutY(field.getStartY()+field.getHeight());
                    l.setFont(Font.font("Veranda",field.getHeight()*0.05));
                    l.setTextFill(Color.WHITE);
                    root.getChildren().addAll(l);
                });

                try {
                    Thread.sleep(durationInMili);
                } catch (InterruptedException e) {
                    GameState.addErrorCount();
                    System.out.println(e+" while showing message");
                }
                Platform.runLater(()->root.getChildren().remove(l));
            }
        }.start();

    }

    private void initializeForRematch(){
        min = 0;
        sec = 0;
        GameState.reset();
        try {
            nc.write("rematch");
        } catch (Exception e) {
            GameState.addErrorCount();
            System.out.println(e+ "initializeForRematch");
        }
        initializeAfterGoal();
    }


    private void colorize(String home, String away){
         if(home.equals("Black")){
            homeColor = Color.BLACK;
        }
        else  if(home.equals("Green")){
            homeColor = Color.FORESTGREEN;
        }
        else  if(home.equals("Pink")){
            homeColor = Color.HOTPINK;
        }
        else  if(home.equals("Orange")){
            homeColor = Color.ORANGE;
        }
        else homeColor = Color.BLUE;

        if(away.equals("RED")){
            awayColor = Color.RED;
        }
        else  if(away.equals("Black")){
            awayColor = Color.BLACK;
        }
        else  if(away.equals("Green")){
            awayColor = Color.FORESTGREEN;
        }
        else  if(away.equals("Pink")){
            awayColor = Color.HOTPINK;
        }
        else  if(away.equals("Orange")){
            awayColor = Color.ORANGE;
        }
        else awayColor = Color.RED;

        if(home.equals(away))
            awayColor = Color.RED;


        for(int i=0; i<5; i++){
            players[i].setFillX(homeColor);
            players[5+i].setFillX(awayColor);
        }
        Player.getDummy().setFillX(homeColor);
    }

}