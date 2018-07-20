import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

import java.io.IOException;
import java.util.Date;

/**
 * This example code demonstrates how to perform serial communications using the
 * Raspberry Pi.
 *
 * @author Robert Savage
 */
public class SerialTest {

	/**
	 * This example program supports the following optional command
	 * arguments/options: "--device (device-path)" [DEFAULT: /dev/ttyAMA0] "--baud
	 * (baud-rate)" [DEFAULT: 38400] "--data-bits (5|6|7|8)" [DEFAULT: 8] "--parity
	 * (none|odd|even)" [DEFAULT: none] "--stop-bits (1|2)" [DEFAULT: 1]
	 * "--flow-control (none|hardware|software)" [DEFAULT: none]
	 *
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String args[]) throws InterruptedException, IOException {

		final Console console = new Console();
		// print program title/header
		console.title("<-- The Pi4J Project -->", "Serial Communication Example");
		// allow for user to exit program using CTRL-C
		console.promptForExit();
		// create an instance of the serial communications class
		final Serial serial = SerialFactory.createInstance();
		// create and register the serial data listener
		serial.addListener(new SerialDataEventListener() {
			@Override
			public void dataReceived(SerialDataEvent event) {

				// print out the data received to the console
				try {
				event.getAsciiString();
				} catch (IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		try {
			// create serial config object
			SerialConfig config = new SerialConfig();

			// set default serial settings (device, baud rate, flow control, etc)
			//
			// by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO
			// header)
			// NOTE: this utility method will determine the default serial port for the
			// detected platform and board/model. For all Raspberry Pi models
			// except the 3B, it will return "/dev/ttyAMA0". For Raspberry Pi
			// model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
			// environment configuration.
			config.device(SerialPort.getDefaultPort()).baud(Baud._38400).dataBits(DataBits._8).parity(Parity.NONE)
					.stopBits(StopBits._1).flowControl(FlowControl.NONE);

			// parse optional command argument options to override the default serial
			// settings.
			if (args.length > 0) {
				config = CommandArgumentParser.getSerialConfig(config, args);
			}

			// display connection details
			console.box(" Connecting to: " + config.toString(),
					" We are sending ASCII data on the serial port every 1 second.",
					" Data received on serial port will be displayed below.");

			// open the default serial device/port with the configuration settings
			serial.open(config);

			// continuous loop to keep the program running until the user terminates the
			// program
			while (console.isRunning()) {
				try {
					// write a formatted string to the serial transmit buffer
					serial.write("100");
				} catch (IllegalStateException ex) {
					ex.printStackTrace();
				}

				// wait 1 second before continuing
				Thread.sleep(100);
			}

		} catch (IOException ex) {
			console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
			return;
		}
	}
}
