public class DLB implements DictInterface
{
	private Node root;

	private class Node
	{
		private char c;	
		private Node neighbor;
		private Node child;

		private Node(char character)
		{
			c = character;
			neighbor = null;
			child = null;
		}
	}
	
	public DLB()
	{
		root = null;
	}
	public boolean add(String st)
	{
		int i;	//index
		
		//if empty or null string added
		if (st == null || st.length() == 0)
			return false;
		//creates new root if it's still null
		if (root == null)
			root = new Node(st.charAt(0));
		
		Node currentNode = root;
		
		//starts to find prefix of String st
		for (i = 0; currentNode.child != null; i++)
		{
			boolean found = false;
			if (currentNode.c == st.charAt(i))
				found = true;
			while (currentNode.neighbor != null && !found)
			{
				currentNode = currentNode.neighbor;
				if (currentNode.c == st.charAt(i))
					found = true;
			}
			if (!found)
			{
				currentNode.neighbor = new Node(st.charAt(i));
				currentNode = currentNode.neighbor;
			}
			if (currentNode.child != null)
				currentNode = currentNode.child;
		}
		for (int j = i ; (st.length() != 1) && (j < st.length()); j++)
		{
			currentNode.child = new Node(st.charAt(j));
			currentNode = currentNode.child;
		}
		currentNode.child = new Node('$');	//end of word
		return true;
	}

	/* Returns 0 if s is not a word or prefix within the DictInterface
	 * Returns 1 if s is a prefix within the DictInterface but not a 
	 *         valid word
	 * Returns 2 if s is a word within the DictInterface but not a
	 *         prefix to other words
	 * Returns 3 if s is both a word within the DictInterface and a
	 *         prefix to other words
	 */
	public int searchPrefix(StringBuilder s)
	{
		// No string to check or no words in dictionary
		if (s == null || s.length() == 0 || root == null)
			return 0;
		return recursiveSearchPrefix(root, s, 0);
	}

	public int searchPrefix(StringBuilder s, int start, int end)
	{
		if (s == null || s.length() == 0 || root == null)
			return 0;
		if (end > s.length())
			return 0;
		StringBuilder sb = new StringBuilder();
		sb.append(s.substring(start, end + 1));
		return recursiveSearchPrefix(root, sb, 0);
	}

	private int recursiveSearchPrefix(Node currentNode, StringBuilder sb, int index)
	{
		boolean prefix = false;
		boolean word = false;
		if (currentNode == null)
			return 0;
		if (index == sb.length())
		{
			if (currentNode.child != null)
				prefix = true;
			if (currentNode.c == '$')
			{
				if (currentNode.neighbor != null)
				{
					prefix = true;
					word = true;
				}
				else
					word = true;
			}
		}
		if (prefix && word)
			return 3;
		else if (prefix)
			return 1;
		else if (word)
			return 2;
		
		boolean prefixExists = false;
		if (currentNode.c == sb.charAt(index))
			prefixExists = true;
		while (currentNode.neighbor != null && !prefixExists)
		{
			currentNode = currentNode.neighbor;
			if (currentNode.c == sb.charAt(index))
				prefixExists = true;
		}
		if (!prefixExists)
			return 0;
		return recursiveSearchPrefix(currentNode.child, sb, index + 1);
	}

	
}