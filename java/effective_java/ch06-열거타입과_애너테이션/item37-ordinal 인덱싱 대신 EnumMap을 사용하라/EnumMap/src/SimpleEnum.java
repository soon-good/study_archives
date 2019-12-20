public class SimpleEnum {
	public enum DeviceType{
		PCS(1, "PCS"){

		},
		BMS(2, "BMS"){

		};

		private int deviceTypeCd;
		private String deviceTypeNm;

		DeviceType(int deviceTypeCd, String deviceTypeNm){
			this.deviceTypeCd = deviceTypeCd;
			this.deviceTypeNm = deviceTypeNm;
		}
	}

	public static void main(String [] args){
		DeviceType d1 = DeviceType.valueOf("PCS");	// 동작
		System.out.println(d1);
//		DeviceType d2 = DeviceType.valueOf(1); 		// 컴파일 에러
	}
}
