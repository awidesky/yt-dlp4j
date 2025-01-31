package io.github.awidesky.ytdllp4j.util;

import java.io.PrintStream;
import java.util.stream.Collectors;

import io.github.awidesky.ytdllp4j.YtdlpResult;

public class YtdlpResultPrinter {
	
	public static void print(YtdlpResult res, String prefix) {
		print(res, prefix, System.out);
	}
	public static void print(YtdlpResult res, String prefix, PrintStream out) {
		out.println(prefix + " process command : \"" + res.getCommand().stream().collect(Collectors.joining(" ")) + "\"");
		out.println(prefix + " process working dir : \"" + res.getDirectory().getAbsolutePath() + "\"");
		out.println(prefix + " process exit code : " + res.getExitCode());
		out.println(prefix + " process execution time : " + res.getElapsedTime() + "ms");
	}
	
}
