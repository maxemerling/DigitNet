package network;

public class NetworkTools {
	
	/** Every value in array is initVal */
	public static double[] createArray(int size, int initVal) {
		if (size < 1) return null;
		double[] array = new double[size];
		for (int i = 0; i < size; i++) {
			array[i] = initVal;
		}
		return array;
	}
	
	/** Every value chosen randomly between two bounds */
	public static double[] createRandomArray(int size, double lowerBound, double upperBound) {
		if (size < 1) return null;
		double[] array = new double[size];
		for (int i = 0; i < size; i++) {
			array[i] = randomValue(lowerBound, upperBound);
		}
		return array;
	}
	
	/** Two dimensional version of previous method */
	public static double[][] createRandomArray(int sizeX, int sizeY, double lowerBound, double upperBound) {
		if (sizeX < 1 || sizeY < 1) return null;
		double[][] array = new double[sizeX][sizeY];
		for (int i = 0; i < sizeX; i++) {
			array[i] = createRandomArray(sizeY, lowerBound, upperBound);
		}
		return array;
	}
	
	/** Returns random number greater than or equal to lowerBound and less than upperBound */
	public static double randomValue(double lowerBound, double upperBound) {
		return (Math.random() * (upperBound - lowerBound)) + lowerBound;
	}
	
	/**
	 * Generates list of random numbers greater than or equal to lowerBound and less than upperBound
	 * No number is repeated
	 */
	public static Integer[] randomValues(int lowerBound, int upperBound, int amount) {
		if (amount >= (upperBound - lowerBound)) return null;
		
		//make it an Integer array (rather than int) so that values are initialized to null instead of 0
		//that way, 0 won't be falsely seen as a duplicate value
		Integer[] values = new Integer[amount];
		for (int i = 0; i < amount; i++) {
			int n;
			do {
				n = (int) randomValue(lowerBound, upperBound);
			} while (containsValue(values, n));
			values[i] = n;
		}
		return values;
	}

	public static <T extends Comparable<T>> boolean containsValue(T[] arr, T value) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].compareTo(value) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static int highestValueIndex(double[] values) {
		int idx = 0;
		for (int i = 1; i < values.length; i++) {
			if (values[i] > values[idx]) {
				idx = i;
			}
		}
		return idx;
	}
}
