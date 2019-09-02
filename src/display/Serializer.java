package display;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
	
	public static final String EXTENSION = ".ser";
	
	/**
	 * Serializes an object
	 * @param obj the object to be serialized
	 * @param filePath full path to the object, including object name and the ".ser" file extension
	 */
	public static void serialize(Object obj, String filePath) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
			oos.writeObject(obj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deserializes the object located at the given path
	 * @param filePath full path to the object, including object name and the ".ser" file extension
	 * @return
	 */
	public static Object deserialize(String filePath) {
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			return ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
