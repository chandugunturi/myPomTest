package sample;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class AbstractMyStringBuilderTest {
    private final int javaVersion = Integer.parseInt(System.getProperty("mystring.compiled.release"));

    @Test
    void exposesEveryPublicBuilderInstanceOverload() throws ReflectiveOperationException {
        for (Method method : StringBuilder.class.getMethods()) {
            if (method.getDeclaringClass() == Object.class || Modifier.isStatic(method.getModifiers())
                    || method.isBridge() || method.isSynthetic()) {
                continue;
            }

            Method contract = IMyStringBuilder.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            Method wrapper = MyStringBuilder.class.getMethod(method.getName(), method.getParameterTypes());
            Class<?> expected = method.getReturnType() == StringBuilder.class
                    ? IMyStringBuilder.class : method.getReturnType();
            assertEquals(expected, contract.getReturnType(), method.toString());
            assertEquals(contract.getGenericReturnType().getTypeName(),
                    wrapper.getGenericReturnType().getTypeName(), method.toString());
            assertArrayEquals(method.getExceptionTypes(), contract.getExceptionTypes(), method.toString());
        }
    }

    @Test
    void everyAppendAndInsertOverloadMutatesAndReturnsTheSameWrapper() throws ReflectiveOperationException {
        for (Method method : StringBuilder.class.getMethods()) {
            if ((!method.getName().equals("append") && !method.getName().equals("insert"))
                    || method.isBridge() || method.isSynthetic()) {
                continue;
            }

            StringBuilder expected = new StringBuilder("seed");
            IMyStringBuilder actual = new MyStringBuilder("seed");
            StringBuilder contained = actual.getValue();
            Object[] arguments = argumentsFor(method);
            method.invoke(expected, arguments);
            Method wrapper = IMyStringBuilder.class.getMethod(method.getName(), method.getParameterTypes());

            assertSame(actual, wrapper.invoke(actual, arguments), method.toString());
            assertSame(contained, actual.getValue(), method.toString());
            assertEquals(expected.toString(), actual.toString(), method.toString());
        }
    }

    @Test
    void editingAndSnapshotsMatchBuilderBehavior() {
        IMyStringBuilder actual = new MyStringBuilder("abcdef");
        StringBuilder expected = new StringBuilder("abcdef");
        String snapshot = actual.substring(1, 4);
        CharSequence subsequence = actual.subSequence(1, 4);

        expected.delete(1, 3).deleteCharAt(1).replace(0, 1, "XY").reverse();
        assertSame(actual, actual.delete(1, 3).deleteCharAt(1).replace(0, 1, "XY").reverse());
        assertEquals(expected.toString(), actual.toString());
        assertEquals("bcd", snapshot);
        assertEquals("bcd", subsequence.toString());

        expected.setCharAt(0, 'Z');
        actual.setCharAt(0, 'Z');
        expected.setLength(8);
        actual.setLength(8);
        assertEquals(expected.toString(), actual.toString());
        assertEquals(expected.substring(2), actual.substring(2));
        assertEquals(expected.indexOf("X"), actual.indexOf("X"));
        assertEquals(expected.indexOf("X", 1), actual.indexOf("X", 1));
        assertEquals(expected.lastIndexOf("X"), actual.lastIndexOf("X"));
        assertEquals(expected.lastIndexOf("X", 2), actual.lastIndexOf("X", 2));
    }

    @Test
    void constructorsAndCapacityOperationsMatchBuilderBehavior() {
        assertEquals(new StringBuilder().capacity(), new MyStringBuilder().capacity());
        IMyStringBuilder actual = new MyStringBuilder(2);
        StringBuilder expected = new StringBuilder(2);
        expected.ensureCapacity(100);
        actual.ensureCapacity(100);
        assertEquals(expected.capacity(), actual.capacity());
        actual.append("abc");
        expected.append("abc");
        actual.trimToSize();
        expected.trimToSize();
        assertEquals(expected.capacity(), actual.capacity());

        StringBuilder original = new StringBuilder("original");
        IMyStringBuilder copy = new MyStringBuilder((CharSequence) original);
        original.append(" changed");
        assertEquals("original", copy.toString());
    }

    @Test
    void unicodeCopiesAndStreamsMatchBuilderBehavior() {
        StringBuilder expected = new StringBuilder("A\uD83D\uDE00B");
        IMyStringBuilder actual = new MyStringBuilder(expected.toString());
        expected.appendCodePoint(0x1F600);
        assertSame(actual, actual.appendCodePoint(0x1F600));
        assertEquals(expected.toString(), actual.toString());
        assertEquals(expected.charAt(1), actual.charAt(1));
        assertEquals(expected.codePointAt(1), actual.codePointAt(1));
        assertEquals(expected.codePointBefore(3), actual.codePointBefore(3));
        assertEquals(expected.codePointCount(0, expected.length()), actual.codePointCount(0, actual.length()));
        assertEquals(expected.offsetByCodePoints(1, 2), actual.offsetByCodePoints(1, 2));
        assertArrayEquals(expected.chars().toArray(), actual.chars().toArray());
        assertArrayEquals(expected.codePoints().toArray(), actual.codePoints().toArray());

        char[] expectedChars = new char[7];
        char[] actualChars = new char[7];
        expected.getChars(1, 4, expectedChars, 2);
        actual.getChars(1, 4, actualChars, 2);
        assertArrayEquals(expectedChars, actualChars);
        expected.reverse();
        assertSame(actual, actual.reverse());
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void newerApisAreEnabledOnlyInTheirVersionedArtifacts() {
        IMyStringBuilder actual = new MyStringBuilder("abc");
        if (javaVersion >= 15) {
            assertEquals(new StringBuilder("abc").compareTo(new StringBuilder("abd")),
                    actual.compareTo(new StringBuilder("abd")));
            assertFalse(actual.isEmpty());
            actual.setLength(0);
            assertTrue(actual.isEmpty());
        } else {
            assertUnsupported("compareTo", () -> actual.compareTo(new StringBuilder("abd")));
            assertUnsupported("isEmpty", () -> actual.isEmpty());
        }

        String beforeRepeat = actual.toString();
        if (javaVersion >= 25) {
            StringBuilder expected = new StringBuilder(beforeRepeat);
            expected.repeat(0x1F600, 2).repeat("xy", 3);
            assertSame(actual, actual.repeat(0x1F600, 2).repeat("xy", 3));
            assertEquals(expected.toString(), actual.toString());
            assertThrows(IllegalArgumentException.class, () -> actual.repeat('a', -1));
        } else {
            assertUnsupported("repeat", () -> actual.repeat(0x1F600, 2));
            assertUnsupported("repeat", () -> actual.repeat("xy", 3));
            assertEquals(beforeRepeat, actual.toString());
        }
    }

    @Test
    void nullsExceptionsAndIdentityMatchMutableBuilderSemantics() {
        IMyStringBuilder actual = new MyStringBuilder("abc");
        StringBuilder expected = new StringBuilder("abc");
        expected.append((String) null).insert(0, (CharSequence) null);
        actual.append((String) null).insert(0, (CharSequence) null);
        assertEquals(expected.toString(), actual.toString());
        assertNotEquals(actual, new MyStringBuilder(actual.toString()));
        assertNotEquals(actual, actual.getValue());
        assertThrows(NullPointerException.class, () -> new MyStringBuilder((String) null));
        assertThrows(NegativeArraySizeException.class, () -> new MyStringBuilder(-1));
        assertThrows(StringIndexOutOfBoundsException.class, () -> actual.deleteCharAt(-1));
        assertThrows(StringIndexOutOfBoundsException.class, () -> actual.substring(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> actual.append(new char[] {'a'}, 0, 2));
    }

    private static void assertUnsupported(String method, Runnable invocation) {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, invocation::run);
        assertTrue(exception.getMessage().contains(method), exception.getMessage());
    }

    private static Object[] argumentsFor(Method method) {
        Class<?>[] types = method.getParameterTypes();
        Object[] arguments = new Object[types.length];
        for (int index = 0; index < types.length; index++) {
            Class<?> type = types[index];
            if (type == int.class) {
                if (method.getName().equals("insert") && index == 0) {
                    arguments[index] = 1;
                } else if (types.length > 2) {
                    arguments[index] = index == types.length - 1 ? 1 : 0;
                } else {
                    arguments[index] = 7;
                }
            } else if (type == char[].class) {
                arguments[index] = new char[] {'x', 'y'};
            } else if (type == StringBuffer.class) {
                arguments[index] = new StringBuffer("xy");
            } else if (type == boolean.class) {
                arguments[index] = true;
            } else if (type == char.class) {
                arguments[index] = 'Z';
            } else if (type == long.class) {
                arguments[index] = 9L;
            } else if (type == float.class) {
                arguments[index] = 1.5F;
            } else if (type == double.class) {
                arguments[index] = 2.5;
            } else {
                arguments[index] = "xy";
            }
        }
        return arguments;
    }
}
