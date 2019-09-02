package display;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import mnist.MnistImageFile;
import mnist.MnistLabelFile;
import network.Network;
import network.NetworkTools;
import network.TrainSet;

public class MnistTrainer implements AutoCloseable {
	
	public static final int INPUT_SIZE = 784; //28*28
	public static final int OUTPUT_SIZE = 10; //representing digits 0 through 9
	
	public static final double TRAIN_RATIO = 5d / 6; //amount of the data to be used for the trainset
	
	MnistImageFile images;
	MnistLabelFile labels;
	
	public MnistTrainer(MnistImageFile images, MnistLabelFile labels) {
		this.images = images;
		this.labels = labels;
	}
	
	@Override
	public void close() throws Exception {
		images.close();
		labels.close();
	}
	
	/**
	 * Start and end are inclusive
	 * Min start value is 1
	 * Max end value is 60_000
	 * end must be greater than or equal to start
	 * @throws IOException 
	 */
	public TrainSet getTrainSet(int start, int end) throws IOException {
		TrainSet set = new TrainSet(INPUT_SIZE, OUTPUT_SIZE);
		
		images.setCurrentIndex(start);
		labels.setCurrentIndex(start);
		
		for (int i = start; i <= end; i++) {
			double[] output = new double[OUTPUT_SIZE];
			output[labels.readLabel()] = 1;
			set.addData(images.readImageDouble(), output);
		}
		
		return set;
	}
	
	/**
	 * Train the network
	 * @param net the network to train
	 * @param data the set of training data
	 * @param trainLoops the number of times to train the network with the set
	 * @param setLoops the number of times to extract a different batch from the set
	 */
	public static void trainNetwork(Network net, TrainSet set, int trainLoops, int setLoops, int batchSize) {
		for (int i = 0; i < trainLoops; i++) {
			net.train(set, setLoops, batchSize);
			System.out.println("Training Progress:   <<<<<<<<<<<<<<<<   " + (int) (100d * i / trainLoops) + "%  >>>>>>>>>>>>>>>");
		}
	}
	
	public static void trainNetwork(Network net, TrainSet set, int trainLoops) {
		trainNetwork(net, set, trainLoops, 1, set.size());
	}
	
	/**
	 * Returns the frequency of the network being correct as a value from 0 to 1
	 */
	public static double testNetwork(Network net, TrainSet testSet) {
		int timesCorrect = 0;
		int totalTimes = testSet.size();
		for (int i = 0; i < totalTimes; i++) {
			double[] input = testSet.getInput(i);
			double[] target = testSet.getTarget(i);
			double[] output = net.calculate(input);
			int guess = NetworkTools.highestValueIndex(net.calculate(input));
			double confidence = output[guess];
			int answer = NetworkTools.highestValueIndex(target);
			if (guess == answer) {
				timesCorrect++;
			}
			if (i % 10 == 0) {
				System.out.println("Testing Progress:   <<<<<<<<<<<<<<<   " + (int) (100d * i / totalTimes) + "%   >>>>>>>>>>>>>>");
			}
		}
		return ((double) timesCorrect) / totalTimes;
	}
	
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		
		//note: hidden layers should move from input size to output size
		Network network = new Network(INPUT_SIZE, 70, 35, OUTPUT_SIZE);
		
		//set up mnist files
		String myPath = new File("").getAbsolutePath();
		MnistImageFile images = new MnistImageFile(myPath + "\\res\\trainImage.idx3-ubyte", "rw");
		MnistLabelFile labels = new MnistLabelFile(myPath + "\\res\\trainLabel.idx1-ubyte", "rw");
		
		//create MnistTrainer
		MnistTrainer trainer = new MnistTrainer(images, labels);
		
		//train the network with the first 5000 data points
		System.out.println("Creating train set");
		TrainSet trainData = trainer.getTrainSet(1, 10_000);
		System.out.println("Done creating train set");
		
		trainNetwork(network, trainData, 100, 100, 100);
		System.out.println("TRAINING DONE");
		
		//first test
		trainData = trainer.getTrainSet(10_001, 11_000);
		double rate = testNetwork(network, trainData);
		System.out.println("1ST TESTING DONE");
		System.out.println("Accuracy: " + rate);
		
		trainer.close();
		
		//serialize the network
		String fileName = myPath + "\\networks\\" + "net6" + Serializer.EXTENSION;
		Serializer.serialize(network, fileName);
		
		System.out.println("DONE");
		
		long endTime = System.currentTimeMillis();
		System.out.println("Time Elapsed: " + (endTime-startTime) / 1000d / 60 + " min");
	}
}
