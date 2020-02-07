package enumexample.basic.enums.iteration;

public class Main {
	public static void main(String [] args){
		for(DeviceType device : DeviceType.values()){
			System.out.println("Type :: " + device);
		}
	}
}
