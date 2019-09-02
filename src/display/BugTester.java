package display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mnist.MnistImageFile;
import mnist.MnistLabelFile;
import network.Network;
import network.TrainSet;

public class BugTester {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Network net = new Network(784, 100, 50, 10);
		net.setLearningRate(0.3);

		//set up mnist files
		String myPath = new File("").getAbsolutePath();
		myPath += "\\res\\";
		MnistImageFile images = new MnistImageFile(myPath + "trainImage.idx3-ubyte", "rw");
		MnistLabelFile labels = new MnistLabelFile(myPath + "trainLabel.idx1-ubyte", "rw");
		
		images.setCurrentIndex(1);
		labels.setCurrentIndex(1);
		
		ArrayList<double[][]> trainData = new ArrayList<double[][]>();
		for (int i = 0; i < 5000; i++) {
			double[] input = images.readImageDouble();
			double[] target = new double[10];
			target[labels.readLabel()] = 1;
			trainData.add(new double[][] {input, target});
		}
		
		//train in sets of 10, 100 loops per set
		for (int i = 0; i < trainData.size(); i += 10) {
			
		}
		
		/*
		Trainset trainData = new TrainSet(784, 10);
		for (int i = 0; i < 5000; i++) {
			double[] input = images.readImageDouble();
			double[] target = new double[10];
			int correctDigit = labels.readLabel();
			target[correctDigit] = 1;
			trainData.addData(input, target);
		}*/
		
		double[] testImage = images.readImageDouble();
		int testTarget = labels.readLabel();
		
		//make an array that represents the image
		int[] image = new int[testImage.length];
		for (int i = 0; i < image.length; i++) {
			image[i] = (int) (testImage[i] * 255);
		}
		
		//display the image
		class DigitPanel extends JPanel {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D  g2 = (Graphics2D) g;
				
				int sideLength = this.getWidth();
				
				int stretch = sideLength / 28;
				g.clearRect(0, 0, sideLength, sideLength);
				for (int i = 0; i < image.length; i++) {
					int x = (i % 28) * stretch;
					int y = (i / 28) * stretch;
					g2.setColor(new Color(image[i]));
					g2.fillRect(x, y, stretch, stretch);
				}
			}
		}
		
		DigitPanel panel = new DigitPanel();
		panel.setPreferredSize(new Dimension(280, 280));
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
		frame.validate();
		frame.setVisible(true);
		
		System.out.println("TARGET: " + testTarget);
		System.out.println("OUTPUT: " + Arrays.toString(net.calculate(testImage)));
	}
}
