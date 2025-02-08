package io.github.awidesky.ytdllp4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.github.awidesky.ytdllp4j.outputConsumer.DownloadProgressListener;
import io.github.awidesky.ytdllp4j.outputConsumer.OutputConsumer;
import io.github.awidesky.ytdllp4j.outputConsumer.OutputStringGobbler;
import io.github.awidesky.ytdllp4j.outputConsumer.PlaylistIndexListener;

public class Ytdlp {

	public static final Charset NATIVECHARSET = Charset.forName(System.getProperty("native.encoding"));
	
	private String ytdlpPath;
	private String ffmpegPath = null;
	
	
	private boolean saveOutputs = true;
	
	private Consumer<IOException> IOExceptionHandler = IOException::printStackTrace;
	
	private OutputConsumer stdoutConsumer = null;
	private OutputConsumer stderrConsumer = null;
	
	private DownloadProgressListener progressListner = null;
	private PlaylistIndexListener playlistIndexListner = null;
	
	
	private Thread[] ioThreads = new Thread[] { null, null };
	
	public Ytdlp() {
		this("yt-dlp", null);
	}
	public Ytdlp(String ytdlpPath) {
		this(ytdlpPath, null);
	}
	public Ytdlp(String ytdlpPath, String ffmpegPath) {
		this.ytdlpPath = ytdlpPath != null ? ytdlpPath : "yt-dlp";
		this.ffmpegPath = ffmpegPath;
	}
    
	public YtdlpResult execute(YtdlpCommand command) throws IOException, InterruptedException {
		OutputStringGobbler outstrs = null;
		OutputStringGobbler errstrs = null;

		if(saveOutputs) {
			outstrs = new OutputStringGobbler();
			errstrs = new OutputStringGobbler();
		}
		
		List<OutputConsumer> outConsumers = Stream.of(stdoutConsumer, outstrs, progressListner, playlistIndexListner).filter(Objects::nonNull).toList();
		List<OutputConsumer> errConsumers = Stream.of(stderrConsumer, errstrs).filter(Objects::nonNull).toList();

		if(progressListner != null) command.addtemporaryOption("--progress-template", DownloadProgressListener.PROGRESSTEMPLATE);
		
		ProcessBuilder pb = new ProcessBuilder(command.buildOptions(ytdlpPath, ffmpegPath));
		// start process
		long starttime = System.currentTimeMillis();
		Process p = pb.directory(command.getWorkingDir()).start();
		
		ioThreads[0] = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), NATIVECHARSET))) {
				br.lines().forEach(s -> outConsumers.forEach(c -> c.consumeString(s)));
			} catch (IOException e) {
				IOExceptionHandler.accept(e);
			}
		});
		
		ioThreads[1] = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), NATIVECHARSET))) {
				br.lines().forEach(s -> errConsumers.forEach(c -> c.consumeString(s)));
			} catch (IOException e) {
				IOExceptionHandler.accept(e);
			}
		});
		
		ioThreads[0].start();
		ioThreads[1].start();
		
		int exitcode = p.waitFor();
		long time = System.currentTimeMillis() - starttime;
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
	
	public OutputConsumer getStdoutConsumer() {
		return stdoutConsumer;
	}
	
	public OutputConsumer getStderrConsumer() {
		return stderrConsumer;
	}
	
	public void setStdoutConsumer(OutputConsumer consumer) {
		stdoutConsumer = consumer;
	}
	
	public void setStderrConsumer(OutputConsumer consumer) {
		stderrConsumer = consumer;
	}
	
	public DownloadProgressListener getProgressListner() {
		return progressListner;
	}
	
	public void setProgressListner(DownloadProgressListener progressListner) {
		this.progressListner = progressListner;
	}
	
	public PlaylistIndexListener getPlaylistIndexListner() {
		return playlistIndexListner;
	}
	
	public void setPlaylistIndexListner(PlaylistIndexListener playlistListner) {
		this.playlistIndexListner = playlistListner;
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
