package model;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DlgProfilKorisnika extends JDialog {

	private static final long serialVersionUID = 1L;
	private User korisnik;

	public DlgProfilKorisnika(User korisnik) {
		this.korisnik = korisnik;
		setTitle("Profil korisnika – " + korisnik.getIme());
		setSize(500, 340);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		JLabel lblNaslov = new JLabel("Moj profil", SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblNaslov.setForeground(new Color(40, 40, 80));
		mainPanel.add(lblNaslov, BorderLayout.NORTH);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBackground(Color.WHITE);
		infoPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.LIGHT_GRAY),
			BorderFactory.createEmptyBorder(15, 20, 15, 20)
		));

		infoPanel.add(stvoriLabelu("<b>Ime:</b> " + korisnik.getIme()));
		infoPanel.add(stvoriLabelu("<b>Email:</b> " + korisnik.getEmail()));
		infoPanel.add(stvoriLabelu("<b>Tip korisnika:</b> " + korisnik.getTip()));
		infoPanel.add(Box.createVerticalStrut(10));

		// Statistika
		try {
			Connection conn = DB.connect();

			PreparedStatement stmt1 = conn.prepareStatement(
				korisnik.getTip().equals("freelancer") ?
				"SELECT COUNT(*) FROM projekt p JOIN zaposlenje z ON p.id = z.id_projekt WHERE z.id_freelancer = ? AND p.zavrsen = TRUE" :
				"SELECT COUNT(*) FROM projekt WHERE id_poslodavca = ? AND zavrsen = TRUE"
			);
			stmt1.setInt(1, korisnik.getId());
			ResultSet rs1 = stmt1.executeQuery();
			if (rs1.next()) {
				infoPanel.add(stvoriLabelu("<b>Završeni projekti:</b> " + rs1.getInt(1)));
			}

			PreparedStatement stmt2 = conn.prepareStatement(
				korisnik.getTip().equals("freelancer") ?
				"SELECT SUM(p.budzet) FROM projekt p JOIN zaposlenje z ON p.id = z.id_projekt WHERE z.id_freelancer = ? AND p.placen = TRUE" :
				"SELECT SUM(budzet) FROM projekt WHERE id_poslodavca = ? AND placen = TRUE"
			);
			stmt2.setInt(1, korisnik.getId());
			ResultSet rs2 = stmt2.executeQuery();
			if (rs2.next()) {
				double iznos = rs2.getDouble(1);
				String label = korisnik.getTip().equals("freelancer") ? "<b>Zarađeno:</b> " : "<b>Potrošeno:</b> ";
				infoPanel.add(stvoriLabelu(label + String.format("%.2f €", iznos)));
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			infoPanel.add(stvoriLabelu("<i>Greška pri dohvaćanju podataka.</i>"));
		}

		mainPanel.add(infoPanel, BorderLayout.CENTER);

		JButton btnZatvori = new JButton("Zatvori");
		btnZatvori.setBackground(new Color(66, 133, 244));
		btnZatvori.setForeground(Color.WHITE);
		btnZatvori.setFocusPainted(false);
		btnZatvori.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnZatvori.addActionListener(e -> dispose());

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(mainPanel.getBackground());
		bottomPanel.add(btnZatvori);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
	}

	private JLabel stvoriLabelu(String html) {
		JLabel lbl = new JLabel("<html>" + html + "</html>");
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
		return lbl;
	}
}
