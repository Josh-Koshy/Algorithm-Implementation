//Josh Koshy
//CS 1501 Summer
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class AdjList 
{
	int vMax, eMax;
    LinkedList<WeightedEdge>[] adj;
    ArrayList<Integer> tracker;
    ArrayList<ArrayList> collections;
	boolean[] status;	//Shows whether vertex is Up(true) and Down(false)
	boolean[] marked;
	boolean connected;
	
    public AdjList(int vNum, int eNum)
    {
        vMax = vNum;
        eMax = eNum;
        connected = true;
        adj = new LinkedList[vNum];
        status = new boolean[vNum];
        marked = new boolean[vNum];
        tracker = new ArrayList();
        collections = new ArrayList();
        
		for(int i = 0; i < vNum; i++)
		{
			adj[i] = new LinkedList<WeightedEdge>();
			status[i] = true;
			marked[i] = false;
		}
    }
   
    
    public void addEdge(int v1, int v2, int weight)
    {
    	adj[v1].add(new WeightedEdge(v2, weight));
    	adj[v2].add(new WeightedEdge(v1, weight));
    }
    
    public ArrayList<Integer> allNeighbors(int v)			//finds all neighbors of v
    {
    	ArrayList<Integer> neighbors = new ArrayList<Integer>();
    	for(int i = 0; i < adj[v].size(); i++)
    		neighbors.add(adj[v].get(i).v2);
    	return neighbors;
    }
    
    public void tickDown(int v)
    {
    	status[v] = false;
    	System.out.println("------------------------");
    	System.out.println("Command D " + v + ":");
    	System.out.println("------------------------");
    	System.out.println("Vertex " + v + " has gone down");
    }
    
    public void tickUp(int v)
    {
    	status[v] = true;
    	System.out.println("------------------------");
    	System.out.println("Command U " + v + ":");
    	System.out.println("------------------------");
    	System.out.println("Vertex " + v + " has gone up");
    }
    
    public void resultReport()
    {
    	System.out.println("------------------------");
    	System.out.println("Command R:");
    	System.out.println("------------------------");
    	
    	
    	collections.clear();
    	tracker.clear();
    	
    	//Checks for connection through dfs
    	cleanMarked();
    	int d = 0;
    	for(int i = 0; i < vMax; i++)
    	{
    		if(status[i])
    		{
    			d = i;
    			break;
    		}
    	}
    	tracker.add(d);
    	dfs(d, true);
    	collections.add(new ArrayList());
    	for(int t = 0; t < tracker.size(); t++)
    		collections.get(collections.size() - 1).add(tracker.get(t));
    	
    	//Looks at result of dfs
    	for(int j = 0; j < vMax; j++)
    	{
    		if(!marked[j] && status[j])
    			connected = false;
    	}
    	
    	if(connected)
    		System.out.println("The network is currently connected\n");
    	else
    	{
    		System.out.println("The network is currently disconnected\n");
    		findCollections();
    	}
    	
    	System.out.println("The following nodes are currently up:");
    	
    	for(int k = 0; k < vMax; k++)
    	{
    		if(status[k])
    			System.out.print(k + " ");
    	}
    	
    	System.out.println("\n\nThe following nodes are currently down:");
    	
    	for(int l = 0; l < vMax; l++)
    	{
    		if(!status[l])
    			System.out.print(l + " ");
    	}
    	
    	System.out.println("\n\nThe connected components are:");
    	
    	for(int m = 0; m < collections.size(); m++)				//For each component
    	{
    		System.out.println("\nComponent " + m + ":");
    		for(int n = 0; n < collections.get(m).size(); n++)			//For each vertex
    		{
    			System.out.print(collections.get(m).get(n) + ": ");
    			for(int o = 0; o < adj[(int)collections.get(m).get(n)].size(); o++)			//For each weighted edge, print statements
    			{
    				if(status[adj[(int)collections.get(m).get(n)].get(o).v2])
    					System.out.print(collections.get(m).get(n) + "-" + adj[(int)collections.get(m).get(n)].get(o).v2 + " " + adj[(int)collections.get(m).get(n)].get(o).weight + "   ");
    			}
    			System.out.println("");
    		}
    	}	
    }

    public void mst()
    {
    	System.out.println("------------------------");
    	System.out.println("Command M:");
    	System.out.println("------------------------");
    	collections.clear();
    	tracker.clear();
    	
    	//Loads vertex's into their respective components
    	cleanMarked();
    	int d = 0;
    	for(int i = 0; i < vMax; i++)
    	{
    		if(status[i])
    		{
    			d = i;
    			break;
    		}
    	}
    	tracker.add(d);
    	dfs(d, true);
    	collections.add(new ArrayList());
    	for(int t = 0; t < tracker.size(); t++)
    		collections.get(collections.size() - 1).add(tracker.get(t));
    	
    	for(int j = 0; j < vMax; j++)
    	{
    		if(!marked[j] && status[j])
    			connected = false;
    	}
    	
    	if(!connected)
    		findCollections();
    	
    	
    	int choice = 0;
    	ArrayList mstSet = new ArrayList();
    	ArrayList mstStrings = new ArrayList();
    	
    	//For each collection
    	for(int k = 0; k < collections.size(); k++)
    	{
    		mstSet.clear();
    		System.out.println("The edges in the MST follow:");
    		mstSet.add(collections.get(k).get(0));
    		
    		//Sets up first vertex
    		if(adj[(int)mstSet.get(0)].size() > 0)
	    		for(int l = 0; l < adj[(int)mstSet.get(0)].size(); l++)			//loops through all edge weights of first vertex
	    		{
	    			if(l == 0)
	    				choice = l;
	    			else
	    				if(adj[(int)mstSet.get(0)].get(choice).weight > adj[(int)mstSet.get(0)].get(l).weight)
	    					choice = l;
	    		}
    		mstSet.add(adj[(int)collections.get(k).get(0)].get(choice).v2);			//adds result
    		mstStrings.add("" + collections.get(k).get(0) + "-" + adj[(int)mstSet.get(mstSet.size()-2)].get(choice).v2 + " " + adj[(int)mstSet.get(mstSet.size()-2)].get(choice).weight);		//adds result to string
    		
    		if(adj[(int)mstSet.get(mstSet.size()-1)].size() > 0)
    		{
	    		choice = 0;
	    		boolean skip = false;
	    		for(int m = 2; m < vMax; m++)		//for every other vertex
	    		{
	    			for(int n = 0; n < adj[(int)mstSet.get(mstSet.size()-1)].size(); n++)			//loops through all edge weights of first vertex end of current tree to find smallest
		    		{
	    				for(int z = 0; z < mstSet.size(); z++)
	    					if(adj[(int)mstSet.get(mstSet.size()-1)].get(n) == mstSet.get(z))
		    					skip = true;
	    				if(!skip)
		    			{
	    					if(n == 0)
	    						choice = n;
			    			else
			    				if(adj[(int)mstSet.get(mstSet.size()-1)].get(choice).weight > adj[(int)mstSet.get(mstSet.size()-1)].get(n).weight)
			    				{
			    					choice = n;
			    				}
		    			}
	    				skip = false;
		    		}
	    			int v = 1;
	    			for(int o = 0; o < adj[(int)mstSet.get(mstSet.size()-2)].size(); o++)			//loops through all edge weights of second vertex end of current tree to find smallest (compares against the first vertex end too)
		    		{
	    				for(int z = 0; z < mstSet.size(); z++)
	    					if(adj[(int)mstSet.get(mstSet.size()-2)].get(o) == mstSet.get(z))
		    					skip = true;
	    				if(!skip)
	    				{
			    			if(adj[(int)mstSet.get(mstSet.size()-v)].get(choice).weight > adj[(int)mstSet.get(mstSet.size()-2)].get(o).weight)
			    			{
			    				choice = o;
			    				if(v == 1)
			    					v++;
			    			}
	    				}
	    				skip = false;
		    		}
	    			mstSet.add(adj[(int)mstSet.get(mstSet.size()-v)].get(choice).v2);		//adds result
	    			mstStrings.add("" + mstSet.get(mstSet.size()-2) + "-" + adj[(int)mstSet.get(mstSet.size()-2)].get(choice).v2 + " " + adj[(int)mstSet.get(mstSet.size()-2)].get(choice).weight);			//adds result to string
	    		}
    		}
    		
    		for(int i = 0; i < mstStrings.size(); i++)
    			System.out.println(mstStrings.get(i));
    			
    	}
    }
    
    public void dijkstra(int v1, int v2)
    {
    	System.out.println("------------------------");
    	System.out.println("Command S:");
    	System.out.println("------------------------");
    	IndexMinPQ pq = new IndexMinPQ(vMax);
    	int sum = 0;
    	
    	int index = 0, source = 0;
    	for(int i = 0; i < adj.length; i++)
    		if(i == v1)
    		{
    			pq.insert(i, adj[i].get(0).weight);
    			source = i;
    		}
    		else
    			pq.insert(i, (double)Integer.MAX_VALUE);
    	
    	
    	double weight = 0;
    	ArrayList<Integer> path = new ArrayList<Integer>();
    	while(!pq.isEmpty() && index != v2)
    	{
    		index = pq.delMin();
    		for(int i = 0; i < adj[index].size(); i++)
    		{
    			if(pq.contains(adj[index].get(i).v2))
    			{
    				for(int d = 0; d < adj[adj[index].get(i).v2].size(); d++)
    				{
    					if(d == 0)
    						weight = adj[adj[index].get(i).v2].get(d).weight;
    					else if(weight > adj[adj[index].get(i).v2].get(d).weight)
    						weight += adj[adj[index].get(i).v2].get(d).weight;
    				}
    				pq.change(adj[index].get(i).v2, adj[index].get(i).weight + weight);
    			}
    		}
    		path.add(index);
    		
    	}
    	
    	String answer = "";
    	for(int j = 0; j < path.size() - 1; j++)
    	{
    		answer += path.get(j) + "->" + path.get(j + 1) + "  ";
    		for(int k = 0; k < adj[path.get(j)].size(); k++)
    			if(adj[path.get(j)].get(k).v2 == path.get(j+1))
    			{
    				answer += adj[path.get(j)].get(k).weight + " | ";
    				sum += adj[path.get(j)].get(k).weight;
    			}
    	}
    	
    	System.out.print("Shortest Path from " + v1 + " to " + v2 + " (" + sum + ")   " + answer);
    }
    
    private void dfs(int v, boolean track)		//assumes that with very first call of dfs[v], status[v] = true 
    {    	
        marked[v] = true;
        for (int w : allNeighbors(v))
        {
        	if(status[w] && !marked[w])
        	{
        		if(track)
        			tracker.add(w);
        		dfs(w, track);
        	}
        }
    }
    
    private void cleanMarked()
    {
    	for(int i = 0; i < vMax; i++)
    	{
    		marked[i] = false;
    	}
    }
    
    private void findCollections()
    {
    	ArrayList<Integer> unmarked = new ArrayList();
    	
    	while(true)
    	{
    		unmarked.clear();
    		tracker.clear();
    		
    		for(int i = 0; i < vMax; i++)
    		{
    			if(!marked[i] && status[i])
    				unmarked.add(i);
    		}
    		tracker.add(unmarked.get(0));
    		dfs(unmarked.get(0), true);
    		collections.add(new ArrayList());
    		for(int t = 0; t < tracker.size(); t++)
        		collections.get(collections.size() - 1).add(tracker.get(t));
    		if(tracker.size() == unmarked.size())
    			break;
    	}
    }

}
