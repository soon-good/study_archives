package enumexample.basic.enums.iteration;

public class IterationClient {
	public static void main(String [] args){
		for(DeviceType device : DeviceType.values()){
			System.out.println("[device : " + device + "] :: " + device.getDeviceTypeCd());
		}
	}
}
