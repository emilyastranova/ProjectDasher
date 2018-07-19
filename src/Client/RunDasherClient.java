package Client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.esotericsoftware.kryonet.Client;

public class RunDasherClient extends Frame implements ActionListener, WindowListener {

	// main timer
	Timer loop = new Timer(10, this);
	// speed variable
	double clientSpeed = 0;
	double clientSteering = 0;
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
	// init graphics storage library for on screen display stuff
	GraphicsLibrary gl = new GraphicsLibrary();

	public static void main(String[] args) {
		RunDasherClient f = new RunDasherClient();
		f.setSize(1080, 720);
		f.setVisible(true);
		f.setTitle("Project Dasher Client");
		f.setLayout(new FlowLayout());
	}

	public RunDasherClient() {
		// set window size
		setSize(1080, 720);
		// get ip
		ip = JOptionPane.showInputDialog("Input IP address");
		if (ip.isEmpty()) {
			ip = "localhost";
		}
		// start main timer
		loop.start();
		// start server
		try {
			startServer();
		} catch (InterruptedException e) {
		}

		addWindowListener(this);
	}

	private void startSpeedThread() {
		// TODO Auto-generated method stub

		updateSpeed = new Thread() {
			public void run() {
				while (true) {
					if (client.isConnected())
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

	}

	public void paint(Graphics g) {
		gl.drawClientText(g);
		gl.drawSpeedometer(g, 10, 10, clientSpeed);
		gl.drawClientConnected(g, client.isConnected());
		gl.drawSpeed(g, (int)clientSpeed);
		gl.drawSteering(g, (int)clientSteering);
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
			if (ClientFramework.client != null) {
				client = ClientFramework.client;
			}
			clientSpeed = ClientFramework.getSpeedForGUI();
			clientSteering = ClientFramework.getSteeringFromGUI();
		}

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		dispose();
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
