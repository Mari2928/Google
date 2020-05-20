import java.util.*;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
/**
 * This program get a random word from "I can haz wordz" as an input 
 * and returns an anagram in a dictionary which has a highest score.
 * @author ashigam
 *
 */
public class STEP_W1_HW2 {
	
	// global variables
	static String[] dict;
	static ArrayList<String> anagrams = new ArrayList<String>();
	
    public static void main(String[] args) {            
    	
    	// test for HOME WORK 2 	
    	ArrayList<String> answers = findAnagram2("\r\n" + 
    			"N\r\n" + 
    			"D\r\n" + 
    			"N\r\n" + 
    			"X\r\n" + 
    			"N\r\n" + 
    			"N\r\n" + 
    			"K\r\n" + 
    			"S\r\n" + 
    			"Qu\r\n" + 
    			"M\r\n" + 
    			"L\r\n" + 
    			"I\r\n" + 
    			"B\r\n" + 
    			"N\r\n" + 
    			"G\r\n" + 
    			"D");
//    	for(int i=0; i<answers.size(); i++)
//    		System.out.println(answers.get(i));
    	//get the word has a highest score
    	System.out.println(getHighPWord(answers));  
    	
//    	int[] anagram = new int[] {2,4,1,0};
//    	int[] dict = new int[] {1,2,1,0};
//    	System.out.println(isSubSet(dict, anagram));
    }
    /**
     * [HOME WORK 2] Get a word has a highest score in O(WC) time
     * where W as the number of words in a list and C as the number of character for each word.
     * @param allAnagrams a list of anagrams
     * @return a word has a highest score 
     */
    static String getHighPWord(ArrayList<String> allAnagrams) {
    	String highPWord = "";
    	int maxScore = 0;
    	int[] scoreTable = new int[] {1,1,2,1,1,2,1,2,1,3,3,2,2,1,1,2,3,1,1,1,1,2,2,3,2,3}; 
    	
    	// calculate the score for each anagram
    	for(int i = 0; i < allAnagrams.size(); i++) {	// O(W)
    		String current = allAnagrams.get(i);
    		int j = 0;
    		int score = 0;
    		while(j < current.length()) {	// O(C)
    			char c = current.charAt(j);
    			int index = getCharNumber(c);	// map to scoreTable O(1)
    			score += scoreTable[index];
    			j++;
    		}
    		// update the current max
    		if(Math.max(maxScore, score) == score) {
    			maxScore = score;
    			highPWord = current;
    		}   			
    	}
    	highPWord.replace("q", "qu");	// back to original
    	return highPWord;
    }
    /**
     * [HOME WORK 2] Find an anagram without using a generated wordList in O(S+C+SC+SA) = O(SA) time
     * where S as the number of strings in dictionary and A as the number of alphabets.
     * This version builds character frequency table for a given string and for each dictionary word,
     * and then return the anagram that has a partially/fully identical table.
     * @param randomS a string of random word
     * @return an anagram in dictionary found first 
     * 		   an error message if it's not found
     */
    static ArrayList<String> findAnagram2(String randomS) {
    	randomS = randomS.replaceAll("(\\r|\\n)", "");	// trim copied string
    	randomS = randomS.replace("Qu", "q");	// treat as one letter

    	// initialize settings
    	anagrams.clear();
    	dict = createDictionary();    	// O(S)
    	randomS = randomS.toLowerCase();
    	int[] table = buildCharFreqTable(randomS);	// O(C)  
    	
    	// each word in dictionary now has a char frequency table
    	HashMap<int[], String> newDict = createNewDict(dict); 	// O(SC)
    	
    	for(int[] i : newDict.keySet()) {	// O(S)
    		if(isSubSet(i, table)) {		// tables have common items  O(A)
    			anagrams.add(newDict.get(i));
    		}
    	}
    	return anagrams;
    }
    /**
     * [HOME WORK 2] Check if a dictionary word is a subset of an given anagram in O(A+A) = O(A) time
     * where A as the number of alphabets 'a' to 'z' (26).
     * [1,2,1,0,0] [1,2,1,0,6] -> true
     * @param dictWord a char frequency table of a dictionary word
     * @param anagram a char frequency table of an anagram
     * @return
     */
    static boolean isSubSet(int[] dictWord, int[] anagram) {    	
    	int validChars = 0;	// the number of alphabets used in the anagram
    	for(int i : anagram)
    		if(i != 0)	validChars++;  

    	int count = 0;
    	for(int i = 0; i < anagram.length; i++) {	// O(A)
    		if(anagram[i] == 0 && dictWord[i] != 0)	return false; // cannot make anagram
    		if(dictWord[i] > anagram[i])			return false;
    		//if(anagram[i] == 0 && dictWord[i] == 0) continue;	  // both is 0 so it's fine    		
    		if(anagram[i] != 0 && dictWord[i] != 0 && dictWord[i] <= anagram[i])
    			count++;    		
    	}    	    	
    	if(count <= validChars )
    		return true;    	 		
    	return false;
    }
    /**
     * [HOME WORK 2] Create a new version of dictionary in O(SC) time
     * where S as the number of strings in a dictionary and C as the number of characters in each string. 
     * @param dict a list of words in dictionary
     * @return a new dictionary in which each word has a char frequency table
     */
    static HashMap<int[], String> createNewDict(String[] dict){
    	HashMap<int[], String> newDict = new HashMap<int[], String>();
    	for(String s : dict) 	// O(S)
    		newDict.put(buildCharFreqTable(s), s);	// O(C)
    	return newDict;
    }
    /**
     * [HOME WORK 2] Count how many times each character appears in a word in O(C) time
     * where C as the number of characters in a given string.
     * @param str a word to be looked
     * @return a character frequency table
     */
    static int[] buildCharFreqTable(String str) {
    	int[] table = new int[Character.getNumericValue('z')-
    	                      Character.getNumericValue('a')+1];
    	for(char c : str.toCharArray()) {
    		int x = getCharNumber(c);
    		if(x != -1)		// character is a letter
    			table[x]++;   
    	}
    	return table;
    }
    /**
     * [HOME WORK 2] Map each character to a number in O(1) constant time.
     * a -> 0, b -> 1, etc. Non-letter characters map to -1.
     * @param c a character to be mapped
     * @return a number of mapped value
     * 		   -1 if it's non-letter character
     */
    static int getCharNumber(char c) {
    	int a = Character.getNumericValue('a');
    	int z = Character.getNumericValue('z');
    	int val = Character.getNumericValue(c);
    	if(a <= val && val <= z)	// char is in a to z
    		return val - a;
    	return -1;					// non-letter char
    }    
    /**
     * [Helper] Create a dictionary in O(S) time
     * where S as the number of strings in dictionary
     * @return a string array of dictionary
     */
    static String[] createDictionary(){
    	ArrayList<String> lines = new ArrayList<String>();
    	try {
    	      File myObj = new File("../dictionary.txt");
    	      Scanner myReader = new Scanner(myObj);
    	      while (myReader.hasNextLine()) {
    	        String data = myReader.nextLine();
    	        data.toLowerCase();			// convert to lower case
    	        data.replace("qu", "q");	// treat as same as a given random word
    	        lines.add(data);	
    	      }   	      
    	      myReader.close();
    	    } catch (FileNotFoundException e) {
    	      System.out.println("An error occurred.");
    	      e.printStackTrace();
    	    }
    	String[] dict = lines.toArray(new String[lines.size()]);
    	return dict;    	
    }
}
