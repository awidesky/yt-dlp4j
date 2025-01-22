package io.github.awidesky.ytdllp4j.outputConsumer;

public class OutputConsolePrinter {
	
	public void stdout(String str) {
		System.out.println(str);
	}
	public void stderr(String str) {
		System.err.println(str);
	}

}
