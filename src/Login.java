import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class Login extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnSignup;
    private final String USER_FILE = "users.txt";

    public Login() {
        setTitle("Login / Sign-up");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        txtUser = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        txtPass = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        // Buttons
        btnLogin = new JButton("Login");
        btnSignup = new JButton("Sign up");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(btnLogin, gbc);
        gbc.gridx = 1;
        panel.add(btnSignup, gbc);

        add(panel);

        // ----- Action Listeners -----
        btnLogin.addActionListener(e -> doLogin());
        btnSignup.addActionListener(e -> doSignup());
    }

    /** Login logic */
    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill both fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(Paths.get(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2 && parts[0].equals(user) && parts[1].equals(pass)) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    dispose();                // close login window
                    new StartWindow().setVisible(true);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Failed", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot read user file.\n" + ex.getMessage(),
                    "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Sign-up logic */
    private void doSignup() {
        String user = JOptionPane.showInputDialog(this, "New username:");
        if (user == null || user.trim().isEmpty()) return;

        String pass = JOptionPane.showInputDialog(this, "New password:");
        if (pass == null || pass.isEmpty()) return;

        user = user.trim();

        // ---- check if username already exists ----
        if (usernameExists(user)) {
            JOptionPane.showMessageDialog(this, "Username already taken.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ---- append to file ----
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(USER_FILE),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(user + ":" + pass);
            bw.newLine();
            JOptionPane.showMessageDialog(this, "Sign-up successful! You can now login.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to write user file.\n" + ex.getMessage(),
                    "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean usernameExists(String username) {
        if (!Files.exists(Paths.get(USER_FILE))) return false;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(USER_FILE))) {
            return br.lines().anyMatch(line -> line.startsWith(username + ":"));
        } catch (IOException e) {
            return false;
        }
    }

    // -------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}