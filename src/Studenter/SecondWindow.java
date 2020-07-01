package Studenter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class SecondWindow extends Application {
    private Stage vindu;
    private Scene scene1, scene2, scene;
    private Stage primaryStage;
    private static Kontroll kontroll = Kontroll.getInstance();
    private int evalID;
    private Bruker bruker;
    private VBox vbox2;
    private Label testing;
    private ToggleGroup group;
    private ArrayList<Sporsmal> sporsmalListe = new ArrayList<>();
    private ArrayList<Alternativ> alternativListe = new ArrayList<Alternativ>();
    private ArrayList<ToggleGroup> gruppe = new ArrayList<>();

    // This method, when called, will receive the original primary stage
// on which a new scene will then be attached
    public void start(Stage stage) {
        try {
            vindu = stage;
            lagScene1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lagScene1() throws SQLException {
        //paneler:
        BorderPane bp = new BorderPane();
        VBox hbox = new VBox();
        VBox vbox = new VBox();

        //henter brukeren:
        bruker = kontroll.getBruker();
        int brukerID = bruker.getStudentID();
        String fornavn = bruker.getNavn();

        //henter evalueringen som ble valgt:
        Evaluering evaluering = kontroll.hentEvaluering();
        String evalNavn = evaluering.getEvalNavn();
        String datoUt = evaluering.getDatoUt();
        evalID = evaluering.getEvalID();

        //Labels:
        Label lbl = new Label(fornavn + " med ID: " + brukerID);
        Label lblEval = new Label(evalNavn);
        lblEval.setPadding(new Insets(10, 0, 10, 0));
        Label lblDato = new Label("Levert ut: " + datoUt);
        lblDato.setPadding(new Insets(0, 0, 50, 0));
        Button startBtn = new Button("Start");
        startBtn.getStyleClass().add("btn");
        startBtn.setOnAction(event -> vindu.setScene(scene2));

        //Hbox'en:
        hbox.setPadding(new Insets(15, 0, 10, 0));
        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.getChildren().add(lblEval);
        hbox.getChildren().add(lblDato);
        hbox.getChildren().add(startBtn);

        Label overskrift = new Label("EVALUERING");
        overskrift.getStyleClass().add("overskrift");
        overskrift.setAlignment(Pos.CENTER);

        //lager scene2:
        lagScene2();

        //knapp for tilbake:
        Button tilbake = new Button("Tilbake");
        tilbake.getStyleClass().add("btn-tilbake");
        Main main = new Main();
        tilbake.setOnAction(e -> {
            try {
                main.start(vindu);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Borderpane plassering her:
        bp.setTop(overskrift);
        bp.setCenter(hbox);
        bp.setBottom(tilbake);

        scene = new Scene(bp, 500, 500);
        scene.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene);
        vindu.show();
    }

    //scenen hvor det dukker opp spørsmålene til evalueringen:
    public void lagScene2() throws SQLException {
        //henter spørsmålene til evalueringen:
        hentSporsmal();
        VBox vbox2 = new VBox();
        testing = new Label();
        vbox2.getChildren().add(testing);
        int teller = 0;
        //Går gjennom hvert spørsmål:
        for (Sporsmal s : sporsmalListe) {
            teller++;
            String spm = teller + ". " + s.getTekst();
            Label sporsmalet = new Label(spm);
            sporsmalet.setPadding(new Insets(10, 0, 0, 0));
            vbox2.getChildren().add(sporsmalet);
            int spmID = s.getSpmID();

            group = new ToggleGroup();
            gruppe.add(group);
            //Henter alternativer til spørsmålet:
            ResultSet alternativer = kontroll.hentAlternativ(spmID);
            while(alternativer.next()) {
                int ID = alternativer.getInt(1);
                String tekst = alternativer.getString(3);
                //Setter først teksten, som brukeren ser:
                RadioButton rb = new RadioButton(tekst);
                rb.setPadding(new Insets(5, 0 ,0 ,0));
                //setter ID til alternativet, brukes for db:
                rb.setUserData(ID);
                //gir den toggle-group group så den hører til riktig spm:
                rb.setToggleGroup(group);
                //Legger alternativet inn:
                vbox2.getChildren().add(rb);
            }
        }

        //Knapp for send:
        Button knapp = new Button("Send");
        knapp.getStyleClass().add("btn-tilbake");
        knapp.setOnAction(event -> {
            try {
                behandleTest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        vbox2.getChildren().add(knapp);
        VBox.setMargin(knapp, new Insets(20, 0, 0, 0));
        Button tilbake = new Button("Tilbake");
        tilbake.setOnAction(e -> vindu.setScene(scene));
        tilbake.getStyleClass().add("btn-tilbake");

        Label overskrift = new Label("EVALUERING");
        overskrift.getStyleClass().add("overskrift");
        overskrift.setAlignment(Pos.CENTER);

        BorderPane pane = new BorderPane();
        ScrollPane scroll = new ScrollPane();
        scroll.getStylesheets().add("/sample/styling.css");
        scroll.setPadding(new Insets(0, 0, 0, 50));
        scroll.setContent(vbox2);
        pane.setTop(overskrift);
        pane.setCenter(scroll);
        pane.setBottom(tilbake);
        BorderPane.setMargin(vbox2, new Insets(0, 0, 0, 50));

        scene2 = new Scene(pane, 1200, 650);
        vindu.setHeight(650);
        scene2.getStylesheets().add("/sample/styling.css");
    }

    //Behandler til neste spørsmål:
    public void behandleTest() throws Exception {
        for (ToggleGroup g : gruppe) {
            RadioButton button = (RadioButton) g.getSelectedToggle();
            int ID = (int) button.getUserData();
            //TODO Legg inn svaret i DB HER:
            kontroll.giSvar(ID);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Evaluering fullført");
        alert.setHeaderText(null);
        alert.setContentText("Takk for at du besvarte undersøkelsen!");

        alert.showAndWait();
        Main main = new Main();
        main.start(vindu);
    }

    //henter spørsmål:
    public void hentSporsmal() throws SQLException {
        sporsmalListe.clear();
        ResultSet resultat = kontroll.hentSporsmal(evalID);
        while (resultat.next()) {
            int spmID = resultat.getInt(1);
            String tekst = resultat.getString(3);

            Sporsmal sporsmal = new Sporsmal(spmID, tekst);
            sporsmalListe.add(sporsmal);
        }
    }



}
