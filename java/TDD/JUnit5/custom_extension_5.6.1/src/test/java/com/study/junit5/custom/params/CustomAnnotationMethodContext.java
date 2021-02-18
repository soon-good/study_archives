package com.study.junit5.custom.params;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.aggregator.DefaultArgumentsAccessor;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.DefaultArgumentConverter;
import org.junit.jupiter.params.support.AnnotationConsumerInitializer;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.commons.util.StringUtils;

public class CustomAnnotationMethodContext {
	private final Parameter[] parameters;
	private final CustomAnnotationMethodContext.Resolver[] resolvers;
	private final List<CustomAnnotationMethodContext.ResolverType> resolverTypes;

	CustomAnnotationMethodContext(Method testMethod) {
		this.parameters = testMethod.getParameters();
		this.resolvers = new CustomAnnotationMethodContext.Resolver[this.parameters.length];
		this.resolverTypes = new ArrayList(this.parameters.length);
		Parameter[] var2 = this.parameters;
		int var3 = var2.length;

		for(int var4 = 0; var4 < var3; ++var4) {
			Parameter parameter = var2[var4];
			this.resolverTypes.add(isAggregator(parameter) ? CustomAnnotationMethodContext.ResolverType.AGGREGATOR : CustomAnnotationMethodContext.ResolverType.CONVERTER);
		}

	}

	private static boolean isAggregator(Parameter parameter) {
		return ArgumentsAccessor.class.isAssignableFrom(parameter.getType()) || AnnotationUtils
			.isAnnotated(parameter, AggregateWith.class);
	}

	boolean hasPotentiallyValidSignature() {
		int indexOfPreviousAggregator = -1;

		for(int i = 0; i < this.getParameterCount(); ++i) {
			if (this.isAggregator(i)) {
				if (indexOfPreviousAggregator != -1 && i != indexOfPreviousAggregator + 1) {
					return false;
				}

				indexOfPreviousAggregator = i;
			}
		}

		return true;
	}

	int getParameterCount() {
		return this.parameters.length;
	}

	Optional<String> getParameterName(int parameterIndex) {
		if (parameterIndex >= this.getParameterCount()) {
			return Optional.empty();
		} else {
			Parameter parameter = this.parameters[parameterIndex];
			if (!parameter.isNamePresent()) {
				return Optional.empty();
			} else {
				return this.hasAggregator() && parameterIndex >= this.indexOfFirstAggregator() ? Optional.empty() : Optional.of(parameter.getName());
			}
		}
	}

	boolean hasAggregator() {
		return this.resolverTypes.contains(CustomAnnotationMethodContext.ResolverType.AGGREGATOR);
	}

	boolean isAggregator(int parameterIndex) {
		return this.resolverTypes.get(parameterIndex) == CustomAnnotationMethodContext.ResolverType.AGGREGATOR;
	}

	int indexOfFirstAggregator() {
		return this.resolverTypes.indexOf(CustomAnnotationMethodContext.ResolverType.AGGREGATOR);
	}

	Object resolve(ParameterContext parameterContext, Object[] arguments) {
		return this.getResolver(parameterContext).resolve(parameterContext, arguments);
	}

	private CustomAnnotationMethodContext.Resolver getResolver(ParameterContext parameterContext) {
		int index = parameterContext.getIndex();
		if (this.resolvers[index] == null) {
			this.resolvers[index] = ((CustomAnnotationMethodContext.ResolverType)this.resolverTypes.get(index)).createResolver(parameterContext);
		}

		return this.resolvers[index];
	}

	private static ParameterResolutionException parameterResolutionException(String message, Exception cause, ParameterContext parameterContext) {
		String fullMessage = message + " at index " + parameterContext.getIndex();
		if (StringUtils.isNotBlank(cause.getMessage())) {
			fullMessage = fullMessage + ": " + cause.getMessage();
		}

		return new ParameterResolutionException(fullMessage, cause);
	}

	static class Aggregator implements CustomAnnotationMethodContext.Resolver {
		private static final CustomAnnotationMethodContext.Aggregator DEFAULT = new CustomAnnotationMethodContext.Aggregator((accessor, context) -> {
			return accessor;
		});
		private final ArgumentsAggregator argumentsAggregator;

		Aggregator(ArgumentsAggregator argumentsAggregator) {
			this.argumentsAggregator = argumentsAggregator;
		}

		public Object resolve(ParameterContext parameterContext, Object[] arguments) {
			DefaultArgumentsAccessor accessor = new DefaultArgumentsAccessor(arguments);

			try {
				return this.argumentsAggregator.aggregateArguments(accessor, parameterContext);
			} catch (Exception var5) {
				throw CustomAnnotationMethodContext.parameterResolutionException("Error aggregating arguments for parameter", var5, parameterContext);
			}
		}
	}

	static class Converter implements CustomAnnotationMethodContext.Resolver {
		private static final CustomAnnotationMethodContext.Converter DEFAULT;
		private final ArgumentConverter argumentConverter;

		Converter(ArgumentConverter argumentConverter) {
			this.argumentConverter = argumentConverter;
		}

		public Object resolve(ParameterContext parameterContext, Object[] arguments) {
			Object argument = arguments[parameterContext.getIndex()];

			try {
				return this.argumentConverter.convert(argument, parameterContext);
			} catch (Exception var5) {
				throw CustomAnnotationMethodContext.parameterResolutionException("Error converting parameter", var5, parameterContext);
			}
		}

		static {
			DEFAULT = new CustomAnnotationMethodContext.Converter(DefaultArgumentConverter.INSTANCE);
		}
	}

	interface Resolver {
		Object resolve(ParameterContext var1, Object[] var2);
	}

	static enum ResolverType {
		CONVERTER {
			CustomAnnotationMethodContext.Resolver createResolver(ParameterContext parameterContext) {
				try {
					return (CustomAnnotationMethodContext.Resolver)AnnotationUtils.findAnnotation(parameterContext.getParameter(), ConvertWith.class).map(ConvertWith::value).map((clazz) -> {
						return (ArgumentConverter) ReflectionUtils.newInstance(clazz, new Object[0]);
					}).map((converter) -> {
						return (ArgumentConverter) AnnotationConsumerInitializer
							.initialize(parameterContext.getParameter(), converter);
					}).map(CustomAnnotationMethodContext.Converter::new).orElse(CustomAnnotationMethodContext.Converter.DEFAULT);
				} catch (Exception var3) {
					throw CustomAnnotationMethodContext.parameterResolutionException("Error creating ArgumentConverter", var3, parameterContext);
				}
			}
		},
		AGGREGATOR {
			CustomAnnotationMethodContext.Resolver createResolver(ParameterContext parameterContext) {
				try {
					return (CustomAnnotationMethodContext.Resolver)AnnotationUtils.findAnnotation(parameterContext.getParameter(), AggregateWith.class).map(AggregateWith::value).map((clazz) -> {
						return (ArgumentsAggregator) ReflectionSupport.newInstance(clazz, new Object[0]);
					}).map(CustomAnnotationMethodContext.Aggregator::new).orElse(CustomAnnotationMethodContext.Aggregator.DEFAULT);
				} catch (Exception var3) {
					throw CustomAnnotationMethodContext.parameterResolutionException("Error creating ArgumentsAggregator", var3, parameterContext);
				}
			}
		};

		private ResolverType() {
		}

		abstract CustomAnnotationMethodContext.Resolver createResolver(ParameterContext var1);
	}
}
