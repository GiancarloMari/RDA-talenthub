package model;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DlgDodajProjekt extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField tfNaziv, tfBudzet, tfRok;
	private JTextArea taOpis;
	private User prijavljeni;

	public DlgDodajProjekt(User poslodavac) {
		this.prijavljeni = poslodavac;

		setTitle("Dodavanje novog projekta");
		setSize(500, 430);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBackground(new Color(245, 245, 250));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		setContentPane(mainPanel);

		JLabel lblNaslov = new JLabel("Dodavanje novog projekta", SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblNaslov.setForeground(new Color(40, 40, 80));
		mainPanel.add(lblNaslov, BorderLayout.NORTH);

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(mainPanel.getBackground());
		mainPanel.add(formPanel, BorderLayout.CENTER);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;

		Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
		Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

		// Naziv
		c.gridx = 0; c.gridy = 0;
		JLabel lblNaziv = new JLabel("Naziv:");
		lblNaziv.setFont(labelFont);
		formPanel.add(lblNaziv, c);

		c.gridx = 1;
		tfNaziv = new JTextField(25);
		tfNaziv.setFont(fieldFont);
		formPanel.add(tfNaziv, c);

		// Opis
		c.gridx = 0; c.gridy = 1;
		JLabel lblOpis = new JLabel("Opis:");
		lblOpis.setFont(labelFont);
		formPanel.add(lblOpis, c);

		c.gridx = 1;
		taOpis = new JTextArea(4, 25);
		taOpis.setFont(fieldFont);
		taOpis.setLineWrap(true);
		taOpis.setWrapStyleWord(true);
		formPanel.add(new JScrollPane(taOpis), c);

		// Budžet
		c.gridx = 0; c.gridy = 2;
		JLabel lblBudzet = new JLabel("Budžet (€):");
		lblBudzet.setFont(labelFont);
		formPanel.add(lblBudzet, c);

		c.gridx = 1;
		tfBudzet = new JTextField(10);
		tfBudzet.setFont(fieldFont);
		formPanel.add(tfBudzet, c);

		// Rok
		c.gridx = 0; c.gridy = 3;
		JLabel lblRok = new JLabel("Rok:");
		lblRok.setFont(labelFont);
		formPanel.add(lblRok, c);

		c.gridx = 1;
		tfRok = new JTextField(15);
		tfRok.setFont(fieldFont);
		formPanel.add(tfRok, c);

		// Gumb
		JButton btnSpremi = new JButton("Spremi");
		btnSpremi.setBackground(new Color(66, 133, 244));
		btnSpremi.setForeground(Color.WHITE);
		btnSpremi.setFocusPainted(false);
		btnSpremi.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnSpremi.setPreferredSize(new Dimension(120, 35));
		btnSpremi.addActionListener(e -> spremiProjekt());

		JPanel bottom = new JPanel();
		bottom.setBackground(mainPanel.getBackground());
		bottom.add(btnSpremi);
		mainPanel.add(bottom, BorderLayout.SOUTH);
	}

	private void spremiProjekt() {
		String naziv = tfNaziv.getText().trim();
		String opis = taOpis.getText().trim();
		String rok = tfRok.getText().trim();
		double budzet;

		try {
			budzet = Double.parseDouble(tfBudzet.getText().trim());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Unesi ispravan broj za budžet.");
			return;
		}

		try {
			Connection conn = DB.connect();
			PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO projekt (naziv, opis, budzet, rok, id_poslodavca) VALUES (?, ?, ?, ?, ?)");
			stmt.setString(1, naziv);
			stmt.setString(2, opis);
			stmt.setDouble(3, budzet);
			stmt.setString(4, rok);
			stmt.setInt(5, prijavljeni.getId());
			stmt.execute();
			conn.close();

			JOptionPane.showMessageDialog(this, "Projekt je uspješno spremljen.");
			dispose();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Greška pri spremanju projekta.");
		}
	}
}
