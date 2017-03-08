package CreateGame;

import additional.NetworkUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import sun.nio.ch.Net;

import java.util.Enumeration;

/**
 * Created by Sifat on 11/27/2015.
 */
public class Person {
    private SimpleStringProperty clientName;
    private Button action;


    public Person(String clientName, NetworkUtil nc, ServerController sc) {
        this.clientName = new SimpleStringProperty(clientName);
        action = new Button("Play");

        action.setOnAction(e->{
            System.out.println("I love it");
            try {

                nc.write("start");
            }catch (Exception exp){
                System.out.println(exp);
            }
            Enumeration x = sc.getNetTable().keys();
            while(x.hasMoreElements()){
                try {
                    sc.getNetTable().get(x.nextElement()).write("refuse");
                }catch (Exception e1){
                    System.out.println(e+" while refusing");
                }
            }

                serverpkg.Main.setServerName(sc.getServerName());

                serverpkg.Main.Launch(sc.getStartMain().getStage());

        });
    }

    public String getClientName() {
        return clientName.get();
    }

    public void setAction(Button action) {
        this.action = action;
    }

    public SimpleStringProperty clientNameProperty() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName.set(clientName);
    }

    public Button getAction() {
        return action;
    }
}