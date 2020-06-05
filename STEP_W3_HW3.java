import java.util.*;
/**
* This program addresses parentheses in addition to the calculation program in HW1.
* If input is typo or invalid the program is stopped with an error message or return -1.
* @author ashigam
*/
public class STEP_W3_HW3 {
    private int index = 0;	// to keep track of index# of input line
    
    enum Type { NUMBER, PLUS, MINUS, MULTIPLY, DIVIDE, PARENTHESES, DUMMY; }

    class Token{
    	Type type;
        double number;
        // constructors
        Token(){};  // default
        Token(Type t){ this.type = t; } 	// take a parameter, type
        Token(double n){	// take a number	
        	this.type = Type.NUMBER; 
        	this.number = n;
        } 	
    }
    /**
     * Evaluate a string of numbers and symbols as a calculator.
     * @param line the string sequence of numbers and symbols
     * @return the number of the calculated answer  
     */
    double evaluate(String line) {	
        // pre_processing
        if(line.length() == 0) return 0;    // no input
        if(line.length() == 1 && !Character.isDigit(line.charAt(0))) return -1;	// one symbol only
        if(line.contains(" "))	line = line.replaceAll("\\s+","");  // has space
        if(line.contains("/") && line.charAt(line.indexOf("/") + 1) == '0') return -1;// x/0

        // phase 1: process parentheses
        Token[] tokens = tokenize(processParentheses(line));

        // phase 2: process '*' and '/'
        Token[] tokens2 = tokenize(processMultiplyDivide(tokens));

        // phase 3: process '+' and '-'
        return processPlusMinus(tokens2);		
    }
    /**
     * Process calculating the parentheses part recursively
     * @param line the string sequence of number and symbol
     * @return the string sequence of number and symbol after the parentheses calculation
     */
    String processParentheses(String line) {
        // base case of recursion
        if(!line.contains("("))	
            return String.valueOf(processMultiplyDivide(tokenize(line)));

        // identify the outside parentheses part in a string
        int start = line.indexOf('(');
        int end = line.length()-1;
        while(line.charAt(end) != ')') 	end--;
        String newLine = line.substring(start + 1, end);

        // no outside (): () is consecutive
        if(newLine.indexOf(")") < newLine.indexOf("("))
            return consecutiveParentheses(line);

        // evaluate the parentheses part recursively passing the smaller one		
        return 	line.substring(0, start) + evaluate(processParentheses(newLine)) + line.substring(end+1);
    }
    /**
     * Address consecutive parentheses.
     * e.g. 2+(5/1)+(10/5) -> 2+5+2
     * @param line the string sequence of number and symbol with consecutive parentheses
     * @return the sequence of number and symbol after parentheses part of calculation
     */
    String consecutiveParentheses(String line) {
        int start = line.indexOf('(');
        int end = line.indexOf(')');
        String candidate = line.substring(start+1, end);
        line = line.substring(0, start) + evaluate(candidate) + line.substring(end+1);
        return String.valueOf(evaluate(line));		
    }
    /**
     * Process '*' and '/' as the first process
     * @param tokens the vector of tokens
     * @return the string after multiplication and division have been calculated
     */
    String processMultiplyDivide(Token[] tokens) {	
        String line = "";
        int i = 1;	
        if(tokens.length == 2)	// input has 1 number
            return line += tokens[1].number;

        while(i < tokens.length) {
            // the last number: add it to line and return
            if(i+1 == tokens.length) {
                if(tokens[i-1].type == Type.PLUS || tokens[i-1].type == Type.MINUS)
                    return line += tokens[i].number;
            }
            // look at each symbol: concatenate if '+' or '-', and calculate if '*' or '/'
            if(tokens[i].type != Type.NUMBER) {

                // symbol is '+' or '-' 	
                if(tokens[i].type == Type.PLUS || tokens[i].type == Type.MINUS) {
                    if(tokens[i-2].type != Type.MULTIPLY && tokens[i-2].type != Type.DIVIDE) 
                        line += tokens[i-1].number;						
                    if(tokens[i].type == Type.PLUS)	
                            line += '+';
                    else	line += '-';
                }
                // symbol is '*' or '/' 		
                else if(tokens[i].type == Type.MULTIPLY || tokens[i].type == Type.DIVIDE)	{						
                    double result[] = totalOfMultiplyDivide(i, tokens);
                    i = (int)result[0];
                    line += result[1];						
                    continue;
                }
                else {
                    System.out.println("Invalid syntax");
                    System.exit(1);
                }
            }	
            i++;			
        }			
        return line;
    }
    /**
     * Helper: Calculate the value while '*' or '/' continues 
     * @param i the index# of tokens to be started
     * @param tokens the array of tokens
     * @return the array of double contains the index# ended and total value
     */
    double[] totalOfMultiplyDivide(int i, Token[] tokens) {
        double answer = tokens[i-1].number;
        while( i < tokens.length) {
            if(tokens[i].type != Type.NUMBER) {
                if(tokens[i].type == Type.PLUS|| tokens[i].type == Type.MINUS) 
                    break;
                if(tokens[i].type == Type.MULTIPLY)
                    answer = answer * tokens[i+1].number;

                else if(tokens[i].type == Type.DIVIDE)
                    answer = answer/tokens[i+1].number;
                else {
                    System.out.println("Invalid symbol: " + tokens[i].type);
                    System.exit(1);
                }
            }
            i++;			
        }
        return new double[] {i, answer};
    }
    /**
     * Process '+' and '-' as the second process  
     * @param tokens the array of tokens
     * @return the number of the calculated answer 
     */
    double processPlusMinus(Token[] tokens) {
        double answer = 0;
        int i = 1;		
        while(i < tokens.length) {
        	if(tokens[i].type == Type.NUMBER) {
        		switch(tokens[i-1].type) {
        			case DUMMY:
        				answer += tokens[i].number;
                		break;          		
        			case PLUS:
        				answer += tokens[i].number;
        				break;
        			case MINUS:
        				answer -= tokens[i].number;
        				break;
            		default:
            			System.out.println("Invalid syntax");
                        System.exit(1);            	 
            	}
        	}        	
            i++;
        }// end of while
        return answer;				
    }
    /**
     * Tokenize a string into a vector of tokens.  
     * @param line the string sequence of number and symbol
     * @return the vector of tokens
     */
    Token[] tokenize(String line) {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(Type.DUMMY));	// insert a dummy token
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
            else if(line.charAt(index) == '(' || line.charAt(index) == ')')
                tokens.add(readParentheses(line));
            else {
                System.out.println("Invalid character found: " + line.charAt(index));
                System.exit(1);
            }
        }
        return tokens.toArray(new Token[tokens.size()]);
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
        return new Token(number);
    }
    /**
     * Read "+" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the PLUS type of token
     */
    Token readPlus(String line) {
        index++;
        return new Token(Type.PLUS);
    }
    /**
     * Read "-" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the MINUS type of token
     */
    Token readMinus(String line) {
        index++;
        return new Token(Type.MINUS);
    }
    /**
     * Read "*" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the MULTIPLY type of token
     */
    Token readProduct(String line) {
        index++;
        return new Token(Type.MULTIPLY);
    }
    /**
     * Read "/" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the DIVIDE type of token
     */
    Token readDivide(String line) {
        index++;
        return new Token(Type.DIVIDE);
    }
    /**
     * Read "(" or ")" symbol and convert it to a token. 
     * @param line the string sequence of number and symbol
     * @return the PARENTHESES type of token
     */
    Token readParentheses(String line) {
        index++;
        return new Token(Type.PARENTHESES);
    }
//    int readXxxx(String line, int index, ArrayList<Token> tokens) {
//        // line の index 番目から Xxxx を読み込む
//        Token token = new Token(...);
//        tokens.add(token);  // 引数で受け取った tokens に追加する
//        return new_index;  // line の続きを読み込むための新しい index を返す
//    }
    /**
     * Check a test case.
     * @param line the string sequence of number and symbol
     * @param expectedAnswer the number of expected answer 
     * 		  (calculated in R in advance as eval() is not available in Java)
     */
    void test(String line, double expectedAnswer) {		
        double actualAnswer = evaluate(line);

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
        // general cases
        test("1+2", 3);
        test("1.0+2.1-3", 0.1);
        test("3.0+4*2-1/5", 10.8);
        test("3.0+4*2-1/5*5", 10);
        test("3.0+4*2-1/5*5+10000", 10010);

        // '*' or '/' is consecutive
        test("0+2*5/7", 1.4285714285714286);  
        test("3.0*4*2*1*5*5", 600);

        // () is addressed
        test("(3+4*(2-1)/5)", 3.8);	// all within ()
        test("3+4*((2-1)/5)", 3.8); // outside() is on left
        test("(3+4*(2-1))/5", 1.4); // outside() is on right
        test("3+4*(500/25)/5", 19); // outside() is in middle
        test("(3+4)*(2-1)/5", 1.4); // consecutive ()s
        test("(10+4)*(5*(9/2))/5", 63);// consecutive() with outside()
        test("((10+4)*((8100*5)*(9/2))/5)", 510300); // more more ()s

        // corner cases, typo, or space etc 
        test("", 0);
        test("1", 1);		
        test("/", -1);	
        test("3/0", -1);	// x/0 is undefined or infinity
        test(" 789 + 87 * ((100*78)  /56 )", 12906.857142857141); // user uses space  

        // invalid inputs (system exit)
        test("7+-2+h+", -1);
        test("7+-2+/+", -1);

        System.out.println("==== Test finished! ====");
    }

    public static void main(String args[]) {	
        STEP_W3_HW3 calc = new STEP_W3_HW3();
        calc.runTest();		
    }
}
