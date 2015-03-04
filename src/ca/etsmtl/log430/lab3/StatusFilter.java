package ca.etsmtl.log430.lab3;



import java.io.*;

/**
 * This class is intended to be a filter that will send the information on an output pipe
 * specific to each project type. In other terms, this filter splits the data by project type.
 * <br><br>
 * 
 * <pre>
 * Pseudo Code:
 *
 * connect to input pipe
 * connect to output pipe
 *
 * while not end of line
 *     if REG appears on line of text 
 *         write line of text to REG output pipe
 *         flush pipe
 *     else if CRI appears on line of text
 *         write line of text to CRI output pipe
 *         flush pipe
 *     end if
 * end while
 * 
 * close pipes
 * </pre>
 *
 * @author R. Champagne
 * @version 1.0
 */

public class StatusFilter extends Thread {

	// Declarations

	boolean done;

	PipedReader inputPipe = new PipedReader();
	PipedWriter outputPipe1 = new PipedWriter();
	PipedWriter outputPipe2 = new PipedWriter();

	public StatusFilter(PipedWriter inputPipe, PipedWriter outputPipe1, PipedWriter outputPipe2) {

		try {

			// Connect inputPipe
			this.inputPipe.connect(inputPipe);
			System.out.println("StatusFilter:: connected to upstream filter.");

			// Connect OutputPipes

			this.outputPipe1 = outputPipe1;
			this.outputPipe2 = outputPipe2;
			System.out.println("StatusFilter:: connected to downstream filters.");

		} catch (Exception Error) {

			System.out.println("StatusFilter:: Error connecting to other filters.");

		} // try/catch

	} // Constructor

	// This is the method that is called when the thread is started

	public void run() {

		// Declarations

		char[] characterValue = new char[1];
		// char array is required to turn char into a string
		String lineOfText = "";
		// string is required to look for the status code
		int integerCharacter; // the integer value read from the pipe

		try {

			done = false;

			while (!done) {

				integerCharacter = inputPipe.read();
				characterValue[0] = (char) integerCharacter;

				if (integerCharacter == -1) { // pipe is closed

					done = true;

				} else {

					if (integerCharacter == '\n') { // end of line

						System.out.println("StatusFilter:: received: " + lineOfText + ".");

						if (lineOfText.indexOf(" REG ") != -1) {

							System.out.println("StatusFilter:: sending: "
									+ lineOfText + " to output pipe 1 (REG).");
							lineOfText += new String(characterValue);
							outputPipe1
									.write(lineOfText, 0, lineOfText.length());
							outputPipe1.flush();
						} else if(lineOfText.indexOf(" CRI ") != -1) {

							System.out.println("StatusFilter:: sending: "
									+ lineOfText + " to output pipe 2 (CRI).");
							lineOfText += new String(characterValue);
							outputPipe2
									.write(lineOfText, 0, lineOfText.length());
							outputPipe2.flush();
 						} // if

						lineOfText = "";

					} else {

						lineOfText += new String(characterValue);

					} // if //

				} // if

			} // while

		} catch (Exception error) {

			System.out.println("StatusFilter:: Interrupted.");

		} // try/catch

		try {

			inputPipe.close();
			System.out.println("StatusFilter:: input pipe closed.");

			outputPipe1.close();
			outputPipe2.close();
			System.out.println("StatusFilter:: output pipes closed.");

		} catch (Exception Error) {

			System.out.println("StatusFilter:: Error closing pipes.");

		} // try/catch

	} // run

} // class