package enumexample.basic.legacy.modified;

import lombok.Getter;

@Getter
public enum DeviceType {
	LAPTOP("LAPTOP", CpuType.INTEL, RamType.SAMSUNG){

	},
	DESKTOP("DESKTOP", CpuType.INTEL, RamType.SAMSUNG){

	},
	SMARTPHONE("SMARTPHONE", CpuType.ARM, RamType.MICRON){

	};

	private String strDeviceTypeNm;
	private CpuType cpuType;
	private RamType ramType;

	DeviceType(String strDeviceTypeNm, CpuType cpuType, RamType ramType){

	}

}
