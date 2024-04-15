import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    public static void main(String[] args) {
        JFrame window = new JFrame("App");
        window.setSize(400, 400);
        // panel
        App app = new App();
        ShowEquationType showEquationType = app.new ShowEquationType();
        window.add(showEquationType);

        ShowEquation showEquation = app.new ShowEquation();
        showEquation.setEquation(showEquationType.equationType);
        showEquation.equationLabel.setText(showEquation.equation);
        window.add(showEquation);

        ShowNotification showNotification = app.new ShowNotification();
        window.add(showNotification);

        ShowButtons showButtons = app.new ShowButtons();
        showButtons.buttonActions(showEquationType, showEquation, showNotification, showButtons);
        window.add(showButtons);
        // update
        window.getContentPane().setLayout(new GridLayout(4, 1));
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    // show equation label
    public class ShowEquationType extends JPanel {
        private String equationType;
        private JLabel label;

        public ShowEquationType() {
            label = new JLabel("");
            equationType = getEquationType();
            label.setText(equationType);
            add(label);
        }

        public String getEquationType() {
            Random rand = new Random();
            java.util.List<String> givenList = Arrays.asList("Addition", "Subtraction", "Multiplication", "Division");
            int randomIndex = rand.nextInt(givenList.size());
            equationType = givenList.get(randomIndex);
            return equationType;
        }
    }

    // show equation
    public class ShowEquation extends JPanel {
        private String equation;
        private JLabel equationLabel;
        private JTextField texbox;

        public ShowEquation() {
            equationLabel = new JLabel("");
            add(equationLabel);
            texbox = new JTextField(10);
            add(texbox);
        }

        public Number[] createEquation() {
            Number[] numberList = new Number[2];
            Random rand = new Random();
            if (rand.nextInt(2) == 1) {
                double numberA = Math.round((rand.nextDouble() + rand.nextInt(-100, 100)) * 100.0) / 100.0;
                double numberB = Math.round((rand.nextDouble() + rand.nextInt(-100, 100)) * 100.0) / 100.0;
                numberList[0] = numberA;
                numberList[1] = numberB;
            } else {
                float numberA = rand.nextFloat() * 200 - 100;
                float numberB = rand.nextFloat() * 200 - 100;
                numberList[0] = numberA;
                numberList[1] = numberB;
            }
            return numberList;
        }

        public String setEquation(String equationType) {
            Number[] numbers = createEquation();
            if (equationType.equalsIgnoreCase("Addition")) {
                equation = numbers[0] + " + " + numbers[1] + " = ";
            } else if (equationType.equalsIgnoreCase("Subtraction")) {
                equation = numbers[0] + " - " + numbers[1] + " = ";
            } else if (equationType.equalsIgnoreCase("Multiplication")) {
                equation = numbers[0] + " * " + numbers[1] + " = ";
            } else if (equationType.equalsIgnoreCase("Division")) {
                equation = numbers[0] + " / " + numbers[1] + " = ";
            }
            return equation;
        }
    }

    // show buttons
    public class ShowButtons extends JPanel {
        private JButton submit;
        private JButton skip;
        private AtomicBoolean condition;

        public ShowButtons() {
            skip = new JButton("Skip");
            add(skip);
            submit = new JButton("Submit");
            add(submit);
        }

        public Boolean buttonActions(ShowEquationType showEquationType, ShowEquation showEquation,
                ShowNotification showNotification, ShowButtons showButtons) {
            condition = new AtomicBoolean(false); // Use AtomicBoolean
            submit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = showEquation.texbox.getText();
                    if (!text.isEmpty()) {
                        double num = Double.parseDouble(text);
                        String str1 = Double.toString(showNotification.evaluateEquation(showEquation.equation));
                        double result = showNotification.evaluateEquation(showEquation.equation);
                        showNotification.notificationLabel.setText(str1);
                        showNotification.compare(num, showNotification.evaluateEquation(showEquation.equation));
                        boolean value = showNotification.areSimilar(num, result, 0.001);
                        if (value) {
                            resetComponents(showEquationType, showEquation, showNotification, showButtons);
                        }
                    }
                }
            });
            skip.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resetComponents(showEquationType, showEquation, showNotification, showButtons);
                }
            });
            return condition.get();
        }
    }

    // notification item
    public class ShowNotification extends JPanel {
        private JLabel notificationLabel;
        private java.util.List<Map.Entry<Double, Boolean>> dataList;
        private JLabel notificationLabel_1;

        public ShowNotification() {
            notificationLabel = new JLabel("");
            add(notificationLabel);

            notificationLabel_1 = new JLabel("");
            add(notificationLabel_1);
        }

        public double evaluateEquation(String equation) {
            equation = equation.trim();
            String[] parts = equation.split(" ");
            // Extract the numbers and operator
            double numberA = Double.parseDouble(parts[0]);
            String operator = parts[1];
            double numberB = Double.parseDouble(parts[2]);
            // Evaluate the expression based on the operator
            double result = 0.0;
            switch (operator) {
                case "+":
                    result = numberA + numberB;
                    break;
                case "-":
                    result = numberA - numberB;
                    break;
                case "*":
                    result = numberA * numberB;
                    break;
                case "/":
                    // Check for division by zero
                    if (numberB != 0) {
                        result = numberA / numberB;
                    } else {
                        System.out.println("Error: Division by zero");
                    }
                    break;
                default:
                    System.out.println("Error: Invalid operator");
            }
            return Math.round(result);
        }

        public static boolean areSimilar(double num1, double num2, double threshold) {
            double difference = Math.abs(num1 - num2);
            return difference <= threshold;
        }

        public void compare(double num1, double num2) {
            dataList = new ArrayList<>();
            dataList.add(Map.entry(num1, areSimilar(num1, num2, 0.001)));
            notificationLabel_1.setText(dataList.toString());
        }
    }

    // reset
    public void resetComponents(ShowEquationType showEquationType, ShowEquation showEquation,
            ShowNotification showNotification, ShowButtons showButtons) {
        // Reset ShowEquationType
        String item = showEquationType.getEquationType();
        showEquationType.label.setText(item);
        showEquationType.revalidate();
        showEquationType.repaint();

        // Reset ShowEquation
        showEquation.setEquation(item);
        showEquation.equationLabel.setText(showEquation.equation);
        showEquation.revalidate();
        showEquation.repaint();

        // Reset ShowNotification
        showNotification.notificationLabel.setText("");
        showNotification.notificationLabel_1.setText("");
        showNotification.revalidate();
        showNotification.repaint();

        // Reset ShowButtons
        showButtons.revalidate();
        showButtons.repaint();
    }
}