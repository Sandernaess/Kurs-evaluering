package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Main extends Application {
    private Stage vindu;
    private Scene scene1, scene2;
    private TextField txtBrukernavn, txtPassord;
    private Label lblBruker, lblPassord;
    Kontroll kontroll = new Kontroll();

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            vindu = primaryStage;
            lagScene1();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Logg-inn scene:
    public void lagScene1() {
        BorderPane rotpanel = new BorderPane();
        scene1 = new Scene(rotpanel, 900, 450);
        vindu.setTitle("Logg inn");
        vindu.setWidth(900);
        vindu.setHeight(450);

        //Layout med brukernavn og passord:
        GridPane layout = new GridPane();
        lblBruker = new Label("Brukernavn");
        txtBrukernavn = new TextField();
        lblPassord = new Label("Passord");
        txtPassord = new PasswordField();
        Button btnLogg = new Button("Logg inn");
        Label overskrift = new Label("LOGG INN");

        layout.add(lblBruker, 0,0);
        layout.add(txtBrukernavn, 0, 1);
        layout.add(lblPassord, 0, 2);
        layout.add(txtPassord, 0, 3);
        layout.add(btnLogg, 0, 4);

        rotpanel.setTop(overskrift);
        rotpanel.setCenter(layout);
        BorderPane.setAlignment(overskrift, Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(10);
        layout.setHgap(10);
        vindu.setScene(scene1);
        vindu.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
