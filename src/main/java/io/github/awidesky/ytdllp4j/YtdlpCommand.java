package io.github.awidesky.ytdllp4j;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class YtdlpCommand {

	private File workingDir;
	private String url;
	private LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();

	public File getWorkingDir() {
		return workingDir;
	}
	public YtdlpCommand setWorkingDir(String workingDir) {
		this.workingDir = new File(workingDir);
		return this;
	}
	public YtdlpCommand setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
		return this;
	}

	public String getUrl() {
		return url;
	}
	public YtdlpCommand setUrl(String url) {
		this.url = url;
		return this;
	}

	public Map<String, String> getOptions() {
		return options;
	}
	public YtdlpCommand addOption(String option) {
		options.put(option, null);
		return this;
	}
	public YtdlpCommand addOption(String key, String value) {
		options.put(key, value);
		return this;
	}
	public YtdlpCommand addOption(String key, int value) {
		options.put(key, Integer.toString(value));
		return this;
	}


	public YtdlpCommand() {}

	public YtdlpCommand(String url) {
		this.url = url;
	}

	public YtdlpCommand(String url, File workingDir) {
		this.url = url;
		this.workingDir = workingDir;
	}

	public List<String> buildOptions(String ytdlpPath, String ffmpegPath) {
		List<String> ret = new LinkedList<>();

		ret.add(ytdlpPath);
		if(ffmpegPath != null) {
			ret.add("--ffmpeg-location");
			ret.add(ffmpegPath);
		}
		options.entrySet().forEach(entry -> {
			ret.add(entry.getKey());
			
			if(entry.getValue() != null) {
				ret.add(entry.getValue());
			}
		});

		if(url != null) {
			ret.add("--");
			ret.add(url);
		}
		
		return ret;
	}
}
