package common;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class Field {
    private ImageView field;
    private ImageView leftBar, rightBar;
    private Image image;
    private double startX, startY, centerX, centerY;
    private double width, height;
    private double goalStartY, goalEndY;
    public Field(AnchorPane pane){
        field = new ImageView();
        image = new Image(getClass().getResourceAsStream("/res/footballFieldmy2.png"));
        field.setImage(image);
        pane.getChildren().add(field);
        width = image.getWidth()*875.0/1024.0;
        height = image.getHeight()*550.0/640.0;

        setPosition(pane.getScene().getWidth()/2-image.getWidth()/2, pane.getScene().getHeight()*0.05);
        /*
        startX = field.getX()+image.getWidth()*75.0/1024.0;
        startY = field.getY()+image.getHeight()*50.0/640.0;
        width = image.getWidth()*875.0/1024.0;
        height = image.getHeight()*550.0/640.0;
        centerX = startX+width/2;
        centerY = startY+height/2;
        goalStartY = field.getY()+image.getHeight()*244/640.0;
        goalEndY = field.getY()+image.getHeight()*406/640.0;
        */



    }

    public double getStartX() {
        return startX;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getStartY() {
        return startY;
    }

    public double getGoalStartY() {
        return goalStartY;
    }

    public double getGoalEndY() {
        return goalEndY;
    }

    public double getCenterX() {
        return centerX;
    }
    public double getCenterY(){
        return centerY;
    }

    public ImageView getImageView() {
        return field;
    }
    public void addBars(Group root){

        leftBar = new ImageView(new Image(getClass().getResourceAsStream("/res/leftBarmyCopy.png")));
        rightBar = new ImageView(new Image(getClass().getResourceAsStream("/res/rightBarmyCopy.png")));
        root.getChildren().addAll(leftBar,rightBar);

        leftBar.setLayoutX(getStartX()-getWidth()*0.03);
        leftBar.setLayoutY(getGoalStartY()-getHeight()*0.045);
        rightBar.setLayoutX(getStartX()+getWidth()-getWidth()*0);
        rightBar.setLayoutY(getGoalStartY()-getHeight()*0.055);
    }
    public void setPosition(double x, double y){
        field.setX(x);
        field.setY(y);
        adjust();
    }
    private void adjust(){
        startX = field.getX()+image.getWidth()*75.0/1024.0;
        startY = field.getY()+image.getHeight()*50.0/640.0;
        centerX = startX+width/2;
        centerY = startY+height/2;
        goalStartY = field.getY()+image.getHeight()*244/640.0;
        goalEndY = field.getY()+image.getHeight()*406/640.0;

        Football.setBound(field.getX(),field.getY(), field.getX()+image.getWidth(), field.getY()+image.getHeight());

        if(leftBar==null||rightBar==null) return;
        leftBar.setLayoutX(getStartX()-getWidth()*0.03);
        leftBar.setLayoutY(getGoalStartY()-getHeight()*0.045);
        rightBar.setLayoutX(getStartX()+getWidth()-getWidth()*0);
        rightBar.setLayoutY(getGoalStartY()-getHeight()*0.055);
    }
}