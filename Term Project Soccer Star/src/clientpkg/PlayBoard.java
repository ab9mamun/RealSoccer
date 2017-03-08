package clientpkg;
import common.*;
import additional.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.w3c.dom.css.Rect;

import java.util.StringTokenizer;


public class PlayBoard {
    final static double NORMAL = 500;
    Group root;
    AnchorPane pane;
    Main main;
    Scene scene;
    long lastFrame = 0;
    int frameCount = 0;
    Football ball;
    Field field;
    int count = 0;
    NetworkUtil nc;
    boolean server;
    Player[] players;
    Collide collide;
    Label score;
    private Label goal;
    private Label lost;
    private Label time;
    private Label summaryTitle,decision;
    private Rectangle table,separator;
    private  Button rematch,exit;
    private Paint homeColor, awayColor;




    public PlayBoard(Main main, boolean server) {
        this.server = server;
        this.main = main;
        root = new Group();
        pane = new AnchorPane();
        scene = new Scene(pane, 1366, 768);
    }


    public void showPlayBoard() {
        Rectangle r = new Rectangle(0,0,1400, 800);
        r.setFill(Color.GRAY);
        pane.getChildren().add(r);
        field = new Field(pane);
        main.getWindow().setMinHeight(760);
        main.getWindow().setMinWidth(1360);
        main.getWindow().setMaximized(true);
        main.getWindow().setScene(scene);
        initializeBoard();

        ball.setBall(3);
        ball.setCenter(field.getCenterX(), field.getCenterY());

        pane.getChildren().add(root);
        scene.setOnKeyPressed(e->{
            if(e.getCode().equals(KeyCode.F4))
                main.getWindow().setFullScreen(true);
        });

    }
   /* private void sendValues(){
        Double[] d= new Double[4];
        d[0] = ball.getCenterX();
        d[1] = ball.getCenterY();
        d[2] = ball.getVelocity();
        d[3] = ball.getAngle();
        try {
            nc.write(d);
        } catch (Exception e) {
            GameState.addErrorCount();
            System.out.println(e+" here");
        }
    }
    */



   synchronized public void setValues(String s){
       if(players==null) return;
       //System.out.println(s);
       StringTokenizer st = new StringTokenizer(s);

       for(int i=0; i<11 && st.hasMoreTokens(); i++) {

           double x = Double.parseDouble(st.nextToken());
           if(!st.hasMoreTokens()) break;
           double y = Double.parseDouble(st.nextToken());
           int j = i-1;
           if(i==0){
               Platform.runLater(()->ball.setCenter(x,y));;
           }
           else Platform.runLater(()->players[j].setCenter(x,y));

       }


    }
    public void setNc(NetworkUtil nc) {
        this.nc = nc;
        Player.setNc(nc);
        while(players== null || players.length<10){

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e+ "in sleep");
            }
        }
        try {
            nc.write("Ready");
        } catch (Exception e) {
            GameState.addErrorCount();
            System.out.println(e+ "while readying");
            if(!GameState.errorTooMuch())setNc(nc);
        }
        GameState.setConnectionEstablished(true);
    }






    private void initializeBoard(){
        Label logo = new Label("Real Soccer");
        logo.setFont(Font.font("AR Destine", 34));
        logo.setLayoutX(scene.getX()+scene.getWidth()/2-scene.getWidth()*0.08);
        logo.setLayoutY(1.5);
        pane.getChildren().addAll(logo);



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
        x[0] = field.getStartX()+field.getWidth()*0.2;
        y[0] = field.getCenterY();
        x[1] = x[2] = x[0];
        y[1] = y[0]+field.getHeight()*0.15;
        y[2] = y[0]-field.getHeight()*0.15;
        y[4] = y[0] - field.getHeight()*0.1;
        y[3] = y[0] + field.getHeight()*0.1;
        x[3] = x[0]+ field.getWidth()*0.15;
        x[4] = x[3];
        for(int i=0; i<5; i++){
            x[5+i] = 2*field.getStartX()+field.getWidth()- x[i];
        }

        for(int i=0; i<5; i++){
            players[i] = new Player(root, Color.BLUE,i, false);
            players[i].setCenter(x[i], y[i]);
            players[5+i] = new Player(root,Color.RED,5+i, true);
            players[5+i].setCenter(x[5+i], y[i]);
        }
        ball = new Football(root);
        collide = new Collide(players,ball);

        Player.getDummy().setFillX(Color.RED);
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

    }

    public void showSummary(){
        // nc.write("end");
        int server = GameState.getServerGoal();
        int client = GameState.getClientGoal();
        String decide = "drawn";
        if(server<client) decide = "won";
        else if(server>client) decide = "lost";

        double fw = field.getWidth(), fh = field.getHeight();
        table = new Rectangle(field.getStartX(), field.getStartY(), fw, fh);
        table.setFill(Color.GRAY);

        summaryTitle = new Label("Match Summary");
        summaryTitle.setFont(Font.font("Aharoni", fw*0.1));

        decision = new Label ("Match "+ decide+ " by  "+server+"-"+client);
        decision.setFont(Font.font("Agency FB", fw*0.08));


        summaryTitle.setLayoutX(table.getX()+table.getWidth()*0.115);
        summaryTitle.setLayoutY(table.getY()+table.getHeight()*0.05);

        separator = new Rectangle(table.getX()+fw*0.08, table.getY()+table.getHeight()*0.25, table.getWidth()*0.84, table.getHeight()*0.075);
        decision.setLayoutX(table.getX()+fw*0.12);
        decision.setLayoutY(table.getY()+fh*0.4);

        rematch = new Button("PLAY AGAIN!");
        rematch.setPrefSize(fw*0.15, fh*0.12);
        rematch.setLayoutX(field.getImageView().getX()-fw*0.15);
        rematch.setLayoutY(field.getCenterY()-fh*0.12/2);
        rematch.setStyle("-fx-base: #009900;");
        rematch.setFont(Font.font("Veranda", fh*0.033));
        rematch.setTextFill(Color.BLACK);

        exit = new Button("EXIT");
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

            try {
                nc.write("Chat: I want to play again");
                if(!GameState.errorTooMuch()) showMessage("(message sent)",1000);
            } catch (Exception e1) {
                GameState.addErrorCount();
                System.out.println(e1+ "here ");
            }

        });
    }

    public void updateScore(){
        Sound.sound2.play();
        Platform.runLater(()->score.setText(GameState.getServerGoal()+"-"+GameState.getClientGoal()));
        goal.setVisible(true);
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(e+" in update score");
                }
                finally {
                    Platform.runLater(()->goal.setVisible(false));
                }
            }
        }.start();
    }
    public void triggerTerminate(){
        lost.setVisible(true);
    }
    public void initializeAfterRematch(){
        Platform.runLater(()->{
            pane.getChildren().removeAll(rematch,exit);
            root.getChildren().removeAll(table, summaryTitle, separator,decision);
            GameState.reset();
            score.setText(GameState.getServerGoal()+"-"+GameState.getClientGoal());
        });


    }

    public void updateTime(String s){
        Platform.runLater(()->time.setText(s));
    }





    public void  showMessage(String s, long durationInMili) {
        new Thread() {
            @Override
            public void run() {

                Label l = new Label(s);
                Platform.runLater(() -> {
                    l.setLayoutX(field.getStartX());
                    l.setLayoutY(field.getStartY() + field.getHeight());
                    l.setFont(Font.font("Veranda", field.getHeight() * 0.05));
                    l.setTextFill(Color.WHITE);
                    root.getChildren().addAll(l);
                });

                try {
                    Thread.sleep(durationInMili);
                } catch (InterruptedException e) {
                    System.out.println(e+" in show message");
                }
                Platform.runLater(() -> root.getChildren().remove(l));
            }
        }.start();
    }



    public void colorize(String s){
        StringTokenizer st = new StringTokenizer(s);
        String home=null, away=null;
        if(st.hasMoreTokens()){
            home = st.nextToken();
        }
        if(st.hasMoreTokens()){
           home = st.nextToken();
        }
        if(st.hasMoreTokens()){
            away = st.nextToken();
        }
        colorize(home,away);
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
            homeColor = Color.BLUE;


        for(int i=0; i<5; i++){
            players[i].setFillX(homeColor);
            players[5+i].setFillX(awayColor);
        }
        Player.getDummy().setFillX(awayColor);
    }

}



