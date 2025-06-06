package model;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FrmAdminPanel extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable tblKorisnici;
	private User prijavljeni;

	public FrmAdminPanel(User korisnik) {
		this.prijavljeni = korisnik;

		setTitle("Admin panel – " + korisnik.getIme());
		setSize(650, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		// Info
		JLabel lblInfo = new JLabel("Prijavljeni ste kao: " + korisnik.getEmail());
		lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		mainPanel.add(lblInfo, BorderLayout.NORTH);

		// Tablica
		tblKorisnici = new JTable();
		tblKorisnici.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblKorisnici.setRowHeight(24);
		tblKorisnici.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		JScrollPane scroll = new JScrollPane(tblKorisnici);
		mainPanel.add(scroll, BorderLayout.CENTER);

		// Gumbi
		JButton btnObrisi = new JButton("Obriši korisnika");
		btnObrisi.setBackground(new Color(219, 68, 55));
		btnObrisi.setForeground(Color.WHITE);
		btnObrisi.setFocusPainted(false);
		btnObrisi.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnObrisi.setPreferredSize(new Dimension(180, 35));

		JButton btnOdjava = new JButton("Odjava");
		btnOdjava.setBackground(new Color(66, 133, 244));
		btnOdjava.setForeground(Color.WHITE);
		btnOdjava.setFocusPainted(false);
		btnOdjava.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnOdjava.setPreferredSize(new Dimension(180, 35));

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		bottomPanel.setBackground(mainPanel.getBackground());
		bottomPanel.add(btnObrisi);
		bottomPanel.add(btnOdjava);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		btnObrisi.addActionListener(e -> obrisiKorisnika());

		btnOdjava.addActionListener(e -> {
			dispose(); // zatvori admin panel
			new FrmLogin().setVisible(true); // otvori login
		});

		ucitajKorisnike();
	}

	private void ucitajKorisnike() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID");
		model.addColumn("Ime");
		model.addColumn("Email");
		model.addColumn("Tip");

		try (Connection conn = DB.connect()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id, ime, email, tip FROM korisnik");

			while (rs.next()) {
				model.addRow(new Object[]{
					rs.getInt("id"),
					rs.getString("ime"),
					rs.getString("email"),
					rs.getString("tip")
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		tblKorisnici.setModel(model);
	}

	private void obrisiKorisnika() {
		int red = tblKorisnici.getSelectedRow();
		if (red == -1) {
			JOptionPane.showMessageDialog(this, "Odaberi korisnika.");
			return;
		}

		int id = (int) tblKorisnici.getValueAt(red, 0);
		int potvrda = JOptionPane.showConfirmDialog(this, "Stvarno obrisati korisnika ID " + id + "?", "Potvrda", JOptionPane.YES_NO_OPTION);

		if (potvrda == JOptionPane.YES_OPTION) {
			try (Connection conn = DB.connect()) {
				PreparedStatement stmt = conn.prepareStatement("DELETE FROM korisnik WHERE id = ?");
				stmt.setInt(1, id);
				stmt.execute();

				JOptionPane.showMessageDialog(this, "Korisnik obrisan.");
				ucitajKorisnike();

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Greška pri brisanju.");
			}
		}
	}
}
