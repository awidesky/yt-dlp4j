package io.github.awidesky.ytdllp4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Ytdlp {

	public static final Charset NATIVECHARSET = Charset.forName(System.getProperty("native.encoding"));
	
	private String ytdlpPath = "yt-dlp";
	
	private Consumer<String> stdout = System.out::println;
	private Consumer<String> stderr = System.err::println;
	
	private boolean saveOutputs = true;
	
	private Consumer<IOException> IOExceptionHandler = e -> e.printStackTrace();
	
	
	public Ytdlp() {}
    
	public YtdlpResult execute(YtdlpCommand command) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(command.buildOptions(ytdlpPath));
		// start process
		long starttime = System.nanoTime();
		Process p = pb.directory(command.getWorkingDir()).start();
		List<String> outstr = saveOutputs ? new LinkedList<>() : null;
		List<String> errstr = saveOutputs ? new LinkedList<>() : null;
		
		Thread outThread = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), NATIVECHARSET))) {
				Stream<String> lines = br.lines();
				if(saveOutputs) lines = lines.peek(outstr::add);
				lines.forEach(stdout);
			} catch (IOException e) {
				IOExceptionHandler.accept(e);
			}
		});
		
		Thread errThread = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), NATIVECHARSET))) {
				Stream<String> lines = br.lines();
				if(saveOutputs) lines = lines.peek(errstr::add);
				lines.forEach(stderr);
			} catch (IOException e) {
				IOExceptionHandler.accept(e);
			}
		});
		
		int exitcode = p.waitFor();
		long time = System.nanoTime() - starttime;
		outThread.join();
		errThread.join();
		
		return new YtdlpResult(pb.command(), pb.directory(), exitcode, time, outstr, errstr);
	}
	
	
	public String getVersion() {
		YtdlpCommand version = new YtdlpCommand();
		version.addOption("--version");
		try {
			return execute(version).getStdout().get(0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public YtdlpResult update() {
		YtdlpCommand update = new YtdlpCommand();
		update.addOption("--update");
		try {
			return execute(update);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getVideoName(String url) {
		return getVideoName(url, "%(title)s");
	}
	
	public String getVideoName(String url, String outputFormat) {
		//--get-filename -o %(title)s.mp3
		YtdlpCommand getVideoName = new YtdlpCommand(url);
		getVideoName.addOption("--get-filename");
		getVideoName.addOption("-o");
		getVideoName.addOption(outputFormat);
		try {
			return execute(getVideoName).getStdout().get(0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
