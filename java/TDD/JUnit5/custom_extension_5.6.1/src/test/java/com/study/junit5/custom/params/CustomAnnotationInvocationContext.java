package com.study.junit5.custom.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

public class CustomAnnotationInvocationContext implements TestTemplateInvocationContext {

	private final CustomAnnotationMethodContext methodContext;
	private final Object[] arguments;

	CustomAnnotationInvocationContext(CustomAnnotationMethodContext methodContext, Object[] arguments){
		this.methodContext = methodContext;
		this.arguments = arguments;
	}

//	public CustomAnnotationInvocationContext(Object[] arguments) {
//		this.arguments = arguments;
//	}

	@Override
	public String getDisplayName(int invocationIndex) {
		return "Argument :: " + String.valueOf(invocationIndex) + " at CustomTestInvocationContext::getDisplayName";
	}

	@Override
	public List<Extension> getAdditionalExtensions() {
		System.out.println(" >>> getAdditionalExtensions");
		Arrays.stream(this.arguments)
			.forEach(argument->{
				System.out.println(">>> " + argument.toString());
			});
		return Collections
			.singletonList(new CustomAnnotationParameterResolver(this.methodContext, this.arguments));
	}
}
