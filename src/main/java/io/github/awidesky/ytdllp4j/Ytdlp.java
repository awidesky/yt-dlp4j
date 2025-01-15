package io.github.awidesky.ytdllp4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Ytdlp {

	public static final Charset NATIVECHARSET = Charset.forName(System.getProperty("native.encoding"));
	
	private String ytdlpPath = "yt-dlp";
	
	private Consumer<String> stdout = System.out::println;
	private Consumer<String> stderr = System.err::println;
	
    
	public YtdlpResult execute(YtdlpCommand command) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(command.buildOptions(ytdlpPath));
		// start process
		long starttime = System.nanoTime();
		Process p = pb.directory(command.getWorkingDir()).start();
		List<String> outstr = new LinkedList<>();
		List<String> errstr = new LinkedList<>();
		
		Thread outThread = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), NATIVECHARSET))) {
				br.lines().peek(outstr::add).forEach(stdout);
			} catch (IOException e) {
				//TODO
			}
		});
		
		Thread errThread = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), NATIVECHARSET))) {
				br.lines().peek(errstr::add).forEach(stderr);
			} catch (IOException e) {
				//TODO
			}
		});
		
		int exitcode = p.waitFor();
		long time = System.nanoTime() - starttime;
		outThread.join();
		errThread.join();
		
		return new YtdlpResult(pb.command(), pb.directory(), exitcode, time, outstr, errstr);
	}
	
}
