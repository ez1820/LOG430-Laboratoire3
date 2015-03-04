package ca.etsmtl.log430.lab3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PipedWriter;

/**
 * This class reads the input file and sends its content downstream via its
 * output pipe.<br><br>
 *
 * Pseudo Code:
 * <pre>
 * open input file whos name is provided in constructor 
 * 
 * while not eof (input file)
 *    read line of text
 *    write line of text to output pipe 
 * end while
 * 
 * close pipes and file
 * </pre>
 *
 * @author A.J. Lattanze, CMU Date: 12/3/99
 * @version 1.0
 */

public class FileReaderFilter extends Thread {

	// Create local pipe to that will connect to downstream filter
	PipedWriter outputPipe1;
	File inputFile;
	
	public FileReaderFilter(String fileName, PipedWriter OutputPipe1) {
		// Lets make sure that file name is provided

		if (fileName == null) {
			//TODO: throw exception
		} else {

			// Declarations:
			inputFile = new File(fileName);

			try {
				// Connect outputPipe to downstream filter
				this.outputPipe1 = OutputPipe1;
				System.out.println("FileReaderFilter:: connected to downstream filter.");
			} catch (Exception Error) {
				System.out.println("FileReaderFilter:: Error connecting output pipe.");
			} // catch
			
			// Open input file. The input file is a field oriented and
			// space-separated. The fields are as follows:
			//
			// LineNumber LangauageCode space delimited list of words
			//
			// Here is an example:
			//
			// 0001 EN A list so words 

			// Check to ensure that the file exists

			if (!inputFile.exists()) {
				System.out.println("\nFileReaderFilter:: file "
						+ inputFile.getAbsolutePath() + " does not exist.");
			} else {
				System.out.println("FileReaderFilter:: file "
						+ inputFile.getAbsolutePath() + " opened.");
			}
		}
	}
		
	public void run() {
		String LineOfText;
		try {
			// Create a buffered reader the file
			BufferedReader InFile = new BufferedReader(
					new InputStreamReader(new FileInputStream(
							(inputFile))));

			// Read each line of text
			boolean Done = false;

			while (!Done) {

				// Read a line of text from the input file and convert
				// it to upper case
				LineOfText = InFile.readLine();
				if (LineOfText == null) {
					Done = true;
				} else {
					LineOfText.toUpperCase();
					System.out.println("FileReaderFilter:: read: "
							+ LineOfText);
					try {
						// write line of text to the pipe
						outputPipe1.write(LineOfText, 0, LineOfText.length());
						outputPipe1.write((int) '\n');
						// signals end of line
						outputPipe1.flush();
						System.out.println("FileReaderFilter:: wrote: "
								+ LineOfText + " to outputPipe.");
					} catch (Exception Error) {
						System.out
						.println("FileReaderFilter:: Error writing to outputPipe.");
					} // try/catch
				} // if
			} // while

			// Close the input file
			InFile.close();
			System.out.println("\nFileReaderFilter:: closing "
					+ inputFile.getAbsolutePath());
		} catch (Exception Error) {
			System.out
			.println("FileReaderFilter:: Unable to create file stream");
		} // try-catch

		try {
			outputPipe1.close();
			System.out.println("FileReaderFilter:: outputPipe closed.");
		} catch (Exception Error) {
			System.out.println("FileReaderFilter:: error closing pipes.");
		} // try/catch
		
	} // run()
	
} // FileReaderFilter