package io.github.awidesky.ytdllp4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import io.github.awidesky.ytdllp4j.outputConsumer.OutputConsumer;

public class Ytdlp {

	public static final Charset NATIVECHARSET = Charset.forName(System.getProperty("native.encoding"));
	
	private String ytdlpPath = "yt-dlp";
	private String ffmpegPath = null;
	
	private List<Consumer<String>> stdoutConsumers = new LinkedList<>();
	private List<Consumer<String>> stderrConsumers = new LinkedList<>();
	
	private boolean saveOutputs = true;
	
	private Consumer<IOException> IOExceptionHandler = e -> e.printStackTrace();
	
	private Thread[] ioThreads = new Thread[] { null, null };
	
	public Ytdlp() {}
    
	public YtdlpResult execute(YtdlpCommand command) throws IOException, InterruptedException {
		List<String> outstrs = null;
		List<String> errstrs = null;
		
		LinkedList<Consumer<String>> outConsumers = new LinkedList<>(stdoutConsumers);
		LinkedList<Consumer<String>> errConsumers = new LinkedList<>(stderrConsumers);
		
		if(saveOutputs) {
			outstrs = new LinkedList<String>();
			errstrs = new LinkedList<String>();
			
			outConsumers.add(outstrs::add);
			errConsumers.add(errstrs::add);
		}
		
		ProcessBuilder pb = new ProcessBuilder(command.buildOptions(ytdlpPath, ffmpegPath));
		// start process
		long starttime = System.nanoTime();
		Process p = pb.directory(command.getWorkingDir()).start();
		
		ioThreads[0] = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), NATIVECHARSET))) {
				br.lines().forEach(s -> outConsumers.forEach(c -> c.accept(s)));
			} catch (IOException e) {
				IOExceptionHandler.accept(e);
			}
		});
		
		ioThreads[1] = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), NATIVECHARSET))) {
				br.lines().forEach(s -> errConsumers.forEach(c -> c.accept(s)));
			} catch (IOException e) {
				IOExceptionHandler.accept(e);
			}
		});
		
		ioThreads[0].start();
		ioThreads[1].start();
		
		int exitcode = p.waitFor();
		long time = System.nanoTime() - starttime;
		ioThreads[0].join();
		ioThreads[0] = null;
		
		ioThreads[1].join();
		ioThreads[1] = null;
		
		return new YtdlpResult(pb.command(), pb.directory(), exitcode, time, outstrs, errstrs);
	}
	
	
	public String getVersion() { //TODO : add OutputStringGobbler
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
	
	public void addStdoutConsumer(OutputConsumer consumer) {
		stdoutConsumers.add(consumer);
	}
	
	public void addStderrConsumer(OutputConsumer consumer) {
		stderrConsumers.add(consumer);
	}
	
	public void interruptThread() {
		for(Thread t : ioThreads) {
			if(t != null) t.interrupt();
		}
	}
	
}
