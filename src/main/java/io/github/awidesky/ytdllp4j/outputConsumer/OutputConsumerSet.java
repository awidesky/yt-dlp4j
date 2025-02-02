package io.github.awidesky.ytdllp4j.outputConsumer;

import java.util.LinkedList;
import java.util.List;

public class OutputConsumerSet {

	private LinkedList<OutputConsumer> outConsumers = new LinkedList<>();
	private LinkedList<OutputConsumer> errConsumers = new LinkedList<>();
	
	public OutputConsumerSet() {}
	
	public OutputConsumerSet(OutputConsumer... stdoutConsumers) {
		for(OutputConsumer c : stdoutConsumers) outConsumers.add(c);
	}
	
	public OutputConsumerSet(OutputConsumer[] outs, OutputConsumer[] errs) {
		for(OutputConsumer c : outs) outConsumers.add(c);
		for(OutputConsumer c : errs) errConsumers.add(c);
	}
	
	public OutputConsumerSet(List<OutputConsumer> outs, List<OutputConsumer> errs) {
		outConsumers.addAll(outs);
		errConsumers.addAll(errs);
	}

	public LinkedList<OutputConsumer> getOutConsumers() {
		return outConsumers;
	}

	public LinkedList<OutputConsumer> getErrConsumers() {
		return errConsumers;
	}

}
