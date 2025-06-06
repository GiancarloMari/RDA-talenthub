package model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DlgPregledPrijava extends JDialog {

	private static final long serialVersionUID = 1L;
	private JComboBox<Project> cbProjekti;
	private JTable tblPrijave;
	private User poslodavac;

	public DlgPregledPrijava(User poslodavac) {
		this.poslodavac = poslodavac;

		setTitle("Pregled prijava");
		setSize(700, 450);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		// Gornji panel
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBackground(mainPanel.getBackground());

		JLabel lblProjekti = new JLabel("Odaberi projekt:");
		lblProjekti.setFont(new Font("Segoe UI", Font.BOLD, 14));
		topPanel.add(lblProjekti);

		cbProjekti = new JComboBox<>();
		cbProjekti.setPreferredSize(new Dimension(300, 25));
		topPanel.add(cbProjekti);

		cbProjekti.addActionListener(e -> ucitajPrijave());

		mainPanel.add(topPanel, BorderLayout.NORTH);

		// Tablica
		tblPrijave = new JTable();
		tblPrijave.setRowHeight(24);
		tblPrijave.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblPrijave.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		JScrollPane scrollPane = new JScrollPane(tblPrijave);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Gumb
		JButton btnZaposli = new JButton("Zaposli freelancera");
		btnZaposli.setBackground(new Color(66, 133, 244));
		btnZaposli.setForeground(Color.WHITE);
		btnZaposli.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnZaposli.setFocusPainted(false);
		btnZaposli.setPreferredSize(new Dimension(200, 35));
		btnZaposli.addActionListener(e -> zaposliFreelancera());

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(mainPanel.getBackground());
		bottomPanel.add(btnZaposli);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		ucitajProjekte();
	}

	private void ucitajProjekte() {
		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement(
				"SELECT * FROM projekt WHERE id_poslodavca = ?");
			stmt.setInt(1, poslodavac.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Project p = new Project(
					rs.getInt("id"),
					rs.getString("naziv"),
					rs.getString("opis"),
					rs.getDouble("budzet"),
					rs.getString("rok"),
					rs.getInt("id_poslodavca")
				);
				cbProjekti.addItem(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ucitajPrijave() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID freelancera");
		model.addColumn("Ime");
		model.addColumn("Email");
		model.addColumn("Ponuda");

		Project projekt = (Project) cbProjekti.getSelectedItem();
		if (projekt == null) return;

		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement("""
				SELECT k.id, k.ime, k.email, p.ponuda
				FROM prijava p
				JOIN korisnik k ON p.id_freelancer = k.id
				WHERE p.id_projekt = ?
			""");
			stmt.setInt(1, projekt.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				model.addRow(new Object[] {
					rs.getInt("id"),
					rs.getString("ime"),
					rs.getString("email"),
					rs.getString("ponuda")
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		tblPrijave.setModel(model);
	}

	private void zaposliFreelancera() {
		int red = tblPrijave.getSelectedRow();
		if (red == -1) {
			JOptionPane.showMessageDialog(this, "Odaberi freelancera.");
			return;
		}

		int idFreelancer = Integer.parseInt(tblPrijave.getValueAt(red, 0).toString());
		Project projekt = (Project) cbProjekti.getSelectedItem();

		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO zaposlenje (id_projekt, id_freelancer) VALUES (?, ?)");
			stmt.setInt(1, projekt.getId());
			stmt.setInt(2, idFreelancer);
			stmt.execute();

			JOptionPane.showMessageDialog(this, "Freelancer je zaposlen.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Greška pri zapošljavanju.");
		}
	}
}
