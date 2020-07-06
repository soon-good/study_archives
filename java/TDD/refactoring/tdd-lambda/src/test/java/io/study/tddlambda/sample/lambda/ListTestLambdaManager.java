package io.study.tddlambda.sample.lambda;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

public class ListTestLambdaManager {

	public static ListTestLambda<List> listTestLambda(){
		ListTestLambda<List> l = (list)->{
			list.add("1");
			verify(list, times(1)).add("1");
			verify(list).add("1");
		};

		return l;
	}
}
