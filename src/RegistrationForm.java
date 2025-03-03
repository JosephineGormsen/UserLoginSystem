import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");

        // Initialize registerPanel and its components
        registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfName = new JTextField();
        tfEmail = new JTextField();
        tfPhone = new JTextField();
        tfAddress = new JTextField();
        pfPassword = new JPasswordField();
        pfConfirmPassword = new JPasswordField();
        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");

        // Add components to registerPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        registerPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        registerPanel.add(tfName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        registerPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerPanel.add(tfEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        registerPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        registerPanel.add(tfPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        registerPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        registerPanel.add(tfAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        registerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 9;
        registerPanel.add(pfPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        registerPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 11;
        registerPanel.add(pfConfirmPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        registerPanel.add(btnRegister, gbc);

        gbc.gridy = 13;
        registerPanel.add(btnCancel, gbc);

        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
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

    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Confirm Password does not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserToDatabase(name, email, phone, address, password);
        if (user != null) {
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register new user",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public User user;
    private User addUserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/MyStore?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Connected to database successfully...

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name, email, phone, address, password) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            //Insert row into the table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }

            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return user;
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if (user != null) {
            System.out.println("Successful registration of: " + user.name);
        }
        else {
            System.out.println("Registration canceled");
        }
    }
}
