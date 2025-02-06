package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionalInterface
public interface PlaylistIndexListener extends OutputConsumer {

	public static final Pattern playlistPattern = Pattern.compile("^\\[download\\] Downloading item (\\d+) of (\\d+)$");

	public void playListItemUpdated(int current, int playlistSize);

	@Override
	default void accept(String t) {
		Matcher m = playlistPattern.matcher(t);
		if (m.find())
			playListItemUpdated(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
	}

}
