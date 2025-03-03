import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel loginPanel;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");

        // Initialize loginPanel and its components
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfEmail = new JTextField();
        tfEmail.setPreferredSize(new Dimension(200, 30));
        pfPassword = new JPasswordField();
        pfPassword.setPreferredSize(new Dimension(200, 30));
        btnOK = new JButton("OK");
        btnOK.setPreferredSize(new Dimension(50, 30));
        btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(50, 30));

        // Add components to loginPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(tfEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(pfPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(btnOK, gbc);

        gbc.gridy = 3;
        loginPanel.add(btnCancel, gbc);

        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAuthenticatedUser(email, password);

                if (user != null) {
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Email or Password Invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    public User user;
    private User getAuthenticatedUser(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/MyStore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Connected to database successfully...

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.phone = resultSet.getString("phone");
                user.address = resultSet.getString("address");
                user.password = resultSet.getString("password");
            }

            stmt.close();
            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }


        return user;
    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
        if (user != null) {
            System.out.println("Successful Authentication of: " + user.name);
            System.out.println("          Email: " + user.email);
            System.out.println("          Phone: " + user.phone);
            System.out.println("          Address: " + user.address);
        }
        else {
            System.out.println("Authentication canceled");
        }
    }
}
