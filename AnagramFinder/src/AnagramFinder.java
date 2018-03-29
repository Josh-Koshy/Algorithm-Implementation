import java.io.*;
import java.util.*;

public class AnagramFinder
{	
	static String inputString;
	static DictInterface dInterface;
	static int wordCount;
	static TreeSet<String> currentInputAnagram;
	protected final static ArrayList<SortedSet<String>> masterList = new ArrayList<SortedSet<String>>(0);

	public static void main(String[] args) throws IOException, InterruptedException
	{
		//Initialize scanners and output writer, and dictionary type
		Scanner dictionaryText = null;
		Scanner inputText = null;
		PrintWriter outputText = null;
		int dictionaryInt = 0;
		String dictionaryType = null;
		String text;
		
		//Applies arguments to input and output files
		try
		{
			dictionaryText = new Scanner(new File("dictionary.txt"));
			inputText = new Scanner(new File(args[0]));
			outputText = new PrintWriter(new File(args[1]));
			dictionaryType = args[2];
		}
		catch (IOException io)
		{
			System.out.println(io);
			System.exit(0);
		}
		
		if (dictionaryInt == 0)
		{
			if (dictionaryType.equals("orig")) 
				dInterface = new MyDictionary();
			else if (dictionaryType.equals("dlb")) 
				dInterface = new DLB();
			else
			{
				System.out.println("Error in dictionary type argument; please restart.");
				System.exit(0);
			}
		}
		
		//Words from dictionaryText fill the dictionary structure that was chosen from arguments.
		//long startTime = System.currentTimeMillis();	//USED FOR COMPARISON TESTS (DEBUG)
		
		while (dictionaryText.hasNext())
		{
			text = dictionaryText.nextLine();
			dInterface.add(text);
		}
		
		//All strings from inputText are placed into an ArrayList
		ArrayList<String> inputList = new ArrayList<String>();
		String currentInput = "";
		
		while(inputText.hasNext())
		{
			currentInput = inputText.nextLine();
			currentInput = currentInput.replaceAll("\\s","");	//removes all whitespace characters
			inputList.add(currentInput);
		}
		
		//Iterates through each word, adding their resulting TreeSets (currentInputAnagram) to a global ArrayList (masterList)
		//Note: sortAnagrams is a recursive method
		for (int i = 0; i <= inputList.size() - 1; i++)
		{
			currentInputAnagram = new TreeSet<String>();
			sortAnagrams(new StringBuilder(), inputList.get(i).toCharArray(), 0, inputList.get(i).length() - 1, currentInputAnagram);
			masterList.add(currentInputAnagram);
		}
		
		//Prints the solutions to the output file in alphabetical order.
		for (int i = 0; i <= masterList.size() - 1; i++)
		{
			outputText.printf("Here are the solutions for %s:", inputList.get(i));
			outputText.printf("\n");
			SortedSet<String> set = masterList.get(i);
			for(String anAnagram : set)
				outputText.println(anAnagram);
		}
		
		//Close input and output files.
		inputText.close();
		outputText.close();
		
		//USED FOR COMPARISON TESTS (DEBUG)
		//long endTime = System.currentTimeMillis();
		//System.out.println("Time(ms): " + (endTime - startTime));

	}

	/*This is the recursive method used to find anagrams of the given word. It begins with the letter from the start of the input (letters).
	* While the method pushes through the method, each letter is immediately marked with my symbol '!'. This symbol shows that the letter has been touched by the method.
	* testString will build using the marked letters (remembered in c) and be tested for a word or prefix by the given dictionary interface (MyDictionary or DLBDict in this case).
	* start represents the start of testString while end represents the length of the initial array of inputLetters (the length of the initial word we are now scrambling)
	* If it is a prefix: it continues finding more anagrams on the same word (skipping over any marked ('!') characters)
	* If it is a word: it must be tested for length before it can be added to the global list. Smaller words are further tested through recursion, just in case.
	* If it is both: it goes through both processes listed above.
	* anagramWords keeps track of all valid anagrams found through recursion to be added to the global ArrayList that this var should be added to.
	*/
	static void sortAnagrams(StringBuilder testString, char[] inputLetters, int start, int end, TreeSet<String> anagramWords)
	{
		//Iterate through the letters of the current word
		for(int i = 0; i <= inputLetters.length - 1; i++)
		{
			if (inputLetters[i] != '!')
			{
				char testedCharacter = inputLetters[i]; 
				testString.append(testedCharacter);
				inputLetters[i] = '!';
				//Check if the testString creates a word or a prefix (may lead to anagram)
				int checkInt = dInterface.searchPrefix(testString, start, testString.length() - 1);
				//If it is a prefix (but not a word) OR a word/prefix, recurse on the current value of the word further
				if (checkInt == 3 || checkInt == 1)
					sortAnagrams(testString, inputLetters, start, end, anagramWords);
				//If it is a word (but not a prefix) OR is a word/prefix
				if (checkInt == 3 || checkInt == 2)
				{
					if (testString.length() - 1 < end)
						//Continue testing with matched word, see if the other letters can do something
						sortAnagrams(new StringBuilder(testString).insert(testString.length(), " "), inputLetters, testString.length() + 1, end + 1, anagramWords);
					//Add the word if it's the right length (used all the letters).
					if (testString.length() - 1 == end)
							anagramWords.add(new String(testString));
					if (inputLetters.length - 1 == 0)
						break;
				}
				//Reset inputLetters back
				inputLetters[i] = testedCharacter;
				//Remove testedCharacter from the end of testedString.
				testString.deleteCharAt(testString.length() - 1);
			}
		}
	}
}