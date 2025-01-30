package io.github.awidesky.ytdllp4j.outputConsumer;

import org.slf4j.Logger;
import org.slf4j.event.Level;

public class OutputLogger implements OutputConsumer {

	private Logger logger;
	private Level level;
	
	public OutputLogger(org.slf4j.Logger logger, org.slf4j.event.Level level) {
		this.logger = logger;
		this.level = level;
	}
	
	@Override
	public void accept(String str) {
		logger.atLevel(level).log(str);
	}

}
