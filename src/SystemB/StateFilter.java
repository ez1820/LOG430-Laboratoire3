package SystemB;

import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * This class is intended to be a filter that will key on a particular state
 * provided at instantiation.  Note that the stream has to be buffered so that
 * it can be checked to see if the specified severity appears on the stream.
 * If this string appears in the input stream, the whole line is passed to the
 * output stream.
 * 
 * <pre>
 * Pseudo Code:
 *
 * connect to input pipe
 * connect to output pipe
 *
 * while not end of line
 *
 *		read input pipe
 *		
 *		if status is REG
 *			if severity RIS or DIF and Progression less than 50%
 *				write line of text to output pipe1
 *				flush pipe
 *			end if
 *		else if status is CRI
 *			if (severity RIS AND progression is 25%) OR (severity !RIS AND progression greater than 75%)
 *				write line of text to output pipe3
 *				flush pipe
 *			end if
 *		end if
 *
 * end while
 * close pipes
 * </pre>
 *
 * @author A.J. Lattanze
 * @version 1.0
 */

public class StateFilter extends Thread {

	// Declarations

	boolean done;

	String severity;
	String status = ""; 	// REG or CRI (3)
	int progression = 0; 	// (2)
	
	PipedReader inputPipe = new PipedReader();
	PipedWriter outputPipe1 = new PipedWriter();
	PipedWriter outputPipe2 = new PipedWriter();

	public StateFilter(PipedWriter inputPipe, PipedWriter outputPipe1, PipedWriter outputPipe2) {

		try {

			// Connect inputPipe
			this.inputPipe.connect(inputPipe);
			System.out.println("StateFilter " + severity
					+ ":: connected to upstream filter.");

			// Connect to all output pipes
			this.outputPipe1 = outputPipe1;
			this.outputPipe2 = outputPipe2;
			System.out.println("StateFilter " + severity
					+ ":: connected to downstream filters.");

		} catch (Exception Error) {

			System.out.println("StateFilter " + severity
					+ ":: Error connecting to other filters.");

		} // try/catch
	} // Constructor

	// This is the method that is called when the thread is started
	public void run() {

		// Declarations

		char[] characterValue = new char[1];
		// char array is required to turn char into a string
		String lineOfText = "";
		// string is required to look for the keyword
		int integerCharacter; // the integer value read from the pipe

		try {

			done = false;

			while (!done) {

				integerCharacter = inputPipe.read();
				characterValue[0] = (char) integerCharacter;

				if (integerCharacter == -1){ // pipe is closed
					done = true;
				} 
				else{
					if (integerCharacter == '\n'){ // end of line						
						System.out.println("StateFilter " + severity
								+ ":: received: " + lineOfText + ".");

						status = lineOfText.substring(5, 8);
						progression = Integer.parseInt(lineOfText.substring(9, 11));
						severity = lineOfText.substring(12, 15);
						
						if(status.equals("REG")){
							if((severity.equals("RIS") || severity.equals("DIF")) && progression < 50){								
								System.out.println("StateFilter "
										+ severity + ":: sending: "
										+ lineOfText + " to output pipe1.");
								lineOfText += new String(characterValue);
								outputPipe1.write(lineOfText, 0, lineOfText.length());
								outputPipe1.flush();
							}
						}
						else if (status.equals("CRI")){
							if((severity.equals("RIS") && progression == 25) || (!(severity.equals("RIS")) && progression > 75)){
								System.out.println("StateFilter "
										+ severity + ":: sending: "
										+ lineOfText + " to output pipe2.");
								lineOfText += new String(characterValue);
								outputPipe2.write(lineOfText, 0, lineOfText.length());
								outputPipe2.flush();
							}
						}
						lineOfText = "";
					} 
					else{
						lineOfText += new String(characterValue);
					} // if //
				} // if
			} // while

		} catch (Exception error) {

			System.out.println("StateFilter::" + severity + " Interrupted.");
		} // try/catch

		try {

			inputPipe.close();
			System.out.println("StateFilter " + severity
					+ ":: input pipe closed.");

			outputPipe1.close();
			System.out.println("StateFilter " + severity
					+ ":: output pipe1 closed.");

			outputPipe2.close();
			System.out.println("StateFilter " + severity
					+ ":: output pipe2 closed.");

		} catch (Exception error) {

			System.out.println("StateFilter " + severity
					+ ":: Error closing pipes.");

		} // try/catch

	} // run

} // class