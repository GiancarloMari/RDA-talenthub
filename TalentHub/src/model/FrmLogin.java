package model;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FrmLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField tfEmail;
	private JPasswordField pfLozinka;
	private JLabel lblStatus;

	public FrmLogin() {
		setTitle("TalentHub – Prijava");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 350);
		setLocationRelativeTo(null);

		// Glavni panel s bojom
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(new Color(245, 245, 250)); // svijetlosiva plava
		setContentPane(mainPanel);

		// Naslov
		JLabel lblNaslov = new JLabel("Prijava u TalentHub");
		lblNaslov.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblNaslov.setForeground(new Color(40, 40, 80));
		lblNaslov.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(lblNaslov);

		mainPanel.add(Box.createVerticalStrut(25));

		// Email
		JPanel emailPanel = new JPanel(new BorderLayout());
		emailPanel.setBackground(mainPanel.getBackground());
		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tfEmail = new JTextField(20);
		emailPanel.add(lblEmail, BorderLayout.NORTH);
		emailPanel.add(tfEmail, BorderLayout.CENTER);
		mainPanel.add(emailPanel);

		mainPanel.add(Box.createVerticalStrut(10));

		// Lozinka
		JPanel passPanel = new JPanel(new BorderLayout());
		passPanel.setBackground(mainPanel.getBackground());
		JLabel lblLozinka = new JLabel("Lozinka:");
		lblLozinka.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pfLozinka = new JPasswordField(20);
		passPanel.add(lblLozinka, BorderLayout.NORTH);
		passPanel.add(pfLozinka, BorderLayout.CENTER);
		mainPanel.add(passPanel);

		mainPanel.add(Box.createVerticalStrut(20));

		// Gumb
		JButton btnPrijava = new JButton("Prijava");
		btnPrijava.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPrijava.setBackground(new Color(66, 133, 244)); // Google plava
		btnPrijava.setForeground(Color.WHITE);
		btnPrijava.setFocusPainted(false);
		btnPrijava.setFont(new Font("Segoe UI", Font.BOLD, 14));
		mainPanel.add(btnPrijava);

		mainPanel.add(Box.createVerticalStrut(15));

		// Status poruka
		lblStatus = new JLabel(" ");
		lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblStatus.setForeground(new Color(200, 50, 50));
		lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		mainPanel.add(lblStatus);

		// Akcija
		btnPrijava.addActionListener(e -> {
			String email = tfEmail.getText().trim();
			String lozinka = new String(pfLozinka.getPassword());

			try {
				Connection conn = DB.connect();
				PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM korisnik WHERE email = ? AND lozinka = ?");
				stmt.setString(1, email);
				stmt.setString(2, lozinka);
				ResultSet rs = stmt.executeQuery();

				if (rs.next()) {
					int id = rs.getInt("id");
					String ime = rs.getString("ime");
					String tip = rs.getString("tip");

					User korisnik = new User(id, ime, email, lozinka, tip);
					lblStatus.setText("Prijava uspješna.");
					dispose();

					if (tip.equals("admin")) {
						new FrmAdminPanel(korisnik).setVisible(true);
					} else {
						new MainTalentHubWindow(korisnik).setVisible(true);
					}
				} else {
					lblStatus.setText("Neispravan email ili lozinka.");
				}
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				lblStatus.setText("Greška u bazi.");
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
	}
}
