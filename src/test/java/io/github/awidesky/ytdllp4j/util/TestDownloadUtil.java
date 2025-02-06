package io.github.awidesky.ytdllp4j.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class TestDownloadUtil {

	private static File downloadLocation = null;
	static {
		try {
			Path ret = Files.createTempDirectory("");
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					Files.walkFileTree(ret, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("\nTemp directory " + ret.toFile().getAbsolutePath() + " removed.");
			}));
			downloadLocation = ret.toFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Cannot make temp directory! Some tests will be disabled...");
		}
	}
	
	public static boolean downloadNotPermitted() {
		return downloadLocation == null;
	}
	
	public static File testDownloadLocation() {
		return downloadLocation;
	}
	
}
