package com.study.junit5.custom.params;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CustomAnnotationParameterResolver implements ParameterResolver {

	private final CustomAnnotationMethodContext methodContext;
	private final Object[] arguments;

	public CustomAnnotationParameterResolver(CustomAnnotationMethodContext methodContext,
		Object[] arguments) {
		this.methodContext = methodContext;
		this.arguments = arguments;
	}

	public boolean supportsParameter(
		ParameterContext parameterContext, ExtensionContext extensionContext) {
		Executable declaringExecutable = parameterContext.getDeclaringExecutable();
		Method testMethod = (Method)extensionContext.getTestMethod().orElse(null);
		int parameterIndex = parameterContext.getIndex();
		if (!declaringExecutable.equals(testMethod)) {
			return false;
		} else if (this.methodContext.isAggregator(parameterIndex)) {
			return true;
		} else if (this.methodContext.hasAggregator()) {
			return parameterIndex < this.methodContext.indexOfFirstAggregator();
		} else {
			return parameterIndex < this.arguments.length;
		}
	}

	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return this.methodContext.resolve(parameterContext, this.arguments);
	}
}
