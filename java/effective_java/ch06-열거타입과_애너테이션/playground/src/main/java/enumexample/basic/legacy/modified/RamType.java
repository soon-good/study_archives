package enumexample.basic.legacy.modified;

import lombok.Getter;

@Getter
public enum RamType {
	SAMSUNG("SAMSUNG", 1){

	},
	MICRON("MICRON", 2){

	};

	private String ramTypeNm;
	private int ramTypeCd;

	RamType(String ramTypeNm, int ramTypeCd){
		this.ramTypeNm = ramTypeNm;
		this.ramTypeCd = ramTypeCd;
	}
}
