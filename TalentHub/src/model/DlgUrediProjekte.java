package model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DlgUrediProjekte extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable tblProjekti;
	private User poslodavac;

	public DlgUrediProjekte(User poslodavac) {
		this.poslodavac = poslodavac;

		setTitle("Uredi projekte");
		setSize(700, 450);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		// Tablica
		tblProjekti = new JTable();
		tblProjekti.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblProjekti.setRowHeight(24);
		tblProjekti.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		JScrollPane scroll = new JScrollPane(tblProjekti);
		mainPanel.add(scroll, BorderLayout.CENTER);

		// Gumbi
		JButton btnZavrsi = new JButton("Označi kao završen");
		JButton btnPlati = new JButton("Označi kao plaćen");

		for (JButton btn : new JButton[] {btnZavrsi, btnPlati}) {
			btn.setBackground(new Color(66, 133, 244));
			btn.setForeground(Color.WHITE);
			btn.setFocusPainted(false);
			btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
			btn.setPreferredSize(new Dimension(180, 35));
		}

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(mainPanel.getBackground());
		bottomPanel.add(btnZavrsi);
		bottomPanel.add(Box.createHorizontalStrut(20));
		bottomPanel.add(btnPlati);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		btnZavrsi.addActionListener(e -> oznaciProjekt("zavrsen"));
		btnPlati.addActionListener(e -> oznaciProjekt("placen"));

		ucitajProjekte();
	}

	private void ucitajProjekte() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID");
		model.addColumn("Naziv");
		model.addColumn("Završeno");
		model.addColumn("Plaćeno");

		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT id, naziv, zavrsen, placen FROM projekt WHERE id_poslodavca = ?");
			stmt.setInt(1, poslodavac.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				model.addRow(new Object[]{
					rs.getInt("id"),
					rs.getString("naziv"),
					rs.getBoolean("zavrsen") ? "DA" : "NE",
					rs.getBoolean("placen") ? "DA" : "NE"
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		tblProjekti.setModel(model);
	}

	private void oznaciProjekt(String polje) {
		int red = tblProjekti.getSelectedRow();
		if (red == -1) {
			JOptionPane.showMessageDialog(this, "Odaberi projekt.");
			return;
		}
		int id = Integer.parseInt(tblProjekti.getValueAt(red, 0).toString());

		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement(
				"UPDATE projekt SET " + polje + " = TRUE WHERE id = ?");
			stmt.setInt(1, id);
			stmt.execute();

			JOptionPane.showMessageDialog(this, "Projekt ažuriran.");
			ucitajProjekte();
			provjeriZaRecenziju(id);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Greška pri ažuriranju.");
		}
	}

	private void provjeriZaRecenziju(int idProjekta) {
		try (Connection conn = DB.connect()) {
			PreparedStatement provjera = conn.prepareStatement(
				"SELECT zavrsen, placen FROM projekt WHERE id = ?");
			provjera.setInt(1, idProjekta);
			ResultSet rs = provjera.executeQuery();

			if (rs.next() && rs.getBoolean("zavrsen") && rs.getBoolean("placen")) {
				PreparedStatement zaposlenje = conn.prepareStatement(
					"SELECT id_freelancer FROM zaposlenje WHERE id_projekt = ?");
				zaposlenje.setInt(1, idProjekta);
				ResultSet rsZ = zaposlenje.executeQuery();

				if (rsZ.next()) {
					int freelancerId = rsZ.getInt("id_freelancer");

					PreparedStatement provjeraRec = conn.prepareStatement(
						"SELECT * FROM recenzija WHERE ocjenjivac_id = ? AND ocijenjeni_id = ? AND id_projekt = ?");
					provjeraRec.setInt(1, poslodavac.getId());
					provjeraRec.setInt(2, freelancerId);
					provjeraRec.setInt(3, idProjekta);
					ResultSet rsRec = provjeraRec.executeQuery();

					if (!rsRec.next()) {
						User freelancer = dohvatiKorisnika(freelancerId);
						if (freelancer != null) {
							new DlgRecenzija(poslodavac, freelancer, idProjekta).setVisible(true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private User dohvatiKorisnika(int id) {
		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM korisnik WHERE id = ?");
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return new User(
					id,
					rs.getString("ime"),
					rs.getString("email"),
					rs.getString("lozinka"),
					rs.getString("tip")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
