package enumexample.basic.enums.iteration;

public enum DeviceType {
	CPU(1, "CPU"){

	},
	RAM(2, "RAM"){

	};

	private int deviceTypeCd;
	private String deviceTypeNm;

	DeviceType(int deviceTypeCd, String deviceTypeNm){
		this.deviceTypeCd = deviceTypeCd;
		this.deviceTypeNm = deviceTypeNm;
	}

	public String getDeviceTypeNm(){
		return deviceTypeNm;
	}
}
