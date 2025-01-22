package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.function.Consumer;

public class DownloadProgressExtractor implements OutputConsumer {

	private Consumer<Integer> processConsumer;
	
	public DownloadProgressExtractor(Consumer<Integer> processConsumer) {
		this.processConsumer = processConsumer;
	}

	@Override
	public void accept(String str) {
		// TODO extract process
		// TODO playlist number too?
		processConsumer.accept(null); // TODO
	}

}
