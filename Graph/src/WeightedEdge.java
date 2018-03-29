//Josh Koshy
//CS 1501 Summer
public class WeightedEdge
{
	public int v2;
	public double weight;
	
	public WeightedEdge()
	{
	}
	
	public WeightedEdge(int v, int w)
	{
		v2 = v;
		weight = w;
	}
	
	public int getDest()
	{
		return v2;
	}
	
	public double getWeight()
	{
		return weight;
	}
	
	public void setWeight(int w)
	{
		weight = w;
	}
	/*
	public int compareTo(WeightedEdge we)
	{
		return Double.compare(weight, we.weight);
	}*/
	
	/*public String toString()
	{
		String tempString = "<" + v2 + ":" + weight + ">";
		
		return tempString;
	}*/
}