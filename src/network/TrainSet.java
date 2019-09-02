package network;

import java.util.ArrayList;
import java.util.Arrays;

public class TrainSet {
	
	public final int INPUT_SIZE; //size of input vector
	public final int TARGET_SIZE; //size of target vector
	
	//double[][] -> first index: 0 = input array, 1 = target array
	private ArrayList<double[][]> data = new ArrayList<>();
	
	private int currIdx;
	
	public TrainSet(int inputSize, int outputSize) {
		INPUT_SIZE = inputSize;
		TARGET_SIZE = outputSize;
		currIdx = 0;
	}
	
	public void addData(double[] input, double[] target) {
		if (input.length != INPUT_SIZE || target.length != TARGET_SIZE) return;
		data.add(new double[][] {input, target});
	}
	
	/**
	 * Extracts a batch of size "size" by taking that many random data points from the set
	 */
	public TrainSet extractBatch(int size) {
		TrainSet batchSet = new TrainSet(INPUT_SIZE, TARGET_SIZE);
		for (int i = 0; i < size; i++) {
			batchSet.data.add(getNextDataPoint());
		}
		return batchSet;
	}
	
	private static double[][] getRandomDataPoint(TrainSet set) {
		return set.data.get((int) (Math.random() * set.size()));
	}
	
	private double[][] getNextDataPoint() {
		double[][] currPoint =  data.get(currIdx);
		currIdx++;
		currIdx %= size();
		return currPoint;
	}
	
	public String toString() {
		String setString = "Trainset [" + INPUT_SIZE + ", " + TARGET_SIZE + "]\n";
		for (int i = 0; i < data.size(); i++) {
			setString += i + ":   ";
			setString += Arrays.toString(data.get(i)[0]);
			setString += "  -->  ";
			setString += Arrays.toString(data.get(i)[1]);
			setString += "\n";
		}
		return setString;
	}
	
	public int size() { return data.size(); }
	
	public double[] getInput(int index) {
		try {
			return data.get(index)[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public double[] getTarget(int index) {
		try {
			return data.get(index)[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
}
