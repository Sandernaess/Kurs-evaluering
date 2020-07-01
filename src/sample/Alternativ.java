package sample;

public class Alternativ {
    private int altID;
    private String tekst;
    private int spmID;

    public Alternativ(int altID, String tekst) {
        this.altID = altID;
        this.tekst = tekst;
    }

    public Alternativ(int altID, String tekst, int spmID) {
        this.altID = altID;
        this.tekst = tekst;
        this.spmID = spmID;
    }

    public int getAltID() {
        return altID;
    }

    public String getTekst() {
        return tekst;
    }

    public void setAltID(int altID) {
        this.altID = altID;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public int getSpmID() {
        return spmID;
    }

    public void setSpmID(int spmID) {
        this.spmID = spmID;
    }

    @Override
    public String toString() {
        return "Alternativ{" +
                "altID=" + altID +
                ", tekst='" + tekst + '\'' +
                '}';
    }
}
