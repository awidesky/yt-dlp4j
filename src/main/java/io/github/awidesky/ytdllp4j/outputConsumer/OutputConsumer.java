package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.function.Consumer;

@FunctionalInterface
public interface OutputConsumer extends Consumer<String> {
	
	public static Consumer<Exception> getExceptionHandler() {
		return ExceptionHandler.exceptionHandler;
	}
	public static void setExceptionHandler(Consumer<Exception> handler) {
		ExceptionHandler.exceptionHandler = handler;
	}
	
	public default void consumeString(String str) {
		try {
			accept(str);
		} catch (Exception e) {
			ExceptionHandler.exceptionHandler.accept(e);
		}
	}
	
	
}

class ExceptionHandler {
	public static Consumer<Exception> exceptionHandler = Exception::printStackTrace;
}