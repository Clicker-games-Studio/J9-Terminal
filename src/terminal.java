import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class terminal extends JFrame {

    private JTextArea outputArea;
    private JTextField inputField;

    public terminal() {
        setTitle("J9 Terminal");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(outputArea);

        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));

        // Handle commands
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cmd = inputField.getText().trim();
                inputField.setText("");
                runCommand(cmd);
            }
        });

        add(scroll, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        print("=== J9 Terminal GUI ===");
        print("Type commands below. Type 'exit' to close.");
    }

    private void print(String msg) {
        outputArea.append(msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private void runCommand(String cmd) {
        print("J9> " + cmd);

        if (cmd.equalsIgnoreCase("exit")) {
            print("Closing terminal...");
            System.exit(0);
        }

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader stdOut = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            BufferedReader stdErr = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            // Output normal text
            String line;
            while ((line = stdOut.readLine()) != null) {
                print(line);
            }

            // Output errors
            while ((line = stdErr.readLine()) != null) {
                print("ERR: " + line);
            }

        } catch (Exception e) {
            print("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new terminal().setVisible(true);
        });
    }
}
