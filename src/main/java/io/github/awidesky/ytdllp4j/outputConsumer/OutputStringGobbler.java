package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.LinkedList;
import java.util.List;

public class OutputStringGobbler implements OutputConsumer {

	private List<String> stdout = new LinkedList<>();
	private List<String> stderr = new LinkedList<>();
	
	@Override
	public void stdout(String str) {
		stdout.add(str);
	}

	@Override
	public void stderr(String str) {
		stderr.add(str);
	}

	public List<String> getStdout() {
		return stdout;
	}

	public List<String> getStderr() {
		return stderr;
	}

}
