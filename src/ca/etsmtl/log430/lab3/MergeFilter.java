package ca.etsmtl.log430.lab3;

import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * This class is intended to be a filter that will collect the streams from 
 * two input pipes and merge them into one output pipe.<br><br>
 * 
 * Pseudo Code:
 * <pre>
 * connect to input pipe 1
 * connect to input pipe 2
 * connect to output pipe
 *
 * while not done
 *		read char1 from input pipe 1
 *		read char2 from input pipe 2
 *		string1 = string1 + char1
 *		string2 = string2 + char2
 *		write string1 to output pipe
 *		write string2 to output pipe
 * end while
 *
 * close pipes
 * close file
 * </pre>
 * 
 * @author A.J. Lattanze
 * @version 1.0
 */

public class MergeFilter extends Thread {
	// Declarations

	// Create local pipes to that will connect to upstream filters
	PipedReader inputPipe1 = new PipedReader();
	PipedReader inputPipe2 = new PipedReader();

	// Create local pipes to that will connect to downstream filter
	PipedWriter outputPipe = new PipedWriter();

	public MergeFilter(PipedWriter InputPipe1, PipedWriter InputPipe2,
			PipedWriter OutputPipe1) {
		try {
			// Connect InputPipes to upstream filters
			this.inputPipe1.connect(InputPipe1);
			this.inputPipe2.connect(InputPipe2);
			System.out.println("MergeFilter:: connected to upstream filters.");
		} catch (Exception Error) {
			System.out.println("MergeFilter:: Error connecting input pipes.");
		} // try/catch

		try {
			// Connect outputPipe to downstream filter
			this.outputPipe = OutputPipe1;
			System.out.println("MergeFilter:: connected to downstream filter.");
		} catch (Exception Error) {
			System.out.println("MergeFilter:: Error connecting output pipe.");
		} // catch

	} // Constructor

	// This is the method that is called when the thread is started

	public void run() {
		// Declarations
		boolean done1, done2; // Flags for reading from each pipe

		// Begin process data from the input pipes
		try {
			// Declarations

			// Need to be an array for easy conversion to string
			char[] characterValue1 = new char[1];
			char[] characterValue2 = new char[1];

			// Indicate when you are done reading on pipes #1 and #2
			done1 = false;
			done2 = false;

			// integer values read from the pipes
			int integerCharacter1;
			int integerCharacter2;

			// lines of text from input pipes #1 and #2
			String lineOfText1 = "";
			String lineOfText2 = "";

			// Indicate whether lines of text are ready to be output
			// to downstream filters
			boolean write1 = false;
			boolean write2 = false;

			// Loop for reading data

			while (!done1 || !done2) {
				// Read pipe #1
				if (!done1) {
					integerCharacter1 = inputPipe1.read();
					characterValue1[0] = (char) integerCharacter1;

					if (integerCharacter1 == -1) // pipe #1 is closed
					{
						done1 = true;
						System.out
								.println("MergeFilter:: Input pipe 1 closed.");

						try {
							inputPipe1.close();
						} catch (Exception Error) {
							System.out
									.println("MergeFilter:: Error closing input pipe 1.");
						} // try/catch

					} else {

						if (integerCharacter1 == '\n') // end of line
						{
							System.out.println("MergeFilter:: Received: "
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

					if (integerCharacter2 == -1) // pipe #2 is closed
					{
						done2 = true;
						System.out
								.println("MergeFilter:: input pipe 2 closed.");

						try {
							inputPipe2.close();
						} catch (Exception Error) {
							System.out
									.println("MergeFilter:: Error closing input pipe 2.");
						} // try/catch
					} else {
						if (integerCharacter2 == '\n') // end of line
						{
							System.out.println("MergeFilter:: Received: "
									+ lineOfText2 + " on input pipe 2.");
							write2 = true;
						} else {
							lineOfText2 += new String(characterValue2);
						} // if

					} // if ( IntegerCharacter2 == -1 )

				} // if (!Done2)

				if (write1) {
					write1 = false;

					try {
						System.out.println("MergeFilter:: Sending "
								+ lineOfText1 + " to output pipe.");
						lineOfText1 += "\n";
						outputPipe.write(lineOfText1, 0, lineOfText1.length());
						outputPipe.flush();
					} catch (Exception IOError) {
						System.out.println("MergeFilter:: Write Error.");
					} // try/catch

					lineOfText1 = "";

				} // if ( Write1 )

				if (write2) {
					write2 = false;

					try {
						System.out.println("MergeFilter:: Sending "
								+ lineOfText2 + " to output pipe.");
						lineOfText2 += "\n";
						outputPipe.write(lineOfText2, 0, lineOfText2.length());
						outputPipe.flush();
					} catch (Exception IOError) {
						System.out.println("MergeFilter:: Write Error.");
					} // try/catch

					lineOfText2 = "";

				} // if (Write2)

			} // while ( !Done1 || !Done2 )

		} // try

		catch (Exception Error) {
			System.out.println("MergeFilter:: Interrupted.");
		} // catch

		try {
			System.out.println("MergeFilter:: output pipe closed.");
			outputPipe.close();
		} catch (Exception Error) {
			System.out.println("MergeFilter:: Error closing pipes");
		} // try/catch

	} // run

} // class