package display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class Rect extends Rectangle2D.Double {
	
	/** (x, y) -> centerPos */
	public Rect(double x, double y, double width, double height) {
		super(x - width / 2, y - height / 2, width, height);
	}
}

public class LoadTester extends JPanel {
	
	private Rect bg;
	private JLabel label;
	private String string;
	
	private int progress;
	
	public LoadTester() {
		setSize(400, 150);
		bg = new Rect(192, 80, 300, 15);
		progress = 0;
		label = new JLabel("Testing Progres..." + progress + "%");
		string  = "Testing Progres..." + progress + "%";
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		//loading bar frame
		g2.setColor(Color.DARK_GRAY);
		g2.setStroke(new BasicStroke(7));
		g2.draw(bg);
		g2.fill(bg);

		//text
		g2.setFont(new Font("TimesRoman", Font.BOLD, 20)); 
		g2.drawString(string, 40, 40);
		
		//loading bar
		Rectangle2D.Double bar = new Rectangle2D.Double(bg.x, bg.y, bg.width * (double) progress / 100d, bg.height);
		g2.setColor(Color.GREEN);
		g2.fill(bar);
	}
	
	public void run() {
		while (progress < 100) {
			progress++;
			if (progress == 100) {
				string = "Training Done";
			} else {
				string = "Training Progress..." + progress + "%";
			}
			try {
				Thread.sleep((int) (Math.random() * 500));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(400, 150);
		LoadTester loadTester = new LoadTester();
		frame.setContentPane(loadTester);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.validate();
		frame.setVisible(true);
		loadTester.run();
	}
}
