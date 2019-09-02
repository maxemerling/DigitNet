package network;

import java.io.Serializable;

public class Network implements Serializable {
	
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -1325102612616286747L;
	
	private double[][] outputs;
	private double[][][] weights;
	private double[][] biases;
	
	private double[][] errorSignal;
	private double[][] outputDerivative;
	
	public final int[] LAYER_SIZES;
	public final int NETWORK_SIZE;
	public final int INPUT_SIZE, OUTPUT_SIZE;
	
	public static final double DEFAULT_ETA = 0.3;
	private double eta; //how much the weights and biases shift by at each correction
	
	//* Network must be at least 2 layers long */
	public Network(int... sizes) {
		eta = DEFAULT_ETA;
		
		LAYER_SIZES = sizes;
		NETWORK_SIZE = LAYER_SIZES.length;
		INPUT_SIZE = LAYER_SIZES[0];
		OUTPUT_SIZE = LAYER_SIZES[NETWORK_SIZE - 1];
		
		outputs = new double[NETWORK_SIZE][]; //stand-in for a neuron class
		weights = new double[NETWORK_SIZE][][];
		biases = new double[NETWORK_SIZE][]; //each neuron has a bias except input layer
		
		errorSignal = new double[NETWORK_SIZE][];
		outputDerivative = new double[NETWORK_SIZE][];
		
		for (int i = 0; i < NETWORK_SIZE; i++) {
			outputs[i] = new double[LAYER_SIZES[i]];
			errorSignal[i] = new double[LAYER_SIZES[i]];
			outputDerivative[i] = new double[LAYER_SIZES[i]];
			
			biases[i] = NetworkTools.createRandomArray(LAYER_SIZES[i], 0.3, 0.7);
			
			if (i > 0) {
				weights[i] = NetworkTools.createRandomArray(LAYER_SIZES[i], LAYER_SIZES[i-1], 
						-0.3, 0.5); //weights from previous neurons
			}
		}
	}
	
	public void setLearningRate(double eta) {
		this.eta = eta;
	}
	
	//feed forward method
	public double[] calculate(double... input) {
		if (input.length != INPUT_SIZE) return null;
		outputs[0] = input;
		for (int layer = 1; layer < NETWORK_SIZE; layer++) {
			for (int neuron = 0; neuron < LAYER_SIZES[layer]; neuron++) {
				
				double sum = 0;
				
				for (int prevNeuron = 0; prevNeuron < LAYER_SIZES[layer - 1]; prevNeuron++) {
					sum += outputs[layer - 1][prevNeuron] * weights[layer][neuron][prevNeuron];
				}
				
				sum += biases[layer][neuron]; //add this neuron's bias
				
				outputs[layer][neuron] = sigmoid(sum);
				outputDerivative[layer][neuron] = sigmoidDerivative(sum);
				
				//would be more efficient to do this:
				//outputDerivative[layer][neuron] = outputs[layer][neuron] * (1 - outputs[layer][neuron]);
			}
		}
		
		return outputs[NETWORK_SIZE- 1];
	}
	
	public Network getReverseNetwork() {
		int[] newLayerSizes = new int[NETWORK_SIZE];
		for (int i = 0; i < NETWORK_SIZE; i++) {
			newLayerSizes[i] = LAYER_SIZES[NETWORK_SIZE - 1 - i];
		}
		Network newNet = new Network(newLayerSizes);
		//copy biases and weights
		for (int layer = 0; layer < NETWORK_SIZE; layer++) {
			newNet.biases[layer] = this.biases[NETWORK_SIZE - 1 - layer].clone();
			if (layer > 0) {
				for (int node = 0; node < newLayerSizes[layer]; node++) {
					for (int prevNode = 0; prevNode < newLayerSizes[layer-1]; prevNode++)
					newNet.weights[layer][node][prevNode] = 
						this.weights[NETWORK_SIZE - layer][prevNode][node];
				}
			}
		}
		return newNet;
	}
	
	public void train(TrainSet set, int loops, int batchSize) {
		if (set.INPUT_SIZE != this.INPUT_SIZE || set.TARGET_SIZE != this.OUTPUT_SIZE) return;
		for (int i = 0; i < loops; i++) {
			TrainSet batch = set.extractBatch(batchSize);
			for (int b = 0; b < batch.size(); b++) {
				this.train(batch.getInput(b), batch.getTarget(b));
			}
			System.out.println(avgMSE(batch));
		}
	}
	
	/**
	 * Trains the network
	 * @param input training data
	 * @param target target outputs
	 */
	public void train(double[] input, double[] target) {
		if (input.length != INPUT_SIZE || target.length != OUTPUT_SIZE) return;
		//calculate output and derivative for each neuron
		calculate(input);
		//get the error signal for each neuron (except input ones)
		backpropError(target);
		//update weights and biases based on backpropogation
		updateWeights(eta);
	}
	
	/**
	 * Mean squared error
	 */
	public double MSE(double[] input, double[] target) {
		if (input.length != INPUT_SIZE || target.length != OUTPUT_SIZE) return 0;
		calculate(input);
		double sum = 0;
		for (int i = 0; i < target.length; i++) {
			sum += Math.pow(target[i] - outputs[NETWORK_SIZE - 1][i], 2);
		}
		return sum / (2D * target.length);
	}
	
	/**
	 * Average of Mean Squared Error over all pieces of data
	 */
	public double avgMSE(TrainSet set) {
		double sum = 0;
		for (int i = 0; i < set.size(); i++) {
			sum += MSE(set.getInput(i), set.getTarget(i));
		}
		//now take average
		return sum / set.size();
	}
	
	public void backpropError(double[] target) {
		//output neurons
		for (int neuron = 0; neuron < LAYER_SIZES[NETWORK_SIZE - 1]; neuron++) {
			double error = outputs[NETWORK_SIZE - 1][neuron] - target[neuron];
			double derivative = outputDerivative[NETWORK_SIZE - 1][neuron];
			errorSignal[NETWORK_SIZE - 1][neuron] = error * derivative;
		}
		//hidden neurons
		//start at last hidden layer (one before the output layer)
		//end before input layer (layer 0)
		for (int layer = NETWORK_SIZE - 2; layer > 0; layer--) {
			for (int neuron = 0; neuron < LAYER_SIZES[layer]; neuron++) {
				double sum = 0;
				//loop through next downstream layer
				for (int nextNeuron = 0; nextNeuron < LAYER_SIZES[layer + 1]; nextNeuron++) {
					//[layer][neuron with weight][previous neuron]
					//neurons "own" the weights connecting them to the neurons in the previous layer
					sum += weights[layer + 1][nextNeuron][neuron] * errorSignal[layer + 1][nextNeuron];
				}
				//the error signal for this neuron is determined by the error signal of the next
				//neuron and the amount that this neuron affects the next neuron and contributes to
				//the next neuron's error signal (the weight between them)
				errorSignal[layer][neuron] = sum * outputDerivative[layer][neuron];
			}
		}
	}
	
	/**
	 * changes weights based on backpropogation error
	 * @param eta learning rate
	 */
	public void updateWeights(double eta) {
		//start at first hidden layer
		for (int layer = 1; layer < NETWORK_SIZE; layer++) {
			for (int neuron = 0; neuron < LAYER_SIZES[layer]; neuron++) {
				
				//update the bias for this neuron
				//no previous neuron output for the bias
				double deltaBias = -1 * eta * errorSignal[layer][neuron];
				biases[layer][neuron] += deltaBias;
				
				for (int prevNeuron = 0; prevNeuron < LAYER_SIZES[layer - 1]; prevNeuron++) {
					double deltaWeight = deltaBias * outputs[layer - 1][prevNeuron];
					weights[layer][neuron][prevNeuron] += deltaWeight;
				}
			}
		}
	}
	
	public static double sigmoid(double x) {
		return 1.0D / (1 + Math.exp(-x));
	}
	
	/** Derivative of sigmoid transfer function at x */
	public static double sigmoidDerivative(double x) {
		// dS/dx = S(1-S)
		double s = sigmoid(x);
		return s*(1-s);
	}
}
