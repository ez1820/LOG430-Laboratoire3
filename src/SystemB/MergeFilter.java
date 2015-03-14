package SystemB;

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
 * connect to input pipe 7
 * connect to input pipe 8
 * connect to output pipe
 * connect to output pipe 2
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
	PipedReader inputPipe7 = new PipedReader();
	PipedReader inputPipe8 = new PipedReader();

	// Create local pipes to that will connect to downstream filter
	PipedWriter outputPipe = new PipedWriter();
	PipedWriter outputPipe2 = new PipedWriter();

	public MergeFilter(PipedWriter InputPipe1, PipedWriter InputPipe2,
			PipedWriter OutputPipe1, PipedWriter pipe07, PipedWriter pipe08, PipedWriter pipe09) {
		try {
			// Connect InputPipes to upstream filters
			this.inputPipe1.connect(InputPipe1);
			this.inputPipe2.connect(InputPipe2);
			this.inputPipe7.connect(pipe07);
			this.inputPipe8.connect(pipe08);
			System.out.println("MergeFilter:: connected to upstream filters.");
		} catch (Exception Error) {
			System.out.println("MergeFilter:: Error connecting input pipes.");
		} // try/catch

		try {
			// Connect outputPipe to downstream filter
			this.outputPipe = OutputPipe1;
			this.outputPipe2 = pipe09;
			System.out.println("MergeFilter:: connected to downstream filters.");
		} catch (Exception Error) {
			System.out.println("MergeFilter:: Error connecting output pipe.");
		} // catch

	} // Constructor

	// This is the method that is called when the thread is started

	public void run() {
		// Declarations
		boolean done1, done2, done7, done8; // Flags for reading from each pipe

		// Begin process data from the input pipes
		try {
			// Declarations

			// Need to be an array for easy conversion to string
			char[] characterValue1 = new char[1];
			char[] characterValue2 = new char[1];
			char[] characterValue7 = new char[1];
			char[] characterValue8 = new char[1];

			// Indicate when you are done reading on pipes #1 and #2
			done1 = false;
			done2 = false;
			done7 = false;
			done8 = false;

			// integer values read from the pipes
			int integerCharacter1;
			int integerCharacter2;
			int integerCharacter7;
			int integerCharacter8;

			// lines of text from input pipes #1 and #2
			String lineOfText1 = "";
			String lineOfText2 = "";
			String lineOfText7 = "";
			String lineOfText8 = "";

			// Indicate whether lines of text are ready to be output
			// to downstream filters
			boolean write1 = false;
			boolean write2 = false;
			boolean write7 = false;
			boolean write8 = false;

			// Loop for reading data

			while (!done1 && !done2 && !done7 && !done8) {
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
				
				// Read pipe #7
				if (!done7) {
					integerCharacter7 = inputPipe7.read();
					characterValue7[0] = (char) integerCharacter7;

					if (integerCharacter7 == -1) // pipe #1 is closed
					{
						done7 = true;
						System.out
								.println("MergeFilter:: Input pipe 7 closed.");

						try {
							inputPipe7.close();
						} catch (Exception Error) {
							System.out
									.println("MergeFilter:: Error closing input pipe 7.");
						} // try/catch

					} else {

						if (integerCharacter7 == '\n') // end of line
						{
							System.out.println("MergeFilter:: Received: "
									+ lineOfText7 + " on input pipe 7.");
							write7 = true;
						} else {
							lineOfText7 += new String(characterValue7);
						} // if

					} // if ( IntegerCharacter7 == -1 )

				} // if (!Done7)
				
				// Read pipe #8
				if (!done8) {
					integerCharacter8 = inputPipe8.read();
					characterValue8[0] = (char) integerCharacter8;

					if (integerCharacter8 == -1) // pipe #1 is closed
					{
						done8 = true;
						System.out
								.println("MergeFilter:: Input pipe 8 closed.");

						try {
							inputPipe8.close();
						} catch (Exception Error) {
							System.out
									.println("MergeFilter:: Error closing input pipe 8.");
						} // try/catch

					} else {

						if (integerCharacter8 == '\n') // end of line
						{
							System.out.println("MergeFilter:: Received: "
									+ lineOfText8 + " on input pipe 8.");
							write8 = true;
						} else {
							lineOfText8 += new String(characterValue8);
						} // if

					} // if ( IntegerCharacter8 == -1 )

				} // if (!Done8)

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

				if (write7) {
					write7 = false;

					try {
						System.out.println("MergeFilter:: Sending "
								+ lineOfText7 + " to output pipe 2.");
						lineOfText7 += "\n";
						outputPipe2.write(lineOfText7, 0, lineOfText7.length());
						outputPipe2.flush();
					} catch (Exception IOError) {
						System.out.println("MergeFilter:: Write Error.");
					} // try/catch

					lineOfText7 = "";

				} // if ( Write7 )

				if (write8) {
					write8 = false;

					try {
						System.out.println("MergeFilter:: Sending "
								+ lineOfText8 + " to output pipe 2.");
						lineOfText8 += "\n";
						outputPipe2.write(lineOfText8, 0, lineOfText8.length());
						outputPipe2.flush();
					} catch (Exception IOError) {
						System.out.println("MergeFilter:: Write Error.");
					} // try/catch

					lineOfText8 = "";

				} // if ( Write8 )

			} // while ( !Done1 || !Done2  || !Done7 || !Done8)

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