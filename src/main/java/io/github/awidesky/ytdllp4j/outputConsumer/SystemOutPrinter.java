package io.github.awidesky.ytdllp4j.outputConsumer;

public class SystemOutPrinter implements OutputConsumer {

	@Override
	public void accept(String str) {
		System.out.println(str);
	}

}
