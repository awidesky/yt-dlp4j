package io.github.awidesky.ytdllp4j.outputConsumer;

public interface StdoutConsumer extends OutputConsumer {

	@Override
	public default void stderr(String str) {}

}
