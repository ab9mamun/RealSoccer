package common;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Football extends Moving{
    private Image[] image;
    private ImageView imageView;
    Field field;
    double r;
    static double x1, x2, y1, y2;



    public Football(Group root){
        //setVelocity(250,Math.PI/4.0);
        setA(70);
        setMass(1.5);
        stop();
        //setCenter(field.getCenterX(),field.getCenterY());
        imageView = new ImageView();
        image = new Image[3];
        image[0] = new Image(getClass().getResourceAsStream("/res/ball1.jpg"));
        image[1] = new Image(getClass().getResourceAsStream("/res/ball2.jpg"));
        image[2] = new Image(getClass().getResourceAsStream("/res/ball3.png"));
        imageView.setImage(image[0]);
        setRadius(imageView.getImage().getWidth()/2);
        r = getRadius();
        root.getChildren().addAll(imageView);
        setEnterGoal(true);

    }
    public void setBall(int i){
        if(i<1||i>3) return;
        imageView.setImage(image[i-1]);
    }

    @Override
    public void setVisibility(double x, double y) {

        if(x<x1+r|| x>x2-r || y<y1+r || y>y2+r){
            imageView.setX(1500.0);
            imageView.setY(1200.0);
        }
        else {
            imageView.setX(x - imageView.getImage().getWidth() / 2);
            imageView.setY(y - imageView.getImage().getHeight() / 2);
        }
    }


    public static void setBound(double x1,double y1,double x2,double y2) {
        Football.x1 = x1;
        Football.y1 = y1;
        Football.x2 = x2;
        Football.y2 = y2;
       // System.out.println(x1+" "+x2+""+y1+""+y2);
    }
}
