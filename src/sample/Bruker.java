package sample;

public class Bruker {
    private int studentID;
    private String navn;
    private String adresse;
    private String passord;


    public Bruker(int studentID, String navn, String adresse, String passord) {
        this.studentID = studentID;
        this.navn = navn;
        this.adresse = adresse;
        this.passord = passord;
    }

    public Bruker(int studentID, String navn) {
        this.studentID = studentID;
        this.navn = navn;
    }

    public int getStudentID() {
        return studentID;
    }

    public String getNavn() {
        return navn;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getPassord() {
        return passord;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setPassord(String passord) {
        this.passord = passord;
    }


}
