package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import network.Network;
import network.NetworkTools;

public class ReversePanel extends JPanel {
	
	public static final int SIDE = 28;
	private double[] currImage;
	
	public ReversePanel(double[] input) {
		currImage = input;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		int sideLength = this.getWidth();
		
		int stretch = sideLength / SIDE;
		g.clearRect(0, 0, sideLength, sideLength);
		for (int i = 0; i < currImage.length; i++) {
			int x = (i % SIDE) * stretch;
			int y = (i / 28) * stretch;
			g2.setColor(new Color((int) (currImage[i] * 255)));
			g2.fillRect(x, y, stretch, stretch);
		}
	}
	
	public static void main(String[] args) {
		String myPath = new File("").getAbsolutePath();
		
		//RECOVER NETWORK net3.ser
		Network net = (Network) Serializer.deserialize(myPath + "\\networks\\" + "net3" + Serializer.EXTENSION);
		
		//reverse net
		Network rNet = net.getReverseNetwork();
		
		int currDigit = 3;
		double[] input = new double[10];
		input[currDigit] = 1;
		
		double[] output = rNet.calculate(input);
		
		System.out.println(NetworkTools.highestValueIndex(net.calculate(output)));
		
		ReversePanel panel = new ReversePanel(output);
		panel.setPreferredSize(new Dimension(SIDE*20, SIDE*20));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(panel.getSize());
		frame.setContentPane(panel);
		frame.pack();
		frame.validate();
		frame.setResizable(false);
		frame.setVisible(true);
	}
}
