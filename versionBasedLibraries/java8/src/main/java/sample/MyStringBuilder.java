package sample;

public final class MyStringBuilder extends AbstractMyStringBuilder {
    public MyStringBuilder() {
        super();
    }

    public MyStringBuilder(int capacity) {
        super(capacity);
    }

    public MyStringBuilder(String str) {
        super(str);
    }

    public MyStringBuilder(CharSequence seq) {
        super(seq);
    }
}
