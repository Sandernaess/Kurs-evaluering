package Studenter;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import sample.Bruker;
import sample.Evaluering;
import sample.Kontroll;
import sample.Kurs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends Application {
    private Stage vindu;
    private Scene scene1, scene2;
    private TextField txtBrukernavn, txtPassord;
    private Label lblBruker, lblPassord;
    private Label feilmeldingLogg;
    private TableView<Kurs> tabellKurs;
    private TableView<Evaluering> tabellEvaluering;
    private ObservableList<Kurs> kursData = FXCollections.observableArrayList();
    private ObservableList<Evaluering> evalueringData = FXCollections.observableArrayList();
    private static Kontroll kontroll = Kontroll.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            kontroll.lagForbindelse();
            vindu = primaryStage;
            //sjekker om brukeren er allerede logget inn:
            Boolean loggetInn = kontroll.sjekk();
            lagScene1();
            if (loggetInn) {
                //er allerede logget inn, kan gå til scene2:
                //dette er for om feks brukeren går tilbake fra en evaluering:
                lagScene2();
                vindu.setScene(scene2);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Logg-inn scene:
    public void lagScene1() {
        BorderPane rotpanel = new BorderPane();
        scene1 = new Scene(rotpanel, 1200, 450);
        scene1.getStylesheets().add("/sample/styling.css");
        vindu.setTitle("Logg inn");
        vindu.setWidth(1200);
        vindu.setHeight(450);

        //Layout med brukernavn og passord:
        GridPane layout = new GridPane();
        lblBruker = new Label("Brukernavn");
        txtBrukernavn = new TextField();
        lblPassord = new Label("Passord");
        txtPassord = new PasswordField();
        Button btnLogg = new Button("Logg inn");
        feilmeldingLogg = new Label();
        feilmeldingLogg.getStyleClass().add("feilmelding");
        Label overskrift = new Label("LOGG INN");
        overskrift.getStyleClass().add("overskrift");
        overskrift.setAlignment(Pos.CENTER);
        btnLogg.getStyleClass().add("btn");
        txtBrukernavn.getStyleClass().add("tx");
        txtPassord.getStyleClass().add("tx");



        layout.add(feilmeldingLogg, 0,0);
        layout.add(lblBruker, 0,1);
        layout.add(txtBrukernavn, 0, 2);
        layout.add(lblPassord, 0, 3);
        layout.add(txtPassord, 0, 4);
        layout.add(btnLogg, 0, 5);

        rotpanel.setTop(overskrift);
        rotpanel.setCenter(layout);
        BorderPane.setAlignment(overskrift, Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        layout.setVgap(5);
        layout.setHgap(10);
        vindu.setScene(scene1);
        vindu.show();

        btnLogg.setOnAction(e -> {
            try {
                loggInn();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    //Behandler logg-inn:
    public void loggInn() throws SQLException {
        String brukernavn = txtBrukernavn.getText();
        String passord = txtPassord.getText();
        if (!brukernavn.equals("") && !passord.equals("")) {
            ResultSet treff = kontroll.loggInn(brukernavn, passord);
            if (treff.next()) {
                //riktig, blir logget inn:
                int studentID = treff.getInt(1);
                String navn = treff.getString(2);
                String adresse = treff.getString(3);
                //kontroll.leggTil(new Bruker(studentID, navn, adresse, passord));
                kontroll.setBruker(new Bruker(studentID, navn, adresse, passord));

                lagScene2();
                feilmeldingLogg.setText("");
                vindu.setScene(scene2);
            } else {
                //Ikke riktig:
                feilmeldingLogg.setText("samsvarer ikke");
            }
        } else {
            feilmeldingLogg.setText("Fyll ut begge felt");
        }
    }


    //lager scenen for logget inn:
    public void lagScene2() throws SQLException {
        //henter info til brukeren:
        Bruker bruker = kontroll.getBruker();
        int brukerID = bruker.getStudentID();
        String fornavn = bruker.getNavn();

        VBox kurs2 = new VBox();
        kurs2.setPrefWidth(500);
        Label kurstekst = new Label("ALLE DINE KURS");
        kurstekst.setPadding(new Insets(10, 0, 10, 0));
        kurstekst.getStyleClass().add("overskrift2");
        tabellKurs = new TableView<>();

        //tabellen med kurs:
        TableColumn colkursN = new TableColumn("Kurs navn");
        colkursN.setMinWidth(350);
        colkursN.setMaxWidth(400);
        colkursN.setCellValueFactory(new PropertyValueFactory<Kurs, String>("KursNr"));
        TableColumn colSemester = new TableColumn("Semester");
        colSemester.setMinWidth(150);
        colSemester.setMaxWidth(200);
        colSemester.setCellValueFactory(new PropertyValueFactory<Kurs, String>("Semester"));
        tabellKurs.getColumns().addAll(colkursN, colSemester);
        tabellKurs.setPlaceholder(new Label("Ingen kurs funnet"));

        kurs2.setAlignment(Pos.TOP_CENTER);
        kurs2.getChildren().add(kurstekst);
        kurs2.getChildren().add(tabellKurs);
        kurs2.setPadding(new Insets(10, 0, 10, 0));
        //skiller dem:
        Separator separator = new Separator(Orientation.VERTICAL);

        //evaluering av kurs-delen:
        //tabellen med kurs:
        tabellEvaluering = new TableView<>();

        TableColumn evalID = new TableColumn("ID");
        evalID.setMinWidth(50);
        evalID.setMaxWidth(100);
        evalID.setCellValueFactory(new PropertyValueFactory<Evaluering, Integer>("EvalID"));
        TableColumn evalNavn = new TableColumn("Kurset");
        evalNavn.setMinWidth(150);
        evalNavn.setMaxWidth(400);
        evalNavn.setCellValueFactory(new PropertyValueFactory<Evaluering, String>("EvalNavn"));
        TableColumn evalUt = new TableColumn("Dato ut");
        evalUt.setCellValueFactory(new PropertyValueFactory<Evaluering, String>("DatoUt"));
        TableColumn evalInn = new TableColumn("Dato Inn");
        evalInn.setCellValueFactory(new PropertyValueFactory<Evaluering, String>("DatoInn"));

        //legger det inn:
        tabellEvaluering.getColumns().addAll(evalID , evalNavn, evalUt, evalInn);
        tabellEvaluering.setPlaceholder(new Label("Ingen tilgjengelige evalueringer nå"));
        //registrerer klikk på tabell:
        tabellEvaluering.setOnMouseClicked(event -> nyttVindu());

        Label kurs = new Label("EVALUER KURS");
        kurs.setPadding(new Insets(10, 0, 10, 0));
        kurs.getStyleClass().add("overskrift2");


        VBox boks = new VBox();
        boks.setAlignment(Pos.TOP_CENTER);
        boks.getChildren().add(kurs);
        boks.getChildren().add(tabellEvaluering);
        boks.setPrefWidth(500);
        boks.setPadding(new Insets(10, 0, 10, 0));

        HBox vbox = new HBox();
        vbox.setPrefWidth(1400);
        vbox.getChildren().add(boks);
        vbox.getChildren().add(separator);
        vbox.setMargin(separator, new Insets(10, 20, 10, 20));
        vbox.getChildren().add(kurs2);
        vbox.setSpacing(50);
        vbox.setAlignment(Pos.CENTER);

        BorderPane test = new BorderPane();
        Label testing = new Label("DINE KURS" + " - " + fornavn);
        Button tilbake = new Button("Logg ut");
        tilbake.setOnAction(e -> vindu.setScene(scene1));

        tilbake.setPadding(new Insets(10, 5, 5, 5));
        testing.setAlignment(Pos.CENTER);
        testing.getStyleClass().add("overskrift");
        test.setTop(testing);
        test.setCenter(vbox);
        test.setBottom(tilbake);
        tilbake.getStyleClass().add("btn-tilbake");
        scene2 = new Scene(test, 1200, 450);
        scene2.getStylesheets().add("/sample/styling.css");

        hentKurs(brukerID);
        hentEvalueringer(brukerID);
    }

    //Henter kurs:
    public void hentKurs(int brID) throws SQLException {
        kursData.clear();
        ResultSet resultatKurs =  kontroll.hentKurs(brID);
        while(resultatKurs.next()) {
            String kursnavn = resultatKurs.getString(1);
            String semester = resultatKurs.getString(2);

            Kurs kurs = new Kurs(kursnavn, semester);
            kursData.add(kurs);
        }
        tabellKurs.setItems(kursData);
    }

    //henter evalueringer:
    public void hentEvalueringer(int brID) throws SQLException {
        evalueringData.clear();
        ResultSet resultatEvaluering = kontroll.hentEvaluering(brID);
        while (resultatEvaluering.next()) {
            int evalID = resultatEvaluering.getInt(1);
            boolean sjekk = kontroll.sjekk(evalID);
            if (!sjekk) {
                int kursID = resultatEvaluering.getInt(2);
                String navn = resultatEvaluering.getString(3);
                String datoUt = resultatEvaluering.getString(4);
                datoUt = datoUt.substring(0, 16);
                String datoInn = resultatEvaluering.getString(5);
                datoInn = datoInn.substring(0, 16);

                Evaluering evaluering = new Evaluering(evalID, navn, datoUt, datoInn);
                evalueringData.add(evaluering);
            } else {
                System.out.println("allerede tatt");
            }
        }
        tabellEvaluering.setItems(evalueringData);
    }

    public void nyttVindu() {
        //henter valgt evaluering og legger i listen i kontroll
        //slik at vi har den tilgjengelig i neste del:
        Evaluering evaluering = tabellEvaluering.getSelectionModel().getSelectedItem();
        kontroll.leggEvaluering(evaluering);

        System.out.println(evaluering.getDatoInn());
        //åpner neste vindu for evaluering av kurset:
        SecondWindow secondWindow = new SecondWindow();
        secondWindow.start(vindu);
    }
}
