package sample;

import java.sql.*;
import java.util.ArrayList;

public class Kontroll {
    private String databasenavn = "jdbc:mysql://localhost:3306/kurs?serverTimezone=UTC";
    private String databasedriver = "com.mysql.jdbc.Driver";
    private Connection forbindelse;
    private ResultSet resultat;
    private Statement utsagn;
    private ArrayList<Evaluering> evalueringInfo = new ArrayList<>();
    private static Kontroll kontroll = new Kontroll();
    private Bruker bruker;
    private Evaluering evaluering;
    private Sporsmal sporsmal;


    //legger til 1 evaluering:
    public void leggEvaluering(Evaluering er) {
        //her rydder først opp for å passe på at det kun er 1:
        evalueringInfo.clear();
        evalueringInfo.add(er);
    }

    //sjekker om brukeren er allerede logget inn:
    public boolean sjekk() {
        boolean loggetInn = false;
        if (evalueringInfo.size() == 1) {
            loggetInn = true;
        }
        return loggetInn;
    }

    //Åpner forbindelsen til databasen:
    public void lagForbindelse() throws Exception {
        try {
            forbindelse = DriverManager.getConnection(databasenavn,"XXX","XXX");//Bruk eget passord
        } catch(Exception e) {
            throw new Exception("Kan ikke oppnå kontakt med databasen");
        }
    }
    //lukker forbindelsen til databasen:
    public void lukk() throws Exception {
        try {
            if(forbindelse != null) {
                forbindelse.close();
                //resultat.close();
                //utsagn.close();
            }
        }catch(Exception e) {
            throw new Exception("Kan ikke lukke databaseforbindelse");
        }
    }

    //Logg inn delen:
    public ResultSet loggInn(String brukernavn, String passord) {
        resultat = null;
        try {
            String sql = "SELECT * FROM tblstudent WHERE studID = " + brukernavn  + " AND studPassord = '" + passord + "';";
            System.out.println(sql);
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    public ResultSet hentInfo(int studID) {
        resultat = null;
        try {
            String sql = "SELECT * FROM tblstudent WHERE studID = " + studID + ";";
            System.out.println(sql);
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    //Hent kursene til studenten:
    public ResultSet hentKurs(int studentID) {
        resultat = null;
        try {
            String sql = "SELECT kursNavn, kursSemester FROM tblstudkurs, tblkurs WHERE kursID = skKursID" +
                    " AND skStudID = " + studentID;
            System.out.println(sql);
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultat;
    }

    public ResultSet hentAlleKurs() {
        resultat = null;
        try {
            String sql = "SELECT kursNavn, kursSemester, kursID FROM tblkurs;";
            System.out.println(sql);
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    //gir evaluering:
    public Evaluering hentEvaluering() {
        return evalueringInfo.get(0);
    }

    //gir brukeren:
    public Bruker getBruker() {
        return bruker;
    }

    //setter ny bruker:
    public void setBruker(Bruker bruker) {
        this.bruker = bruker;
    }

    //henter evalueringen som har blitt satt:
    public Evaluering getEvaluering() {
        return evaluering;
    }
    //setter evaluering
    public void setEvaluering(Evaluering evaluering) {
        this.evaluering = evaluering;
    }

    //henter evalueringer som er tilgjengelige
    public ResultSet hentEvaluering(int studentID) {
        resultat = null;
        try {
            String sql = "SELECT evalID, evalKursID, evalNavn, evalDatoUt, evalDatoInn " +
                    "FROM tblevaluering, tblstudkurs WHERE evalKursID = skKursID AND skStudID = " + studentID +
                    " AND evalDatoInn > NOW() AND evalDatoUt < NOW();";
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    //henter spørsmål til evaluering:
    public ResultSet hentSporsmal(int evalID) {
        resultat = null;
        try {
            String sql = "SELECT * FROM tblsporsmal WHERE spmEvalID = " + evalID;
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);
            System.out.println(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    //henter alternativ:
    public ResultSet hentAlternativ(int spmID) {
        resultat = null;
        try {
            String sql = "SELECT * FROM tblalternativ WHERE altSpmID = " + spmID + ";";
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    //Legger inn svar:
    public void giSvar(int altID) {
        //henter student ID:
        Bruker bruker = getBruker();
        int studentID = bruker.getStudentID();
        try {
            String sql = "INSERT INTO tblsvar (svarStudID, svarAltID) VALUES (" + studentID + "," + altID + ");";
            utsagn = forbindelse.createStatement();
            utsagn.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Sjekker om brukeren har allerede besvart evalueringen:
    public boolean sjekk(int evalID) {
        int studentID = getBruker().getStudentID();
        boolean sjekkEv = false;
        String sql = "SELECT * FROM tblsvar, tblalternativ, tblsporsmal WHERE " +
                "svarAltID = altID AND spmID = altSpmID AND spmEvalID = " + evalID + " AND svarStudID = " + studentID + ";";
        System.out.println(sql);

        try {
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);

            if (resultat.next()) {
                sjekkEv = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sjekkEv;
    }

    //Legger inn ny evaluering for et kurs:
    public int nyEvaluering(int kursID, String navn, String datoUt, String datoInn) {
        resultat = null;
        int key = 0;
        try {
            String sql = "INSERT INTO tblevaluering (evalKursID, evalNavn, evalDatoUt, evalDatoInn) " +
                    "VALUES (" + kursID + ", '" + navn + "', '" + datoUt + "', '" + datoInn + "');";
            utsagn = forbindelse.createStatement();
            utsagn.executeUpdate(sql, utsagn.RETURN_GENERATED_KEYS); //returnerer også ID'en

            //vi må hente ut ID'en som ble generert:
            resultat = utsagn.getGeneratedKeys();
            if (resultat.next()) {
                key = resultat.getInt(1);
                System.out.println("Key: " + key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public int nyttSporsmal(String spm) {
        int spmID = 0;
        int evalID = getEvaluering().getEvalID();
        try {
            String sql ="INSERT INTO tblsporsmal (spmEvalID, spmTekst) VALUES" +
                    " (" + evalID + ", '" + spm + "');";
            System.out.println(sql);
            utsagn = forbindelse.createStatement();
            utsagn.executeUpdate(sql, utsagn.RETURN_GENERATED_KEYS);

            //Henter ut ID'en som ble generert:
            resultat = utsagn.getGeneratedKeys();
            if (resultat.next()) {
                spmID = resultat.getInt(1);
                System.out.println("Key: " + spmID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spmID;
    }

    public void nyttAlternativ(String tekst) {
        int spmID = getSporsmal().getSpmID();
        try {
            String sql = "INSERT INTO tblalternativ (altSpmID, altTekst) VALUES " +
                    "(" + spmID + ", '" + tekst + "');";
            System.out.println(sql);
            utsagn = forbindelse.createStatement();
            utsagn.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //hent alle evalueringer:
    public ResultSet hentAlleEvalueringer() {
        resultat = null;
        try {
           String sql = "SELECT * FROM tblevaluering";
           System.out.println(sql);
           utsagn = forbindelse.createStatement();
           resultat = utsagn.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultat;
    }


    public int hentAntallSvar(int id) {
        int antall = 0;
        try {
            String sql = "SELECT COUNT(*) FROM tblsvar WHERE svarAltID = " + id + ";";
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);
            if (resultat.next()) {
                antall = resultat.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return antall;
    }

    public ResultSet hentBesvarelser(int spmID) {
        resultat = null;
        try {
            String sql = "SELECT * FROM tblsvar, tblalternativ " +
                    "WHERE altSpmID = " + spmID + " AND svarAltID = altID;";
            utsagn = forbindelse.createStatement();
            resultat = utsagn.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    public Sporsmal getSporsmal() {
        return sporsmal;
    }

    public void setSporsmal(Sporsmal sporsmal) {
        this.sporsmal = sporsmal;
    }



    //Gjør at man henter "samme" kontroll:
    public static Kontroll getInstance() {
        //private static Kontroll INSTANCE = new Kontroll();
        return KontrollHolder.INSTANCE;
    }

    public static class KontrollHolder {
        private static final Kontroll INSTANCE = new Kontroll();
    }



}
