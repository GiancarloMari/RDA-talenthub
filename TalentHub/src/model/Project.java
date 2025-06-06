package model;

public class Project {
	private int id;
	private String naziv;
	private String opis;
	private double budzet;
	private String rok;
	private int idPoslodavca;

	public Project(int id, String naziv, String opis, double budzet, String rok, int idPoslodavca) {
		this.id = id;
		this.naziv = naziv;
		this.opis = opis;
		this.budzet = budzet;
		this.rok = rok;
		this.idPoslodavca = idPoslodavca;
	}

	public int getId() {
		return id;
	}

	public String getNaziv() {
		return naziv;
	}

	public String getOpis() {
		return opis;
	}

	public double getBudzet() {
		return budzet;
	}

	public String getRok() {
		return rok;
	}

	public int getIdPoslodavca() {
		return idPoslodavca;
	}

	@Override
	public String toString() {
		return naziv + " (ID: " + id + ")";
	}
}
