package enumexample.basic.legacy.modified;

import lombok.Getter;

@Getter
public enum CpuType {

	ARM("ARM", 1){

	},
	INTEL("INTEL", 2){

	},
	AMD("AMD", 3){

	};

	private String cpuTypeNm;
	private int cpuTypeCd;

	CpuType(String cpuTypeNm, int cpuTypeCd){
		this.cpuTypeNm = cpuTypeNm;
		this.cpuTypeCd = cpuTypeCd;
	}

}
