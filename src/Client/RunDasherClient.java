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

public class RunDasherClient extends Applet implements ActionListener, MouseListener{

	Timer loop = new Timer(10,this);
	double speed = 100;
	Thread updateSpeed;
	MouseInfo mouse;
	
	//Our client object.
		static Client client;
		//IP to connect to.
		static String ip = "localhost";
		//static String ip = "192.168.1.64";
		
		//Ports to connect on.
		static int tcpPort = 25565, udpPort = 25565;
		
	
	public void init() {
		setSize(1080,720);
		loop.start();
		addMouseListener(this);
		
		updateSpeed = new Thread(){
			public void run() {
				while(true) {
				speed = ClientFramework.getSpeedForGUI();
				}
			}
		};
		
		System.out.println("Connecting to the server...");
		//Create the client.
		client = new Client();
		
		//Register the packet object.
		client.getKryo().register(PacketMessage.class);

		//Start the client
		client.start();
		//The client MUST be started before connecting can take place.
		
		//Connect to the server - wait 5000ms before failing.
		try {
			client.connect(5000, ip, tcpPort, udpPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Add a listener
		client.addListener(new ClientFramework());
		
		System.out.println("Connected! The client program is now waiting for a packet...\n");
		
		updateSpeed.start();
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
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
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
