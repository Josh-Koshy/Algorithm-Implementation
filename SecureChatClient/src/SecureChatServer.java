/* CS 1501 Summer 2017
   Josh Koshy
   Improved Chat Server (now with encryption!)
*/
import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;

public class SecureChatServer
{
    public static final int PORT = 8765;

    private int MaxUsers;
    private Socket [] users;         // Need array of Sockets and Threads,
    private UserThread [] threads;   // one of each per client -- using an
    private int numUsers;            // ArrayList for these would make less work
                                     // for the programmer
    
	private String e_string, d_string, n_string;
	private BigInteger E, D, N;
	private Random R;

    public SecureChatServer(int MaxU) throws IOException
    {
        MaxUsers = MaxU;
        users = new Socket[MaxUsers];
        threads = new UserThread[MaxUsers];   // Set things up and start
        numUsers = 0;                         // Server running

		Scanner scan = new Scanner(new File("keys.txt"));
		e_string = scan.nextLine();
		d_string = scan.nextLine();
		n_string = scan.nextLine();
		E = new BigInteger(e_string);
		D = new BigInteger(d_string);
		N = new BigInteger(n_string);
		R = new Random();
		
		System.out.println("E: " + E);
		System.out.println("D: " + D);
		System.out.println("N: " + N);
		
        try
        {
            runServer();
        }
        catch (Exception e)
        {
           System.out.println("Problem with server");
        }
    }

    public synchronized void SendMsg(String msg)
			// Send current message to all clients (even sender).  This
            // must be synchronized so that chatters do not "interrupt"
            // each other.  For each chatter, get the cipher, then use
            // it to encode the message, and send the result.
                        
    {
        for (int i = 0; i < numUsers; i++)
        {
			System.out.println("For user " + i + "\n");
            ObjectOutputStream writer = threads[i].getWriter();
			SymCipher cipher = threads[i].getCipher();
			byte[] byteMessage = cipher.encode(msg);
			try
			{
				writer.writeObject(byteMessage);
			}
			catch (IOException e)
			{
				System.out.println("Problem writing message.");
			}
        }
    }

    public synchronized void removeClient(int id, String name)
    {
        try                          // Remove a client from the server.  This
        {                            // also must be synchronized, since we
            users[id].close();       // could have an inconsistent state if this
        }                            // is interrupted in the middle
        catch (IOException e)
        {
            System.out.println("Already closed");
        }
        users[id] = null;
        threads[id] = null;
        for (int i = id; i < numUsers-1; i++)   // Shift remaining clients in
        {                                       // array up one position
            users[i] = users[i+1];
            threads[i] = threads[i+1];
            threads[i].setId(i);
        }
        numUsers--;
        SendMsg(name + " has logged off");    // Announce departure
    }

    private void runServer() throws IOException
    {
	    ServerSocket s = new ServerSocket(PORT);
		System.out.println("Started: " + s);
	    Socket newSocket = null;
	    
	    SymCipher cipher = null;
	    
		try
		{
			while (true)
			{
				if (numUsers < MaxUsers)
				{
					try
					{
						newSocket = s.accept();    // get next client
						ObjectOutputStream tempReader =
								  new ObjectOutputStream(newSocket.getOutputStream());
						tempReader.flush();
						ObjectInputStream tempWriter =
								  new ObjectInputStream(newSocket.getInputStream());
						
						tempReader.writeObject(E); tempReader.flush();
						tempReader.writeObject(N); tempReader.flush();
						
						// Selecting encryption method
						double coinFlip = R.nextDouble();
						String encryptMethod = null;
						if (coinFlip > 0.5)
							encryptMethod = new String("Add");	
						else
							encryptMethod = new String("Sub");
						tempReader.writeObject(encryptMethod);
						tempReader.flush();
						
						// Printing key selection
						BigInteger key = (BigInteger) tempWriter.readObject();
						System.out.println("Encrypted key: " + key);
						key = key.modPow(D, N);
						System.out.println("Decrypted key: " + key);
						byte[] byteKey = key.toByteArray();
						System.out.println("Byte array length: " + byteKey.length);
						
						// Printing cipher selection
						if (encryptMethod.equals("Add"))
						{
							if(byteKey.length > 128)
							{
								byte[] newKey = new byte[128];
								System.arraycopy(byteKey, 1, newKey, 0, 128);
								byteKey = newKey;
							}
							System.out.println("Server has chosen the Add128 cipher method.");
							cipher = new Add128(byteKey);
						}
						else if (encryptMethod.equals("Sub"))
						{
							if(byteKey.length > 256)
							{
								byte [] temp = new byte[256];
								System.arraycopy(byteKey, 1, temp, 0, 256);
								byteKey = temp;
							}
							System.out.println("Server has chosen Substitution cipher method.");
							cipher = new Substitute(byteKey);
						}
						
						// Printing key
						System.out.print("\nKey: ");
						for (int i = 0; i < byteKey.length; i++)
							System.out.print(byteKey[i] + " ");
						System.out.println();
						
						// Receive client name
						byte[] name_encrypt = (byte []) tempWriter.readObject();
						String newName = cipher.decode(name_encrypt);
						
						newSocket.setSoTimeout(0);
						synchronized (this)
						{
							users[numUsers] = newSocket;
							SendMsg(newName + " joined the chat group");
							// Above the server gets the new chatter's and announces
							// to the rest of the group
	
							threads[numUsers] = new UserThread(newSocket, numUsers,
														newName, tempWriter, tempReader, cipher);
							threads[numUsers].start();
							// Above a new thread is created and started for the
							// new user
	
							System.out.println("Connection: " + numUsers + users[numUsers]);
							numUsers++;
						}
					}
					catch (java.net.SocketTimeoutException e)
					{
						System.out.println("Problem logging in." + e);
						newSocket.close();
					}
					catch (Exception e)
					{
						System.out.println("Problem with connection...terminating");
					}
				}  // if
				else
				{
					Thread.sleep(1000);
				}
	
			}  // while
		}   // try
		catch (Exception e)
		{
			System.out.println("Problem with running. " + e);
		}
	    finally 
	    {
	        System.out.println("Server shutting down");
	
	    }
    }  //  end of runServer method

    // Below is the class used by the server to keep track of the clients.  Each
    // client is a new UserThread object, with the data shown.

    private class UserThread extends Thread
    {
		 private Socket mySocket;
         private ObjectInputStream myReader;
         private ObjectOutputStream myWriter;
         private SymCipher myCipher;
         private int myId;
         private String myName;

         private UserThread(Socket newSocket, int id, String newName,
                            ObjectInputStream newReader,
							ObjectOutputStream newWriter, SymCipher c) throws IOException
         {
              mySocket = newSocket;
              myId = id;
              myName = newName;
              myReader = newReader;
			  myWriter = newWriter;
			  myCipher = c;
         }

         public ObjectInputStream getReader()
         {
              return myReader;
         }

         public ObjectOutputStream getWriter()
         {
              return myWriter;
         }

		 public SymCipher getCipher()
		 {
			 return myCipher;
		 }

         public synchronized void setId(int newId)
         {
              myId = newId;   // id may change when a previous chatter quits
         }

         // While running, each UserThread will get the next message from its
         // corresponding client, and then send it to the other clients (through
         // the Server).  A departing client is detected by an IOException in
         // trying to read, which causes the removeClient method to be executed.

         public void run()
         {
              boolean ok = true;
              while (ok)
              {
            	  	byte[] readBytes = null;
                    String newMsg = null;
					
                    try {
                         readBytes = (byte []) myReader.readObject(); 
						 newMsg = myCipher.decode(readBytes);
						 if (readBytes == null || newMsg.equals("CLIENT CLOSING"))
							 	ok = false;
						 else
                             	SecureChatServer.this.SendMsg(newMsg);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Client closing!!" + e);
                        ok = false;
                    }
              }
              removeClient(myId, myName);
         }
    }

    public static void main(String [] args) throws IOException
    {
         SecureChatServer JR = new SecureChatServer(20);
    }
}


