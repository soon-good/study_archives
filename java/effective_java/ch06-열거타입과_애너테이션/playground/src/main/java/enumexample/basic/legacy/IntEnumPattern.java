package enumexample.basic.legacy;

import lombok.Getter;

@Getter
public class IntEnumPattern {
	public static final int ARM_CPU_TYPE_CD = 1;
	public static final int INTEL_CPU_TYPE_CD = 2;
	public static final int AMD_CPU_TYPE_CD = 3;

	public static final int SAMSUNG_RAM_TYPE_CD = 1;
	public static final int MICRON_RAM_TYPE_CD = 2;

	public static boolean equalsRamTypeCd(int ramTypeCd){
		if(ARM_CPU_TYPE_CD == ramTypeCd){	// 잘못된 비교 (CPU와 RAM을 비교하고 있다.)
			return true;
		}
		else{
			return false;
		}
	}

	public static void main(String [] args){
		System.out.println(equalsRamTypeCd(SAMSUNG_RAM_TYPE_CD)); // true
		// ARM CPU 코드 vs 삼성 RAM 코드 를 비교하고 있는데 true의 결과를 내고 있다.
	}
}