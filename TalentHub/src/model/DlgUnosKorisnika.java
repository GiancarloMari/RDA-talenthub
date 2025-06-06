package model;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class DlgUnosKorisnika extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField tfIme;
	private JTextField tfEmail;
	private JPasswordField pfLozinka;
	private JComboBox<String> cbTip;

	public static void main(String[] args) {
		try {
			DlgUnosKorisnika dialog = new DlgUnosKorisnika();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DlgUnosKorisnika() {
		setTitle("Unos novog korisnika");
		setBounds(100, 100, 400, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblIme = new JLabel("Ime:");
		lblIme.setBounds(30, 30, 80, 20);
		contentPanel.add(lblIme);

		tfIme = new JTextField();
		tfIme.setBounds(120, 30, 200, 20);
		contentPanel.add(tfIme);
		tfIme.setColumns(10);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(30, 70, 80, 20);
		contentPanel.add(lblEmail);

		tfEmail = new JTextField();
		tfEmail.setBounds(120, 70, 200, 20);
		contentPanel.add(tfEmail);
		tfEmail.setColumns(10);

		JLabel lblLozinka = new JLabel("Lozinka:");
		lblLozinka.setBounds(30, 110, 80, 20);
		contentPanel.add(lblLozinka);

		pfLozinka = new JPasswordField();
		pfLozinka.setBounds(120, 110, 200, 20);
		contentPanel.add(pfLozinka);

		JLabel lblTip = new JLabel("Tip korisnika:");
		lblTip.setBounds(30, 150, 100, 20);
		contentPanel.add(lblTip);

		cbTip = new JComboBox<>();
		cbTip.addItem("freelancer");
		cbTip.addItem("poslodavac");
		cbTip.setBounds(140, 150, 180, 22);
		contentPanel.add(cbTip);

		JButton btnSpremi = new JButton("Spremi");
		btnSpremi.setBounds(140, 200, 100, 25);
		contentPanel.add(btnSpremi);

		// AKCIJA na gumb
		btnSpremi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ime = tfIme.getText();
				String email = tfEmail.getText();
				String lozinka = new String(pfLozinka.getPassword());
				String tip = cbTip.getSelectedItem().toString();

				try {
					Connection conn = DB.connect();
					String sql = "INSERT INTO korisnik (ime, email, lozinka, tip) VALUES (?, ?, ?, ?)";
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setString(1, ime);
					stmt.setString(2, email);
					stmt.setString(3, lozinka);
					stmt.setString(4, tip);
					stmt.execute();

					conn.close();
					JOptionPane.showMessageDialog(null, "Korisnik uspješno unesen!");
					dispose();

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Greška pri unosu u bazu.");
				}
			}
		});
	}
}
