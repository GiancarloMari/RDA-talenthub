package model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DlgZavrseniPlaceni extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable tblZavrseni;
	private User freelancer;

	public DlgZavrseniPlaceni(User freelancer) {
		this.freelancer = freelancer;

		setTitle("Završeni i plaćeni projekti");
		setSize(750, 450);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		JLabel lblNaslov = new JLabel("Završeni i plaćeni projekti", SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblNaslov.setForeground(new Color(40, 40, 80));
		mainPanel.add(lblNaslov, BorderLayout.NORTH);

		tblZavrseni = new JTable();
		tblZavrseni.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblZavrseni.setRowHeight(24);
		tblZavrseni.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		JScrollPane scroll = new JScrollPane(tblZavrseni);

		mainPanel.add(scroll, BorderLayout.CENTER);

		ucitajZavrsene();
	}

	private void ucitajZavrsene() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Naziv");
		model.addColumn("Opis");
		model.addColumn("Budžet");
		model.addColumn("Rok");
		model.addColumn("Plaćeno");
		model.addColumn("Završeno");

		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement("""
				SELECT p.naziv, p.opis, p.budzet, p.rok, p.placen, p.zavrsen
				FROM projekt p
				JOIN zaposlenje z ON z.id_projekt = p.id
				WHERE z.id_freelancer = ?
				  AND p.placen = TRUE
				  AND p.zavrsen = TRUE
			""");
			stmt.setInt(1, freelancer.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				model.addRow(new Object[]{
					rs.getString("naziv"),
					rs.getString("opis"),
					rs.getDouble("budzet"),
					rs.getString("rok"),
					rs.getBoolean("placen") ? "DA" : "NE",
					rs.getBoolean("zavrsen") ? "DA" : "NE"
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		tblZavrseni.setModel(model);
	}
}
