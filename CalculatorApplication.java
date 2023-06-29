package Calculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class CalculatorApplication extends JFrame implements ActionListener {
    private JTextField display;
    private JButton[] numberButtons;
    private JButton[] operatorButtons;
    private JButton equalsButton;
    private JButton clearButton;
    private JButton deleteButton;
    private JButton historyButton; // New button for displaying history

    private int num1, num2;
    private char operator;
    private String equation;
    private boolean resultUsed;
    private StringBuilder historyLog; // StringBuilder for storing history log

    public CalculatorApplication() {
        setTitle("Calculator");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(Color.black);
        displayPanel.setPreferredSize(new Dimension(300, 100));

        display = new JTextField();
        display.setBackground(Color.black);
        display.setForeground(Color.white);
        display.setFont(new Font("Arial", Font.PLAIN, 30));
        display.setPreferredSize(new Dimension(280, 60));

        // Remove the border around the display JTextField
        Border emptyBorder = BorderFactory.createEmptyBorder();
        display.setBorder(emptyBorder);

        displayPanel.add(display);
        add(displayPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(Color.BLACK);

        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setFont(new Font("Arial", Font.BOLD, 20));
            numberButtons[i].setBackground(Color.BLACK);
            numberButtons[i].setForeground(Color.WHITE);
            buttonPanel.add(numberButtons[i]);
        }

        operatorButtons = new JButton[4];
        operatorButtons[0] = new JButton("+");
        operatorButtons[1] = new JButton("-");
        operatorButtons[2] = new JButton("*");
        operatorButtons[3] = new JButton("/");
        for (int i = 0; i < 4; i++) {
            operatorButtons[i].addActionListener(this);
            operatorButtons[i].setFont(new Font("Arial", Font.BOLD, 20));
            operatorButtons[i].setBackground(Color.BLACK);
            operatorButtons[i].setForeground(Color.WHITE);
            buttonPanel.add(operatorButtons[i]);
        }

        equalsButton = new JButton("=");
        equalsButton.addActionListener(this);
        equalsButton.setFont(new Font("Arial", Font.BOLD, 20));
        equalsButton.setBackground(Color.GREEN);
        equalsButton.setForeground(Color.BLACK);
        buttonPanel.add(equalsButton);

        clearButton = new JButton("C");
        clearButton.addActionListener(this);
        clearButton.setFont(new Font("Arial", Font.BOLD, 20));
        clearButton.setBackground(Color.ORANGE);
        clearButton.setForeground(Color.BLACK);
        buttonPanel.add(clearButton);

        deleteButton = new JButton("Del");
        deleteButton.addActionListener(this);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 15));
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.BLACK);
        buttonPanel.add(deleteButton);

        historyButton = new JButton("H"); // New button for displaying history
        historyButton.addActionListener(this);
        historyButton.setFont(new Font("Arial", Font.BOLD, 15));
        historyButton.setBackground(Color.LIGHT_GRAY);
        historyButton.setForeground(Color.BLACK);
        buttonPanel.add(historyButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Initialize the history log
        historyLog = new StringBuilder();    

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String buttonText = source.getText();

        for (int i = 0; i < 10; i++) {
            if (buttonText.equals(String.valueOf(i))) {
                if (resultUsed) {
                    display.setText("");
                    resultUsed = false; // Reset resultUsed to false when a new number is entered
                }
                display.setText(display.getText() + buttonText);
                return;
            }
        }

        if (buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("*")|| buttonText.equals("/") ) {
            operator = buttonText.charAt(0);
            num1 = Integer.parseInt(display.getText());
            equation = display.getText() + buttonText;
            display.setText(equation);
            return;
        }

        if (buttonText.equals("=")) {
            num2 = Integer.parseInt(display.getText().substring(display.getText().indexOf(operator) + 1));
            int result = (int) calculateResult(num1, num2, operator);
        
            StringBuilder resultDisplay = new StringBuilder();
            if (equation.isEmpty()) {
                resultDisplay.append(num1).append(" ").append(operator).append(" ").append(num2).append(" =\n");
            } else {
                resultDisplay.append(equation).append(num2).append(" = \n");
            }
            resultDisplay.append(result);
            display.setText(resultDisplay.toString());
        
            // Append the current calculation and result to the history log
            historyLog.append(resultDisplay).append("\n");
        
            num1 = result;
            resultUsed = true;
            equation = ""; // Reset the equation
            return;
        }
        

        if (buttonText.equals("Del")) {
            String currentText = display.getText();
            if (currentText.length() > 0) {
                // Remove the last character from the display
                display.setText(currentText.substring(0, currentText.length() - 1));
            }
            return;
        }

        if (buttonText.equals("C")) {
            display.setText("");
            equation = "";
            resultUsed = false;
        }

        if (buttonText.equals("H")) {
            // Show the history log in a separate dialog
            showHistoryLogDialog();
        }

        if (buttonText.equals("/") && num2 == 0) {
            display.setText("Error: Division by zero");
            return;
        }
        
    }

    private double calculateResult(int num1, int num2, char operator) {
        switch (operator) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                return (double) num1 / num2;
            default:
                return 0;
        }
    }

    private void showHistoryLogDialog() {
        // Create a JTextArea to display the history log
        JTextArea historyTextArea = new JTextArea(historyLog.toString());
        historyTextArea.setEditable(false);
        historyTextArea.setFont(new Font("Arial", Font.PLAIN, 14));

        // Create a JScrollPane to enable scrolling if the history log exceeds the visible area
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // Create a dialog to display the history log
        JDialog historyDialog = new JDialog(this, "History Log");
        historyDialog.setResizable(false);
        historyDialog.getContentPane().add(scrollPane);
        historyDialog.pack();
        historyDialog.setLocationRelativeTo(this);
        historyDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CalculatorApplication();
            }
        });
    }
}