/* CS 1501
   Josh Koshy
   Improved chat client.
*/
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener
{
    public static final int PORT = 8765;	//port listed in directions

    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;
	
	SymCipher cipher;

    public SecureChatClient() 
    {
        try 
        {
        	myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        	
	        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
	        InetAddress addr =
	                InetAddress.getByName(serverName);
	        connection = new Socket(addr, PORT);   // Connect to server with new
	                                               // Socket
			
	        myReader= new ObjectInputStream(connection.getInputStream());
			myWriter = new ObjectOutputStream(connection.getOutputStream());
			myWriter.flush();
			
	        BigInteger E = (BigInteger) myReader.readObject();
			BigInteger N = (BigInteger) myReader.readObject();
			String method = (String) myReader.readObject();
			
			if (method.equals("Add"))
			{
				System.out.println("Type of encryption: Add128");
				cipher = new Add128();
			}
			else
			{
				System.out.println("Type of encryption: Substitute");
				cipher = new Substitute();
			}
			
			BigInteger key = new BigInteger(1, cipher.getKey());
			BigInteger encryptKey = key.modPow(E, N);
			myWriter.writeObject(encryptKey);	// Sending server the encryption key
			myWriter.flush();
			byte[] encName = cipher.encode(myName);
			myWriter.writeObject(encName);	// Send name to Server. Server will need
			myWriter.flush();				// this to announce sign-on and sign-off
											// of clients
			
	        this.setTitle(myName);      // Set title to identify chatter
	        
	        Box b = Box.createHorizontalBox();  // Set up graphical environment for
	        outputArea = new JTextArea(8, 30);  // user
	        outputArea.setEditable(false);
	        b.add(new JScrollPane(outputArea));
	
	        outputArea.append("Welcome to the Chat Group, " + myName + "\n");
	
	        inputField = new JTextField("");  // This is where user will type input
	        inputField.addActionListener(this);
	
	        prompt = new JLabel("Type your messages below:");
	        Container c = getContentPane();
	
	        c.add(b, BorderLayout.NORTH);
	        c.add(prompt, BorderLayout.CENTER);
	        c.add(inputField, BorderLayout.SOUTH);
	
	        Thread outputThread = new Thread(this);  // Thread is to receive strings
	        outputThread.start();                    // from Server
	
			addWindowListener(
	                new WindowAdapter()
	                {
	                    public void windowClosing(WindowEvent e)
	                    { 
							
							try
							{
								String close = new String("CLIENT CLOSING");
								myWriter.writeObject( cipher.encode(close) );
								myWriter.flush();
								System.exit(0);
							}
							catch( IOException b){}
	                     }
	                }
	            );
	
	        setSize(500, 200);
	        setVisible(true);

        }
		
        catch (Exception e)
        {
            System.out.println("Client not starting. Please check server name and restart.");
        }
    }

    public void run() 
    {
        while (true)
        {
             try
             {	
				byte[] byteMessage = (byte[]) myReader.readObject();
				String currMsg = cipher.decode(byteMessage);
			    outputArea.append(currMsg+"\n");
             }
             catch (Exception e)
             {
                System.out.println("Problem running decoder in run().");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) 
    {
		try		// Was enforced to try-catch due to use of writeObject
		{		
			String currMsg = e.getActionCommand();      // Get input value
			inputField.setText("");
			myWriter.writeObject(cipher.encode(myName + ":  " + currMsg));		// Add name and send it
		}																	// to Server
		catch (IOException a)
		{
			System.out.println("Problem with encoding name and writing it.");
		}
    }                                               

    public static void main(String [] args) throws IOException
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}