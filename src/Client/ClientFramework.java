package Client;

import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPort;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

public class ClientFramework extends Listener {

	// DEBUG MODE TOGGLE
	boolean debugMode = false;

	// speed variable
	static double clientSpeed = 0;
	static double clientSteering = 0;
	// Thread for setting speed inside server
	static Thread updateSpeed;
	// Our client object.
	static Client client;
	// IP to connect to.
	static String ip = "107.131.154.241";
	// Ports to connect on.
	static int tcpPort = 25565, udpPort = 25565;
	
	static Console console;
	static Serial serial;

	static int x = 0;

	// A boolean value.
	static boolean messageReceived = false;

	public static void main(String[] args) throws Exception {
	//	ip = JOptionPane.showInputDialog("Input IP address");
//		if (ip.isEmpty()) {
//			ip = "localhost";
//		}
		 ip = args[0];
		System.out.println("Connecting to the server...");
		// Create the client.
		client = new Client();

		// Register the packet object.
		client.getKryo().register(PacketMessage.class);

		// Start the client
		client.start();
		// The client MUST be started before connecting can take place.

		// Connect to the server - wait 5000ms before failing.
		for (int x = 0; x < 1000000; x++) {
			if (!client.isConnected()) {
				try {
					client.connect(5000, ip, tcpPort, udpPort);
				} catch (IOException ex) {
				}
				Thread.sleep(10);
			} else {
				x = 1000000;
				startSpeedThread();
			}

		}

		// Add a listener
		client.addListener(new ClientFramework());

		System.out.println("Connected! The client program is now waiting for a packet...\n");

		console = new Console();
		// print program title/header
		console.title("<-- The Pi4J Project -->", "Serial Communication Example");
		// allow for user to exit program using CTRL-C
		console.promptForExit();
		// create an instance of the serial communications class
		serial = SerialFactory.createInstance();
		// create and register the serial data listener
		serial.addListener(new SerialDataEventListener() {
			@Override
			public void dataReceived(SerialDataEvent event) {

				// print out the data received to the console
				try {
					System.out.println(event.getAsciiString());
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
			startSerial();

		} catch (IOException ex) {
			console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
			return;
		}

		// This is here to stop the program from closing before we receive a message.
		while (!messageReceived) {
			Thread.sleep(1000);
		}

		Scanner in = new Scanner(System.in);
		PacketMessage send = new PacketMessage();

		while (true) {
			send.speedPacket = in.nextLine();
			client.sendTCP(send);
			Thread.sleep(1);
		}

	}

	static double tempSpeed = 0.0;

	public static double getSpeedForGUI() {
		return tempSpeed;
	}

	static double tempSteering = 0.0;

	public static double getSteeringFromGUI() {
		return tempSteering * 1;
	}

	// I'm only going to implement this method from Listener.class because I only
	// need to use this one.
	public void received(Connection c, Object p) {
		// Is the received packet the same class as PacketMessage.class?
		if (p instanceof PacketMessage) {
			// Cast it, so we can access the message within.
			PacketMessage packet = (PacketMessage) p;
			if (debugMode) {
				System.out.println("speed: " + packet.speedPacket + " Steering: " + packet.steeringPacket);
			}
			tempSpeed = Double.parseDouble(packet.speedPacket);
			if (!packet.steeringPacket.isEmpty())
				tempSteering = Double.parseDouble(packet.steeringPacket);

			// We have now received the message!
			messageReceived = true;
		}
	}

	public void disconnected(Connection connection) {
		new Thread() {
			public void run() {
				try {
					System.out.println("Reconnecting: ");
					// Create the client.
					client = new Client();

					// Register the packet object.
					client.getKryo().register(PacketMessage.class);

					// Start the client
					client.start();
					// The client MUST be started before connecting can take place.

					// Connect to the server - wait 5000ms before failing.
					for (int x = 0; x < 10000; x++) {
						if (!client.isConnected()) {
							try {
								client.connect(5000, ip, tcpPort, udpPort);
							} catch (IOException ex) {
								ex.printStackTrace();
							}
							Thread.sleep(10);
						} else {
							x = 10000;
						}
					}

					// Add a listener
					client.addListener(new ClientFramework());

					System.out.println("Connected! The client program is now waiting for a packet...\n");
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

	private static void startSpeedThread() {
		// TODO Auto-generated method stub

		updateSpeed = new Thread() {
			public void run() {
				while (true) {
					if (client.isConnected())
						clientSpeed = getSpeedForGUI();

					if (ClientFramework.client != null) {
						client = ClientFramework.client;
					}
					clientSpeed = ClientFramework.getSpeedForGUI();
					clientSteering = ClientFramework.getSteeringFromGUI();
				}
			}
		};

		updateSpeed.start();
	}

	public static void startSerial() {

		Thread serialThread = new Thread() {
			public void run() {
				while (console.isRunning()) {
					try {
						// write a formatted string to the serial transmit buffer
						System.out.println("Sending to serial: "+Integer.toString((int)(tempSpeed)));
						serial.write(Integer.toString((int)(tempSpeed)) + ","+Integer.toString((int)tempSteering)+","+"\n");
					} catch (IllegalStateException ex) {
						ex.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// wait 1 second before continuing
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		serialThread.start();

	}

}
