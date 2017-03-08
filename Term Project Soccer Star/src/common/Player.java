package common;

import additional.NetworkUtil;
import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.prism.shader.DrawCircle_LinearGradient_PAD_AlphaTest_Loader;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;


public class Player extends Moving {

    static GameState state;
    static NetworkUtil nc;
    Circle outBorder;
    Circle inBorder;
    Circle inCircle;
    double sX, sY;
    static Player dummy = new Player(Color.BLUE);
    static Line line = new Line();
    static{
        line.setVisible(false);
        dummy.setVisibleX(false);
    }


    @Override
    public void setVisibility(double x, double y) {
        outBorder.setCenterX(x);
        outBorder.setCenterY(y);
        inCircle.setCenterX(x);
        inCircle.setCenterY(y);
        inBorder.setCenterX(x);
        inBorder.setCenterY(y);
    }

    public void setVisibleX(boolean bool){
        outBorder.setVisible(bool);
        inBorder.setVisible(bool);
        inCircle.setVisible(bool);
    }

    public void setFillX(Paint fill){
        inCircle.setFill(fill);
        if(fill.equals(Color.ORANGE)||fill.equals(Color.HOTPINK)){
            inBorder.setFill(Color.WHITE);
        }
    }

    private Player(Paint fill){
        outBorder = new Circle(24,Color.BLACK);
        outBorder.setOpacity(0.5);
        inBorder = new Circle(21,Color.NAVAJOWHITE);
        inBorder.setOpacity(0.5);
        inCircle = new Circle(16,fill);
        inCircle.setOpacity(0.5);
    }

    //constructor
    public Player(Group root, Paint fill,int i, boolean controllable){

        outBorder = new Circle(24,Color.BLACK);
        inBorder = new Circle(21,Color.NAVAJOWHITE);
        this.setFill(Color.BLACK);
        inCircle = new Circle(16,fill);

        setVelocity(0,0);
        setA(80);
        setMass(2);
        setRadius(outBorder.getRadius());

        root.getChildren().addAll(outBorder,inBorder, inCircle);
        setEnterGoal(false);

        outBorder.setOnMouseDragged(e->{
            if(!controllable) return;
            dragControl(e);
        });
        inBorder.setOnMouseDragged(e->{
            if(!controllable) return;
            dragControl(e);
        });
        inCircle.setOnMouseDragged(e->{
            if(!controllable) return;
            dragControl(e);
        });

        inCircle.setOnMouseReleased(e->{
            if(!controllable) return;
            releaseControl(e,i);
        });
        outBorder.setOnMouseReleased(e->{
            if(!controllable) return;
            releaseControl(e,i);
        });
        inBorder.setOnMouseReleased(e->{
            if(!controllable) return;
            releaseControl(e,i);
        });

    }

    public static Player getDummy() {
        return dummy;
    }
    public static void addDummy(Group root){
        root.getChildren().addAll(dummy.outBorder,dummy.inBorder,dummy.inCircle, line);
    }
    public static void setNc(NetworkUtil nc) {
        Player.nc = nc;
    }

    private void dragControl(MouseEvent e){
        //System.out.println("i'm there");
        dummy.setVisibleX(true);
        dummy.setCenter(e.getX(),e.getY());
       // dummy.makeVisible();
        line.setVisible(true);
        line.setStartX(e.getX());
        line.setStartY(e.getY());
        line.setEndX(getCenterX());
        line.setEndY(getCenterY());
    }
    private void releaseControl(MouseEvent e, int i){

        dummy.setVisibleX(false);
        if(line.isVisible()==false) return;
        line.setVisible(false);
        if(!GameState.isConnectionEstablished()) return;
        // if(!state.equals(GameState.MyTurn)) return;
        System.out.println("I'm there");
        double dist = findDistance2(e.getX(),e.getY(),getCenterX(), getCenterY());
        double angle = findAngle(e.getX(),e.getY(), getCenterX(),getCenterY());
        if(dist>10000){
            this.setVelocity(350, angle);
            state = GameState.YourTurn;
        }
        else if(dist>getRadius()*0.9*0.9*getRadius()){
            this.setVelocity(Math.sqrt(dist)*5,angle);
            state = GameState.YourTurn;
        }
        if(nc!=null) {
            try {
                String s = i+" "+ getVelocity()+" "+ getAngle();
                nc.write(s);
            }catch (Exception exp){
                GameState.addErrorCount();
                System.out.println(exp+" in Player class nc");
            }
        }
    }
}
