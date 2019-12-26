package simple;

import simple.types.ExtendedOperation;
import simple.types.Operation;

// 1) class 리터럴 사용하기
public class App1 {
	public static void main(String [] args){
		double x = Double.parseDouble("11.1");
		double y = Double.parseDouble("1.1");

		// ExtendedOperation의 class 리터럴을 넘기고 있다.
		// 이렇게 함으로써 확장된 연산을 어떤 것(ExtendedOperation)으로  지정할 지 알려준다.
		test(ExtendedOperation.class, x, y);
	}

	// 리턴타입 앞에 <T extends ...> 를 사용했다.
	// 매개변수 opEnumType이 "Enum 타입이면서 Operation 하위 타입이여야 한다"는 의미
	private static <T extends Enum<T> & Operation> void test (
		Class<T> opEnumType, double x, double y
	){
		for(Operation op : opEnumType.getEnumConstants()){
			System.out.printf("%f %s %f = %s%n", x, op, y, op.apply(x, y));
		}
	}
}
