package SystemB;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**************************************************************************
 * This class is intended to be a filter that will collect the
 * input from its input pipe and write it to an output file.<br><br>
 *
 * Pseudo Code:
 * <pre>
 * connect to upstream filter for input
 * open output file
 * while not done
 *		read char from input pipe
 *		string = string + char
 *		write string to string array 
 * end while
 * close pipes
 * write string array to file
 *</pre>
 *
 * @author R. Champagne
 * @version 1.1
 */

public class FileWriterFilter extends Thread {
	// Declarations

	String outputFileName, outputFileName2;

	// Maximum number of lines of text to be sorted
	int maxBufferSize = 100;

	// Create local pipe that will connect to upstream filter
	PipedReader inputPipe = new PipedReader();
	PipedReader inputPipe2 = new PipedReader();

	public FileWriterFilter(String outputFileName, String outputFileName2, PipedWriter inputPipe, PipedWriter pipe09) {
		this.outputFileName = outputFileName;
		this.outputFileName2 = outputFileName2;
		
		try {
			// Connect inputPipe to upstream filter
			this.inputPipe.connect(inputPipe);
			this.inputPipe2.connect(pipe09);
			System.out.println("FileWriterFilter:: connected to upstream filters.");
		} catch (Exception Error) {
			System.out.println("FileWriterFilter:: Error connecting input pipes.");
		} // catch

	} // Constructor

	// This is the method that is called when the thread is started in
	// SystemInitialize

	public void run() {
		// Declarations

		boolean done, done2; // Flags for reading from pipe
		String directory, directory2; // Part of output file name
		File fileObject, fileObject2; // Output file object
		File directoryObject, directoryObject2; // Output directory object
		BufferedWriter bout = null; // Output file buffer writer object
		BufferedWriter bout2 = null; // Output file buffer writer object

		// Open and/or create the output file

		fileObject = new File(outputFileName);
		outputFileName = fileObject.getName();
		fileObject2 = new File(outputFileName2);
		outputFileName2 = fileObject2.getName();

		directory = fileObject.getAbsolutePath();
		directory = directory.substring(0,
				(directory.length() - outputFileName.length()));
		directoryObject = new File(directory);

		directory2 = fileObject2.getAbsolutePath();
		directory2 = directory2.substring(0,
				(directory2.length() - outputFileName2.length()));
		directoryObject2 = new File(directory2);

		// Create directory if it doesn't exist
		if (!directoryObject.exists()) {
			try {
				directoryObject.mkdirs();
				System.out.println("FileWriterFilter:: Created directory: "
						+ directory + ".");
			} catch (SecurityException Error) {
				System.out.println("FileWriterFilter:: Unable to create directory: "
						+ directory + ".");
			} // try/catch
		} // if

		// Create directory if it doesn't exist
		if (!directoryObject2.exists()) {
			try {
				directoryObject2.mkdirs();
				System.out.println("FileWriterFilter:: Created directory: "
						+ directory2 + ".");
			} catch (SecurityException Error) {
				System.out.println("FileWriterFilter:: Unable to create directory: "
						+ directory2 + ".");
			} // try/catch
		} // if

		// Indicate that file exists and will be overwritten
		if (fileObject.exists()) {
			System.out.println("FileWriterFilter:: overwriting file "
					+ fileObject.getAbsolutePath() + ".");
		}

		// Indicate that file exists and will be overwritten
		if (fileObject2.exists()) {
			System.out.println("FileWriterFilter:: overwriting file "
					+ fileObject2.getAbsolutePath() + ".");
		}

		// Create a buffered writer
		try {
			bout = new BufferedWriter(new FileWriter(fileObject));
		} catch (IOException IOError) {
			System.out.println("FileWriterFilter:: Buffered Writer Creation Error.");
		} // try/catch

		// Create a buffered writer
		try {
			bout2 = new BufferedWriter(new FileWriter(fileObject2));
		} catch (IOException IOError) {
			System.out.println("FileWriterFilter:: Buffered Writer 2 Creation Error.");
		} // try/catch

		// Create a temporary String array of a big size (for sorting)
		String[] tmpArray = new String[maxBufferSize];
		String[] tmpArray2 = new String[maxBufferSize];
		int count = 0;
		int count2 = 0;
		int i;

		// Begin process data from the input pipes
		try {
			// Declarations

			// Needs to be an array for easy conversion to string
			char[] characterValue1 = new char[1];
			char[] characterValue2 = new char[1];

			done = false; // Indicates when you are done
			done2 = false;
			// reading on pipe #1
			int integerCharacter1; // integer value read from pipe
			String lineOfText1 = ""; // line of text from inputpipe #1
			int integerCharacter2; // integer value read from pipe
			String lineOfText2 = ""; // line of text from inputpipe #1
			String formattedText = "";
			String placeHolder = "";
			boolean write1 = false; // line of text to write to output
			boolean write2 = false; // line of text to write to output
			// pipe #1

			// SystemInitialize loop for reading data

			while (!done || !done2) {
				// Read pipe #1
				if (!done) {
					integerCharacter1 = inputPipe.read();
					characterValue1[0] = (char) integerCharacter1;

					if (integerCharacter1 == -1) // pipe #1 is closed
					{
						done = true;
						System.out.println("FileWriterFilter:: input pipe closed.");
						try {
							inputPipe.close();
						} catch (Exception Error) {
							System.out
									.println("FileWriterFilter:: Error closing input pipe.");
						} // try/catch
					} else {
						if (integerCharacter1 == '\n') // end of line
						{
							System.out.println("FileWriterFilter:: Received: "
									+ lineOfText1 + " on input pipe 1.");
							write1 = true;
						} else {
							lineOfText1 += new String(characterValue1);
						} // if
					} // if ( IntegerCharacter1 == -1 )
				} // if (!Done1)
				
				// Read pipe #2
				if (!done2) {
					integerCharacter2 = inputPipe2.read();
					characterValue2[0] = (char) integerCharacter2;

					if (integerCharacter2 == -1) // pipe #1 is closed
					{
						done2 = true;
						System.out.println("FileWriterFilter:: input pipe closed.");
						try {
							inputPipe.close();
						} catch (Exception Error) {
							System.out
									.println("FileWriterFilter:: Error closing input pipe.");
						} // try/catch
					} else {
						if (integerCharacter2 == '\n') // end of line
						{
							System.out.println("FileWriterFilter:: Received: "
									+ lineOfText2 + " on input pipe 2.");
							write2 = true;
						} else {
							lineOfText2 += new String(characterValue2);
						} // if
					} // if ( IntegerCharacter2 == -1 )
				} // if (!Done2)

				if (write1) {
					// Add LineOfText1 to temporary string array,
					// increment arrayindex and reset Write1 to false.
					formattedText = "";
					write1 = false;
					formattedText = lineOfText1.substring(5, 8);
					formattedText += " ";
					formattedText += lineOfText1.substring(12, 15);
					formattedText += " ";
					formattedText += lineOfText1.substring(9, 11);
					formattedText += " ";
					formattedText += lineOfText1.substring(0, 4);
					
					tmpArray[count] = formattedText;
					++count;
					lineOfText1 = "";
					formattedText = "";
				} // if

				if (write2) {
					// Add LineOfText1 to temporary string array,
					// increment arrayindex and reset Write1 to false.
					formattedText = "";
					write2 = false;
					formattedText = lineOfText2.substring(5, 8);
					formattedText += " ";
					formattedText += lineOfText2.substring(12, 15);
					formattedText += " ";
					formattedText += lineOfText2.substring(9, 11);
					formattedText += " ";
					formattedText += lineOfText2.substring(0, 4);
					
					tmpArray2[count2] = formattedText;
					++count2;
					lineOfText2 = "";
					formattedText = "";
				} // if

			} // while (!Done1 || !Done2)

		} // try		
		catch (Exception error) {
			System.out.println("FileWriterFilter:: Interrupted.");
		} // catch
		
		// At this point, we have all lines of text in tmpArray.
		// Now we sort the array alphabetically by status
		if(tmpArray.length > 1){
			Arrays.sort(tmpArray, new Comparator<String>() {
		        @Override
		        public int compare(String o1, String o2) {
		            if (o1 == null && o2 == null) {
		                return 0;
		            }
		            if (o1 == null) {
		                return 1;
		            }
		            if (o2 == null) {
		                return -1;
		            }
		            return o1.compareTo(o2);
		        }});
		}
		
		if(tmpArray2.length > 1){
			Arrays.sort(tmpArray2, new Comparator<String>() {
		        @Override
		        public int compare(String o1, String o2) {
		            if (o1 == null && o2 == null) {
		                return 0;
		            }
		            if (o1 == null) {
		                return 1;
		            }
		            if (o2 == null) {
		                return -1;
		            }
		            return o1.compareTo(o2);
		        }});
		}
		
		// Write the string array to output file
		try {
			for (i = 0; i < count; i++) {
				System.out.println("FileWriterFilter:: Writing: " + tmpArray[i]);
				bout.write(tmpArray[i]);

				// Add a platform independent newline
				bout.newLine();
			} // for
		} catch (Exception IOError) {
			System.out.println("FileWriterFilter:: Write Error.");
		} // try/catch
		
		// Write the string array to output file
		try {
			for (i = 0; i < count2; i++) {
				System.out.println("FileWriterFilter:: Writing: " + tmpArray2[i]);
				bout2.write(tmpArray2[i]);

				// Add a platform independent newline
				bout2.newLine();
			} // for
		} catch (Exception IOError) {
			System.out.println("FileWriterFilter:: Write Error.");
		} // try/catch

		// Close the output file
		try {
			System.out.println("FileWriterFilter:: Closing output file "
					+ fileObject.getAbsolutePath() + ".");
			bout.close();
		} catch (Exception Error) {
			System.out.println("FileWriterFilter:: Error closing output file.");
		} // try/catch

		// Close the output file
		try {
			System.out.println("FileWriterFilter:: Closing output file "
					+ fileObject2.getAbsolutePath() + ".");
			bout2.close();
		} catch (Exception Error) {
			System.out.println("FileWriterFilter:: Error closing output file.");
		} // try/catch

	} // run

} // class
