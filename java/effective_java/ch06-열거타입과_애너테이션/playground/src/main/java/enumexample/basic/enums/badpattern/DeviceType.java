package enumexample.basic.enums.badpattern;

public enum DeviceType {
	RAM, CPU;

	public int getDeviceTypeCd(){
		return ordinal() + 1;
	}
}
