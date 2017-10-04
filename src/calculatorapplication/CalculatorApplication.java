/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculatorapplication;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.lang.StringBuilder;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author antti
 */
public class CalculatorApplication extends Application {
    
    private Calculator calc;
    private List<String> earlierResults;

    public CalculatorApplication() {
        this.calc = new Calculator();
        this.earlierResults = new ArrayList<>();
        this.earlierResults.add("No earlier undo history"); 
        
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        BorderPane root = new BorderPane();
        
        // CREATE SYMBOLS USED IN THE CALCULATOR BUTTONS
        List<String> symbols = new ArrayList<>();
        symbols.add("7");
        symbols.add("8");
        symbols.add("9");
        symbols.add("÷");
        symbols.add("Undo");
        symbols.add("Erase");
        symbols.add("4");
        symbols.add("5");
        symbols.add("6");
        symbols.add("×");
        symbols.add("(");
        symbols.add(")");
        symbols.add("1");
        symbols.add("2");
        symbols.add("3");
        symbols.add("-");
        symbols.add("^");
        symbols.add("√");
        symbols.add("0");
        symbols.add(".");
        symbols.add("%");
        symbols.add("+");
        symbols.add("=");
        symbols.add("=");
        
        
        // CREATE A GRID OF BUTTONS AND ASSIGN SYMBOLS TO THEM
        GridPane buttonGrid = new GridPane();
        
        int i = 0;
        
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                if (y == 3 && x == 4) {
                    Button equalsSign = new Button("=");
                    equalsSign.setMinSize(60, 40);
                    equalsSign.setId("=");
                    buttonGrid.add(equalsSign, 4, 3);
                    break;
                } else {
                    Button btn = new Button(symbols.get(i));
                    btn.setMinSize(60, 40);
                    btn.setId(btn.getText());
                    buttonGrid.add(btn, x, y);
                    i++;
                }
            }
        }
        
        buttonGrid.setPadding(new Insets(10, 10, 10, 10));
        buttonGrid.setHgap(5);
        buttonGrid.setVgap(5);
        
        // ADD TEXTFIELD FOR EXPRESSIONS AND RESULTS
        TextField resultField = new TextField("");
        resultField.setFont(Font.font("Monospaced", 40));
        
        
        // ADD BUTTONGRID AND TEXTFIELD TO ROOT BORDERPANE
        root.setTop(resultField);
        root.setBottom(buttonGrid);
        root.setPadding(new Insets(10, 10, 10, 10));

        
        // ADD EVENTLISTENER FOR EVALUATING RESULT ON PRESSING ENTER
        root.setOnKeyReleased((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                buttonGrid.getChildren().forEach(button -> {
                    if (button.getId().equals("=")) {
                        Event.fireEvent(button, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
                        1, true, true, true, true, true, true, true, true, true, true, null));
                    }
                });
                resultField.positionCaret(resultField.getText().length());
            }
        });

        
        // ADD EVENTLISTENERS TO BUTTONS ON THE CALCULATOR
        
        buttonGrid.getChildren().forEach((button) -> {
            button.setOnMouseClicked((event) -> {
                if (button.getId().equals("=")) {
                    
                    String input = resultField.getText();
                    
                    if (!input.matches("(([0-9]*)|([\\.\\-/÷×\\*%√\\^\\(\\)])?|(\\+)?|(x)?)*")) {
                        resultField.setFont(Font.font("Monospaced", 20));
                        resultField.setText("Incorrect operation");
                        return;
                    }
                    
                    if (input.length() > 20) {
                        resultField.setFont(Font.font("Monospaced", 20));
                        resultField.setText("Too long expression");
                        return;
                    }
                    
                    String[] expression = input.split("");
                    StringBuilder sb1 = new StringBuilder();
                    
                    for (int j = 0; j < expression.length; j++) {
                        
                        
                        // REFACTOR THIS TO ONE LINE OF REGEX
                        if (expression[j].matches("[0-9]")) {
                            sb1.append(expression[j]);
                        } else if (expression[j].matches("\\.")) {
                            sb1.append(".");
                        } else if (expression[j].matches("x") 
                                || expression[j].matches("×")
                                || expression[j].matches("\\*")) {
                            sb1.append("*");
                        } else if (expression[j].matches("÷") ||
                                expression[j].matches("/")) {
                            sb1.append(".0");
                            sb1.append("/");
                        } else if (expression[j].matches("\\+")) {
                            sb1.append("+");
                        } else if (expression[j].matches("-")) {
                            sb1.append("-");
                        } else if (expression[j].matches("√")){
                            sb1.append("√");
                        } else if (expression[j].matches("\\^")) {
                            sb1.append("^");
                        } else if (expression[j].matches("\\(")) {
                            sb1.append("(");
                        } else if (expression[j].matches("\\)")) {
                            sb1.append(")");
                        } else if (expression[j].matches("%")) {
                            sb1.append("%");
                        } else {
                            System.out.println("Input contains invalid symbols!");
                        }
                    }
                    
                    String toEvaluate = sb1.toString();
                    String result = this.calc.evaluate(toEvaluate);
                    
                    this.earlierResults.add(resultField.getText());
                    
                    resultField.setFont(Font.font("Monospaced", 40));
                    if (result.equals("Division by zero is undefined")) {
                        resultField.setFont(Font.font("Monospaced", 20));
                    }
                    resultField.setText("" + result);
                    
                } else if (button.getId().equals("Erase")) {
                    this.earlierResults.add(resultField.getText());
                    resultField.clear();
                    
                } else if (button.getId().equals("Undo")) {
                    if (this.earlierResults.get(this.earlierResults.size() - 1).equals("No earlier undo history")) {
                        resultField.setFont(Font.font("Monospaced", 20));
                    }
                    
                    resultField.setText(this.earlierResults.get(this.earlierResults.size() - 1));
                    
                    if (this.earlierResults.size() > 1) {
                        this.earlierResults.remove(this.earlierResults.size() - 1);
                    }
                    
                } else {
                    
                    if (resultField.getText().equals("No earlier undo history")) {
                        resultField.clear();
                    }
                    if (resultField.getText().equals("Division by zero is undefined")) {
                        resultField.clear();
                    }
                    
                    resultField.setFont(Font.font("Monospaced", 40));
                    this.earlierResults.add(resultField.getText());
                    
                    resultField.setText(resultField.getText() + button.getId());
                }
            });
        });
        
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(CalculatorApplication.class);
    }

    
    
}
