package display;

import java.io.File;
import java.io.IOException;

import mnist.MnistImageFile;
import mnist.MnistLabelFile;
import network.Network;
import network.NetworkTools;
import network.TrainSet;

public class MnistTrainer1 implements AutoCloseable {
	
	private MnistImageFile images;
	private MnistLabelFile labels;
	
	public MnistTrainer1(MnistImageFile images, MnistLabelFile labels) {
		this.images = images;
		this.labels = labels;
	}
	
    public static void main(String[] args) throws Exception {
    	String path = new File("").getAbsolutePath() + "\\res\\";

        MnistImageFile m = new MnistImageFile(path + "trainImage.idx3-ubyte", "rw");
        MnistLabelFile l = new MnistLabelFile(path + "trainLabel.idx1-ubyte", "rw");
        
        MnistTrainer1 trainer = new MnistTrainer1(m, l);
    	
    	Network network = new Network(784, 80, 40, 10);
        TrainSet set = trainer.createTrainSet(1, 5000);
        trainData(network, set, 100, 50, 100);

        TrainSet testSet = trainer.createTrainSet(5001, 10_000);
        testTrainSet(network, testSet);
        
        trainer.close();
    }

    @Override
    public void close() throws Exception {
    	images.close();
    	labels.close();
    }
    
    public TrainSet createTrainSet(int start, int end) throws IOException {

        TrainSet set = new TrainSet(28 * 28, 10);
        
        images.setCurrentIndex(start);
        labels.setCurrentIndex(start);
        
        for(int i = start; i <= end; i++) {
            if(i % 100 ==  0){
                System.out.println("prepared: " + i);
            }

            double[] output = new double[10];

            output[labels.readLabel()] = 1;

            set.addData(images.readImageDouble(), output);
        }
         return set;
    }

    public static void trainData(Network net,TrainSet set, int trainLoops, int batchLoops, int batchSize) {
        for(int i = 0; i < trainLoops; i++) {
            net.train(set, batchLoops, batchSize);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>   " + (int) (i / ((double) trainLoops)) + "%   <<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }

    public static void testTrainSet(Network net, TrainSet set) {
        int correct = 0;
        for(int i = 0; i < set.size(); i++) {

            double guess = NetworkTools.highestValueIndex(net.calculate(set.getInput(i)));
            double answer = NetworkTools.highestValueIndex(set.getTarget(i));
            if(guess == answer) {
                correct++;
            }
        }
        System.out.println("Testing finished, RESULT: " + correct + " / " + set.size()+ "  ->  " + (double)correct / (double)set.size() +" %");
    }
}