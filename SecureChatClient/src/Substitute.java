/* CS 1501 Summer 2017
   Josh Koshy
   Substitute Cipher Method
*/
import java.util.*;

public class Substitute implements SymCipher
{
	byte[] key;
	byte[] revkey;
	
	public Substitute()
	{
		ArrayList<Integer> choices = new ArrayList<Integer>(256);
		Random rand = new Random();
		key = new byte[256];
		int value = -1;
		int in = 0;
		
		for(int i = 0; i < 256 ; i++)		//Sets up possible byte values
			choices.add((Integer)i);
		
		for(int i = 0; i < 256; i++)		//Sets up random byte array using "choices"
		{
			value = rand.nextInt(256) % (256-i);
			in = choices.get(value);
			key[i]= (byte)in;
			choices.remove(value);
		}
	}
	
	public Substitute(byte[] givenAr)
	{
		key = givenAr;
	}
	
	public byte[] getKey()
	{
		return key;
	}
	
	public byte[] encode(String S)
	{
		byte[] s_bytes = S.getBytes();
		byte[] eMessage = new byte[s_bytes.length];
		
		//Encrypts with corresponding value from substitution key
		for(int i = 0; i < s_bytes.length; i++ )
			eMessage[i] = key[s_bytes[i] & 0x000000ff];
		
		System.out.println("\nFor encrypted message...");
		System.out.println("\n\tOriginal String Message: " + S);
		System.out.println("\n\tCorresponding array of bytes: " + s_bytes);
		System.out.println("\n\tEncrypted array of bytes: " + eMessage + "\n");
		
		return eMessage;
	}
	
	public String decode(byte[] bytes)
	{
		byte[] reverse = new byte[key.length];
		byte[] dMessage = new byte[bytes.length];
		
		//Decrypts with corresponding value from substitution key
		for(int i = 0; i < 256; i++)
			reverse[key[i & 0x000000ff] & 0x000000ff] = (byte)i;
		for(int i = 0; i < bytes.length; i++)
			dMessage[i] = reverse[bytes[i & 0x000000ff] & 0x000000ff];
		String msg = new String(dMessage);
		
		System.out.println("\nFor decrypted message...");
		System.out.println("\n\tArray of bytes received: " + bytes);
		System.out.println("\n\tDecrypted array of bytes: " + dMessage);
		System.out.println("\n\tCorresponding string: " + msg + "\n");
		
		return (msg);
	}
}