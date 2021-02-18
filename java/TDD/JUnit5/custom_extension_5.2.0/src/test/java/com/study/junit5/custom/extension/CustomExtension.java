package com.study.junit5.custom.extension;

import static org.junit.jupiter.params.aggregator.AggregationUtils.hasAggregator;
import static org.junit.platform.commons.util.AnnotationUtils.findRepeatableAnnotations;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;

import com.study.junit5.custom.annotation.CustomAnnotation;
import com.study.junit5.custom.params.CustomTestInvocationContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.params.aggregator.AggregationUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.AnnotationConsumerInitializer;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;

public class CustomExtension implements TestTemplateInvocationContextProvider {

	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		System.out.println(" === supportsTestTemplate === ");
		if (!context.getTestMethod().isPresent()){
			return false;
		}

		Method testMethod = context.getTestMethod().get();
		if (!isAnnotated(testMethod, CustomAnnotation.class)){
			return false;
		}

		Preconditions.condition(AggregationUtils.hasPotentiallyValidSignature(testMethod),
			() -> String.format(
				"@CustomAnnotation method [%s] declares formal parameters in an invalid order: "
					+ "argument aggregators must be declared after any indexed arguments "
					+ "and before any arguments resolved by another ParameterResolver.",
				testMethod.toGenericString()));

		return true;
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
		ExtensionContext context) {
		System.out.println(" === provideTestTemplateInvocationContexts === ");
		Method templateMethod = context.getRequiredTestMethod();

		/**
		 * context를 통해 얻어오는 testTemplateMethod 는 testCustomAnnotation 이다.
		 * (@Test, @CustomAnnotation 을 달아놓은 메서드)
		 */
		System.out.println(" >>> templateMethod :: " + templateMethod);
		AtomicLong invocationCount = new AtomicLong(0);

		Stream<TestTemplateInvocationContext> result = findRepeatableAnnotations(templateMethod,
			ArgumentsSource.class)
			.stream()
			.map(ArgumentsSource::value)
			.map(ReflectionUtils::newInstance)
			.map(provider -> AnnotationConsumerInitializer.initialize(templateMethod, provider))
			.flatMap(provider -> arguments(provider, context))
			.map(Arguments::get)
			.map(arguments -> consumedArguments(arguments, templateMethod))
			.map(arguments -> createInvocationContext(arguments))
			.peek(invocationContext -> invocationCount.incrementAndGet())
			.onClose(() ->
				Preconditions.condition(invocationCount.get() > 0, "test"));

		System.out.println("===== Stream<TestTemplateInvocationContext> ===== ");
//		result.forEach(ctx -> {
//			System.out.println(ctx.toString());
//		});

		return result;
	}

	private TestTemplateInvocationContext createInvocationContext(Object[] arguments){
		return new CustomTestInvocationContext();
	}

	protected static Stream<? extends Arguments> arguments(ArgumentsProvider provider, ExtensionContext context) {
		try {
			return provider.provideArguments(context);
		}
		catch (Exception e) {
			throw ExceptionUtils.throwAsUncheckedException(e);
		}
	}

	private Object[] consumedArguments(Object[] arguments, Method templateMethod) {
		int parameterCount = templateMethod.getParameterCount();
		return hasAggregator(templateMethod) ? arguments
			: (arguments.length > parameterCount ? Arrays.copyOf(arguments, parameterCount) : arguments);
	}
}
