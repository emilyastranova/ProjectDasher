package Server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JProgressBar;
import javax.swing.Timer;

import com.esotericsoftware.kryonet.Server;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import Client.GraphicsLibrary;
import Client.PacketMessage;

public class RunDasherServer extends Frame implements ActionListener, KeyListener, WindowListener {

	// main loop
	Timer loop = new Timer(1, this);
	// speed variable
	double speed = 0;
	double steering = 0;
	// Thread for setting speed inside server
	Thread updateSpeed;
	Thread controllerThread;
	// Mouse for controlling test UI
	MouseInfo mouse;
	// Server object
	static Server server;
	// Ports to listen on
	static int udpPort = 25565, tcpPort = 25565;
	// graphics storage library for on screen display
	GraphicsLibrary gl = new GraphicsLibrary();
	// mouse boolean
	boolean mouseIsHeld = false;
	
	public static void main(String[] args) {
		RunDasherServer f = new RunDasherServer();
	//	f.setSize(1080, 720);
		f.setVisible(true);
		f.setTitle("Project Dasher Server");
		f.setLayout(new FlowLayout());
		
	}

	public RunDasherServer() {
		// set window size
		setSize(1080, 720);
		// start main timer
		loop.start();
		// add mouse
		addKeyListener(this);
		// start server
		startServer();
		// start sending speed to client
		startSpeedThread();
		//add window listener
		addWindowListener(this);
		startControllerThread();
	}

	public void startServer() {
		System.out.println("Creating the server...");
		// Create the server
		server = new Server();

		// Register a packet class.
		server.getKryo().register(PacketMessage.class);
		// We can only send objects as packets if they are registered.

		// Bind to a port
		try {
			server.bind(tcpPort, udpPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Start the server
		server.start();

		// Add the listener
		server.addListener(new ServerFramework());

		System.out.println("Server is operational!");
	}

	public void startSpeedThread() {
		updateSpeed = new Thread() {
			public void run() {
				while (true) {
					ServerFramework.setSpeedFromGUI(Double.toString(speed/100));
					ServerFramework.setSteeringFromGUI(Double.toString(steering));
				}
			}
		};
		updateSpeed.start();
	}
	
	public void startControllerThread() {
		controllerThread = new Thread() {
			public void run() {
				ControllerManager controllers = new ControllerManager();
				controllers.initSDLGamepad();
				//Print a message when the "A" button is pressed. Exit if the "B" button is pressed 
				//or the controller disconnects.
				ControllerState currState;
				while(true) {
				  currState = controllers.getState(0);
				  
				  if(!currState.isConnected || currState.b) {
				    break;
				  }
				  if(currState.rightTrigger>0.05)
				    speed = (currState.rightTrigger)*100;
				  else
					  speed = -currState.leftTrigger*100;
				  
				  steering = currState.rightStickX;
				}
			}
			
		};
		controllerThread.start();
	}

	public void paint(Graphics g) {
		gl.drawSpeedometer(g, 10, 10, speed);
		gl.drawDashboardText(g);
		gl.drawClientConnected(g, server.getConnections().length != 0);
		gl.drawSpeed(g, (int)speed);
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
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_W) {
			mouseIsHeld = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_W) {
			mouseIsHeld = false;
		}
	}

	public void windowClosing(WindowEvent e) {
		dispose();
		System.exit(0);
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e){
	    System.exit(0);
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

}
