package sample;

public class Evaluering {
    private int evalID;
    private String evalNavn;
    private String datoUt;
    private String datoInn;
    private int kursID;

    public Evaluering(int evalID, String evalNavn, String datoUt, String datoInn) {
        this.evalID = evalID;
        this.evalNavn = evalNavn;
        this.datoUt = datoUt;
        this.datoInn = datoInn;
    }

    public Evaluering(int evalID, String evalNavn, String datoUt, String datoInn, int kursID) {
        this.evalID = evalID;
        this.evalNavn = evalNavn;
        this.datoUt = datoUt;
        this.datoInn = datoInn;
        this.kursID = kursID;
    }



    public int getEvalID() {
        return evalID;
    }

    public String getEvalNavn() {
        return evalNavn;
    }

    public String getDatoUt() {
        return datoUt;
    }

    public String getDatoInn() {
        return datoInn;
    }

    public void setEvalID(int evalID) {
        this.evalID = evalID;
    }

    public void setEvalNavn(String evalNavn) {
        this.evalNavn = evalNavn;
    }

    public void setDatoUt(String datoUt) {
        this.datoUt = datoUt;
    }

    public void setDatoInn(String datoInn) {
        this.datoInn = datoInn;
    }

    public int getKursID() {
        return kursID;
    }

    public void setKursID(int kursID) {
        this.kursID = kursID;
    }

    @Override
    public String toString() {
        return "Evaluering{" +
                "evalID=" + evalID +
                ", evalNavn='" + evalNavn + '\'' +
                ", datoUt='" + datoUt + '\'' +
                ", datoInn='" + datoInn + '\'' +
                '}';
    }
}
