import java.util.*;
/**
* This program calculates the string sequence of numbers and symbols.  
* It handles '+', '-', '*', and '/'. Please run runTest() to check the result.
* @author ashigam
*/
public class STEP_W3_HW1 {

    private int index = 0;	// to keep track of index# of input line

    class Token{
        String type;
        double number;
        // constructors
        Token(){};	// default
        Token(String t){type = t;}	// take a parameter type
        Token(String t, double n){type = t; number = n;} //take number and type		
    }
    /**
     * Read number and convert it to a token.
     * @param line the string sequence of number and symbol
     * @return the NUMBER type of token 
     */
    Token readNumber(String line) {
        double number = 0;
        double keta = 0;
        // read all digits before decimal point
        while(index < line.length() && Character.isDigit(line.charAt(index))) {
            number = number * 10 + Character.getNumericValue(line.charAt(index));
            index++;
        }
        // read all digits after decimal point
        if(index < line.length() && line.charAt(index) == '.') {
            index++;
            keta = 0.1;
            while(index < line.length() && Character.isDigit(line.charAt(index))) {
                number += Character.getNumericValue(line.charAt(index)) * keta;
                keta /= 10;	// increment decimal place by 1
                index++;
            }			
        }
        return new Token("NUMBER", number);
    }
    /**
     * Read "+" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the PLUS type of token
     */
    Token readPlus(String line) {
        index++;
        return new Token("PLUS");
    }
    /**
     * Read "-" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the MINUS type of token
     */
    Token readMinus(String line) {
        index++;
        return new Token("MINUS");
    }
    /**
     * Read "*" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the MULTIPLY type of token
     */
    Token readProduct(String line) {
        index++;
        return new Token("MULTIPLY");
    }
    /**
     * Read "/" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the DIVIDE type of token
     */
    Token readDivide(String line) {
        index++;
        return new Token("DIVIDE");
    }
    /**
     * Tokenize a string into a vector of tokens.  
     * @param line the string sequence of number and symbol
     * @return the vector of tokens
     */
    Token[] tokenize(String line) {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token("DUMMY"));	// insert a dummy token
        index = 0;

        while(index < line.length()) {
            if(Character.isDigit(line.charAt(index)))
                tokens.add(readNumber(line));
            else if(line.charAt(index) == '+')
                tokens.add(readPlus(line));
            else if(line.charAt(index) == '-')
                tokens.add(readMinus(line));
            else if(line.charAt(index) == '*')
                tokens.add(readProduct(line));
            else if(line.charAt(index) == '/')
                tokens.add(readDivide(line));
            else {
                System.out.println("Invalid character found: " + line.charAt(index));
                System.exit(1);
            }
        }
        return tokens.toArray(new Token[tokens.size()]);
    }
    /**
     * Process '*' and '/' as the first process
     * @param tokens the vector of tokens
     * @return the string after multiplication and division have been calculated
     */
    String firstProcess(Token[] tokens) {
        String line = "";
        int i = 1;
        // treat corner cases
        if(tokens.length == 1)	// input has 0 token
            return line += 0;
        if(tokens[1].type != "NUMBER")	// input is symbol only
            return "-1";
        if(tokens.length == 2)	// input has 1 number
            return line += tokens[1].number;

        while(i < tokens.length) {
            // the last number: add it to line and return
            if(i+1 == tokens.length) {
                if(tokens[i-1].type == "PLUS" || tokens[i-1].type == "MINUS")
                    return line += tokens[i].number;
            }
            // look at each symbol: concatenate if '+' or '-', and calculate if '*' or '/'
            if(tokens[i].type != "NUMBER") {

                if(tokens[i].type == "PLUS") {	// '+'
                    if(tokens[i-2].type != "MULTIPLY" && tokens[i-2].type != "DIVIDE")
                        line += tokens[i-1].number;
                    line += '+';
                }				
                else if(tokens[i].type == "MINUS") {	// '-'
                    if(tokens[i-2].type != "MULTIPLY" && tokens[i-2].type != "DIVIDE")
                        line += tokens[i-1].number;
                    line += '-';
                }			
                else if(tokens[i].type == "MULTIPLY" || tokens[i].type == "DIVIDE")	{	// '*' or '/' 					
                    double result[] = totalOfMultiplyDivide(i, tokens);
                    line += result[0];	
                    i = (int)result[1];
                    continue;
                }
                else {
                    System.out.println("Invalid syntax");
                    System.exit(1);
                }
            }		
            i++;
        }		//System.out.println(line);		
        return line;
    }
    /**
     * Helper: Calculate the value while '*' or '/' continues 
     * @param i the index# of tokens to be started
     * @param tokens the array of tokens
     * @return the array of double contains total value and the index# ended
     */
    double[] totalOfMultiplyDivide(int i, Token[] tokens) {
        double answer = tokens[i-1].number;
        while( i < tokens.length) {
            if(tokens[i].type != "NUMBER") {
                if(tokens[i].type == "PLUS"|| tokens[i].type == "MINUS") 
                    break;
                if(tokens[i].type == "MULTIPLY")
                    answer = answer * tokens[i+1].number;

                else if(tokens[i].type == "DIVIDE")
                    answer = answer/tokens[i+1].number;
                else {
                    System.out.println("Invalid symbol: " + tokens[i].type);
                    System.exit(1);
                }
            }
            i++;			
        }
        return new double[] {answer, i};
    }
    /**
     * Process '+' and '-' as the second process  
     * @param tokens the array of tokens
     * @return the number of the calculated answer 
     */
    double secondProcess(Token[] tokens) {
        double answer = 0;
        int i = 1;		
        while(i < tokens.length) {
            if(tokens[i].type == "NUMBER") {
                if(tokens[i-1].type == "DUMMY")
                    answer += tokens[i].number;
                else if(tokens[i-1].type == "PLUS")
                    answer += tokens[i].number;
                else if(tokens[i - 1].type == "MINUS")
                    answer -= tokens[i].number;
                else {
                    System.out.println("Invalid syntax");
                    System.exit(1);
                }
            }	
            i++;
        }// end of while
        return answer;				
    }
    /**
     * Evaluate the tokens as calculation.
     * @param tokens the array of tokens
     * @return the number of the calculated answer  
     */
    double evaluate(Token[] tokens) {

        // phase 1: process '*' and '/'
        String line = firstProcess(tokens);
        Token[] tokens2 = tokenize(line);

        // phase 2: process '+' and '-'
        return secondProcess(tokens2);		
    }
    /**
     * Check a test case.
     * @param line the string sequence of number and symbol
     * @param expectedAnswer the number of expected answer 
     * 		  (calculated in R in advance as eval() is not available in Java)
     */
    void test(String line, double expectedAnswer) {
        Token[] tokens = tokenize(line);
        double actualAnswer = evaluate(tokens);
        if(Math.abs(actualAnswer - expectedAnswer) < 1e-8)
            System.out.println("PASS! " + line + " = " + expectedAnswer);
        else
            System.out.println("FAIL! " + line + " should be " + expectedAnswer 
                    + " but was " + actualAnswer);
    }
    /**
     * Run test cases for the calculating program. 
     */
    void runTest() {
        System.out.println("==== Test started! ====");
        test("1+2", 3);
        test("1", 1);
        test("0+2*5/7", 1.4285714285714286);
        test("1.0+2.1-3", 0.1);
        test("3.0+4*2-1/5", 10.8);
        test("3.0+4*2-1/5*5", 10);
        test("", 0);
        test("3.0+4*2-1/5*5+10000", 10010);
        test("/", -1);
        test("3.0*4*2*1*5*5", 600);
        System.out.println("==== Test finished! ====");
    }

    public static void main(String args[]) {	
        STEP_W3_HW1 calc = new STEP_W3_HW1();
        calc.runTest();		
    }
}
