package sample.context;

import java.lang.reflect.Method;
import sample.annotation.SampleAnnotation;

public class MyContextContainer {
	public MyContextContainer(){}

	private <T> T invokeAnnotations(T instance) throws IllegalAccessException{
		Method [] methods = instance.getClass().getDeclaredMethods();

		for(Method method : methods){
			SampleAnnotation annotation = method.getAnnotation(SampleAnnotation.class);
			if(annotation != null){
				System.out.println(annotation.value());
			}
		}

		return instance;
	}

	public <T> T get(Class clazz) throws IllegalAccessException, InstantiationException{
		T instance = (T) clazz.newInstance();
		instance = invokeAnnotations(instance);
		return instance;
	}
}
