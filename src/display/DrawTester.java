package display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import network.Network;

class Cell extends JPanel {
	
	double value;
	private DrawTester parent;
	ArrayList<Cell> sideCells, cornerCells;
	
	public Cell(DrawTester dt) {
		parent = dt;
		value = 0;
		sideCells = new ArrayList<Cell>();
		cornerCells = new ArrayList<Cell>();
	}
	
	public void paint() {
		value = 1;
		for (Cell cell : sideCells) {
			if (0.6 > cell.value) {
				cell.value = 0.8;
			}
		}
		
		for (Cell cell : cornerCells) {
			if (0.2 > cell.value) {
				cell.value = 0.2;
			}
		}
		
		parent.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		this.setBackground(new Color((int) (value * 255)));
		super.paintComponent(g);
	}
}

public class DrawTester extends JPanel {
	
	Cell[][] board;
	private int cellSize;
	private boolean drawing;
	
	/** size must be greater than 28 and divisible by 28 */
	public DrawTester(int size) throws IllegalArgumentException {
		
		drawing = false;
		
		if (size < 28 || size % 28 != 0) {
			throw new IllegalArgumentException();
		}
		
		cellSize = size / 28;
		this.setSize(size, size);
		board = new Cell[28][28];
		setLayout(new GridLayout(28, 28));
		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 28; j++) {
				board[i][j] = new Cell(this);
				board[i][j].setSize(cellSize, cellSize);
				
				board[i][j].addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						if (drawing) {
							((Cell) e.getSource()).paint();
						}
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mousePressed(MouseEvent e) {
						drawing = true;
						((Cell) e.getSource()).paint();
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						drawing = false;
						
						synchronized (DrawTester.class) {
							DrawTester.class.notify();
						}
					}
					
				});
				
				add(board[i][j]);
			}
		}
		
		for (int i = 0; i < 28; i++) {
			for (int j = 0; j < 28; j++) {
				if (i > 0) {
					board[i][j].sideCells.add(board[i-1][j]);
					if (j > 0) {
						board[i][j].cornerCells.add(board[i-1][j-1]);
					}
					if (j < 27) {
						board[i][j].cornerCells.add(board[i-1][j+1]);
					}
				}
				
				if (i < 27) {
					board[i][j].sideCells.add(board[i+1][j]);
					if (j > 0) {
						board[i][j].cornerCells.add(board[i+1][j-1]);
					}
					if (j < 27) {
						board[i][j].cornerCells.add(board[i+1][j+1]);
					}
				}
				
				if (j > 0) {
					board[i][j].sideCells.add(board[i][j-1]);
				}
				
				if (j < 27) {
					board[i][j].sideCells.add(board[i][j+1]);
				}
			}
		}
	}
	
	public void clear() {
		for (Cell[] row : board) {
			for (Cell cell : row) {
				cell.value = 0;
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		for (Cell[] row : board) {
			for (Cell cell : row) {
				cell.repaint();
			}
		}
		super.paintComponent(g);
	}
	
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame();
		DrawTester panel = new DrawTester(280);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 1));
		contentPane.add(panel, BorderLayout.WEST);
		contentPane.setSize(panel.getSize().width * 2, panel.getSize().height);
		
		
		//guesser panel
		JPanel panel2 = new JPanel();
		panel2.setSize(panel.getSize());
		JLabel numLabel = new JLabel();
		Font numFont = numLabel.getFont();
		int stringWidth = numLabel.getFontMetrics(numFont).stringWidth("0");
		//use height b/c digit is taller than it is wider
		double sizeRatio = 0.7 * panel2.getSize().width / stringWidth;
		int newFontSize = (int) (numFont.getSize() * sizeRatio);
		numLabel.setFont(new Font(numFont.getName(), Font.BOLD, newFontSize));
		panel2.setLayout(new BorderLayout(0, 0));
		panel2.add(numLabel, BorderLayout.CENTER);
		contentPane.add(panel2, BorderLayout.EAST);
		
		frame.setContentPane(contentPane);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(contentPane.getSize().width, contentPane.getSize().height + 40);
		frame.validate();
		frame.setVisible(true);
		
		JFrame testFrame = new JFrame();
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		class TestPanel extends JPanel {
			public double[] image = new double[28*28];
			
			public TestPanel() {
				super();
				setSize(280, 280);
			}
			
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				
				int sideLength = this.getWidth();
				
				int stretch = sideLength / 28;
				g.clearRect(0, 0, sideLength, sideLength);
				for (int i = 0; i < image.length; i++) {
					int x = (i % 28) * stretch;
					int y = (i / 28) * stretch;
					g2.setColor(new Color((int) (image[i] * 255)));
					g2.fillRect(x, y, stretch, stretch);
				}
			}
		}
		
		TestPanel testPanel = new TestPanel();
		testFrame.setContentPane(testPanel);
		testFrame.setSize(280, 280);
		testFrame.validate();
		testFrame.setVisible(true);
		
		String myPath = new File("").getAbsolutePath();
		
		//RECOVER NETWORK net3.ser
		Network net = (Network) Serializer.deserialize(myPath + File.separator + "networks" + File.separator + "net3" + Serializer.EXTENSION);
		//Network net = new Network(784, 200, 100, 10);
		
		//run
		while(true) {
			synchronized (DrawTester.class) {
				DrawTester.class.wait();
			}
			//read off outputs
			double[] input = new double[784];
			for (int i = 0; i < 784; i++) {
				input[i] = panel.board[i / 28][i % 28].value;
			}
			testPanel.image = input;
			testPanel.repaint();
			double[] output = net.calculate(input);
			
			int guess = 0;
			for (int i = 1; i < output.length; i++) {
				System.out.printf("%d: %.2f%% | ", i, output[i]*100);
				if (output[i] > output[guess]) {
					guess = i;
				}
			}
			System.out.print("\n");
			
			numLabel.setText("" + guess);
			System.out.printf("Guess: %d, Confidence: %.2f%%%n", guess, output[guess]*100);
			panel.clear();
			panel.repaint();
		}
	}
}
