import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class PasswordGenerator extends JFrame {

    private final JCheckBox lowercase;
    private final JCheckBox uppercase;
    private final JCheckBox numbers;
    private final JCheckBox symbols;

    private final JSpinner lengthSpinner;
    private final JTextField passwordField;
    private final JLabel strengthLabel;
    private final JProgressBar strengthBar;

    private final JButton generateButton;

    private static final SecureRandom random = new SecureRandom();

    public PasswordGenerator() {

        setTitle("Password Generator");
        setSize(550, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        // Heading
        JLabel title = new JLabel("PASSWORD GENERATOR");
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Create a strong and secure password");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(20));

        // Password Length
        JPanel lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lengthLabel = new JLabel("Password Length:");

        lengthSpinner = new JSpinner(
            new SpinnerNumberModel(12, 4, 64, 1)
        );

        lengthSpinner.setPreferredSize(new Dimension(80, 30));

        lengthPanel.add(lengthLabel);
        lengthPanel.add(lengthSpinner);

        mainPanel.add(lengthPanel);

        // Character Selection
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        optionsPanel.setBorder(
            BorderFactory.createTitledBorder("Select Characters")
        );

        lowercase = new JCheckBox("Lowercase (a-z)", true);
        uppercase = new JCheckBox("Uppercase (A-Z)", true);
        numbers = new JCheckBox("Numbers (0-9)", true);
        symbols = new JCheckBox("Special Characters", true);

        optionsPanel.add(lowercase);
        optionsPanel.add(uppercase);
        optionsPanel.add(numbers);
        optionsPanel.add(symbols);

        mainPanel.add(optionsPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Generate Button
        generateButton = new JButton("Generate Password");

        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.setBackground(new Color(40, 100, 200));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);

        mainPanel.add(generateButton);
        mainPanel.add(Box.createVerticalStrut(20));

        // Password Output
        JLabel outputLabel = new JLabel("Generated Password:");

        passwordField = new JTextField();
        passwordField.setFont(new Font("Arial", Font.BOLD, 16));
        passwordField.setEditable(false);

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(passwordField, BorderLayout.CENTER);

        mainPanel.add(outputPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Strength Indicator
        strengthLabel = new JLabel("Password Strength: -");

        strengthLabel.setFont(
            new Font("Arial", Font.BOLD, 15)
        );

        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setString("Not Generated");

        JPanel strengthPanel = new JPanel(new BorderLayout(5, 5));

        strengthPanel.add(strengthLabel, BorderLayout.NORTH);
        strengthPanel.add(strengthBar, BorderLayout.CENTER);

        mainPanel.add(strengthPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Copy and Clear Buttons
        JButton copyButton = new JButton("Copy Password");
        JButton clearButton = new JButton("Clear");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(copyButton);
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel);

        add(mainPanel);

        // Event Listeners
        generateButton.addActionListener(e -> generatePassword());

        copyButton.addActionListener(e -> copyPassword());

        clearButton.addActionListener(e -> {
            passwordField.setText("");
            updateStrength();
        });

        lowercase.addActionListener(e -> updateStrength());
        uppercase.addActionListener(e -> updateStrength());
        numbers.addActionListener(e -> updateStrength());
        symbols.addActionListener(e -> updateStrength());

        lengthSpinner.addChangeListener(e -> updateStrength());

        updateStrength();
    }

    // Generate Random Password
    private void generatePassword() {

        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digit = "0123456789";
        String special = "!@#$%^&*()_+-=";

        StringBuilder characterPool = new StringBuilder();

        List<String> selectedSets = new ArrayList<>();

        if (lowercase.isSelected()) {
            characterPool.append(lower);
            selectedSets.add(lower);
        }

        if (uppercase.isSelected()) {
            characterPool.append(upper);
            selectedSets.add(upper);
        }

        if (numbers.isSelected()) {
            characterPool.append(digit);
            selectedSets.add(digit);
        }

        if (symbols.isSelected()) {
            characterPool.append(special);
            selectedSets.add(special);
        }

        if (characterPool.length() == 0) {

            JOptionPane.showMessageDialog(
                this,
                "Please select at least one character type!"
            );

            return;
        }

        int length = (Integer) lengthSpinner.getValue();

        List<Character> passwordCharacters = new ArrayList<>();

        // Include at least one character from each selected type
        for (String set : selectedSets) {

            passwordCharacters.add(
                set.charAt(random.nextInt(set.length()))
            );
        }

        // Fill remaining password characters
        while (passwordCharacters.size() < length) {

            int index = random.nextInt(characterPool.length());

            passwordCharacters.add(
                characterPool.charAt(index)
            );
        }

        // Shuffle password characters
        Collections.shuffle(passwordCharacters, random);

        StringBuilder password = new StringBuilder();

        for (char ch : passwordCharacters) {
            password.append(ch);
        }

        passwordField.setText(password.toString());

        updateStrength();
    }

    // Check Password Strength
    private void updateStrength() {

        int types = 0;

        if (lowercase.isSelected()) types++;
        if (uppercase.isSelected()) types++;
        if (numbers.isSelected()) types++;
        if (symbols.isSelected()) types++;

        int length = (Integer) lengthSpinner.getValue();

        String strength;
        int progress;
        Color color;

        if (types == 0 || length < 8 || types == 1) {

            strength = "Weak";
            progress = 25;
            color = Color.RED;

        }
        else if (length < 10 || types == 2) {

            strength = "Medium";
            progress = 50;
            color = Color.ORANGE;

        }
        else if (length < 12 || types == 3) {

            strength = "Strong";
            progress = 75;
            color = new Color(30, 140, 220);

        }
        else {

            strength = "Very Strong";
            progress = 100;
            color = new Color(0, 150, 70);
        }

        if (types == 0) {

            strengthLabel.setText(
                "Password Strength: Select characters"
            );

            strengthBar.setValue(0);
            strengthBar.setString("No characters selected");

        } else {

            strengthLabel.setText(
                "Password Strength: " + strength
            );

            strengthBar.setValue(progress);
            strengthBar.setString(strength);
        }

        strengthBar.setForeground(color);
    }

    // Copy Password
    private void copyPassword() {

        String password = passwordField.getText();

        if (password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please generate a password first!"
            );

            return;
        }

        StringSelection selection =
            new StringSelection(password);

        Toolkit.getDefaultToolkit()
            .getSystemClipboard()
            .setContents(selection, null);

        JOptionPane.showMessageDialog(
            this,
            "Password copied successfully!"
        );
    }

    // Main Method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            PasswordGenerator app = new PasswordGenerator();

            app.setVisible(true);

        });
    }
}