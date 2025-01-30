package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OutputStringGobbler implements OutputConsumer {

	private List<String> out = new LinkedList<>();
	
	@Override
	public void accept(String str) {
		out.add(str);
	}

	public List<String> getLines() {
		List<String> ret = new ArrayList<>(out);
		out.clear();
		return ret;
	}

}
