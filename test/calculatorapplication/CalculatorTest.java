/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculatorapplication;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author antti
 */
public class CalculatorTest {
    
    private Calculator calculator;
    
    
    
    public CalculatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.calculator = new Calculator();
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void addsSubtractsCorrectly() {
        assertEquals("2", this.calculator.evaluate("1+1"));
        assertEquals("0", this.calculator.evaluate("1+-1"));
        assertEquals("-32", this.calculator.evaluate("-31-1"));
        assertEquals("-1", this.calculator.evaluate("2-+3"));
    }
    
    @Test
    public void multipliesCorrectly() {
        assertEquals("48", this.calculator.evaluate("2*8*3"));
        assertEquals("9", this.calculator.evaluate("-3*-3"));
        assertEquals("-9", this.calculator.evaluate("-3*3"));
    }
    
    @Test
    public void dividesCorrectly() {
        assertEquals("8", this.calculator.evaluate("48/2/3"));
        assertEquals("-8", this.calculator.evaluate("48/2/-3"));
        assertEquals("3", this.calculator.evaluate("-27/-9"));
    }
    
    @Test
    public void doesMixedOperationsCorrectly() {
        assertEquals("49", this.calculator.evaluate("6*7+3*4/6+5"));
        assertEquals("19159", this.calculator.evaluate("456*6*7+3*4/6+5"));
    }
    
    @Test
    public void doesPowerAndSquareRootCorrectly() {
        assertEquals("9", this.calculator.evaluate("3^2"));
        assertEquals("41", this.calculator.evaluate("3^2*4+5"));
        assertEquals("6", this.calculator.evaluate("3^2/9+5"));
        assertEquals("3", this.calculator.evaluate("√9"));
        assertEquals("5", this.calculator.evaluate("√25"));
        assertEquals("12", this.calculator.evaluate("√144"));
    }
    
    @Test
    public void doesParenthesesCorrectly() {
        assertEquals("10", this.calculator.evaluate("0+(1+4)*2"));
        assertEquals("7", this.calculator.evaluate("(4+3)"));
        assertEquals("10", this.calculator.evaluate("8+1+(5+4)/9"));
        assertEquals("79", this.calculator.evaluate("6*5+(4+3)*7"));
        assertEquals("84", this.calculator.evaluate("6*5+(5/1)+(4+3)*7"));
        assertEquals("229", this.calculator.evaluate("6*5*(5+1)+(4+3)*7"));
        assertEquals("29", this.calculator.evaluate("5+2(6+6)"));
        assertEquals("29", this.calculator.evaluate("5+(6+6)2"));
        
    }
    
    @Test
    public void doesPercentageCorrectly() {
        
        assertEquals("6", this.calculator.evaluate("5+20%"));
        assertEquals("0.03", this.calculator.evaluate("3%"));
        assertEquals("4.08", this.calculator.evaluate("3+(6*6)%"));
        assertEquals("0.9", this.calculator.evaluate("3*30%"));
        assertEquals("0.125", this.calculator.evaluate("25%/2"));
        assertEquals("27.5", this.calculator.evaluate("25+10%"));
        assertEquals("-2.75", this.calculator.evaluate("5^2%-3"));
        assertEquals("0.25", this.calculator.evaluate("5^2%"));
        assertEquals("0.05", this.calculator.evaluate("√5^2%"));
        assertEquals("1.9996", this.calculator.evaluate("2-2%^2"));
        assertEquals("Malformed expression", this.calculator.evaluate("2*%"));
        assertEquals("Malformed expression", this.calculator.evaluate("2+%"));
        assertEquals("0.04", this.calculator.evaluate("2*2%"));
        assertEquals("0.04", this.calculator.evaluate("2%*2"));
        assertEquals("0.04", this.calculator.evaluate("(2+2)%"));
        assertEquals("0.03", this.calculator.evaluate("3%"));
        assertEquals("0.0004", this.calculator.evaluate("2%^2"));
    }
    
    @Test
    public void multiPliesByZeroCorrectly() {
        assertEquals("0", this.calculator.evaluate("4*0"));
        assertEquals("0", this.calculator.evaluate("0*0"));
        assertEquals("0", this.calculator.evaluate("0*4"));   
    }
    
    @Test
    public void dividesByZeroCorrectly() {
        assertEquals("Division by zero is undefined", 
                this.calculator.evaluate("4/0"));   
        assertEquals("Division by zero is undefined", 
                this.calculator.evaluate("1044/0"));
    }
    
    @Test
    public void dividesZeroCorrectly() {
        assertEquals("0", this.calculator.evaluate("0/4"));
        assertEquals("0", this.calculator.evaluate("0/1054"));
        
    }
    
    @Test
    public void squareRootOfNegativeNumberWorksCorrectly() {
        assertEquals("Square root of a negative number is undefined within "
                + "real numbers", this.calculator.evaluate("√-9"));
    }
    
    
    
}
