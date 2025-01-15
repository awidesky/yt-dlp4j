package io.github.awidesky.ytdllp4j;

import java.io.File;
import java.util.List;

public class YtdlpResult {
	private List<String> command;
	private File directory;
	private List<String> stdout;
	private List<String> stderr;
	private int exitCode;
	private long elapsedTime;

	public YtdlpResult(List<String> command, File directory, int exitCode, long elapsedTime, List<String> stdout, List<String> stderr) {
        this.command = command;
        this.directory = directory;
        this.elapsedTime = elapsedTime;
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
    }

	public List<String> getCommand() {
		return command;
	}

	public File getDirectory() {
		return directory;
	}

	public List<String> getStdout() {
		return stdout;
	}

	public List<String> getStderr() {
		return stderr;
	}

	public int getExitCode() {
		return exitCode;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}
	
}
