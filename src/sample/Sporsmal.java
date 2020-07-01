package sample;

public class Sporsmal {
    private int spmID;
    private int evalID;
    private String tekst;

    public Sporsmal(int spmID, String tekst) {
        this.spmID = spmID;
        this.tekst = tekst;
    }

    public Sporsmal(int spmID, int evalID, String tekst) {
        this.spmID = spmID;
        this.tekst = tekst;
        this.evalID = evalID;
    }

    public int getSpmID() {
        return spmID;
    }

    public String getTekst() {
        return tekst;
    }

    public void setSpmID(int spmID) {
        this.spmID = spmID;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public int getEvalID() {
        return evalID;
    }

    public void setEvalID(int evalID) {
        this.evalID = evalID;
    }

    @Override
    public String toString() {
        return "sporsmal{" +
                "spmID=" + spmID +
                ", tekst='" + tekst + '\'' +
                '}' + evalID;
    }
}
