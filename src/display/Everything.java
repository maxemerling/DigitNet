package display;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import network.Network;
import network.TrainSet;

public class Everything {
	private static boolean training = false;
	
	public static void main(String[] args) throws IOException {
		
		Network net = new Network(784, 80, 40, 10);
        NetTrainer2 trainer = new NetTrainer2(net);
        TrainSet set = trainer.createTrainSet(1, 5000);
        
        JFrame frame = new JFrame("Network");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(trainer, BorderLayout.NORTH);
        
        DrawTester drawTester = new DrawTester(280);
        frame.add(drawTester, BorderLayout.SOUTH);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Run");
        menuBar.add(menu);
        
        JMenuItem train = new JMenuItem(new AbstractAction("Train Network") {

			@Override
			public void actionPerformed(ActionEvent e) {
				training = true;
				trainer.trainData(net, set, 100, 50, 100);
			}
        	
        });
        
        menu.add(train);
        
        JMenuItem test = new JMenuItem("Test Network");
        menu.add(test);
        
        JMenuItem load = new JMenuItem("Load Network");
        menu.addSeparator();
        menu.add(load);
        
        frame.setJMenuBar(menuBar);
        
        
        frame.pack();
        frame.validate();
        frame.setVisible(true);
	}
}
