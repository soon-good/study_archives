package com.study.junit5.custom.params;

import com.study.junit5.custom.annotation.CustomAnnotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.AnnotationConsumerInitializer;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;


public class CustomAnnotationExtension implements TestTemplateInvocationContextProvider {

	private static final String METHOD_CONTEXT_KEY = "context";

	CustomAnnotationExtension(){

	}

	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		System.out.println(" === supportsTestTemplate === ");
		if (!context.getTestMethod().isPresent()) {
			return false;
		} else {
			Method testMethod = (Method)context.getTestMethod().get();
			if (!AnnotationUtils.isAnnotated(testMethod, CustomAnnotation.class)) {
				return false;
			} else {
				CustomAnnotationMethodContext methodContext = new CustomAnnotationMethodContext(testMethod);
				Preconditions.condition(methodContext.hasPotentiallyValidSignature(), () -> {
					return String.format("@ParameterizedTest method [%s] declares formal parameters in an invalid order: argument aggregators must be declared after any indexed arguments and before any arguments resolved by another ParameterResolver.", testMethod.toGenericString());
				});
				this.getStore(context).put("context", methodContext);
				return true;
			}
		}
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
		ExtensionContext extensionContext) {
		Method templateMethod = extensionContext.getRequiredTestMethod();
		String displayName = extensionContext.getDisplayName();
		CustomAnnotationMethodContext methodContext = (CustomAnnotationMethodContext)this.getStore(extensionContext).get("context", CustomAnnotationMethodContext.class);
//		ParameterizedTestNameFormatter formatter = this.createNameFormatter(templateMethod, methodContext, displayName);
		AtomicLong invocationCount = new AtomicLong(0L);
		return
			(Stream)AnnotationUtils
				.findRepeatableAnnotations(templateMethod, ArgumentsSource.class)
				.stream()
				.map(ArgumentsSource::value)
				.map(ReflectionUtils::newInstance)
				.map((provider) -> {
					return (ArgumentsProvider) AnnotationConsumerInitializer
						.initialize(templateMethod, provider);
				})
				.flatMap((provider) -> {
					return arguments(provider, extensionContext);
				})
				.map(Arguments::get)
				.map((arguments) -> {
					return this.consumedArguments(arguments, methodContext);
				})
				.map((arguments) -> {
					return this.createInvocationContext(methodContext, arguments);
				})
				.peek(invocationContext -> invocationCount.incrementAndGet())
				.onClose(() -> {
					Preconditions.condition(invocationCount.get() > 0L, "Configuration error: You must configure at least one set of arguments for this @ParameterizedTest");
				});
	}

	private ArgumentsProvider instantiateArgumentsProvider(Class<? extends ArgumentsProvider> clazz) {
		try {
			return (ArgumentsProvider) ReflectionUtils.newInstance(clazz, new Object[0]);
		} catch (Exception var4) {
			if (var4 instanceof NoSuchMethodException) {
				String message = String.format("Failed to find a no-argument constructor for ArgumentsProvider [%s]. Please ensure that a no-argument constructor exists and that the class is either a top-level class or a static nested class", clazz.getName());
				throw new JUnitException(message, var4);
			} else {
				throw var4;
			}
		}
	}

	private Store getStore(ExtensionContext context) {
		return context.getStore(
			Namespace.create(new Object[]{CustomAnnotationExtension.class, context.getRequiredTestMethod()}));
	}

//	private TestTemplateInvocationContext createInvocationContext(Object[] arguments){
//		return new CustomAnnotationInvocationContext(arguments);
//	}

	private TestTemplateInvocationContext createInvocationContext(CustomAnnotationMethodContext methodContext, Object[] arguments){
//		return new ParameterizedTestInvocationContext(formatter, methodContext, arguments);
		return new CustomAnnotationInvocationContext(methodContext, arguments);
	}

	protected static Stream<? extends Arguments> arguments(ArgumentsProvider provider, ExtensionContext context) {
		try {
			return provider.provideArguments(context);
		}
		catch (Exception e) {
			throw ExceptionUtils.throwAsUncheckedException(e);
		}
	}

	private Object[] consumedArguments(Object[] arguments, CustomAnnotationMethodContext methodContext) {
		int parameterCount = methodContext.getParameterCount();
		return methodContext.hasAggregator() ? arguments : (arguments.length > parameterCount ? Arrays.copyOf(arguments, parameterCount) : arguments);
	}

}
