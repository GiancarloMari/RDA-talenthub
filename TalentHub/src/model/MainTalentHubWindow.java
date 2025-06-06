package model;

import javax.swing.*;
import java.awt.*;

public class MainTalentHubWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private User prijavljeni;

	public MainTalentHubWindow(User korisnik) {
		this.prijavljeni = korisnik;

		setTitle("TalentHub – Dobrodošao " + korisnik.getIme() + " (" + korisnik.getTip() + ")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(null); // centriraj

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		JLabel lblDobrodosao = new JLabel("Dobrodošao, " + korisnik.getIme() + " (" + korisnik.getTip() + ")");
		lblDobrodosao.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblDobrodosao.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblDobrodosao.setForeground(new Color(40, 40, 80));
		mainPanel.add(lblDobrodosao);

		mainPanel.add(Box.createVerticalStrut(25));

		// Gumb: Moj profil
		JButton btnProfil = stilizirajGumb("Moj profil");
		mainPanel.add(btnProfil);
		mainPanel.add(Box.createVerticalStrut(15));

		// Freelanceri
		if (korisnik.getTip().equals("freelancer")) {
			JButton btnPretraga = stilizirajGumb("Pretraga projekata");
			JButton btnMoji = stilizirajGumb("Moji projekti");
			JButton btnZavrseni = stilizirajGumb("Završeni i plaćeni");

			mainPanel.add(btnPretraga);
			mainPanel.add(Box.createVerticalStrut(10));
			mainPanel.add(btnMoji);
			mainPanel.add(Box.createVerticalStrut(10));
			mainPanel.add(btnZavrseni);

			btnPretraga.addActionListener(e -> new DlgPretragaProjekata(prijavljeni).setVisible(true));
			btnMoji.addActionListener(e -> new DlgMojiProjekti(prijavljeni).setVisible(true));
			btnZavrseni.addActionListener(e -> new DlgZavrseniPlaceni(prijavljeni).setVisible(true));
		}

		// Poslodavci
		else if (korisnik.getTip().equals("poslodavac")) {
			JButton btnDodaj = stilizirajGumb("Dodaj projekt");
			JButton btnPregled = stilizirajGumb("Pregled prijava");
			JButton btnZavrsi = stilizirajGumb("Označi kao završen");
			JButton btnPlati = stilizirajGumb("Označi kao plaćen");

			mainPanel.add(btnDodaj);
			mainPanel.add(Box.createVerticalStrut(10));
			mainPanel.add(btnPregled);
			mainPanel.add(Box.createVerticalStrut(10));
			mainPanel.add(btnZavrsi);
			mainPanel.add(Box.createVerticalStrut(10));
			mainPanel.add(btnPlati);

			btnDodaj.addActionListener(e -> new DlgDodajProjekt(prijavljeni).setVisible(true));
			btnPregled.addActionListener(e -> new DlgPregledPrijava(prijavljeni).setVisible(true));
			btnZavrsi.addActionListener(e -> new DlgUrediProjekte(prijavljeni).setVisible(true));
			btnPlati.addActionListener(e -> new DlgUrediProjekte(prijavljeni).setVisible(true));
		}

		// Gumb: Odjava (dostupan svima)
		mainPanel.add(Box.createVerticalStrut(30));
		JButton btnOdjava = stilizirajGumb("Odjava");
		mainPanel.add(btnOdjava);

		// Akcije
		btnProfil.addActionListener(e -> new DlgProfilKorisnika(prijavljeni).setVisible(true));
		btnOdjava.addActionListener(e -> {
			dispose(); // zatvori glavni prozor
			new FrmLogin().setVisible(true); // otvori login
		});
	}

	private JButton stilizirajGumb(String tekst) {
		JButton btn = new JButton(tekst);
		btn.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn.setBackground(new Color(66, 133, 244));
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btn.setMaximumSize(new Dimension(220, 35));
		return btn;
	}
}
