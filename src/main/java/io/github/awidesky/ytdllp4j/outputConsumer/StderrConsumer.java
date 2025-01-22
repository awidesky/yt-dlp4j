package io.github.awidesky.ytdllp4j.outputConsumer;

public interface StderrConsumer extends OutputConsumer {

	@Override
	public default void stdout(String str) {}

}
