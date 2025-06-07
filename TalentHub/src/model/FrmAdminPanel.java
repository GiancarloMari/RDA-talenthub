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
		setSize(950, 500); // prošireno
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
		mainPanel.setBackground(new Color(245, 245, 250));
		setContentPane(mainPanel);

		JLabel lblInfo = new JLabel("Prijavljeni ste kao: " + korisnik.getEmail());
		lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		mainPanel.add(lblInfo, BorderLayout.NORTH);

		tblKorisnici = new JTable();
		tblKorisnici.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblKorisnici.setRowHeight(24);
		tblKorisnici.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		JScrollPane scroll = new JScrollPane(tblKorisnici);
		mainPanel.add(scroll, BorderLayout.CENTER);

		JButton btnDodaj = new JButton("Dodaj korisnika");
		btnDodaj.setBackground(new Color(76, 175, 80));
		btnDodaj.setForeground(Color.WHITE);
		btnDodaj.setFocusPainted(false);
		btnDodaj.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnDodaj.setPreferredSize(new Dimension(180, 35));

		JButton btnObrisi = new JButton("Obriši korisnika");
		btnObrisi.setBackground(new Color(219, 68, 55));
		btnObrisi.setForeground(Color.WHITE);
		btnObrisi.setFocusPainted(false);
		btnObrisi.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnObrisi.setPreferredSize(new Dimension(180, 35));

		JButton btnPromijeniLozinku = new JButton("Promijeni lozinku");
		btnPromijeniLozinku.setBackground(new Color(255, 193, 7));
		btnPromijeniLozinku.setForeground(Color.BLACK);
		btnPromijeniLozinku.setFocusPainted(false);
		btnPromijeniLozinku.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnPromijeniLozinku.setPreferredSize(new Dimension(180, 35));

		JButton btnOdjava = new JButton("Odjava");
		btnOdjava.setBackground(new Color(66, 133, 244));
		btnOdjava.setForeground(Color.WHITE);
		btnOdjava.setFocusPainted(false);
		btnOdjava.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnOdjava.setPreferredSize(new Dimension(180, 35));

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		bottomPanel.setBackground(mainPanel.getBackground());
		bottomPanel.add(btnDodaj);
		bottomPanel.add(btnObrisi);
		bottomPanel.add(btnPromijeniLozinku);
		bottomPanel.add(btnOdjava);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		btnDodaj.addActionListener(e -> {
			JDialog dialog = new JDialog(this, "Dodavanje korisnika", true);
			dialog.setSize(400, 300);
			dialog.setLocationRelativeTo(this);
			dialog.setLayout(new GridLayout(5, 2, 10, 10));

			JTextField tfIme = new JTextField();
			JTextField tfEmail = new JTextField();
			JPasswordField tfLozinka = new JPasswordField();
			JComboBox<String> cmbTip = new JComboBox<>(new String[]{"admin", "poslodavac", "freelancer"});

			dialog.add(new JLabel("Ime:"));
			dialog.add(tfIme);
			dialog.add(new JLabel("Email:"));
			dialog.add(tfEmail);
			dialog.add(new JLabel("Lozinka:"));
			dialog.add(tfLozinka);
			dialog.add(new JLabel("Tip korisnika:"));
			dialog.add(cmbTip);

			JButton btnSpremi = new JButton("Spremi");
			JButton btnOtkazi = new JButton("Otkaži");
			dialog.add(btnSpremi);
			dialog.add(btnOtkazi);

			btnOtkazi.addActionListener(ev -> dialog.dispose());

			btnSpremi.addActionListener(ev -> {
				String ime = tfIme.getText().trim();
				String email = tfEmail.getText().trim();
				String lozinka = new String(tfLozinka.getPassword()).trim();
				String tip = (String) cmbTip.getSelectedItem();

				if (ime.isEmpty() || email.isEmpty() || lozinka.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Sva polja moraju biti popunjena.");
					return;
				}

				try (Connection conn = DB.connect()) {
					PreparedStatement provjera = conn.prepareStatement("SELECT * FROM korisnik WHERE email = ?");
					provjera.setString(1, email);
					ResultSet rs = provjera.executeQuery();
					if (rs.next()) {
						JOptionPane.showMessageDialog(dialog, "Email je već u upotrebi.");
						return;
					}

					PreparedStatement stmt = conn.prepareStatement("INSERT INTO korisnik (ime, email, lozinka, tip) VALUES (?, ?, ?, ?)");
					stmt.setString(1, ime);
					stmt.setString(2, email);
					stmt.setString(3, lozinka);
					stmt.setString(4, tip);
					stmt.execute();

					JOptionPane.showMessageDialog(dialog, "Korisnik uspješno dodan.");
					dialog.dispose();
					ucitajKorisnike();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(dialog, "Greška prilikom unosa korisnika.");
				}
			});

			dialog.setVisible(true);
		});

		btnObrisi.addActionListener(e -> obrisiKorisnika());

		btnOdjava.addActionListener(e -> {
			dispose();
			new FrmLogin().setVisible(true);
		});

		btnPromijeniLozinku.addActionListener(e -> {
			int red = tblKorisnici.getSelectedRow();
			if (red == -1) {
				JOptionPane.showMessageDialog(this, "Odaberi korisnika.");
				return;
			}

			int id = (int) tblKorisnici.getValueAt(red, 0);
			String email = (String) tblKorisnici.getValueAt(red, 2);

			JDialog dlg = new JDialog(this, "Promjena lozinke: " + email, true);
			dlg.setSize(400, 250);
			dlg.setLayout(new GridLayout(4, 2, 10, 10));
			dlg.setLocationRelativeTo(this);

			JPasswordField pfStara = new JPasswordField();
			JPasswordField pfNova = new JPasswordField();
			JPasswordField pfPotvrda = new JPasswordField();

			dlg.add(new JLabel("Stara lozinka:"));
			dlg.add(pfStara);
			dlg.add(new JLabel("Nova lozinka:"));
			dlg.add(pfNova);
			dlg.add(new JLabel("Potvrdi novu:"));
			dlg.add(pfPotvrda);

			JButton btnSpremi = new JButton("Spremi");
			JButton btnOdustani = new JButton("Odustani");

			dlg.add(btnSpremi);
			dlg.add(btnOdustani);

			btnOdustani.addActionListener(ev -> dlg.dispose());

			btnSpremi.addActionListener(ev -> {
				String stara = new String(pfStara.getPassword()).trim();
				String nova = new String(pfNova.getPassword()).trim();
				String potvrda = new String(pfPotvrda.getPassword()).trim();

				if (stara.isEmpty() || nova.isEmpty() || potvrda.isEmpty()) {
					JOptionPane.showMessageDialog(dlg, "Sva polja moraju biti popunjena.");
					return;
				}

				if (!nova.equals(potvrda)) {
					JOptionPane.showMessageDialog(dlg, "Nova lozinka i potvrda se ne podudaraju.");
					return;
				}

				try (Connection conn = DB.connect()) {
					PreparedStatement provjera = conn.prepareStatement("SELECT * FROM korisnik WHERE id = ? AND lozinka = ?");
					provjera.setInt(1, id);
					provjera.setString(2, stara);
					ResultSet rs = provjera.executeQuery();

					if (!rs.next()) {
						JOptionPane.showMessageDialog(dlg, "Stara lozinka nije točna.");
						return;
					}

					PreparedStatement update = conn.prepareStatement("UPDATE korisnik SET lozinka = ? WHERE id = ?");
					update.setString(1, nova);
					update.setInt(2, id);
					update.execute();

					JOptionPane.showMessageDialog(dlg, "Lozinka uspješno promijenjena.");
					dlg.dispose();

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(dlg, "Greška prilikom promjene lozinke.");
				}
			});

			dlg.setVisible(true);
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
