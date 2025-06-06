package model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DlgPretragaProjekata extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField tfKljucnaRijec;
	private JTable tblProjekti;
	private User freelancer;

	public DlgPretragaProjekata(User freelancer) {
		this.freelancer = freelancer;
		setTitle("Pretraga projekata");
		setSize(750, 450);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
		mainPanel.setLayout(new BorderLayout(10, 10));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		// Gornji red – pretraga
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBackground(mainPanel.getBackground());
		topPanel.add(new JLabel("Ključna riječ:"));
		tfKljucnaRijec = new JTextField(20);
		topPanel.add(tfKljucnaRijec);

		JButton btnPretrazi = new JButton("Pretraži");
		btnPretrazi.setBackground(new Color(66, 133, 244));
		btnPretrazi.setForeground(Color.WHITE);
		btnPretrazi.setFocusPainted(false);
		topPanel.add(btnPretrazi);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		// Tablica
		tblProjekti = new JTable();
		JScrollPane scroll = new JScrollPane(tblProjekti);
		mainPanel.add(scroll, BorderLayout.CENTER);

		// Donji gumb
		JButton btnPrijaviSe = new JButton("Prijavi se");
		btnPrijaviSe.setPreferredSize(new Dimension(140, 35));
		btnPrijaviSe.setBackground(new Color(52, 168, 83));
		btnPrijaviSe.setForeground(Color.WHITE);
		btnPrijaviSe.setFocusPainted(false);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(mainPanel.getBackground());
		bottomPanel.add(btnPrijaviSe);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		// akcije
		btnPretrazi.addActionListener(e -> dohvatiProjekte());
		btnPrijaviSe.addActionListener(e -> prijaviSeNaProjekt());

		dohvatiProjekte();
	}

	private void dohvatiProjekte() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID");
		model.addColumn("Naziv");
		model.addColumn("Opis");
		model.addColumn("Budžet");
		model.addColumn("Rok");

		try {
			Connection conn = DB.connect();
			String kljucna = "%" + tfKljucnaRijec.getText().trim() + "%";

			PreparedStatement stmt = conn.prepareStatement("""
				SELECT * FROM projekt
				WHERE (naziv LIKE ? OR opis LIKE ?)
				AND zavrsen = FALSE
			""");

			stmt.setString(1, kljucna);
			stmt.setString(2, kljucna);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				model.addRow(new Object[] {
					rs.getInt("id"),
					rs.getString("naziv"),
					rs.getString("opis"),
					rs.getDouble("budzet"),
					rs.getString("rok")
				});
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		tblProjekti.setModel(model);
	}

	private void prijaviSeNaProjekt() {
		int red = tblProjekti.getSelectedRow();
		if (red == -1) {
			JOptionPane.showMessageDialog(null, "Odaberi projekt.");
			return;
		}
		int idProjekta = Integer.parseInt(tblProjekti.getValueAt(red, 0).toString());

		String ponuda = JOptionPane.showInputDialog("Unesi ponudu:");
		if (ponuda == null || ponuda.isBlank()) return;

		try {
			Connection conn = DB.connect();
			PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO prijava (id_freelancer, id_projekt, ponuda) VALUES (?, ?, ?)");
			stmt.setInt(1, freelancer.getId());
			stmt.setInt(2, idProjekta);
			stmt.setString(3, ponuda);
			stmt.execute();
			conn.close();
			JOptionPane.showMessageDialog(null, "Prijava poslana.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Greška pri prijavi.");
		}
	}
}
