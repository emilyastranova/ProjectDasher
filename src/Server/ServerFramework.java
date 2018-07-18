package Server;

import java.util.Date;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import Client.PacketMessage;

public class ServerFramework extends Listener {

	// Server object
	static Server server;
	// Ports to listen on
	static int udpPort = 25565, tcpPort = 25565;

	static Scanner input;

	static boolean carConnected = false;
	static Connection carConnection;

	public static void main(String[] args) throws Exception {
		System.out.println("Creating the server...");
		// Create the server
		server = new Server();
		input = new Scanner(System.in);

		// Register a packet class.
		server.getKryo().register(PacketMessage.class);
		// We can only send objects as packets if they are registered.

		// Bind to a port
		server.bind(tcpPort, udpPort);

		// Start the server
		server.start();

		// Add the listener
		server.addListener(new ServerFramework());

		System.out.println("Server is operational!");
		

	}
	
	static String tempMessage ="";
	
	public static void setMessageFromGUI(String s) {
		tempMessage = s;
	}
	
	public String getMessageFromGUI() {
		return tempMessage;
	}

	// This is run when a connection is received!
	public void connected(Connection c) {
		System.out.println("Received a connection from " + c.getRemoteAddressTCP().getHostString());
		// Create a message packet.
		PacketMessage packetMessage = new PacketMessage();
		// Assign the message text.
		packetMessage.message = "0";
		// Send the message
		
		c.sendTCP(packetMessage);
		
		(new Thread() {
			  public void run() {
			    // do stuff
				  while(true) {
					  packetMessage.message = getMessageFromGUI();
					  if(!tempMessage.equals(""))
				  c.sendTCP(packetMessage);
				  try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  }
			  }
			 }).start();
		// Alternatively, we could do:
		// c.sendUDP(packetMessage);
		// To send over UDP.

	}

	// This is run when we receive a packet.
	public void received(Connection c, Object p) {
		// We will do nothing here.
		// We do not expect to receive any packets.
		// (But if we did, nothing would happen)
		if (p instanceof PacketMessage) {
			// Cast it, so we can access the message within.
			PacketMessage packet = (PacketMessage) p;
			System.out.println("Client: " + packet.message);

		}

	}

	// This is run when a client has disconnected.
	public void disconnected(Connection c) {
		System.out.println("A client disconnected!");
	}
}
