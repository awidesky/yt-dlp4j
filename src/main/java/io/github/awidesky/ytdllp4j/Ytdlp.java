package io.github.awidesky.ytdllp4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.awidesky.ytdllp4j.outputConsumer.OutputConsumer;
import io.github.awidesky.ytdllp4j.outputConsumer.OutputStringGobbler;

public class Ytdlp {

	public static final Charset NATIVECHARSET = Charset.forName(System.getProperty("native.encoding"));
	
	private String ytdlpPath;
	private String ffmpegPath = null;
	
	private List<OutputConsumer> stdoutConsumers = new LinkedList<>();
	private List<OutputConsumer> stderrConsumers = new LinkedList<>();
	
	private boolean saveOutputs = true;
	
	private Consumer<IOException> IOExceptionHandler = e -> e.printStackTrace();
	
	private Thread[] ioThreads = new Thread[] { null, null };
	
	public Ytdlp() {
		this("yt-dlp", null);
	}
	public Ytdlp(String ytdlpPath) {
		this(ytdlpPath, null);
	}
	public Ytdlp(String ytdlpPath, String ffmpegPath) {
		this.ytdlpPath = ytdlpPath;
		this.ffmpegPath = ffmpegPath;
	}
    
	public YtdlpResult execute(YtdlpCommand command) throws IOException, InterruptedException {
		LinkedList<Consumer<String>> outConsumers = new LinkedList<>(stdoutConsumers);
		LinkedList<Consumer<String>> errConsumers = new LinkedList<>(stderrConsumers);
		
		OutputStringGobbler outstrs = null;
		OutputStringGobbler errstrs = null;
		
		if(saveOutputs) {
			outstrs = new OutputStringGobbler();
			errstrs = new OutputStringGobbler();
			
			outConsumers.add(outstrs);
			errConsumers.add(errstrs);
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
		
		return new YtdlpResult(pb.command(), pb.directory(), exitcode, time,
				Optional.ofNullable(outstrs).map(OutputStringGobbler::getLines).orElse(null),
				Optional.ofNullable(errstrs).map(OutputStringGobbler::getLines).orElse(null)
				);
	}
	
	public String getYtdlpPath() {
		return ytdlpPath;
	}
	
	public void setYtdlpPath(String ytdlpPath) {
		this.ytdlpPath = ytdlpPath;
	}
	
	public String getFfmpegPath() {
		return ffmpegPath;
	}
	
	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}
	
	public boolean isSaveOutputs() {
		return saveOutputs;
	}
	
	public void setSaveOutputs(boolean saveOutputs) {
		this.saveOutputs = saveOutputs;
	}
	
	public Consumer<IOException> getIOExceptionHandler() {
		return IOExceptionHandler;
	}
	
	public void setIOExceptionHandler(Consumer<IOException> iOExceptionHandler) {
		IOExceptionHandler = iOExceptionHandler;
	}
	
	public List<OutputConsumer> getStdoutConsumer() {
		return stdoutConsumers;
	}
	
	public List<OutputConsumer> getStderrConsumer() {
		return stderrConsumers;
	}
	
	
	public String getVersion() {
		boolean saveout = isSaveOutputs();
		setSaveOutputs(true);
		
		YtdlpCommand version = new YtdlpCommand();
		version.addOption("--version");
		try {
			return execute(version).getStdout().get(0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			setSaveOutputs(saveout);
		}
	}
	
	public YtdlpResult update() {
		boolean saveout = isSaveOutputs();
		setSaveOutputs(true);
		
		YtdlpCommand update = new YtdlpCommand();
		update.addOption("--update");
		try {
			return execute(update);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			setSaveOutputs(saveout);
		}
	}
	
	public String getVideoName(String url) {
		return getVideoName(url, "%(title)s");
	}
	
	public String getVideoName(String url, String outputFormat) {
		boolean saveout = isSaveOutputs();
		setSaveOutputs(true);
		
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
		} finally {
			setSaveOutputs(saveout);
		}
	}

	
	public void interruptThread() {
		for(Thread t : ioThreads) {
			if(t != null) t.interrupt();
		}
	}
	
}
