package ac;

import java.util.*;
import java.io.*;

public class ac_test {
	
	static BufferedWriter writer_buffer = null;
	static FileWriter writer_file = null;
	
	public static void main(String[] args) throws IOException {
		File file = new File("user_history.txt");
		File file2 = new File("dictionary.txt");
		Scanner dictionaryText = null;
		ArrayList list = new ArrayList();

		NodeChain nc = new NodeChain();
		NodeChain userDLB = new NodeChain();
		
		long sum = 0; 
		long start = 0; 
		long estimate = 0; 
		
		String userInput = "";
		char[] w = new char[100];
		int index = 0;
		boolean programRunning = true;
		
		  
		try
		{
			dictionaryText = new Scanner(file2);
		}
		catch (IOException io)
		{
			System.out.println(io);
			System.exit(0);
		}
		
		String firstWord = dictionaryText.nextLine();
		nc.addFirstWord(firstWord.toLowerCase());
		while (dictionaryText.hasNext()){
			String dictionaryWord = dictionaryText.nextLine();
			
			nc.add(dictionaryWord.toLowerCase());
		}
		
		dictionaryText.close();
		while(programRunning){
			System.out.print("Enter the next character:  ");
			Scanner input = new Scanner(System.in);
			userInput += input.nextLine().toLowerCase().charAt(0);
			
			
			PrintWriter writer = new PrintWriter("user_history.txt", "UTF-8");
			writer.close();
			Scanner userHistory = new Scanner(new File("user_history.txt"));
			start = System.nanoTime();
			while (userHistory.hasNext()) {
				String userWord = userHistory.nextLine();
				userDLB.add(userWord.toLowerCase());
				userInput = userInput.replace("$", "");
				if (userWord.startsWith(userInput)) {
					System.out.println(userWord);
				}
				userInput = userInput + "$";
			}
			userHistory.close();
	
			estimate = System.nanoTime() - start;
			double estimatedTimeInSeconds = estimate/(Math.pow(10, 9));
			list.add(estimatedTimeInSeconds);
			
			if (userInput.contains("$")) {
				if (userInput.contains("!")){
					System.out.println("Average time: (" + estimatedTimeInSeconds + " s)" );
					System.out.println("End of program");
					System.exit(0);
				}
				else {
					enterPrefixIntoUserApproach(userInput);
					userInput = userInput.replace("$", "");
				}
			}
			
			if (userInput.contains("!")){
				System.out.println("Bye!");
				double totalTime = 0;
				for(int i = 0; i < list.size(); i++)
				{
					totalTime += (double)list.get(i);
				}
				System.out.println("Average Time:\t" + totalTime + "s");
				break;
			}
			else {
				if (userInput.contains("$")) {
					userInput.replaceAll("$", "");
				}
				Node n = nc.reachLastNodeInPrefix(userInput);
	
				if (n == null) {
					System.out.println("No suggestions from auto-complete.");
				}
				else {
					nc.search(userInput, n.child, w, index);
				}
				int count = 0;
				String[] wordList = nc.returnWordList();
				estimate = System.nanoTime() - start;
				estimatedTimeInSeconds = estimate/(Math.pow(10, 9));
				System.out.println("(" + estimatedTimeInSeconds + " s)");
				System.out.println("Predictions");
				for (int i=0; i<wordList.length; i++) {
					if (count < 5){
						try {
							if (wordList[i] == null) {
								break;
							}
							else {
								String outputSuggestion = wordList[i].replaceAll("\0", "");
								System.out.print("(" + (count + 1) + ")" + userInput + outputSuggestion + "\t");
								if(count == 4){
									System.out.print("\n");
								}
								count++;
							}
						}catch (NullPointerException e) {
							System.out.println("");
						}
					}
					else {
						break;
					}
				}
			}
		}	
	}
	
	public static void enterPrefixIntoUserApproach(String word) throws IOException{
		word = word.replace("$", "");
		writer_file = new FileWriter("user_history.txt", true);
		writer_buffer = new BufferedWriter(writer_file);
	
				writer_buffer.write(word);
				writer_buffer.newLine();
				writer_buffer.close();
	}
	
}
