package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class GraphicsLibrary {

	public void drawSpeedometer(Graphics g, double speed) {
		g.setColor(Color.black);
		g.drawRect(100, 100, 100, 500);

		g.setColor(Color.GREEN);
		g.fillRect(101, 101 + (100 - (int) speed) * 5, 99, 500 - ((100 - (int) speed) * 5) - 1);

		if (speed > 50) {
			g.setColor(Color.RED);
			g.fillRect(101, 101 + (100 - (int) speed) * 5, 99, 500 - ((100 - (int) speed) * 5) - 250);
		}

		g.setColor(Color.black);
		g.drawLine(100, 350, 200, 350);
	}

	public void drawDashboardText(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Segoe UI", Font.BOLD, 50));
		g.drawString("Project Dasher", 360, 50);
		g.setFont(new Font("Segoe UI", Font.PLAIN, 40));
		g.drawString("Control Panel", 415, 85);

	}

	public void drawClientText(Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Segoe UI", Font.BOLD, 50));
		g.drawString("Project Dasher", 360, 50);
		g.setFont(new Font("Segoe UI", Font.PLAIN, 40));
		g.drawString("Client Debugger", 387, 85);
	}

	public void drawClientConnected(Graphics g, boolean status) {
		g.setFont(new Font("Arial", Font.BOLD, 40));
		if (status) {
			g.setColor(Color.GREEN);
			g.drawString("Connected", 430, 130);
		} else {
			g.setColor(Color.RED);
			g.drawString("Disconnected", 400, 130);
		}
	}

}
