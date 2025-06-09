package model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DlgMojiProjekti extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTable tblMoji;
	private User freelancer;

	public DlgMojiProjekti(User freelancer) {
		this.freelancer = freelancer;

		setTitle("Moji projekti");
		setSize(750, 500);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		JLabel lblNaslov = new JLabel("Pregled mojih projekata", SwingConstants.CENTER);
		lblNaslov.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblNaslov.setForeground(new Color(40, 40, 80));
		mainPanel.add(lblNaslov, BorderLayout.NORTH);

		tblMoji = new JTable();
		tblMoji.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblMoji.setRowHeight(24);
		tblMoji.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		JScrollPane scroll = new JScrollPane(tblMoji);
		mainPanel.add(scroll, BorderLayout.CENTER);

		JButton btnOdustani = new JButton("Odustani od projekta");
		btnOdustani.setBackground(new Color(219, 68, 55));
		btnOdustani.setForeground(Color.WHITE);
		btnOdustani.setFocusPainted(false);
		btnOdustani.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnOdustani.setPreferredSize(new Dimension(200, 35));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(mainPanel.getBackground());
		bottomPanel.add(btnOdustani);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		btnOdustani.addActionListener(e -> obrisiZaposlenje());

		ucitajProjekte();
	}

	private void ucitajProjekte() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID");
		model.addColumn("Naziv");
		model.addColumn("Opis");
		model.addColumn("Budžet");
		model.addColumn("Rok");
		model.addColumn("Plaćeno");
		model.addColumn("Završeno");

		try (Connection conn = DB.connect()) {
			PreparedStatement stmt = conn.prepareStatement("""
				SELECT p.id, p.naziv, p.opis, p.budzet, p.rok, p.placen, p.zavrsen
				FROM projekt p
				JOIN zaposlenje z ON z.id_projekt = p.id
				WHERE z.id_freelancer = ?
			""");
			stmt.setInt(1, freelancer.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				model.addRow(new Object[] {
					rs.getInt("id"),
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
		tblMoji.setModel(model);
	}

	private void obrisiZaposlenje() {
		int red = tblMoji.getSelectedRow();
		if (red == -1) {
			JOptionPane.showMessageDialog(this, "Odaberite projekt.");
			return;
		}

		int idProjekta = (int) tblMoji.getValueAt(red, 0);
		int potvrda = JOptionPane.showConfirmDialog(this,
			"Stvarno želite odustati od projekta?", "Potvrda", JOptionPane.YES_NO_OPTION);

		if (potvrda == JOptionPane.YES_OPTION) {
			try (Connection conn = DB.connect()) {
				PreparedStatement stmt = conn.prepareStatement(
					"DELETE FROM zaposlenje WHERE id_freelancer = ? AND id_projekt = ?"
				);
				stmt.setInt(1, freelancer.getId());
				stmt.setInt(2, idProjekta);
				stmt.execute();

				JOptionPane.showMessageDialog(this, "Projekt uklonjen s vašeg popisa.");
				ucitajProjekte();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Greška pri uklanjanju projekta.");
			}
		}
	}
}
