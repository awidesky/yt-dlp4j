package io.github.awidesky.ytdllp4j.util;

public class ProgressFormatter {

	public static String formatSeconds(long seconds) {
		if(seconds < 0) return "Unknown";
		
		int hours = (int) (seconds / 3600);
		int minutes = (int) ((seconds % 3600) / 60);
		seconds = seconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds).substring(hours > 0 ? 0 : 3);
	}
	
	
	public static String formatBytes(long bytes) {
		
		if(bytes < 0L) return "Unknown";
		if(bytes == 0L) return "0.00B";
		
		switch ((int)(Math.log(bytes) / Math.log(1024))) {
		
		case 0:
			return String.format("%d", bytes) + "B";
		case 1:
			return String.format("%.2f", bytes / 1024.0) + "KiB";
		case 2:
			return String.format("%.2f", bytes / (1024.0 * 1024)) + "MiB";
			
		}
		return String.format("%.2f", bytes / (1024.0 * 1024 * 1024)) + "GiB";
		
	}
}
