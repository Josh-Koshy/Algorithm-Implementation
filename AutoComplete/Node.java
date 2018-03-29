package ac;

public class Node {
	public char c; 
	public Node neighbor = null; 
	public Node child = null; 
	
	
	public Node(char d) {
		this.c = d;
	}
	
	public Node() {
	}
	
	//make next node
	public void setNeighbor(Node n){
		this.neighbor = n;
	}	
	//make down node
	public void setChild(Node d) {
		this.child = d; 
	}
	
	//get the value that's below the specified node
	public Node getSibling(Node d) {
		return child; 
	}
	//get the value of the node of the next
	public Node getNeighbor(Node n) {
		return neighbor;
	}
	//get data of node
	public char getChar() {
		return this.c; 
	}
	
	public void setChar(char d) {
		this.c = d;
	}
}

