import java.lang.Iterable;
import java.util.Iterator;
import java.lang.Comparable;
import java.lang.IllegalArgumentException;

public class PHPArray<V> implements Iterable<V> {
	
	private int N;	//the # of key-value pairs
	private int M;	//the size of hash table
	
	private Node<V>[] table;	//hashtable (w/ nodes)
	private Node<V> orig;
	private Node<V> last;
	private Node<V> ptr;

	@SuppressWarnings("unchecked")
	public PHPArray(int initialCapacity)
	{
		N = 0;
		M = initialCapacity;
    	Node<V>[] hashTable = (Node<V>[]) new Node<?>[M];
    	table = hashTable;
		orig = null;
		last = null;
		ptr = null;
	}
	
	// return the number of key-value pairs in the symbol table
	public int length()
	{
		return N;
	}
	
	// is the symbol table empty?
	public boolean isEmpty()
	{
		return length() == 0;
	}
	
	// does a key-value pair with the given key exist in the symbol table?
	public boolean contains(String k)
	{
		return get(k) != null;
	}
	
	// insert the key-value pair into the symbol table
	public void put(String k, V v)
	{
		
		if (k == null)
			return;
		if (v == null)
			unset(k);
		if (N >= M/2)
			resize(2*M);
		int i;
		N++;
		for (i = hash(k, M); table[i] != null; i++)
			if (table[i].data.key.equals(k))
			{
				table[i].data.value = v;
				return;
			}
		table[i] = new Node<V>(new Pair<V>(k,v));
		if (orig == null)
		{
			orig = table[i];
			ptr = orig;
		}
		if (last == null)
			last = table[i];
		table[i].previous = last;
		last.next = table[i];
		last = table[i];
	}
	
	// insert the key-value pair into the symbol table
	public void put(Integer i, V v)
	{
		String k = i.toString();
		put(k,v);
	}
	
	// resize the hash table to the given capacity by re-hashing all of the keys
	@SuppressWarnings("unchecked")
	private void resize(int newCap)
	{
		Node<V>[] temp = (Node<V>[]) new Node<?>[newCap];
		for (int i = 0; i < M; i++)
			if (table[i] != null)
				temp[hash(table[i].data.key, newCap)] = table[i];
		table = temp;
		M = newCap;
	}
	
    // return the value associated with the given key, null if no such value (if key is string)
	public V get(String k)
	{
		for (int i = hash(k, M); table[i] != null; i = (i + 1) % M)
			if (table[i].data.key.equals(k))
				return table[i].data.value;
		return null;
	}
	
    // return the value associated with the given key, null if no such value (if key is integer)
	public V get(Integer i)
	{
		String k = i.toString();
		return get(k);
	}
	
	// delete the key (and associated value) from the symbol table (if key is string)
	public void unset(String k)
	{
		if (!contains(k))
			return;

		// find position i of key
		int i = hash(k, M);
		while (!k.equals(table[i].data.key))
			i = (i + 1) % M;

		// delete key and associated value
		if (last == table[i])
			last = table[i].previous;
		if (orig == table[i])
			orig = table[i].next;
		
		table[i].previous.next = table[i].next;
		table[i].next.previous = table[i].previous;
		table[i] = null;

		// rehash all keys in same cluster
		i = (i + 1) % M;
		while (table[i] != null)
		{
			String keyToRehash = table[i].data.key;
			System.out.println("\t\tKey " + keyToRehash + " rehashed...\n");
            V valToRehash = table[i].data.value;
			table[i] = null;
			int newHash = hash(keyToRehash, M);
			
			while (table[newHash] != null)
				newHash++;
			
			table[newHash] = new Node<V>(new Pair<V>(keyToRehash, valToRehash));
			i = (i + 1) % M;
		}
		N--;
	}
	
	// delete the key (and associated value) from the symbol table (if key is an integer)
	public void unset(Integer i)
	{
		String k = i.toString();
		unset(k);
	}
	
	public Pair<V> each()
	{
		if (ptr == null)
			return null;

		Pair<V> currentPair = ptr.data;
		ptr = ptr.next;
		return currentPair;
	}
	
	//sets all pointers back to their original value
	public void reset()
	{
		ptr = orig;
	}
	
	//resorts keys in table
	@SuppressWarnings("unchecked")
	public void sort()
	{
		if (!(orig.data.value instanceof Comparable)) throw new IllegalArgumentException();
		if (orig == null || orig.next == null)
			return;
		
		int i;
		
		//reconstructs and gets rid of null nodes
		Node<V>[] a = listToArray();		
		Node<V>[] temp = (Node<V>[]) new Node<?>[N];
		mergeSort(a, temp, 0, N-1);
		
		//generates key for new linked list
		orig = a[0];
		table[0] = a[0];
		table[0].data.key = "0";
		
		for (i = 1; i < a.length; i++)
		{
			String key = Integer.toString(i);
			table[hash(key, M)] = a[i];
			table[hash(key, M)].data.key = key;
			a[i-1].next = a[i];
			a[i].previous = a[i-1];
		}
		a[i-1].next = null;
	}
	
	//resorts but keeps the initial keys
	@SuppressWarnings("unchecked")
	public void asort()
	{
		if (!(orig.data.value instanceof Comparable)) throw new IllegalArgumentException();
		if (orig == null || orig.next == null)
			return;

		//reconstructs and gets rid of null nodes
		Node<V>[] a = listToArray();
		Node<V>[] temp = (Node<V>[]) new Node<?>[N];
		mergeSort(a, temp, 0, N-1);
		
		//generates new linked list
		orig = a[0];
		int i;
		for (i = 1; i < N; i++)
		{
			a[i-1].next = a[i];
			a[i].previous = a[i-1];
		}
		a[i-1].next = null;
	}
	
	//merges both segments together with merge-sort algorithm
	private void mergeSort(Node<V>[] a, Node<V>[] temp, int left, int right)
	{
		if(left < right)
		{
			int center = (left + right) / 2;
			
			//recursive calls
			mergeSort(a, temp, left, center);
			mergeSort(a, temp, center + 1, right);
			
			//the meat of the merge-sort work
			merge(a, temp, left, center + 1, right);
		}
	}
	
	//merge-sort algorithm's merging process
	private void merge(Node<V>[] a, Node<V>[] temp, int left, int middle, int right )
	{
		int leftEnd = middle - 1;
		int k = left;
		int num = right - left + 1;

		while(left <= leftEnd && middle <= right)
		{
			if(a[left].compareTo(a[middle]) <= 0)
				temp[k++] = a[left++];
			else temp[k++] = a[middle++];
		}
		while(left <= leftEnd)
		{
			temp[k] = a[left];
			k++;
			left++;
		}
		while(middle <= right)
		{
			temp[k] = a[middle];
			k++;
			middle++;
		}
		for(int i = 0; i < num; i++, right--)
			a[right] = temp[right];
	}
	
	//gets rid of null elements within linked list, returning an array
	@SuppressWarnings("unchecked")
	public Node<V>[] listToArray()
	{	
    	Node<V>[] temp = (Node<V>[]) new Node<?>[N];
    	Node<V> current = orig;
    	int index = 0;
		while(index < N)
		{
			temp[index] = current;
			current = current.next;
			index++;
		}
		return temp;
	}
	
	//the hash function for table
	private int hash(String key, int M)
	{
		return (key.hashCode() & 0x7fffffff) % M;
	}
	
	//easier process for iterating through array
	public Iterator<V> iterator()
	{	
		return new ArrayIterator();
	}
	
	//prints table (DEBUGGING)
	public void showTable()
	{
		System.out.println("Hash Table Shown:");
		for (int i = 0; i < M; i++)
		{
			System.out.print(i + ": ");
			if (table[i] == null)
				System.out.println("null");
			else
				table[i].printData();
		}
	}
	
	//gets key-value pairs from data
	public static class Pair<V>
	{
		public String key;
		public V value;
		
		private Pair(String k, V v)
		{
			key = k;
			value = v;
		}
	}

	//node structure
	private static class Node<V> implements Comparable<Node<V>>
	{
		private Pair<V> data;
		private Node<V> next;
		private Node<V> previous;

		private Node(Pair<V> d)
		{
			data = d;
		}

		// used for next() return
		public String toString() 
		{
			return data.value.toString();
		}

		// used for showTable()
		private void printData()
		{
			System.out.println("Key: " + data.key + " Value: " + data.value);
		}

		@SuppressWarnings("unchecked")
		public int compareTo(Node<V> n)
		{
			V thisData = this.data.value;
			V nData = n.data.value;
			int compare = ((Comparable<V>)thisData).compareTo(nData);
			return compare;
		}
	}

	//iterates through linked list
	private class ArrayIterator implements Iterator<V>
	{
		private Node<V> current;

		public ArrayIterator()
		{
			current = orig;
		}

		public boolean hasNext()
		{
			if (current != null)
				return true;
			return false;
		}

		public V next()
		{
			V val = current.data.value;
			current = current.next;
			return val;
		}
	}
}