package io.github.awidesky.ytdllp4j.outputConsumer;

public final class SystemErrPrinter implements OutputConsumer {

	@Override
	public void accept(String str) {
		System.err.println(str);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof SystemErrPrinter;
	}
}
