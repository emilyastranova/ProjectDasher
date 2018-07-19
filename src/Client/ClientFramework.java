package Client;

import java.io.IOException;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientFramework extends Listener {

	// DEBUG MODE TOGGLE
	boolean debugMode = true;

	// Our client object.
	static Client client;
	// IP to connect to.
	static String ip = "107.131.154.241";
	// Ports to connect on.
	static int tcpPort = 25565, udpPort = 25565;

	static int x = 0;

	// A boolean value.
	static boolean messageReceived = false;

	public static void main(String[] args) throws Exception {
		System.out.println("Connecting to the server...");
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
			}
			else {
				x = 10000;
			}
		}

		// Add a listener
		client.addListener(new ClientFramework());

		System.out.println("Connected! The client program is now waiting for a packet...\n");

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
		return tempSteering*100;
	}

	// I'm only going to implement this method from Listener.class because I only
	// need to use this one.
	public void received(Connection c, Object p) {
		// Is the received packet the same class as PacketMessage.class?
		if (p instanceof PacketMessage) {
			// Cast it, so we can access the message within.
			PacketMessage packet = (PacketMessage) p;
			if (debugMode)
				System.out.println("speed: " + packet.steeringPacket);
			tempSpeed = Double.parseDouble(packet.speedPacket);
			if(!packet.steeringPacket.isEmpty())
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
						}
						else {
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

}
