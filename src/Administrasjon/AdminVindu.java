package Administrasjon;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class AdminVindu extends Application {
    private Stage vindu;
    private Scene scene, scene2, scene3, scene4, scene5, scene6;
    private TableView<Kurs> kursTabell;
    private TableView<Sporsmal> spmTabell;
    private TableView<Alternativ> altTabell;
    private TableView<Evaluering> evalTabell;
    private TextField navn, tid, tidUt, txtNyttSpm, nyAlt;
    private DatePicker innVelg, utVelg;
    private static Kontroll kontroll = Kontroll.getInstance();
    private ObservableList<Kurs> kursData = FXCollections.observableArrayList();
    private ObservableList<Sporsmal> spmData = FXCollections.observableArrayList();
    private ObservableList<Alternativ> altData = FXCollections.observableArrayList();
    private ObservableList<Evaluering> evalData = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws Exception {
        try {
            vindu = stage;
            lagScene1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //lager første scenen:
    public void lagScene1() throws SQLException {
        BorderPane bp = new BorderPane();
        HBox knappbox = new HBox();
        knappbox.setSpacing(20);
        knappbox.setAlignment(Pos.CENTER);
        Button ny = new Button("Nytt spørreskjema");
        lagScene2();
        kontroll.setEvaluering(new Evaluering(1, "testing", "sada", "dsaada"));
        lagScene5();
        ny.setOnAction(e -> {
            try {
                lagScene2();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        Button se = new Button("Se spørreskjemaer");
        se.setOnAction(e -> {
            try {
                lagScene5();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        ny.getStyleClass().add("btn-tilbake");
        se.getStyleClass().add("btn-tilbake");
        knappbox.getChildren().addAll(ny, se);

        bp.setCenter(knappbox);
        Button tilbake = new Button("Tilbake");
        MainAdmin adminVindu = new MainAdmin();
        tilbake.setOnAction(e -> {
            try {
                adminVindu.start(vindu);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        bp.setBottom(tilbake);

        scene = new Scene(bp, 500, 500);
        scene.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene);
        vindu.setTitle("Admin");
        vindu.show();
    }

    //Lager vinduet for å lage nye spørreskjemaer:
    public void lagScene2() throws SQLException {
        BorderPane root = new BorderPane();
        VBox vb = new VBox();
        vb.setMaxWidth(700);
        Label tekst = new Label("VELG KURSET DET GJELDER");
        kursTabell = new TableView<>();
        kursTabell.setOnMouseClicked(e -> lagScene3());

        //tabellen med alle kursene:
        //her velger brukeren ett kurs:
        TableColumn colkursN = new TableColumn("Kurs navn");
        colkursN.setMinWidth(350);
        colkursN.setMaxWidth(400);
        colkursN.setCellValueFactory(new PropertyValueFactory<Kurs, String>("KursNr"));
        TableColumn colSemester = new TableColumn("Semester");
        colSemester.setMinWidth(150);
        colSemester.setMaxWidth(200);
        colSemester.setCellValueFactory(new PropertyValueFactory<Kurs, String>("Semester"));
        kursTabell.getColumns().addAll(colkursN, colSemester);
        kursTabell.setPlaceholder(new Label("Ingen funnet"));

        vb.setAlignment(Pos.TOP_CENTER);
        vb.getChildren().add(tekst);
        vb.getChildren().add(kursTabell);
        root.setCenter(vb);
        Button tilbake = new Button("Tilbake");
        tilbake.setOnAction(e -> vindu.setScene(scene));
        root.setBottom(tilbake);

        //henter kursene fra db og legger i tabellen:
        hentKurs();

        scene2 = new Scene(root, 1200, 450);
        scene2.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene2);
    }

    //neste scene når brukeren har valgt et kurs:
    public void lagScene3() {
        BorderPane root = new BorderPane();
        VBox vbRoot = new VBox();
        HBox hbInn = new HBox();
        HBox hbUt = new HBox();

        Kurs kurs = kursTabell.getSelectionModel().getSelectedItem();
        int valgtKursID = kurs.getKursFNR();
        Label valgtTekst = new Label("Nytt spørreskjema for " + kurs.getKursNr());
        root.setAlignment(valgtTekst, Pos.CENTER);

        Label lblNavn = new Label("Navn på Spørreskjemaet");
        navn = new TextField();
        navn.setMaxWidth(400);
        tid = new TextField();
        tid.setMaxWidth(100);
        tidUt = new TextField();
        tidUt.setMaxWidth(100);
        tidUt.setPromptText("Tid 00:00");
        tid.setPromptText("Tid 00:00");

        Label datoInn = new Label("Velg tidspunkt for fristen");
        Label datoUt = new Label("Velg tidspunkt for når den blir tilgjengelig");

        innVelg = new DatePicker();
        utVelg = new DatePicker();

        hbInn.setAlignment(Pos.CENTER);
        hbInn.getChildren().addAll(innVelg, tid);
        hbUt.setAlignment(Pos.CENTER);
        hbUt.getChildren().addAll(utVelg, tidUt);

        vbRoot.getChildren().addAll(lblNavn, navn, datoUt, hbUt);
        vbRoot.getChildren().addAll(datoInn, hbInn);
        vbRoot.setAlignment(Pos.CENTER);
        vbRoot.setMaxWidth(600);
        vbRoot.setSpacing(10);
        hbInn.setSpacing(10);
        hbUt.setSpacing(10);

        Button lagre = new Button("Opprett");
        lagre.getStyleClass().add("btn-tilbake");
        lagre.setOnAction(e -> {
            try {
                behandleEvaluering(valgtKursID);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        vbRoot.getChildren().add(lagre);

        root.setTop(valgtTekst);
        root.setCenter(vbRoot);
        Button tilbake = new Button("Tilbake");
        tilbake.setOnAction(e -> vindu.setScene(scene2));
        root.setBottom(tilbake);

        scene3 = new Scene(root, 1200, 450);
        scene3.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene3);

    }

    public void hentKurs() throws SQLException {
        kursData.clear();
        ResultSet kursene = kontroll.hentAlleKurs();
        while(kursene.next()) {
            String kursnavn = kursene.getString(1);
            String semester = kursene.getString(2);
            int kursID = kursene.getInt(3);
            Kurs kurs = new Kurs(kursID, kursnavn, semester);
            kursData.add(kurs);
        }
        kursTabell.setItems(kursData);
    }

    //legger inn den nye evaluering i DB:
    public void behandleEvaluering(int kursID) throws SQLException {
        String navnet = navn.getText();
        //henter først dato, deretter legger til tiden også:
        String datoInn = innVelg.getValue().toString() + " " + tid.getText();
        String datoUt = utVelg.getValue().toString() + " " + tidUt.getText();

        //legger inn i db fra kontroll:
        int ID = kontroll.nyEvaluering(kursID, navnet,datoUt, datoInn);
        //Alert til brukeren:
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ny evaluering lagt til!");
        alert.setHeaderText(null);
        if (ID != 0) {
            //lager et objekt av evalueringen
            kontroll.setEvaluering(new Evaluering(ID, navnet, datoUt, datoInn));
            alert.setTitle("Ny evaluering lagt til!");
            alert.setContentText("Din evaluering er nå lagt til!");
            alert.showAndWait();
            lagScene4();
            vindu.setScene(scene4);
        } else {
            alert.setTitle("En feil oppsto");
            alert.setContentText("Din evaluering ble ikke lagt til, vennligst prøv på nytt");
            //går tilbake:
            vindu.setScene(scene2);
        }

    }

    //scene for å legge til spørsmål:
    public void lagScene4() throws SQLException {
        BorderPane root = new BorderPane();
        GridPane layout = new GridPane();
        BorderPane bp = new BorderPane();
        Label lblEval = new Label("Spørsmål for evaluering: " + kontroll.getEvaluering().getEvalNavn());

        Label lblNySpm = new Label("Legg til nytt spørsmål");
        txtNyttSpm = new TextField();
        txtNyttSpm.setPromptText("Skriv inn spørsmålet");
        Button knapp = new Button("Lagre");
        knapp.setOnAction(e -> {
            try {
                nyttSporsmal();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        layout.add(lblNySpm, 0, 0);
        layout.add(txtNyttSpm, 0, 1);
        layout.add(knapp, 1, 1);

        root.setRight(layout);
        root.setTop(lblEval);

        spmTabell = new TableView<>();
        spmTabell.setMaxHeight(300);
        //spmTabell.setOnMouseClicked();

        //tabellen med alle kursene:
        //her velger brukeren ett kurs:
        TableColumn spmTekst = new TableColumn("Spørsmålene");
        spmTekst.setMinWidth(350);
        spmTekst.setMaxWidth(400);
        spmTekst.setCellValueFactory(new PropertyValueFactory<Sporsmal, String>("Tekst"));
        spmTabell.getColumns().addAll(spmTekst);
        spmTabell.setPlaceholder(new Label("Ingen Spørsmål funnet, legg til!"));
        Button slettBtn = new Button("Slett");
        slettBtn.setOnAction(e -> behandleSlett());
        Button altBtn = new Button("Se Alternativer");
        altBtn.setOnAction(e -> {
            try {
                behandleSeAlt();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        bp.setTop(spmTabell);
        bp.setLeft(slettBtn);
        bp.setRight(altBtn);
        root.setLeft(bp);

        Button tilbake = new Button("Tilbake");
        tilbake.setOnAction(e -> vindu.setScene(scene5));
        root.setBottom(tilbake);

        //henter spørsmål til evalueringen:
        int ID = kontroll.getEvaluering().getEvalID();
        hentSpm(ID);

        scene4 = new Scene(root, 1200, 450);
        scene4.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene4);
    }

    public void lagScene5() throws SQLException {
        BorderPane root = new BorderPane();
        VBox vb = new VBox();
        vb.setMaxWidth(700);
        Label lblEval = new Label("ALLE EVALUERINGER");
        lblEval.getStyleClass().add("overskrift");
        lblEval.setAlignment(Pos.CENTER);
        root.setTop(lblEval);

        evalTabell = new TableView<>();
        evalTabell.setOnMouseClicked(e -> {
            try {
                behandleValgtEv();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        //Tabellen med evalueringer:
        TableColumn colTekst = new TableColumn("Evaluering Navn");
        colTekst.setMinWidth(350);
        colTekst.setMaxWidth(400);
        colTekst.setCellValueFactory(new PropertyValueFactory<Evaluering, String>("EvalNavn"));

        evalTabell.getColumns().addAll(colTekst);
        evalTabell.setPlaceholder(new Label("Ingen funnet"));

        vb.setAlignment(Pos.TOP_CENTER);
        vb.getChildren().add(evalTabell);
        root.setCenter(vb);
        Button tilbake = new Button("Tilbake");
        tilbake.setOnAction(e -> vindu.setScene(scene));
        root.setTop(lblEval);
        root.setBottom(tilbake);

        hentEvalueringer();

        scene5 = new Scene(root, 1200, 450);
        scene5.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene5);
    }

    public void hentSpm(int ID) throws SQLException {
        spmData.clear();
        ResultSet spormsal = kontroll.hentSporsmal(ID);
        while(spormsal.next()) {
            int spmID = spormsal.getInt(1);
            int spmEvalID = spormsal.getInt(2);
            String tekst = spormsal.getString(3);

            Sporsmal spm = new Sporsmal(spmID, spmEvalID, tekst);
            spmData.add(spm);
        }
        spmTabell.setItems(spmData);
    }

    //henter evalueringer:
    public void hentEvalueringer() throws SQLException {
        evalData.clear();
        ResultSet resultat = kontroll.hentAlleEvalueringer();
        while(resultat.next()) {
            int id = resultat.getInt(1);
            int kursID = resultat.getInt(2);
            String tekst = resultat.getString(3);
            String datoUt = resultat.getString(4);
            String datoInn = resultat.getString(5);

            Evaluering eval = new Evaluering(id, tekst, datoUt, datoInn, kursID);
            evalData.add(eval);
        }
        evalTabell.setItems(evalData);

    }

    public void behandleValgtEv() throws SQLException {
       Evaluering eval = evalTabell.getSelectionModel().getSelectedItem();
       kontroll.setEvaluering(eval);
       lagScene4();
    }

    //legger til nytt spørsmål:
    public void nyttSporsmal() throws SQLException {
        String spm = txtNyttSpm.getText();
        int evalID = kontroll.getEvaluering().getEvalID();
        //Legger inn spm i DB:
        int spmID = kontroll.nyttSporsmal(spm);
        txtNyttSpm.clear(); //gjør feltet tomt

        hentSpm(evalID);
    }

    //Se og endre alternativer for et spørsmål:
    public void behandleSeAlt() throws SQLException {
        Sporsmal spm = spmTabell.getSelectionModel().getSelectedItem();
        Label lblSporsmalet = new Label(spm.getTekst());
        kontroll.setSporsmal(spm);
        int spmID = spm.getSpmID();

        BorderPane root = new BorderPane();
        VBox vb = new VBox();
        HBox hb = new HBox();
        vb.setMaxWidth(700);
        altTabell = new TableView<>();
        //Tabellen med evalueringer:
        TableColumn colTekst = new TableColumn("Alternativ");
        colTekst.setMinWidth(350);
        colTekst.setMaxWidth(400);
        colTekst.setCellValueFactory(new PropertyValueFactory<Alternativ, String>("Tekst"));

        altTabell.getColumns().addAll(colTekst);
        altTabell.setPlaceholder(new Label("Ingen funnet"));


        vb.getChildren().add(altTabell);
        Button leggTil = new Button("Legg til");
        leggTil.setOnAction(e -> {
            try {
                nyttAlternativ();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        nyAlt = new TextField();
        nyAlt.setPromptText("Nytt alternativ");
        hb.getChildren().addAll(nyAlt, leggTil);
        vb.getChildren().addAll(hb);


        hentAlternativ(spmID);


        Button tilbake = new Button("Tilbake");
        tilbake.setOnAction(e -> vindu.setScene(scene4));
        root.setLeft(vb);
        root.setTop(lblSporsmalet);
        root.setBottom(tilbake);

        scene6 = new Scene(root, 1200, 450);
        scene6.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene6);
    }

    public void behandleSlett() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Slette spørsmål");
        alert.setHeaderText("Er du sikker at du ønsker å slette spørsmålet? ");
        alert.setContentText("Velg");

        ButtonType btnNei = new ButtonType("Avbryt", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnJa = new ButtonType("Slett Spørsmål");

        alert.getButtonTypes().setAll(btnJa, btnNei);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btnJa){
            System.out.println("Sletter spm..");
        } else {
            System.out.println("nei");
        }
    }

    public void hentAlternativ(int spmID) throws SQLException {
        altData.clear();
       ResultSet resultat = kontroll.hentAlternativ(spmID);
       while(resultat.next()) {
            int altID = resultat.getInt(1);
            int altSpmID = resultat.getInt(2);
            String teksten = resultat.getString(3);

            Alternativ alt = new Alternativ(altID, teksten, altSpmID);
           altData.add(alt);
       }
        altTabell.setItems(altData);
    }

    public void nyttAlternativ() throws SQLException {

        String tekst = nyAlt.getText();
        kontroll.nyttAlternativ(tekst);
        hentAlternativ(kontroll.getSporsmal().getSpmID());
    }


}
