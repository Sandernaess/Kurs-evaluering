package sample;

public class Kurs {
    private String kursNr;
    private String semester;
    private int kursFNR;

    public Kurs(String kursNr, String semester) {
        this.kursNr = kursNr;
        this.semester = semester;
    }

    //for administrasjon: (vi trenger kursNr)
    public Kurs(int kursFNR, String kursNr, String semester) {
        this.kursFNR = kursFNR;
        this.kursNr = kursNr;
        this.semester = semester;
    }

    public String getKursNr() {
        return kursNr;
    }

    public String getSemester() {
        return semester;
    }

    public int getKursFNR() {
        return kursFNR;
    }

    @Override
    public String toString() {
        return "Kurs{" +
                "kursNr='" + kursNr + '\'' +
                ", semester='" + semester + '\'' +
                '}';
    }
}
