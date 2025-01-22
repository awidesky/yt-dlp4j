package io.github.awidesky.ytdllp4j.outputConsumer;

public final class SystemOutPrinter implements OutputConsumer {

	@Override
	public void accept(String str) {
		System.out.println(str);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof SystemOutPrinter;
	}
}
