package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.function.Consumer;

@FunctionalInterface
public interface OutputConsumer extends Consumer<String> {
	
	public static final OutputConsumer redirectToSystemout = System.out::println;
	public static final OutputConsumer redirectToSystemerr = System.err::println;

}
