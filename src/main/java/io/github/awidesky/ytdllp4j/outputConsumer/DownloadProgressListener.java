package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionalInterface
public interface DownloadProgressListener extends OutputConsumer {
	
	public static final String PROGRESSTEMPLATE = "download:[download] %(progress._default_template)s | raw: %(progress.downloaded_bytes|-1)d/%(progress.total_bytes,progress.total_bytes_estimate|-1)d/%(progress.speed|0)d/%(progress.eta|-1)d";
	//[download]  78.1% of ~  84.08MiB at    5.77MiB/s ETA 00:04 (frag 29/39) | raw: 68868152/88165448/6051431/4
	public static final Pattern OUTPUTPATTERN = Pattern.compile("^\\[download\\]\\s+(.+?) \\| raw: (-?\\d+)/(-?\\d+)/(-?\\d+)/(-?\\d+)$");

	//%(progress.downloaded_bytes)d/%(progress.total_bytes,progress.total_bytes_estimate)d/%(progress.speed|0)d/%(progress.eta|-1)d
	public void downloadProgressUpdated(String default_template, long downloaded_bytes, long total_bytes, long speed, int eta);
	
	@Override
	default void accept(String t) {
		Matcher m = OUTPUTPATTERN.matcher(t);
		if (m.find())
			downloadProgressUpdated(m.group(1), Long.parseLong(m.group(2)), Long.parseLong(m.group(3)), Long.parseLong(m.group(4)), Integer.parseInt(m.group(5)));
			//downloadProgressUpdated(m.group(1), convertToLong(m.group(2)), convertToLong(m.group(3)), convertToLong(m.group(4)), (int)convertToLong(m.group(5)));
	}

//	private long convertToLong(String str) {
//		try {
//			return Long.parseLong(str);
//		} catch(NumberFormatException e) {
//			e.printStackTrace();
//			return -1L;
//		}
//	}
}
