package io.github.tapcard.emvnfccard.testutil;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Reflection helpers for calling non-public methods from tests.
 * Replaces PowerMock {@code Whitebox.invokeMethod}.
 */
public final class Reflect {

	private Reflect() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(final Object target, final String methodName, final Object... args) throws Exception {
		final Class<?>[] parameterTypes = typesOf(args);
		return (T) invoke(target, methodName, args, parameterTypes);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(final Object target, final String methodName, final Object[] args,
			final Class<?>[] parameterTypes) throws Exception {
		final Method method = findMethod(target.getClass(), methodName, parameterTypes);
		method.setAccessible(true);
		return (T) method.invoke(target, args);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeStatic(final Class<?> type, final String methodName, final Object... args)
			throws Exception {
		final Class<?>[] parameterTypes = typesOf(args);
		return (T) invokeStatic(type, methodName, args, parameterTypes);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeStatic(final Class<?> type, final String methodName, final Object[] args,
			final Class<?>[] parameterTypes) throws Exception {
		final Method method = findMethod(type, methodName, parameterTypes);
		method.setAccessible(true);
		return (T) method.invoke(null, args);
	}

	private static Class<?>[] typesOf(final Object[] args) {
		if (args == null) {
			return new Class<?>[0];
		}
		final Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) {
				throw new IllegalArgumentException(
						"Null argument at index " + i + " requires explicit parameterTypes");
			}
			types[i] = args[i].getClass();
		}
		return types;
	}

	private static Method findMethod(final Class<?> type, final String methodName, final Class<?>[] parameterTypes)
			throws NoSuchMethodException {
		Class<?> current = type;
		while (current != null) {
			for (final Method method : current.getDeclaredMethods()) {
				if (method.getName().equals(methodName) && compatible(method.getParameterTypes(), parameterTypes)) {
					return method;
				}
			}
			current = current.getSuperclass();
		}
		throw new NoSuchMethodException(type.getName() + "#" + methodName + Arrays.toString(parameterTypes));
	}

	private static boolean compatible(final Class<?>[] declared, final Class<?>[] actual) {
		if (declared.length != actual.length) {
			return false;
		}
		for (int i = 0; i < declared.length; i++) {
			if (actual[i] == null) {
				if (declared[i].isPrimitive()) {
					return false;
				}
				continue;
			}
			if (!wrap(declared[i]).isAssignableFrom(wrap(actual[i]))) {
				return false;
			}
		}
		return true;
	}

	private static Class<?> wrap(final Class<?> type) {
		if (!type.isPrimitive()) {
			return type;
		}
		if (type == boolean.class) {
			return Boolean.class;
		}
		if (type == byte.class) {
			return Byte.class;
		}
		if (type == char.class) {
			return Character.class;
		}
		if (type == short.class) {
			return Short.class;
		}
		if (type == int.class) {
			return Integer.class;
		}
		if (type == long.class) {
			return Long.class;
		}
		if (type == float.class) {
			return Float.class;
		}
		if (type == double.class) {
			return Double.class;
		}
		return type;
	}
}
