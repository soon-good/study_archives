/**
 * Collection을 매개변수로 받아 test 하는 방식
 */
package simple;

import java.util.Arrays;
import java.util.Collection;
import simple.types.ExtendedOperation;
import simple.types.Operation;

// 2) Collection <? extends Operation> 을 넘기는 방법
public class App2 {

	public static void main(String [] args){
		double x = Double.parseDouble("11.1");
		double y = Double.parseDouble("1.1");
		test(Arrays.asList(ExtendedOperation.values()), x, y);
	}

	private static void test(Collection<? extends Operation> opSet,
		double x, double y){
		for(Operation op : opSet){
			System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x,y));
		}
	}
}
