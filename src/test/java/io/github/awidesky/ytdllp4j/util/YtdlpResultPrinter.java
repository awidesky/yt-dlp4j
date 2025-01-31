package io.github.awidesky.ytdllp4j.util;

import java.io.PrintStream;
import java.util.stream.Collectors;

import io.github.awidesky.ytdllp4j.YtdlpResult;

public class YtdlpResultPrinter {
	
	public static void print(YtdlpResult res, String prefix) {
		print(res, prefix, System.out);
	}
	public static void print(YtdlpResult res, String prefix, PrintStream out) {
		out.println(prefix + " Process command : \"" + res.getCommand().stream().collect(Collectors.joining(" ")) + "\"");
		out.println(prefix + " Process working dir : \"" + res.getDirectory() + "\"");
		out.println(prefix + " Process exit code : " + res.getExitCode());
		out.println(prefix + " Process execution time : " + res.getElapsedTime() + "ms");
	}
	
}
