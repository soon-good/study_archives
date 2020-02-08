package enumexample.basic.enums.badpattern;

public class BadPatternClient {
	public static void main(String [] args){
		for(DeviceType d : DeviceType.values()){
			System.out.println("[device : " + d + "] :: " + d.getDeviceTypeCd());
		}
	}
}
