package io.github.awidesky.ytdllp4j.outputConsumer;

public class SystemErrPrinter implements OutputConsumer {

	@Override
	public void accept(String str) {
		System.err.println(str);
	}

}
