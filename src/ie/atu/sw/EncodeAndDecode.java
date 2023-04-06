package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

public class EncodeAndDecode {
	
	//Using a HashMap to store the morse code.
	//As it's not going to change, and I'll always have a key, it'll retrieve values in O(1).
	//(I think)
	final static StringBuilder emptyLine = new StringBuilder("");
	
	public static Map<Character, String> morseMap = initMorseMap();
	
	public static HashMap<Character, String> initMorseMap() {
		HashMap<Character, String> initMap = new HashMap<>();
		
		initMap.put('a', ".-");
		initMap.put('b', "-...");
		initMap.put('c', "-.-.");
		initMap.put('d', "-..");
		initMap.put('e', ".");
		initMap.put('f', "..-.");
		initMap.put('g', "--.");
		initMap.put('h', "....");
		initMap.put('i', "..");
		initMap.put('j', ".---");
		initMap.put('k', "-.-");
		initMap.put('l', ".-..");
		initMap.put('m', "--");
		initMap.put('n', "-.");
		initMap.put('o', "---");
		initMap.put('p', ".--.");
		initMap.put('q', "--.-");
		initMap.put('r', ".-.");
		initMap.put('s', "...");
		initMap.put('t', "-");
		initMap.put('u', "..-");
		initMap.put('v', "...-");
		initMap.put('w', ".--");
		initMap.put('x', "-..-");
		initMap.put('y', "-.--");
		initMap.put('z', "--..");
		
		return initMap;
	}
	
	//Reversed HashMap, initialized in Runner.java
	public static Map<String, Character> invertedMap = new HashMap<>();
	
	//Both encode and decode should operate in O(n) time, using StringBuilders instead of Strings to append text.
	//Encoding & Decoding the Bible takes a few seconds!
	public static StringBuilder encode(JTextField input) {
		
		//StringBuilder is used because it is much, much faster than String when appending.
		//O(n) instead of O(n^2)
		StringBuilder book = new StringBuilder("");
		
	
		//Using the BufferedReader format we used in labs.
		try(var br = new BufferedReader(new InputStreamReader(new FileInputStream(input.getText())))) {
			String line;
			
			//For every line: Seperate into Strings by spaces,
			//Strip out special characters
			//Translate into morse
			//Append to variable used to build up the translated text.
			while((line = br.readLine()) !=  null) {
				String[] sentence = line.split("\\s+");
				
				for(String s : sentence)
				{
					//Strip out special characters using Regex. From the email sent in March.
					s = s.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
					s = alphabetToMorse(s);
					
					//Check for empty lines. Occurs in cases where there's a bunch of line breaks in a row, like the bible.
					if(!s.isEmpty()) {
						book.append(s+"/ ");
					}

				}
				book.append("\n");
			}
		} catch (IOException woops) {
			woops.printStackTrace();
		}
		
		return book;
	}
	
	public static StringBuilder decode(JTextField input) {
		
		StringBuilder book = new StringBuilder("");
		
			try(var br = new BufferedReader(new InputStreamReader(new FileInputStream(input.getText())))) {
			String line;
			
			//For every line: Seperate by /, trim trailing spaces, translate morse and append to book.
			while((line = br.readLine()) !=  null) {
				String[] sentence = line.split("/");				
				for(String s : sentence)
				{
					s = s.trim();
					s = morseToAlphabet(s);	
					
					book.append(s);
				}
				book.append("\n");
			}
		} catch (IOException woops) {
			woops.printStackTrace();
		}
		
		return book;
	}

	//Translate character by character using the morseMap.
	public static String alphabetToMorse(String word) {
		String translation = "";
		for(int i = 0; i < word.length(); i++) {
			translation += morseMap.get(word.charAt(i)); //O(1)
			translation += " ";
		}
		return translation;
	}
	
	//Split morse word into individual morse characters, then translate them using an inverted version of morseMap.
	//Skip null characters (new lines).
	public static String morseToAlphabet(String word) {
		String[] builtWord = word.split(" ");
		StringBuilder translation = new StringBuilder("");
		
		for(int i = 0; i < builtWord.length; i++) {
			if(invertedMap.get(builtWord[i]) ==  null)
				return "";
			
			translation.append(invertedMap.get(builtWord[i]));
		}

		return translation.toString()+" ";
	}
}
