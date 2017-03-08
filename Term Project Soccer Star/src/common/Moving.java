package common;

import additional.Sound;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import serverpkg.PlayBoard;


abstract public class Moving extends Circle {

    serverpkg.PlayBoard play;
    protected double angle;
    protected double velocity;
    protected double a;
    private boolean enterGoal;
    private double mass;
    private boolean goaling = false;
    private boolean goalTriggered = false;

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;

    }

    public double vx() {
        return velocity*Math.cos(angle);
    }


    public double vy() {
        return velocity*Math.sin(angle);
    }



    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(double velocity, double angle){
        this.velocity = velocity;
        this.angle = angle;
    }
    public void makeVelocity(double vx, double vy){
        velocity = Math.sqrt(vx*vx+vy*vy);
        angle = (Math.atan2(vy,vx)+2*Math.PI)%(2*Math.PI);
    }
    public void makeVelocity(double v1, double angle1, double v2, double angle2){
        double vx = v1*Math.cos(angle1)+v2*Math.cos(angle2);
        double vy = v1*Math.sin(angle1)+v2*Math.sin(angle2);
        makeVelocity(vx, vy);
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public boolean canEnterGoal() {
        return enterGoal;
    }

    public void setEnterGoal(boolean enterGoal) {
        this.enterGoal = enterGoal;
    }



    synchronized public void setCenter(double x, double y){
        setCenterX(x);
        setCenterY(y);
        setVisibility(x, y);
    }

    abstract public void setVisibility(double x, double y);

    //returns square of distance between two points
    public static double findDistance2(double x, double y, double x2, double y2){

        return (x-x2)*(x-x2)+(y-y2)*(y-y2);
    }
    public static double findAngle(double x, double y, double x2, double y2){

        return (Math.atan2(y2-y, x2-x)+2*Math.PI)%(2*Math.PI);
    }

    public void detectWallCollisions(Field field) {
        //goal
        if(goaling) return;
            if (canEnterGoal() && getCenterX() < field.getStartX() && getCenterY() > field.getGoalStartY() + getRadius() &&
                    getCenterY() < field.getGoalEndY() - getRadius()) {
                goaling = true;
                clientGoal();

                return;
            }
            if (canEnterGoal() && getCenterX() > field.getStartX() + field.getWidth() && getCenterY() > field.getGoalStartY() + getRadius() &&
                    getCenterY() < field.getGoalEndY() - getRadius()) {
                goaling = true;
                serverGoal();

                return;
            }


        if (getCenterX() <=(field.getStartX()+ getRadius())) {

            if(canEnterGoal()&& getCenterY()>=field.getGoalStartY() && getCenterY() <= field.getGoalEndY()){
                detectBarCollisions(field, field.getStartX());
                return;
            }
            setCenterX(field.getStartX()+getRadius());

            //vx = -vx; that means cos = -cos but sin unchanged
            angle = (3*Math.PI-angle)%(2*Math.PI);
            velocity*=GameState.collisionFactor;
        }
        else if (getCenterX() >= (field.getStartX()+field.getWidth() - getRadius())) {
            if(canEnterGoal()&& getCenterY()>=field.getGoalStartY() && getCenterY() <= field.getGoalEndY()){
                detectBarCollisions(field, field.getStartX()+field.getWidth());
                return;
            }

            setCenterX((field.getStartX()+field.getWidth() - getRadius()));
            //vx = -vx;

            angle = (3*Math.PI-angle)%(2*Math.PI);
            velocity*=GameState.collisionFactor;
        }
        ///////
        if (getCenterY() <=(field.getStartY()+ getRadius())) {

            setCenterY(field.getStartY() + getRadius());

            //vy = -vy; that means sin = -sin but cos unchanged
            angle = (-angle+4*Math.PI)%(2*Math.PI);
            velocity*=GameState.collisionFactor;
        }
        else if (getCenterY() >= (field.getStartY()+field.getHeight() - getRadius())) {

            setCenterY((field.getStartY()+field.getHeight() - getRadius()));
            //vy = -vy;
            angle = (-angle+4*Math.PI)%(2*Math.PI);
            velocity*=GameState.collisionFactor;
        }
        setVisibility(getCenterX(), getCenterY());

    }

    public void updatePosition(double elapsed, Field field) {

        setCenter(getCenterX() + (vx() * elapsed), getCenterY() + (vy() * elapsed));

        detectWallCollisions(field);

        if(velocity<=a*elapsed) {
            velocity  = 0;
        }
        else{
            velocity-= a*elapsed;
        }
    }

    synchronized private void serverGoal(){
        if(goalTriggered) return;
        goalTriggered = true;
        Sound.sound2.play();
        GameState.addServerGoal();
        System.out.println("ServerScored");
        play.notifyClient("Server");
        play.showGoalLabel();
        new Thread(){
            @Override
            public void run(){
                try{
                    Thread.sleep(2000);
                    goaling = false;
                }catch (Exception e){
                    goaling = false;
                    System.out.println(e);
                }

                goaling = false;
                Platform.runLater(()->play.initializeAfterGoal());

            }
        }.start();
    }
    synchronized private void clientGoal(){
        if(goalTriggered) return;
        goalTriggered = true;
        Sound.sound2.play();
        GameState.addClientGoal();
        System.out.println("ClientScored");
        play.notifyClient("Client");
        play.showGoalLabel();
        new Thread(){
            @Override
            public void run(){
                try{
                    Thread.sleep(2000);
                    goaling = false;
                }catch (Exception e){
                    goaling = false;
                    System.out.println(e);
                }

                goaling = false;
                Platform.runLater(()->play.initializeAfterGoal());

            }
        }.start();
    }

    public void setPlay(PlayBoard play) {
        this.play = play;
    }
    public void stop(){
        setVelocity(0,0);
    }


    private boolean detectBarCollisions(Field field,double x){
       if(findDistance2(getCenterX(),getCenterY(),x, field.getGoalStartY())<=getRadius()*getRadius()){
           detectBarCollisions(x, field.getGoalStartY());
           return true;
       }
        else if(findDistance2(getCenterX(),getCenterY(),x, field.getGoalEndY())<=getRadius()*getRadius()){
           detectBarCollisions(x, field.getGoalEndY());
           return true;
       }
        return false;
    }
    private void detectBarCollisions(double x, double y){
        double x1 = getCenterX();
        double x2 = getCenterY();
        double ang = findAngle(x1, x2, x, y);
        double a1, a2;
        double v1, v2;
        setCenter(x - getRadius()*Math.cos(ang)-2*Math.cos(ang), y- getRadius()*Math.sin(ang)-2*Math.sin(ang));
        a1 = ang+Math.PI;
        v1 = velocity*Math.cos(angle-ang);
        if(angle>ang){
            a2 = ang+Math.PI/2;
        }
        else  {
            a2 = ang-Math.PI/2;
        }
        v2 = velocity*Math.cos(angle-a2);
        makeVelocity(v1,a1, v2, a2);
        velocity*=GameState.collisionFactor;
    }

    public void makeVisible(){
        setVisibility(getCenterX(), getCenterY());
    }

    public void setGoaling(boolean goaling) {
        if(!canEnterGoal()) return;
        this.goaling = goaling;
    }

    public void setGoalTriggered(boolean goalTriggered) {
        this.goalTriggered = goalTriggered;
    }


}
