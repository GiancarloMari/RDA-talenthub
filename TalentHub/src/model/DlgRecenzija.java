package model;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DlgRecenzija extends JDialog {

	private static final long serialVersionUID = 1L;
	private User ocjenjivac;
	private User ocijenjeni;
	private int idProjekta;

	private JComboBox<Integer> cbOcjena;
	private JTextArea taKomentar;

	public DlgRecenzija(User ocjenjivac, User ocijenjeni, int idProjekta) {
		this.ocjenjivac = ocjenjivac;
		this.ocijenjeni = ocijenjeni;
		this.idProjekta = idProjekta;

		setTitle("Recenzija za " + ocijenjeni.getIme());
		setSize(450, 350);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
		panel.setBackground(new Color(245, 245, 250));
		setContentPane(panel);

		JLabel lblNaslov = new JLabel("⭐ Ostavi recenziju za " + ocijenjeni.getIme(), SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblNaslov.setForeground(new Color(40, 40, 80));
		panel.add(lblNaslov, BorderLayout.NORTH);

		JPanel form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
		form.setBackground(panel.getBackground());

		form.add(new JLabel("Ocjena (1–5):"));
		cbOcjena = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
		cbOcjena.setMaximumSize(new Dimension(60, 25));
		form.add(cbOcjena);
		form.add(Box.createVerticalStrut(10));

		form.add(new JLabel("Komentar:"));
		taKomentar = new JTextArea(5, 30);
		taKomentar.setLineWrap(true);
		taKomentar.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(taKomentar);
		form.add(scroll);

		panel.add(form, BorderLayout.CENTER);

		JButton btnSpremi = new JButton("Spremi recenziju");
		btnSpremi.setBackground(new Color(66, 133, 244));
		btnSpremi.setForeground(Color.WHITE);
		btnSpremi.setFocusPainted(false);
		btnSpremi.addActionListener(e -> spremiRecenziju());
		JPanel bottom = new JPanel();
		bottom.setBackground(panel.getBackground());
		bottom.add(btnSpremi);
		panel.add(bottom, BorderLayout.SOUTH);
	}

	private void spremiRecenziju() {
		int ocjena = (int) cbOcjena.getSelectedItem();
		String komentar = taKomentar.getText().trim();

		try {
			Connection conn = DB.connect();
			PreparedStatement stmt = conn.prepareStatement("""
				INSERT INTO recenzija (ocjenjivac_id, ocijenjeni_id, id_projekt, ocjena, komentar)
				VALUES (?, ?, ?, ?, ?)
			""");
			stmt.setInt(1, ocjenjivac.getId());
			stmt.setInt(2, ocijenjeni.getId());
			stmt.setInt(3, idProjekta);
			stmt.setInt(4, ocjena);
			stmt.setString(5, komentar);
			stmt.execute();

			conn.close();
			JOptionPane.showMessageDialog(this, "Recenzija spremljena.");
			dispose();
		} catch (SQLIntegrityConstraintViolationException ex) {
			JOptionPane.showMessageDialog(this, "Već si ocijenio ovog korisnika za taj projekt.");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Greška pri spremanju recenzije.");
		}
	}
}
