package Client;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.Timer;

import com.esotericsoftware.kryonet.Client;

public class RunDasherClient extends Applet implements ActionListener {

	// main timer
	Timer loop = new Timer(10, this);
	// speed variable
	double clientSpeed = 0;
	// Thread for setting speed inside server
	Thread updateSpeed;
	// Mouse for controlling test UI
	MouseInfo mouse;
	// Our client object.
	static Client client;
	// IP to connect to.
	static String ip = "localhost";
	// Ports to connect on.
	static int tcpPort = 25565, udpPort = 25565;
	//init graphics storage library for on screen display stuff
	GraphicsLibrary gl = new GraphicsLibrary();

	public void init() {
		//set window size
		setSize(1080,720);
		//start main timer
		loop.start();
		//start server
		try {
			startServer();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//start sending speed to client
		
	}

	private void startSpeedThread() {
		// TODO Auto-generated method stub

		updateSpeed = new Thread() {
			public void run() {
				while (true) {
					if(client.isConnected())
					clientSpeed = ClientFramework.getSpeedForGUI();
				}
			}
		};

		updateSpeed.start();
	}

	private void startServer() throws InterruptedException {
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
					ex.printStackTrace();
				}
				Thread.sleep(10);
			}
			else {
				x = 1000000;
				startSpeedThread();
			}
		
		}

		// Add a listener
		client.addListener(new ClientFramework());

		System.out.println("Connected! The client program is now waiting for a packet...\n");
		
	}

	public void paint(Graphics g) {
		gl.drawClientText(g);
		gl.drawSpeedometer(g,clientSpeed);
		gl.drawClientConnected(g,client.isConnected());
	}


	public void update(Graphics g) {
		Graphics offgc;
		Image offscreen = null;
		Dimension d = size();

		// create the offscreen buffer and associated Graphics
		offscreen = createImage(d.width, d.height);
		offgc = offscreen.getGraphics();
		// clear the exposed area
		offgc.setColor(getBackground());
		offgc.fillRect(0, 0, d.width, d.height);
		offgc.setColor(getForeground());
		// do normal redraw
		paint(offgc);
		// transfer offscreen to window
		g.drawImage(offscreen, 0, 0, this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == loop) {
			repaint();
			if(ClientFramework.client != null) {
				client = ClientFramework.client;
			}
			clientSpeed = ClientFramework.getSpeedForGUI();
		}

	}


}
