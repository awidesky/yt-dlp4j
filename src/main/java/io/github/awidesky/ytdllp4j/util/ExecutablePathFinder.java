package io.github.awidesky.ytdllp4j.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecutablePathFinder {
	
	private static String[] path = evaluatePath();
	
	public static String findYtdlp() {
		return findFromPath("yt-dlp", "--version");
	}
	
	public static String findFfmpeg() {
		return findFromPath("ffmpeg", "-version");
	}

	public static String findFromPath(String... commands) {
		return findFromPath(Arrays.asList(commands));
	}
	
	 //TODO add test
	public static String findFromPath(List<String> options) {
		for(String path : path()) {
			try {
				String executable = options.get(0);
				File execFile = new File(path, executable);
				if(!execFile.exists())
					continue;
				
				executable = execFile.getCanonicalPath();
				LinkedList<String> command = new LinkedList<>(options);
				command.set(0, executable);
				
				ProcessBuilder pb = new ProcessBuilder(command);
				Process p = pb.directory(new File(System.getProperty("user.home"))).start();
				
				if(p.waitFor() == 0)
					return executable;
			} catch (IOException | InterruptedException e) {
				// TODO ignore?
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String[] path() {
		return path;
	}
	
	public static String[] evaluatePath() {
		String ret = System.getenv("PATH");
		if(OSUtil.isUnix() && System.getenv("SHELL") != null) {
			ProcessBuilder pb = new ProcessBuilder(System.getenv("SHELL"), "-c", "echo $PATH");
			try {
				Process p = pb.directory(new File(System.getProperty("user.home"))).start();
				if(p.waitFor() == 0) {
					try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
						ret = br.lines().collect(Collectors.joining(File.pathSeparator)) + File.pathSeparator + ret;
					}
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return (path = Arrays.stream(ret.split(Pattern.quote(File.pathSeparator))).distinct().toArray(String[]::new));
	}
}
