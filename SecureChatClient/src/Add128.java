/* CS 1501 Summer 2017
   Josh Koshy
   Add128 Cipher Method
*/
import java.util.*;

public class Add128 implements SymCipher
{
	byte[] key;
	
	public Add128()
	{
		Random rand =  new Random();
		
		//Setting up random 128 byte additive key
		key = new byte[128];
		for( int i = 0; i < 128; i++)
			key[i] = (byte)rand.nextInt(128);
	}
	
	public Add128(byte [] givenAr)
	{
		key = givenAr;	//Uses given array instead
	}
	
	public byte[] getKey()
	{
		return key;
	}
	
	public byte[] encode(String S)
	{	
		byte[] s_bytes = S.getBytes();	
		byte[] eMessage = new byte[s_bytes.length];
		
		//Encrypts using random additive key array
		int value = 0;
		for(int i = 0; i < s_bytes.length; i++)
		{
			eMessage[i] = (byte)(s_bytes[i] + key[value]);
			value = (value + 1) % 128;
		}
		
		System.out.println("\nFor encrypted message...");
		System.out.println("\n\tOriginal String Message: " + S);
		System.out.println("\n\tCorresponding array of bytes: " + s_bytes);
		System.out.println("\n\tEncrypted array of bytes: " + eMessage + "\n");
		
		return eMessage;
	}
	
	public String decode(byte[] bytes)
	{
		byte[] d_bytes = new byte[bytes.length];
		
		//Decrypt using random additive key array
		int value = 0;
		for( int i = 0; i < bytes.length ; i++ )
		{
			d_bytes[i] = (byte)(bytes[i] - key[value]);
			value = (value + 1) % 128;
		}
		String dMessage = new String(d_bytes);
		
		System.out.println("\nFor decrypted message...");
		System.out.println("\n\tArray of bytes received: " + bytes);
		System.out.println("\n\tDecrypted array of bytes: " + d_bytes);
		System.out.println("\n\tCorresponding string: " + dMessage + "\n");
		
		return dMessage;
	}
}