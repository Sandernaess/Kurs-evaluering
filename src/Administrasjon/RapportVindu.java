package Administrasjon;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.Bruker;
import sample.Evaluering;
import sample.Kontroll;
import sample.Sporsmal;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RapportVindu extends Application {
    private Stage vindu;
    private Scene scene, scene2, scene3;
    private TableView<Evaluering> evalTabell;
    private TableView<Bruker> studentTabell;
    private TableView<Sporsmal> spmTabell;
    private PieChart pieChart;
    private static Kontroll kontroll = Kontroll.getInstance();
    private ObservableList<Evaluering> evalData = FXCollections.observableArrayList();
    private ObservableList<Bruker> studentData = FXCollections.observableArrayList();
    private ObservableList<Sporsmal> spmData = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws Exception {
        try {
            vindu = stage;
            lagScene1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lagScene1() throws SQLException {
        BorderPane root = new BorderPane();
        VBox vb = new VBox();
        vb.setMaxWidth(700);
        Label overskrift = new Label("VELG EVALUERING");
        overskrift.getStyleClass().add("overskrift");
        overskrift.setAlignment(Pos.CENTER);
        root.setTop(overskrift);

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

        vb.setAlignment(Pos.CENTER);
        vb.getChildren().add(evalTabell);
        root.setCenter(vb);

        Button tilbake = new Button("Tilbake");
        MainAdmin adminVindu = new MainAdmin();

        tilbake.setOnAction(e -> {
            try {
                adminVindu.start(vindu);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        root.setBottom(tilbake);

        hentEvalueringer();

        scene = new Scene(root, 500, 500);
        scene.getStylesheets().add(("/sample/styling.css"));
        vindu.setScene(scene);
        vindu.setTitle("Rapport");
        vindu.show();
    }

    public void lagScene2() throws SQLException {
        BorderPane root = new BorderPane();
        Evaluering ev = kontroll.getEvaluering();
        studentData.clear();
        //henter hvem som har besvart undersøkelsen:
        int antallSvar = 0;
        String navn;
        ResultSet sporsmal = kontroll.hentSporsmal(ev.getEvalID());
        if(sporsmal.next()) {
            int spmID = sporsmal.getInt(1);
            ResultSet besvarelser = kontroll.hentBesvarelser(spmID);
            while(besvarelser.next()) {
                int studentID = besvarelser.getInt(1);
                System.out.println(studentID);
                //henter navnet til studenten:
                ResultSet hentInfo = kontroll.hentInfo(studentID);
                if (hentInfo.next()) {
                    navn = hentInfo.getString(2);
                } else {
                    navn = "Anonym";
                }
                Bruker bruker = new Bruker(studentID, navn);
                studentData.add(bruker);
                antallSvar++;
            }
        }

        Label lblEval = new Label("RAPPORT OVER EVALUERING");
        Label lblRespondenter = new Label("Antall Respondenter: " + antallSvar);
        lblRespondenter.getStyleClass().add("hvit");
        lblEval.getStyleClass().add("overskrift");
        lblEval.setAlignment(Pos.CENTER);
        root.setTop(lblEval);
        VBox vb = new VBox();
        vb.getStyleClass().add("bg-test");
        vb.setSpacing(20);
        vb.setMinWidth(350);
        vb.setPadding(new Insets(20, 5, 0, 20));

        studentTabell = new TableView<>();
        studentTabell.setMaxHeight(300);
        TableColumn colID = new TableColumn("ID ");
        colID.setMaxWidth(200);
        colID.setCellValueFactory(new PropertyValueFactory<Bruker, Integer>("StudentID"));
        TableColumn colNavn = new TableColumn("Navn");
        colNavn.setCellValueFactory(new PropertyValueFactory<Bruker, String>("Navn"));

        studentTabell.getColumns().addAll(colID, colNavn);
        studentTabell.setPlaceholder(new Label("Ingen funnet"));
        studentTabell.setItems(studentData);

        Button tilbake = new Button("Tilbake");
        tilbake.setOnAction(e -> vindu.setScene(scene));
        root.setBottom(tilbake);

        Button lagreBtn = new Button("Lagre til Fil");
        lagreBtn.setOnAction(e -> lagreFil());

        vb.getChildren().addAll(lblRespondenter, studentTabell,tilbake, lagreBtn);

        spmTabell = new TableView<>();
        spmTabell.setMaxHeight(300);
        TableColumn colSpm = new TableColumn("Spørsmålet");
        colSpm.setCellValueFactory(new PropertyValueFactory<Sporsmal, String>("Tekst"));
        spmTabell.getColumns().addAll(colSpm);
        spmTabell.setPlaceholder(new Label("Ingen funnet"));

        hentSporsmal(ev.getEvalID());
        spmTabell.setOnMouseClicked(e -> {
            try {
                behandleValgtSpm();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        pieChart = new PieChart();

        VBox vbox = new VBox(pieChart);
        root.setCenter(spmTabell);
        root.setAlignment(spmTabell, Pos.TOP_CENTER);
        
        root.setRight(vbox);
        root.setLeft(vb);
        scene2 = new Scene(root, 1200, 450);
        scene2.getStylesheets().add("/sample/styling.css");
        vindu.setScene(scene2);
    }

    public void behandleValgtEv() throws SQLException {
        Evaluering eval = evalTabell.getSelectionModel().getSelectedItem();
        kontroll.setEvaluering(eval);
        lagScene2();
    }


    public void hentSporsmal(int ID) throws SQLException {
        spmData.clear();
        ResultSet spm =  kontroll.hentSporsmal(ID);
        while (spm.next()) {
            int spmID = spm.getInt(1);
            String spmTekst = spm.getString(3);
            Sporsmal sp = new Sporsmal(spmID, spmTekst);
            spmData.add(sp);
        }
        spmTabell.setItems(spmData);
    }

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

    public void behandleValgtSpm() throws SQLException {
        pieChart.getData().clear();
        Sporsmal spm = spmTabell.getSelectionModel().getSelectedItem();
        //Henter alternativene og svarene på dem:
        ResultSet alt =  kontroll.hentAlternativ(spm.getSpmID());
        while(alt.next()) {
           int id = alt.getInt(1);
           String tekst = alt.getString(3);
           //hent antall svar:
           int antall = kontroll.hentAntallSvar(id);
           PieChart.Data slice = new PieChart.Data(tekst + " (" + antall + ")", antall);
           pieChart.getData().add(slice);
        }

    }

    public void lagreFil() {
        for (Sporsmal s : spmData) {

            System.out.println(s.toString());
        }
    }
}
