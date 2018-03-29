//Josh Koshy
//CS 1501 Summer

/*************************************************************************
 
*  Compilation:  javac GraphDriver.java
 *  Execution:    java GraphDriver [r || d val || u val || s val val || m || q ] 
 *
 *
 *********************************************************************/

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class GraphDriver
{
	public static void main(String[] args)
	{
		AdjList list;
		Scanner user = new Scanner(System.in);
		Scanner input = null;
		String userInput;
		int vCount, eCount;
		
		try
		{
			input = new Scanner(new File(args[0]));
		}
		catch (IOException io)
		{
			System.out.println(io);
			System.exit(0);
		}
		
		vCount = input.nextInt();
		eCount = input.nextInt();
		list = new AdjList(vCount, eCount);
		
		if(input.hasNext())
		{
			while(input.hasNext())
			{
				list.addEdge(input.nextInt(), input.nextInt(), input.nextInt());;
			}
		}
		while(true)
		{
			System.out.println("\nWaiting for command");
			userInput = user.nextLine();
			if(userInput.equals("r") && userInput.length() == 1)
				list.resultReport();
			else if(userInput.charAt(0) == 'd' && userInput.length() == 3)
				list.tickDown(userInput.charAt(2) - 48);
			else if(userInput.charAt(0) == 'u' && userInput.length() == 3)
				list.tickUp(userInput.charAt(2) - 48);
			else if(userInput.charAt(0) == 's' && userInput.length() == 5)
				list.dijkstra(userInput.charAt(2) - 48, userInput.charAt(4) - 48);
			else if(userInput.charAt(0) == 'm' && userInput.length() == 1)
				list.mst();
			else if(userInput.charAt(0) == 'q' && userInput.length() == 1)
			{
				System.out.println("Terminating program.");
				System.exit(0);
			}
			else
				System.out.println("Please enter a correct command: r | d | u | s | m | q "); 
				
		}
	}
}
