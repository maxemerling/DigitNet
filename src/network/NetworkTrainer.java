package network;

import mnist.MnistImageFile;
import mnist.MnistLabelFile;

public class NetworkTrainer {
	
	private MnistImageFile images;
	private MnistLabelFile labels;
	
	public NetworkTrainer(MnistImageFile imageFile, MnistLabelFile labelFile) {
		images = imageFile;
		labels = labelFile;
	}
}
