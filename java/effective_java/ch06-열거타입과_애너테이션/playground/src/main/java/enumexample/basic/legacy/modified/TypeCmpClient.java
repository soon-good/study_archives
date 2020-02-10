package enumexample.basic.legacy.modified;

public class TypeCmpClient {
	public static void main(String [] args){
		DeviceType laptop = DeviceType.LAPTOP;
		DeviceType smartphone = DeviceType.SMARTPHONE;

		boolean isEqual = laptop.equals(smartphone);

		System.out.println("Is type " + laptop + " equals to " + smartphone + " ? " + isEqual);
	}
}
