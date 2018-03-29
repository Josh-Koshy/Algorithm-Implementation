package ac;

import java.util.Arrays;


public class NodeChain{
	
	Node root; 
	public NodeChain() {
		root = new Node(); 
	}
	public void addFirstWord(String s) {
		char[] wordArray = s.toCharArray();
		root = new Node(wordArray[0]);
		Node curr = root; 
		for (int i = 1; i < wordArray.length; i++) {
			Node d = new Node (wordArray[i]);
			curr.setChild(d);
			curr = curr.child;
		}
		
		
	}
	public void add (String s) {
		char[] wordArray = s.toCharArray();
		addAnyOtherWord (wordArray, root);
		
	}
	
	private void addAnyOtherWord (char[] chars, Node root) {
		Node current = root;
		
		for (int i = 0; i<chars.length; i++) {
			if (current.c == chars[i]) {
				if (current.child == null) {
					if (i == chars.length-1){
						Node d = new Node ('^');
						current.child = d;
					}
					else {
						Node d = new Node (chars[i+1]);
						current.child = d;
						current = current.child;
					}
					//idk if i need this below
					//don't need this because i'm losing a letter when going child
					//i--;
				}
				if (current.child != null) {
					//System.out.println("They equal each other and move child");
					current = current.child;
				}
			}
			
			else if (current.getChar() !=chars[i]) {
				//System.out.println("They do not equal each other");
				//is neighbor node null? if so then put value into neighbor node
				if (current.neighbor == null) {
					//create node and add value
					//System.out.println("create neighbor node");
					Node n = new Node(chars[i]);
					current.neighbor = n; 
					current = current.neighbor;
					
					//need to backtrack array though because I'm now pointing at the newly entered letter. 
					//If I do that then my logic would put the neighbor letter as a neighbor node 
					//instead of a child node
					i--;
				}
				else {
					//move to the neighbor node
					//System.out.println("neighbor node value: " + current.neighbor.c);
					//System.out.println("just move to the neighbor node");
					current = current.neighbor;
					i--;
				}
			} // end else if 
			
		} // end for loop
		
		//need to add logic where we add a symbol in the child node to signify if it is a completed word
		
	} //end private addAnyOtherWord function
	
	
	// *******************************************************
	// *****************SEARCH FUNCTION **********************
	// *******************************************************
	//WordList array
	//capable of storing more than 5 words because there's a chance the word matches the user input so we need to move on to the neighbor name
	String[] WL = new String[10000];
	int openIndex = 0;
	
	public String[] returnWordList() {
		return WL;
	}
	
	public void search(String word, Node n, char[] w, int index) {
		// does it have a carrot? AND the string of characters in the char array is not in my large Word array?
		// then output the word
		String charArrayToString = new String(w);
		charArrayToString = charArrayToString.replaceAll("\0", "");
		//using child node from current
		//System.out.println("Index = " + index);
		//System.out.println("OpenIndex: " + openIndex);
		//System.out.println("Found last node value is: " + n.c);
		// need to start looking for words by making sure we're at the last node of the prefix
		// is this correct?!!?
		// you want to pass the child node into carrot to see if it has a carrot. It will then check 
		// other sibling nodes if there is a carrot
		// if no carrot symbol then it will return false
		// if it returns true then we know it is a word and must be added to the WordList array
		// secondary check -- if it is already in word list array then move on to other words
		// -- this may mean that we fall backwards in a previous recursive step
		
		//System.out.println("charArrayToString: " + charArrayToString);
		for (int i = 0; i<openIndex; i++) {	
			String wordList = new String(WL[i]).replaceAll("\0", "");
			//System.out.println("WL[openIndex]: " + wordList + " at openIndex: " + i);
		}
		//System.out.println("---------------------------------------------------");
		//System.out.println("Node we're at n: " + n);
		/*if (n.c == '^') {
			char c = n.c;
			String s = Character.toString(c);
			WL[openIndex] = s;
			openIndex++;
		}
		*/
		if (n.c != '^' && hasCarrot(n.child) && checkWordList(WL, openIndex, w)) {
			//System.out.println("Has carrot and adding to word list");
			//System.out.println("Before inputting c into w array");
			//System.out.println("w[index]: " + w[index]);
			//System.out.println("n.c: " + n.c);
			w[index] = n.c;
			//System.out.println("After inputting c into w array");
			//System.out.println("w[index]: " + w[index]);
			//System.out.println("n.c: " + n.c);
			WL[openIndex] = new String(w);
			//System.out.println("WL[openIndex]: " + WL[openIndex]);
			openIndex++;
		}

				
		//if the node is not null && neighbor isn't null OR the node itself isn't a carrot
		if ((n.child !=null) && ((n.child.getChar() != '^') || (n.child.neighbor != null))) {
			char[] newArray = (char[]) w.clone();
			//System.out.println("cloning array...");
			newArray[index] = n.c;
			//System.out.println("newArray[index]: " + newArray[index]);
			//System.out.println("n.c: " + n.c);
			index++;
			//System.out.println("Moving child...");
			search(word,n.child,newArray,index);
		}
		// if the neighbor one is not null then go to the neighbor one
		if (n.neighbor != null) {
			//System.out.println("Moving neighbor...");
			search(word,n.neighbor,w,index);
		}
		
	}
	public boolean checkWordList (String[] wordList, int openIndex, char[] charArray) {
		String word = new String(charArray);
		word = word.replace("\0", "");
		//System.out.println("wordCheck: " + word);
		for (int i = 0; i < openIndex; i++) {
			if (word == wordList[i]) {
				return false;
			}
		}
		return true;
	}
	
	public Node reachLastNodeInPrefix (String s) {
		char[] prefix = s.toCharArray();
		Node current = root;
	
		//System.out.println("Root value: " + current.c);
		for (int i = 0; i<prefix.length; i++) {
			//System.out.println("Comparing current.c: "+ current.c + " to prefix["+ i + "]: " + prefix[i]);
			//System.out.println(current.c == prefix[i]);
			if (current == null) {
				break;
			}
			else if (current.c == prefix[i]) {
				//move child to see if it continues to be a prefix
				if (i == prefix.length-1){
					return current;
				}
				else {
					//System.out.println("They equal!");
					//System.out.println("Moving child...");
					current = current.child;
				}
				//else{
				//	return current;
				//}
				//System.out.println("Current node value: " + current.c);
			}
			//else if (current.child == null) {
				// child is null, meaning not a prefix
			//	break;
			//}
			else if ((current.c != prefix[i]) && (current.neighbor != null)) {
				//System.out.println("Not equal to each other");
				//System.out.println("Current node value: " + current.c);
				//System.out.println("Moving neighbor...");
				current = current.neighbor;
				i--;
				//System.out.println("Current node value: " + current.c);
	
			}
			else if ((current.c !=prefix[i]) && (current.neighbor == null)) {
				//System.out.println("The prefix at index does not equal the current node c, and neighbor is null");
				//if doesn't exist, go to neighbor and point it to a null node
				try {
					current = current.neighbor;
				}catch (NullPointerException e){
					System.out.println("No suggestions 2");
					break;
				}
			}
		}
		return current;
	}
	
	//does the node 
	private boolean hasCarrot(Node curr) {
		//System.out.println("In hasCarrot...");
		if (curr.c == '^') {
			//System.out.println("In carrot, return true");
			return true;
		}
		else if (curr.neighbor != null) {
			//System.out.println("In hasCarrot but looking neighbor nodes to see if there is carrot");
			return hasCarrot(curr.neighbor);
		}
		else {
			//System.out.println("returns false");
			return false;
		}
	}
	
}