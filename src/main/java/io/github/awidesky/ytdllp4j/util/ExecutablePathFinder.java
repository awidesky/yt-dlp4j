package io.github.awidesky.ytdllp4j.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecutablePathFinder {

	public static String findFromPath(String... commands) {
		return findFromPath(Arrays.asList(commands));
	}
	
	public static String findFromPath(List<String> commands) {
		return null; //TODO
	}
	
	public static String[] path() {
		String ret = System.getenv("PATH");
		if(OSUtil.isUnix() && System.getenv("SHELL") != null) {
			ProcessBuilder pb = new ProcessBuilder(System.getenv("SHELL"), "-c", "echo $PATH");
			try {
				Process p = pb.directory(null).start();
				if(p.waitFor() == 0) {
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					ret += File.pathSeparator + br.lines().collect(Collectors.joining(File.pathSeparator));
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}		
		}
		
		return Arrays.stream(ret.split(Pattern.quote(File.pathSeparator))).distinct().toArray(String[]::new);
	}
}
