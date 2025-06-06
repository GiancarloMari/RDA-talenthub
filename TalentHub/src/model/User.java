package model;

public class User {
    private int id;
    private String ime;
    private String email;
    private String lozinka;
    private String tip; // freelancer ili poslodavac

    public User(int id, String ime, String email, String lozinka, String tip) {
        this.id = id;
        this.ime = ime;
        this.email = email;
        this.lozinka = lozinka;
        this.tip = tip;
    }

    public int getId() { return id; }
    public String getIme() { return ime; }
    public String getEmail() { return email; }
    public String getLozinka() { return lozinka; }
    public String getTip() { return tip; }

    @Override
    public String toString() {
        return ime + " (" + tip + ")";
    }
}