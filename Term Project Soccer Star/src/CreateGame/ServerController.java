package CreateGame;

import additional.NetworkUtil;
import common.GameState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import serverpkg.Main;
import startpkg.StartMain;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by Sifat on 11/27/2015.
 */

public class ServerController implements Runnable {
    @FXML
    private TableView<Person> table;
    @FXML
    private ComboBox<String> timeChoice;
    @FXML
    private ComboBox<String> homeColor;
    @FXML
    private ComboBox<String> awayColor;
    @FXML
    private TextField serverName;
    @FXML
    private ComboBox<String> portCount;

    private StartMain startMain;

    private ObservableList<Person> data = FXCollections.observableArrayList();
    private ServerSocket ServSock;
    private Hashtable<Person, NetworkUtil> netTable;
    private Thread t;
    private NetworkUtil nc;

    public ServerController()
    {
        t=new Thread(this);
        t.start();
    }

    public void run()
    {
        netTable = new Hashtable<>();
        try {
            ServSock = new ServerSocket(30000);
            while (true) {
                Socket clientSock = ServSock.accept();

                nc=new NetworkUtil(clientSock);
                String clientName=(String) nc.read();
                Person person=new Person(clientName, nc, this);
                data.add(person);
                table.setItems(data);
                netTable.put(person, nc);
            }
        }catch(Exception e) {
            System.out.println("Server starts:"+e);
        }
    }

    public void initializeColumns() {
        TableColumn<Person, String> clientNameCol = new TableColumn<>("Client Name");
        clientNameCol.setMinWidth(200);
        clientNameCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        //clientNameCol.setCellFactory(TextFieldTableCell.<Person>forTableColumn());
        TableColumn<Person, Button> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));

        /*Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory =
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
                    @Override
                    public TableCell call( final TableColumn<Person, String> param ) {
                        final TableCell<Person, String> cell = new TableCell<Person, String>() {
                            final Button btn = new Button("Play");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                btn.setOnAction( ( ActionEvent event ) -> {
                                    System.out.println("I love it");
                                    {
                                        try {

                                            nc.write("start");
                                        }catch (Exception e){
                                            System.out.println(e);
                                        }
                                    }
                                   // nc.closeConnection();
                                    serverpkg.Main.Launch(startMain.getStage());
                                        }
                                );
                                if(!empty){
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        actionCol.setCellFactory(cellFactory);*/
        table.getColumns().addAll(clientNameCol,actionCol);

        timeChoice.setItems(FXCollections.observableArrayList(
                "1 min","3 min", "5 min", "8 min", "10 min"
        ));

        GameState.valuateInt(timeChoice.getPromptText());
        timeChoice.setOnAction(e-> {
            GameState.valuateInt(timeChoice.getValue());
        });

        homeColor.setItems(FXCollections.observableArrayList(
                "Blue", "Black", "Green", "Pink", "Orange"
        ));

        serverpkg.Main.setHomeColor(homeColor.getPromptText());
        homeColor.setOnAction(e-> {
            serverpkg.Main.setHomeColor(homeColor.getValue());
        });

        awayColor.setItems(FXCollections.observableArrayList(
                "Red", "Black", "Green", "Pink", "Orange"
        ));

        serverpkg.Main.setAwayColor(awayColor.getPromptText());
        awayColor.setOnAction(e-> {
            serverpkg.Main.setAwayColor(awayColor.getValue());
        });

        portCount.setItems(FXCollections.observableArrayList(
                "1", "5"
        ));
        for(int i = 10; i<=100; i+=5){
            portCount.getItems().add(i+"");
        }
        serverpkg.Main.setPortCount(Integer.parseInt(portCount.getPromptText()));
        portCount.setOnAction(e->{
            serverpkg.Main.setPortCount(Integer.parseInt(portCount.getValue()));
        });
    }

    public void setStartMain(StartMain t){
        startMain = t;
    }

    public Hashtable<Person, NetworkUtil> getNetTable() {
        return netTable;
    }


    public StartMain getStartMain() {
        return startMain;
    }

    public String getServerName() {
        return serverName.getText();
    }

    public String getPortCount() {
        return portCount.getValue();
    }

    public void enablePortEdit(){
        portCount.setDisable(true^portCount.isDisable());
    }
}
