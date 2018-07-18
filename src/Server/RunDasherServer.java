package Server;

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
import java.util.Scanner;

import javax.swing.Timer;

import com.esotericsoftware.kryonet.Server;

import Client.PacketMessage;

public class RunDasherServer extends Applet implements ActionListener, MouseListener{

	Timer loop = new Timer(10,this);
	double speed = 100;
	Thread updateSpeed;
	MouseInfo mouse;
	

	// Server object
	static Server server;
	// Ports to listen on
	static int udpPort = 25565, tcpPort = 25565;

	public void init() {
		setSize(1080,720);
		loop.start();
		addMouseListener(this);
		
		updateSpeed = new Thread(){
			public void run() {
				while(true) {
				speed = MouseInfo.getPointerInfo().getLocation().getY()/7.2;
				speed = Math.abs(100-speed);
				System.out.println(speed);
				ServerFramework.setMessageFromGUI(Double.toString(speed));
				}
			}
		};
		
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
	
	public void paint(Graphics g) {
		drawSpeedometer(g);
	}
	
	public void drawSpeedometer(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(100, 100, 100, 500);
		
		g.setColor(Color.blue);
		g.fillRect(100, 100+(100-(int)speed)*5, 100, 500-((100-(int)speed)*5));
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
		
		if(e.getSource()==loop) {
			repaint();
		}
		
	}
	
	

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(!updateSpeed.isAlive())
		updateSpeed.start();
		
		else
			updateSpeed.resume();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		updateSpeed.suspend();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
