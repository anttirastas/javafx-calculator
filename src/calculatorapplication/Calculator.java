/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculatorapplication;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author antti
 */
public class Calculator {

    public String evaluate(String input) {
        
        // CORRECT ORDER:
        // 1. Parentheses
        // 2. Power & squareroot
        // 3. Percentage
        // 4. Multiply & divide
        // 5. Add & subtract
        
       
        // If there are multiple "+" or "-" signs in a row, delete the extras
        // and leave only one ("+" or "-") sign 
        input = eatExtraPlusesAndMinuses(input);
        
        
        // There are separate functions to process 1) parentheses,
        // 2) power & square root, 3) percentages, 4) multiplication & division
        // and finally 5) addition & subtraction.
        
        // The logic is that no function can process the input if it contains 
        // any symbols or signs that should have already been processed by an
        // earlier (more advanced) function. I.e. multiplyDivide-function cannot
        // process input that contains symbols like "(", ")", "^", "√" or "%".

        if (input.contains("(") || input.contains(")")) {
            input = doParentheses(input);
        } else if (input.contains("^") || input.contains("√")) {
            input = doPowerAndSquareRoot(input);
        } else if (input.contains("%")) {
            input = doPercentages(input);
        } else if (input.contains("*") || input.contains("/")) {
            input = multiplyDivide(input);
        } else if (input.contains("+") || input.contains("-")) {
            input = doAdditionSubtraction(input);
        } else {
            return input;
        }
        
        String output = input;
        
        if (output.equals("Division by zero is undefined")) {
            return input;
        }
        
        if (output.equals("Square root of a negative number is undefined within"
                + " real numbers")) {
            return input;
        }
        
        if (output.equals("Malformed expression")) {
            return input;
        }
        
        
        
        Double resultInDouble = Double.parseDouble(output);
        
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        
        return df.format(resultInDouble);
    }
    
    public String doParentheses(String input) {
        String result = "";
        String resultInsideParenthesis = "";
        
        // 1. Find opening parenthesis, and mark its index to a variable
        // 2. Go forward
        // 3. If you find closing parenthesis, mark its index to a variable
        // 4. If you have found opening and closing parenthesis, calculate the
        //    value of its content, and replace the parenthesis-limited 
        //    substring with the result
        // 5. If you find another opening parenthesis before finding closing
        //    parenthesis, mark this new index to the openingParenthesis
        //    variable.
        
        while (input.contains("(") || input.contains(")")) {
            
            int openParenthesisIndex = -1;
            int closeParenthesisIndex = -1;

            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == '(') {
                    openParenthesisIndex = i;
                }
                if (input.charAt(i) == ')') {
                    closeParenthesisIndex = i;
                }
                if (openParenthesisIndex > -1 && closeParenthesisIndex > -1) {
                    break;
                }
            }

            String toCalculate = input.substring(openParenthesisIndex + 1, 
                    closeParenthesisIndex);
            
            resultInsideParenthesis = doParentheses(toCalculate);
            
            StringBuilder sb2 = new StringBuilder();
            
            sb2.append(input.substring(0, openParenthesisIndex));
            
            // If there's a (number) multiplier right before the parenthesis, 
            // and it doesn't have a * (or x) after it, add the * sign:
            if (openParenthesisIndex > 0 && input.substring(openParenthesisIndex
                    - 1, openParenthesisIndex).matches("[0-9]")) {
                sb2.append("*");
            }
            
            sb2.append(resultInsideParenthesis);
            
            // If there's a multiplier right after the parenthesis, and it 
            // doesn't have a * (or x) in front of it, add the * sign:
            if (input.length() > closeParenthesisIndex + 1 &&
                    input.substring(closeParenthesisIndex + 1,
                            closeParenthesisIndex + 2).matches("[0-9]")) {
                sb2.append("*");
            }
            
            sb2.append(input.substring(closeParenthesisIndex + 1,
                    input.length()));
            input = sb2.toString();
        }
        
        return evaluate(input);
    }

    
    public String doPowerAndSquareRoot(String input) {
        
        while (input.contains("√") || input.contains("^")) {
            String[] parts = input.split("(?=[/*\\+-])|(?=%)|(?=[\\^√])|(?<="
                    + "[/*\\+-])|(?<=[\\^√])|(?<=%)");
            
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("√") || parts[i].equals("^")) {

                    String operand = parts[i];
                    double prevValue = 0;
                    double nextValue = 0;
                    if (i > 0) {
                        // 2%^2
                        // causes numberformatexception "%"
                        if (parts[i-1].equals("%")) {
                            parts[i-2] = "" + (Double.parseDouble(parts[i-2])
                                    / 100);
                            parts = shrinkArrayByOne(parts, i-1);
                            i -= 2;
                            continue;
                        }
                                
                        prevValue = Double.parseDouble(parts[i-1]);
                    }
                    
                    if (parts[i+1].equals("-")) {
                        double modifiedValue = 0 
                                - Double.parseDouble(parts[i+2]);
                        parts[i+1] = "" + modifiedValue;
                        parts = shrinkArrayByOne(parts, i+2);
                    }
                    
                    if (parts[i+1].equals("+")) {
                        double modifiedValue = Double.parseDouble(parts[i+2]);
                        parts[i+1] = "" + modifiedValue;
                        parts = shrinkArrayByOne(parts, i+2);
                    }
                    
                    nextValue = Double.parseDouble(parts[i+1]);

                    switch (operand) {
                        case "√":
                            if (nextValue < 0) {
                                return "Square root of a negative number is "
                                        + "undefined within real numbers";
                            }
                            if (i < parts.length - 3) {
                                if (parts[i+2].equals("^")
                                        && parts[i+3].equals("2")) {
                                    parts[i] = parts[i+1];
                                    parts = shrinkArrayByThree(parts, i+1);
                                    break;
                                }
                            }
                            double squareRoot = Math.sqrt(nextValue);
                            parts[i] = "" + squareRoot;
                            parts = shrinkArrayByOne(parts, i+1);
                            break;

                        case "^":
                            double power = Math.pow(prevValue, nextValue);
                            DecimalFormat df = new DecimalFormat("0", 
                                    DecimalFormatSymbols
                                            .getInstance(Locale.ENGLISH));
                            //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
                            df.setMaximumFractionDigits(340); 
                            parts[i-1] = df.format(power);
                            parts = shrinkArrayByTwo(parts, i);
                            break;
                    }
                    i--;
                }
            }
            
            input = buildStringFromPartsArray(parts);
        }
        
        return evaluate(input);
    }
    
    public String doPercentages(String input) {
        
        while (input.contains("%")) {
            
            String[] parts = input.split("(?=[/*\\+-])|(?=%)|(?<=[/*\\+-])|"
                    + "(?<=%)");
            
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("%")) {
                    
                    if (i == 0 || !parts[i-1].matches("(([0-9])*(\\.)?"
                            + "([0-9])*)")) {

                        return "Malformed expression";
                        
                    } else if (i < 2) {
                        
                        if (!parts[i-1].matches("(([0-9])*(\\.)?([0-9])*)")) {
                            
                            return "Malformed expression 2";
                            
                        } else {
                            
                            double prevValue = Double.parseDouble(parts[i-1]);
                            double newValue = prevValue / 100;
                            parts[i-1] = "" + newValue;
                            parts = shrinkArrayByOne(parts, i);
                            i--;
                        }
                        
                        
                    } else if (i > 2 && parts[i-2].matches("[*/]")) {

                        String operand = parts[i-2];

                        double prevValue = Double.parseDouble(parts[i-3]);
                        double nextValue = Double.parseDouble(parts[i-1]);
                        double newValue = 0.0;

                        switch (operand) {
                            case "*":
                                newValue = prevValue * (nextValue / 100);
                                parts[i-3] = "" + newValue;
                                parts = shrinkArrayByThree(parts, i-2);
                                break;
                            case "/":
                                newValue = prevValue / (nextValue / 100);
                                parts[i-3] = "" + newValue;
                                parts = shrinkArrayByThree(parts, i-2);
                                break;
                        }
                        i -= 3;

                    } else if (i > 2 && parts[i-2].matches("[\\+-]")) {

                        String operand = parts[i-2];

                        double prevValue = Double.parseDouble(parts[i-3]);
                        double nextValue = Double.parseDouble(parts[i-1]);
                        double newValue = 0.0;

                        switch (operand) {
                            case "+":
                                newValue = prevValue + (prevValue / 100 * 
                                        nextValue);
                                parts[i-3] = "" + newValue;
                                parts = shrinkArrayByThree(parts, i-2);
                                break;
                            case "-":
                                newValue = prevValue - (prevValue / 100 * 
                                        nextValue);
                                parts[i-3] = "" + newValue;
                                parts = shrinkArrayByThree(parts, i-2);
                                break;
                        }
                        
                        i -= 3;
                        
                    } else {
                        return "Malformed expression 3";
                    }
                }
            }

            input = buildStringFromPartsArray(parts);
        }
        
        return evaluate(input);
    }
    
    public String multiplyDivide(String input) {
        
        while (input.contains("*") || input.contains("/")) {
            String[] parts = input.split("(?=[/*\\+-])|(?<=[/*\\+-])");
            
            parts = mergeLeadingMinusOrPlusToFirstValue(parts);
            
            
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("*") || parts[i].equals("/")) {

                    if (i == 0) {
                        return "Malformed expression: * or / operator cannot be"
                                + "the first symbol";
                    }
                    
                    if (parts[i-1].equals("-") || parts[i-1].equals("+")) {
                        return "Malformed expression: Cannot have - or + in"
                                + "front of * or /";
                    }
                    
                    String operand = parts[i];
                    
                    double prevValue = Double.parseDouble(parts[i-1]);
                    
                    // NEXT LINE CAUSES ERROR "INPUT STRING '-'"
                    if (parts[i+1].equals("-")) {
                        double modifiedValue = 0 
                                - Double.parseDouble(parts[i+2]);
                        parts[i+1] = "" + modifiedValue;
                        parts = shrinkArrayByOne(parts, i+2);
                    }
                    
                    if (parts[i+1].equals("+")) {
                        double modifiedValue = Double.parseDouble(parts[i+2]);
                        parts[i+1] = "" + modifiedValue;
                        parts = shrinkArrayByOne(parts, i+2);
                    }
                    
                    double nextValue = Double.parseDouble(parts[i+1]);
                    
                    switch (operand) {
                        case "*":
                            double product = prevValue * nextValue;
                            parts[i-1] = "" + product;
                            parts = shrinkArrayByTwo(parts, i);
                            break;
                            
                        case "/":
                            if (nextValue == 0) {
                                return "Division by zero is undefined";
                            }

                            double quotient = prevValue / nextValue;
                            parts[i-1] = "" + quotient;
                            parts = shrinkArrayByTwo(parts, i);
                            break;
                    }
                    
                    i--;
                }
            }

            input = buildStringFromPartsArray(parts);
            
        }
        
        if (input.contains("+") || input.contains("-")) {
            return doAdditionSubtraction(input);
        } else {
            return input;
        }
    }
    
    public String doAdditionSubtraction(String input) {
        
        while (input.contains("+") || input.substring(1, input.length())
                .contains("-")) {
            String[] parts = input.split("(?=[\\+-])|(?<=[\\+-])");
            
            parts = mergeLeadingMinusOrPlusToFirstValue(parts);
            
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("+") || parts[i].equals("-")) {

                    String operand = parts[i];
                    
                    double prevValue = 0;
                    
                    if (i > 0) {
                        prevValue = Double.parseDouble(parts[i-1]);
                    }
                    
                    double nextValue = Double.parseDouble(parts[i+1]);

                    switch (operand) {
                        case "+":
                            double sum = prevValue + nextValue;
                            if (i == 0) {
                                parts[0] = "" + sum;
                                parts = shrinkArrayByOne(parts, i+1);
                            } else {
                                parts[i-1] = "" + sum;
                                parts = shrinkArrayByTwo(parts, i);
                            }
                            break;
                            
                        case "-":
                            double difference = prevValue - nextValue;
                            if (i == 0) {
                                parts[0] = "" + difference;
                                parts = shrinkArrayByOne(parts, i+1);
                            } else {
                                parts[i-1] = "" + difference;
                                parts = shrinkArrayByTwo(parts, i);
                            }
                            break;
                    }
                    
                    i--;
                }
            }
            
            input = buildStringFromPartsArray(parts);
        }
        
        return input;
    }
    
    public String[] shrinkArrayByThree(String[] array, int index) {
        return shrinkArrayByInteger(array, 3, index);
    }
    
    public String[] shrinkArrayByTwo(String[] array, int index) {
        return shrinkArrayByInteger(array, 2, index);
    }
    
    public String[] shrinkArrayByOne(String[] array, int index) {
        return shrinkArrayByInteger(array, 1, index);
    }
    
    public String[] shrinkArrayByInteger(String[] array, int howManyToCut,
            int index) {
        for (int i = index; i < array.length - howManyToCut; i++) {
            array[i] = array[i+howManyToCut];
        }
        
        String[] newArray = new String[array.length-howManyToCut];
        System.arraycopy(array, 0, newArray, 0, newArray.length);
        
        return newArray;
    }
    
    public String eatExtraPlusesAndMinuses(String input) {
        String[] parts = input.split("");
        
        for (int i = 0; i < parts.length; i++) {
            if ((parts[i].equals("-") || parts[i].equals("+")) &&
                    ((parts[i+1].equals("-") || parts[i+1].equals("+")))) {
                if (parts[i].equals("-") && parts[i+1].equals("-")) {
                    parts[i] = "+";
                    parts = shrinkArrayByOne(parts, i+1);
                } else if (parts[i].equals("-") && parts[i+1].equals("+")) {
                    parts[i] = "-";
                    parts = shrinkArrayByOne(parts, i+1);
                } else if (parts[i].equals("+") && parts[i+1].equals("-")) {
                    parts[i] = "-";
                    parts = shrinkArrayByOne(parts, i+1);
                } else {
                    parts[i] = "+";
                    parts = shrinkArrayByOne(parts, i+1);
                }
            }
        }
        
        input = buildStringFromPartsArray(parts);
        
        return input;
    }
    
    public String buildStringFromPartsArray(String[] parts) {
        StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                sb.append(parts[i]);
            }
            
            return sb.toString();
    }
    
    public String[] mergeLeadingMinusOrPlusToFirstValue(String[] parts) {
        
        if (parts[0].equals("-")) {
            double modifiedValue = 0 - Double.parseDouble(parts[1]);
            parts[0] = "" + modifiedValue;
            parts = shrinkArrayByOne(parts, 1);
        }
        if (parts[0].equals("+")) {
            double modifiedValue = Double.parseDouble(parts[1]);
            parts[0] = "" + modifiedValue;
            parts = shrinkArrayByOne(parts, 1);
        }
        
        return parts;
    }
}
