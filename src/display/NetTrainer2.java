package display;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import mnist.MnistImageFile;
import mnist.MnistLabelFile;
import network.Network;
import network.TrainSet;

public class NetTrainer2 extends JPanel implements AutoCloseable {
	
	private MnistImageFile images;
	private MnistLabelFile labels;
	
	private Network network;
	
	private double progress = 0;
	private double dotNum = 0;
	
	private JLabel header;
	
	public NetTrainer2(Network net) {
		
		String myPath = new File("").getAbsolutePath();
		
		network = net;
		
		myPath += File.separator + "res" + File.separator;
		try {
			images = new MnistImageFile(myPath + "trainImage.idx3-ubyte", "rw");
			labels = new MnistLabelFile(myPath + "trainLabel.idx1-ubyte", "rw");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.setSize(500, 200);
		this.setLayout(new BorderLayout());
		header = new JLabel();
		header.setFont(new Font("Arial", 1, 30));
		this.add(header, BorderLayout.PAGE_START);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.DARK_GRAY);
		Rectangle base = new Rectangle(50, 100, 400, 50);
		g2.fill(base);
		g2.setColor(Color.GREEN);
		Rectangle bar = new Rectangle(50, 100, (int)(progress*400), 50);
		g2.fill(bar);
		g2.setColor(Color.DARK_GRAY);
		g2.setStroke(new BasicStroke(5));
		g2.draw(base);
	}
	
	public TrainSet createTrainSet(int start, int end) throws IOException {
		
        TrainSet set = new TrainSet(28 * 28, 10);
        
        images.setCurrentIndex(start);
        labels.setCurrentIndex(start);
        
        for(int i = start; i <= end; i++) {
            progress = ((double) i) / (end - start);

            double[] output = new double[10];

            output[labels.readLabel()] = 1;

            set.addData(images.readImageDouble(), output);
            repaint();
        }
        return set;
    }
	
	public void startTraining() throws IOException {
		header.setText("Gathering Training Data");
		TrainSet set = createTrainSet(1, 10_000);
		
		header.setText("Training Network");
		trainData(network, set, 10, 100, 100);
		
		header.setText("Done");
	}
	
	public void trainData(Network net,TrainSet set, int trainLoops, int batchLoops, int batchSize) {
        for(int i = 0; i < trainLoops; i++) {
            net.train(set, batchLoops, batchSize);
            progress = ((double) i) / trainLoops;
            repaint();
        }
    }
	
	@Override
	public void close() throws Exception {
		images.close();
		labels.close();
	}
	
	
	public static void main(String[] args ) {
		JFrame frame = new JFrame();
		frame.setTitle("Network Trainer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Network net = new Network();
		NetTrainer2 trainer = new NetTrainer2(net);
		frame.setContentPane(trainer);
		frame.setSize(trainer.getSize());
		
		frame.pack();
		frame.validate();
		frame.setVisible(true);
	}
}
