package mnist;


import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * MNIST database image file. Contains additional header information for the
 * number of rows and columns per each entry.
 * 
 */
public class MnistImageFile extends MnistDbFile {
    private int rows;
    private int cols;

    /**
     * Creates new MNIST database image file ready for reading.
     * 
     * @param name
     *            the system-dependent filename
     * @param mode
     *            the access mode
     * @throws IOException
     * @throws FileNotFoundException
     */
    public MnistImageFile(String name, String mode) throws FileNotFoundException, IOException {
        super(name, mode);

        // read header information
        rows = readInt();
        cols = readInt();
    }

    /**
     * Reads the image at the current position.
     * 
     * @return matrix representing the image
     * @throws IOException
     */
    public int[][] readImageMat() throws IOException {
        int[][] dat = new int[getRows()][getCols()];
        for (int i = 0; i < getCols(); i++) {
            for (int j = 0; j < getRows(); j++) {
                dat[i][j] = readUnsignedByte();
            }
        }
        return dat;
    }
    
    /**
     * Reads the image at the current position
     * 
     * @return an array containing each pixel value of the image from 0 to 255, inclusive
     * @throws IOException
     */
    public int[] readImage() throws IOException {
    	int[] imgArr = new int[getRows() * getCols()];
    	for (int i = 0; i < imgArr.length; i++) {
    		imgArr[i] = readUnsignedByte();
    	}
    	return imgArr;
    }
    
    /**
     * Reads the image at the current position
     * 
     * @return an array containing each pixel value of the image from 0 to 1, inclusive
     * @throws IOException
     */
    public double[] readImageDouble() throws IOException {
    	double[] imgArr = new double[getRows() * getCols()];
    	for (int i = 0; i < imgArr.length; i++) {
    		imgArr[i] = readUnsignedByte() / 255d;
    	}
    	return imgArr;
    }

    @Override
    protected int getMagicNumber() {
        return 2051;
    }

    /**
     * Number of rows per image.
     * 
     * @return int
     */
    public int getRows() {
        return rows;
    }

    /**
     * Number of columns per image.
     * 
     * @return int
     */
    public int getCols() {
        return cols;
    }

    @Override
    public int getEntryLength() {
        return cols * rows;
    }

    @Override
    public int getHeaderSize() {
        return super.getHeaderSize() + 8; // to more integers - rows and columns
    }
}