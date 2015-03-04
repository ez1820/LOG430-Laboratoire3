package ca.etsmtl.log430.lab3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.File;

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

	String outputFileName;

	// Maximum number of lines of text to be sorted
	int maxBufferSize = 100;

	// Create local pipe that will connect to upstream filter
	PipedReader inputPipe = new PipedReader();

	public FileWriterFilter(String outputFileName, PipedWriter inputPipe) {
		this.outputFileName = outputFileName;

		try {
			// Connect inputPipe to upstream filter
			this.inputPipe.connect(inputPipe);
			System.out.println("FileWriterFilter:: connected to upstream filter.");
		} catch (Exception Error) {
			System.out.println("FileWriterFilter:: Error connecting input pipe.");
		} // catch

	} // Constructor

	// This is the method that is called when the thread is started in
	// SystemInitialize

	public void run() {
		// Declarations

		boolean done; // Flags for reading from pipe
		String directory; // Part of output file name
		File fileObject; // Output file object
		File directoryObject; // Output directory object
		BufferedWriter bout = null; // Output file buffer writer object

		// Open and/or create the output file

		fileObject = new File(outputFileName);
		outputFileName = fileObject.getName();

		directory = fileObject.getAbsolutePath();
		directory = directory.substring(0,
				(directory.length() - outputFileName.length()));
		directoryObject = new File(directory);

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

		// Indicate that file exists and will be overwritten
		if (fileObject.exists()) {
			System.out.println("FileWriterFilter:: overwriting file "
					+ fileObject.getAbsolutePath() + ".");
		}

		// Create a buffered writer
		try {
			bout = new BufferedWriter(new FileWriter(fileObject));
		} catch (IOException IOError) {
			System.out.println("FileWriterFilter:: Buffered Writer Creation Error.");
		} // try/catch

		// Create a temporary String array of a big size (for sorting)
		String[] tmpArray = new String[maxBufferSize];
		int count = 0;
		int i;

		// Begin process data from the input pipes
		try {
			// Declarations

			// Needs to be an array for easy conversion to string
			char[] characterValue1 = new char[1];

			done = false; // Indicates when you are done
			// reading on pipe #1
			int integerCharacter1; // integer value read from pipe
			String lineOfText1 = ""; // line of text from inputpipe #1
			boolean write1 = false; // line of text to write to output
			// pipe #1

			// SystemInitialize loop for reading data

			while (!done) {
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
									+ lineOfText1 + " on input pipe.");
							write1 = true;
						} else {
							lineOfText1 += new String(characterValue1);
						} // if
					} // if ( IntegerCharacter1 == -1 )
				} // if (!Done1)

				if (write1) {
					// Add LineOfText1 to temporary string array,
					// increment arrayindex and reset Write1 to false.
					write1 = false;
					tmpArray[count] = lineOfText1;
					++count;
					lineOfText1 = "";
				} // if

			} // while (!Done1)

		} // try

		catch (Exception error) {
			System.out.println("FileWriterFilter:: Interrupted.");
		} // catch

		// At this point, we have all lines of text in tmpArray.

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

		// Close the output file
		try {
			System.out.println("FileWriterFilter:: Closing output file "
					+ fileObject.getAbsolutePath() + ".");
			bout.close();
		} catch (Exception Error) {
			System.out.println("FileWriterFilter:: Error closing output file.");
		} // try/catch

	} // run

} // class
