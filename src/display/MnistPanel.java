package display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import mnist.MnistImageFile;
import mnist.MnistLabelFile;
import network.Network;
import network.NetworkTools;
import network.TrainSet;

/**
 * Panel must be a square
 * @author maxem
 *
 */
public class MnistPanel extends JPanel implements AutoCloseable {
	
	public static final int SIDE = 28;
	
	private MnistImageFile images;
	private MnistLabelFile labels;
	
	private double[] currImage;
	private int currLabel;
	
	public MnistPanel(MnistImageFile imageFile, MnistLabelFile labelFile) {
		images = imageFile;
		labels = labelFile;
		
		currImage = new double[SIDE*SIDE];
		currLabel = -1;
	}
	
	private void setIndex(int idx) {
		images.setCurrentIndex(idx);
		labels.setCurrentIndex(idx);
	}
	
	public void next() throws IOException {
		currImage = images.readImageDouble();
		currLabel = labels.readLabel();
	}
	
	public int getCurrLabel() {
		return currLabel;
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
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		final int sideLength = SIDE * 10;
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setBounds(100, 100, sideLength * 2, sideLength);
		
		String myPath = new File("").getAbsolutePath();
		
		
		//RECOVER NETWORK net3.ser
		Network net = (Network) Serializer.deserialize(myPath + "\\networks\\" + "net3" + Serializer.EXTENSION);
		
		
		//Network net = new Network(784, 80, 40, 10);
		
		//set up mnist files
		myPath += "\\res\\";
		MnistImageFile images = new MnistImageFile(myPath + "trainImage.idx3-ubyte", "rw");
		MnistLabelFile labels = new MnistLabelFile(myPath + "trainLabel.idx1-ubyte", "rw");
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane);
		
		//add image pane and number label
		MnistPanel imgPanel = new MnistPanel(images, labels);
		imgPanel.setPreferredSize(new Dimension(sideLength, sideLength));
		JPanel numPanel = new JPanel();
		numPanel.setPreferredSize(new Dimension(sideLength, sideLength));
		numPanel.setLayout(new BorderLayout());
		JLabel numLabel = new JLabel();
		Font numFont = numLabel.getFont();
		int stringWidth = numLabel.getFontMetrics(numFont).stringWidth("0");
		
		//use height b/c digit is taller than it is wider
		double sizeRatio = 0.7 * sideLength / stringWidth;
		int newFontSize = (int) (numFont.getSize() * sizeRatio);
		
		numLabel.setFont(new Font(numFont.getName(), Font.BOLD, newFontSize));
		
		numPanel.add(numLabel, BorderLayout.CENTER);
		
		contentPane.setLayout(new BorderLayout());
		contentPane.add(imgPanel, BorderLayout.WEST);
		contentPane.add(numPanel, BorderLayout.EAST);
		
		contentPane.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {				
				try {
					imgPanel.next();
					
					//get network's guess
					double[] result = net.calculate(imgPanel.currImage);
					int guess = NetworkTools.highestValueIndex(result);
					int confidence = (int) (result[guess] * 100 + 0.5);
					System.out.println("INDEX: " + imgPanel.labels.getCurrentIndex() + ":  " 
							+ guess + " (" + confidence + "% confident)");
					imgPanel.repaint();
					numLabel.setText("" + guess);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}
			
		});
		
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {}

			@Override
			public void windowClosed(WindowEvent arg0) {
				try {
					imgPanel.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void windowClosing(WindowEvent arg0) {}

			@Override
			public void windowDeactivated(WindowEvent arg0) {}

			@Override
			public void windowDeiconified(WindowEvent arg0) {}

			@Override
			public void windowIconified(WindowEvent arg0) {}

			@Override
			public void windowOpened(WindowEvent arg0) {}
			
		});
		
		imgPanel.setIndex(25_000);
		
		frame.pack();
		frame.validate();
		frame.setVisible(true);
	}

	@Override
	public void close() throws Exception {
		images.close();
		labels.close();
	}
}
