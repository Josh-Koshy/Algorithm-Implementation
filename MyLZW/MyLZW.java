/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
import java.util.ArrayList;

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       		 // number of codewords = 2^W
    private static int W = 9;         		 // codeword width
    private static String mode = "n";

    private static int uncompData = 0;     //Uncompressed data
    private static int compData = 0;       //Compressed data
    private static float newRatio = 0;
    private static float oldRatio = 0;
    private static boolean monitoring = false;

    public static void compress() 
    {
        if(mode.equals("n")) BinaryStdOut.write(0, 2);  //Nothing mode
        if(mode.equals("m")) BinaryStdOut.write(1, 2);  //Monitoring mode
    	if(mode.equals("r")) BinaryStdOut.write(2, 2);  //Reset mode

        String input = BinaryStdIn.readString();
        TST<Integer> st = newCompressedCodebook();
        int code = R+1;  // R is codeword for EOF

        int uncompData = 0;     //Uncompressed data
        int compData = 0;       //Compressed data
        float newRatio = 0;
        float oldRatio = 0;
        boolean monitoring = false;

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            uncompData += t * 8;
            compData += W;

            if(t < input.length()) {
                if(code >= L) {     //If codebook is full...
                    if (!changeBookSize(W + 1))    //If max size is reached...
                    {
                        boolean reset = false;
                        if (mode.equals("m")) {     //Monitor mode section
                            newRatio = uncompData / (float) compData;   //Set new iteration's ratio
                            if (!monitoring) {      //If first time monitoring...
                                oldRatio = newRatio;    //Set old ratio to compare against
                                monitoring = true;      //Starts monitoring ratios
                            } else if (oldRatio / newRatio > 1.1) {     //If ratio of ratios greater than 1.1 ...
                                reset = true;           //Resets book
                                monitoring = false;     //Stops monitoring ratios
                            }
                        }
                        else if (mode.equals("r")) { // Reset mode section
                            reset = true;   //Resets book
                        }
                        if (reset) {
                            st = newCompressedCodebook();   //Creates fresh codebook
                            code = R + 1;                   //Resets codeword
                            changeBookSize(9);     //Resets codeword width and size of codewords
                        }
                    }
                }
                if(code < L) {
                    st.put(input.substring(0, t + 1), code++);  //Add s to codebook
                }
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }

    public static void expand() 
    {
        int modeInt = BinaryStdIn.readInt(2);
        if(modeInt == 2) mode = "r";    //Nothing mode
        if(modeInt == 1) mode = "m";    //Monitoring mode
        if(modeInt == 0) mode = "n";    //Reset mode

        ArrayList<String> st = new ArrayList();
        for(int i = 0; i < R; i++) {
            st.add("" + (char) i);
        }
        st.add("");    // (unused) lookahead for EOF
        int i = R + 1; // next available codeword value

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;
        String val = st.get(codeword);

        int uncompData = 0;     //Uncompressed data
        int compData = 0;       //Compressed data
        float newRatio = 0;
        float oldRatio = 0;
        boolean monitoring = false;

        while (true) {
            uncompData += val.length() + 8;
            compData += W;

            if(i >= L) {    //If codebook is full...
                if (!changeBookSize(W + 1)) {   //If max size is reached...
                    boolean reset = false;
                    if (mode.equals("m")) {     //Monitor mode section
                        newRatio = uncompData / (float) compData;   //Set new iteration's ratio
                        if (!monitoring) {      //If first time monitoring...
                            oldRatio = newRatio;    //Set old ratio to compare against
                            monitoring = true;      //Starts monitoring ratios
                        } else if (oldRatio / newRatio > 1.1) {
                            reset = true;           //Resets book
                            monitoring = false;     //Stop monitoring ratios
                        }
                    }
                    else if (mode.equals("r")) { //Reset mode section
                        reset = true;   //Resets book
                    }
                    if (reset) {
                        st = newExpandedCodebook();   //Creates fresh codebook
                        i = R + 1;                      //Resets codeword
                        changeBookSize(9);     //Resets codeword width and size of codewords
                    }
                }
            }
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = null;
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            else s = st.get(codeword);     //Gets codeword from book otherwise
            if (i < L){
                st.add(val + s.charAt(0));  //Adds codeword to book
                i++;
            }
            val = s;
        }
        BinaryStdOut.close();
    }

    public static boolean changeBookSize(int size) {
        if (size <= 16) {
            L = (int) Math.pow(2, W);
            W = size;
            return true;
        } else {
            return false;
        }
    }

    public static TST<Integer> newCompressedCodebook() {
        TST<Integer> cleanBook = new TST();
        for (int i = 0; i < R; i++)
            cleanBook.put("" + (char) i, i);
        return cleanBook;
    }

    public static ArrayList<String> newExpandedCodebook() {
        ArrayList<String> book = new ArrayList();
        int i = 0;
        while (i < R) {
            book.add("" + (char) i);
            i++;
        }
        book.add("");	// (unused) lookahead for EOF
        return book;
    }

    public static void main(String[] args) {
        if (args[0].equals("+")) expand();
        else if (args[0].equals("-") && args[1].equals("n"))
        {
            mode = "n";
            compress();  
        } 

        else if (args[0].equals("-") && args[1].equals("r"))
        {
            mode = "r";
            compress();  
        } 
        else if (args[0].equals("-") && args[1].equals("m"))
        {
            mode = "m";
            compress();
        }
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
