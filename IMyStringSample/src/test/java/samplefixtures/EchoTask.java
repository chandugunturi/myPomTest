package samplefixtures;

import java.util.concurrent.Callable;

public final class EchoTask implements Callable<String> {
    private final String value;

    public EchoTask(String value) {
        this.value = value;
    }

    @Override
    public String call() {
        return value;
    }
}
