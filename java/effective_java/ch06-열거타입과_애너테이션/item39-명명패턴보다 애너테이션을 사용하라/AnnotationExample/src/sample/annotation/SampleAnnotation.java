package sample.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited // 자식클래스가 있을 경우 상속받을 수 있다.
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SampleAnnotation {
	String value() default "Hello, It's Sample Annotation. It's default String value";
}
