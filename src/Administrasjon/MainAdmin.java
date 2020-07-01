package Administrasjon;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sample.Kontroll;

public class MainAdmin extends Application {
    private static Kontroll kontroll = Kontroll.getInstance();
    private Stage vindu;
    private Scene scene1;

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            kontroll.lagForbindelse();
            vindu = primaryStage;
            lagScene1();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void lagScene1() {
        BorderPane rotpanel = new BorderPane();
        vindu.setTitle("Administrasjon - Kurs");
        vindu.setWidth(1200);
        vindu.setHeight(450);

        HBox hbox = new HBox();
        Button rapportKnapp = new Button("Rapport");
        rapportKnapp.setOnAction(e -> {
            try {
                lagRapportVindu();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Button adminKnapp = new Button("Administrasjon");
        rapportKnapp.getStyleClass().add("btn-tilbake");
        adminKnapp.getStyleClass().add("btn-tilbake");
        adminKnapp.setOnAction(e -> {
            try {
                nyttVindu();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        hbox.getChildren().addAll(rapportKnapp, adminKnapp);

        Label overskrift = new Label("EVALUERING");
        overskrift.getStyleClass().add("overskrift");
        overskrift.setAlignment(Pos.CENTER);

        rotpanel.setTop(overskrift);
        rotpanel.setCenter(hbox);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(30);

        BorderPane.setAlignment(hbox, Pos.CENTER);
        scene1 = new Scene(rotpanel, 1200, 450);
        scene1.getStylesheets().add("sample/styling.css");
        vindu.setScene(scene1);
        vindu.show();

    }

    //Åpner administrasjons-vindu:
    public void nyttVindu() throws Exception {
        AdminVindu adminVindu = new AdminVindu();
        adminVindu.start(vindu);
    }

    public void lagRapportVindu() throws Exception {
        RapportVindu rapport = new RapportVindu();
        rapport.start(vindu);
    }

    //Åpner nytt vindu for rapporter:
    public void rapportVindu() {
        RapportVindu rapportVindu = new RapportVindu();
        //rapportVindu.start(vindu);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
