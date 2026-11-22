package sample;

import java.util.stream.IntStream;

public abstract class AbstractMyStringBuilder implements IMyStringBuilder {
    private final StringBuilder value;

    protected AbstractMyStringBuilder() {
        value = new StringBuilder();
    }

    protected AbstractMyStringBuilder(int capacity) {
        value = new StringBuilder(capacity);
    }

    protected AbstractMyStringBuilder(String str) {
        value = new StringBuilder(str);
    }

    protected AbstractMyStringBuilder(CharSequence seq) {
        value = new StringBuilder(seq);
    }

    @Override
    public final StringBuilder getValue() {
        return value;
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("isEmpty requires Java 15 or later");
    }

    @Override
    public int capacity() {
        return value.capacity();
    }

    @Override
    public void ensureCapacity(int minimumCapacity) {
        value.ensureCapacity(minimumCapacity);
    }

    @Override
    public void trimToSize() {
        value.trimToSize();
    }

    @Override
    public void setLength(int newLength) {
        value.setLength(newLength);
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public int codePointAt(int index) {
        return value.codePointAt(index);
    }

    @Override
    public int codePointBefore(int index) {
        return value.codePointBefore(index);
    }

    @Override
    public int codePointCount(int beginIndex, int endIndex) {
        return value.codePointCount(beginIndex, endIndex);
    }

    @Override
    public int offsetByCodePoints(int index, int codePointOffset) {
        return value.offsetByCodePoints(index, codePointOffset);
    }

    @Override
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        value.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    @Override
    public void setCharAt(int index, char ch) {
        value.setCharAt(index, ch);
    }

    @Override
    public String substring(int start) {
        return value.substring(start);
    }

    @Override
    public String substring(int start, int end) {
        return value.substring(start, end);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public IntStream chars() {
        return value.chars();
    }

    @Override
    public IntStream codePoints() {
        return value.codePoints();
    }

    @Override
    public int compareTo(StringBuilder another) {
        throw new UnsupportedOperationException("compareTo requires Java 11 or later");
    }

    @Override
    public IMyStringBuilder append(Object obj) {
        value.append(obj);
        return this;
    }

    @Override
    public IMyStringBuilder append(String str) {
        value.append(str);
        return this;
    }

    @Override
    public IMyStringBuilder append(StringBuffer sb) {
        value.append(sb);
        return this;
    }

    @Override
    public IMyStringBuilder append(CharSequence s) {
        value.append(s);
        return this;
    }

    @Override
    public IMyStringBuilder append(CharSequence s, int start, int end) {
        value.append(s, start, end);
        return this;
    }

    @Override
    public IMyStringBuilder append(char[] str) {
        value.append(str);
        return this;
    }

    @Override
    public IMyStringBuilder append(char[] str, int offset, int len) {
        value.append(str, offset, len);
        return this;
    }

    @Override
    public IMyStringBuilder append(boolean b) {
        value.append(b);
        return this;
    }

    @Override
    public IMyStringBuilder append(char c) {
        value.append(c);
        return this;
    }

    @Override
    public IMyStringBuilder append(int i) {
        value.append(i);
        return this;
    }

    @Override
    public IMyStringBuilder append(long lng) {
        value.append(lng);
        return this;
    }

    @Override
    public IMyStringBuilder append(float f) {
        value.append(f);
        return this;
    }

    @Override
    public IMyStringBuilder append(double d) {
        value.append(d);
        return this;
    }

    @Override
    public IMyStringBuilder appendCodePoint(int codePoint) {
        value.appendCodePoint(codePoint);
        return this;
    }

    @Override
    public IMyStringBuilder delete(int start, int end) {
        value.delete(start, end);
        return this;
    }

    @Override
    public IMyStringBuilder deleteCharAt(int index) {
        value.deleteCharAt(index);
        return this;
    }

    @Override
    public IMyStringBuilder replace(int start, int end, String str) {
        value.replace(start, end, str);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int index, char[] str, int offset, int len) {
        value.insert(index, str, offset, len);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, Object obj) {
        value.insert(offset, obj);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, String str) {
        value.insert(offset, str);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, char[] str) {
        value.insert(offset, str);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int dstOffset, CharSequence s) {
        value.insert(dstOffset, s);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
        value.insert(dstOffset, s, start, end);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, boolean b) {
        value.insert(offset, b);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, char c) {
        value.insert(offset, c);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, int i) {
        value.insert(offset, i);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, long l) {
        value.insert(offset, l);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, float f) {
        value.insert(offset, f);
        return this;
    }

    @Override
    public IMyStringBuilder insert(int offset, double d) {
        value.insert(offset, d);
        return this;
    }

    @Override
    public int indexOf(String str) {
        return value.indexOf(str);
    }

    @Override
    public int indexOf(String str, int fromIndex) {
        return value.indexOf(str, fromIndex);
    }

    @Override
    public int lastIndexOf(String str) {
        return value.lastIndexOf(str);
    }

    @Override
    public int lastIndexOf(String str, int fromIndex) {
        return value.lastIndexOf(str, fromIndex);
    }

    @Override
    public IMyStringBuilder reverse() {
        value.reverse();
        return this;
    }

    @Override
    public IMyStringBuilder repeat(int codePoint, int count) {
        throw new UnsupportedOperationException("repeat(int, int) requires Java 21 or later");
    }

    @Override
    public IMyStringBuilder repeat(CharSequence cs, int count) {
        throw new UnsupportedOperationException("repeat(CharSequence, int) requires Java 21 or later");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
