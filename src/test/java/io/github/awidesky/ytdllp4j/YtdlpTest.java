package io.github.awidesky.ytdllp4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.awidesky.ytdllp4j.util.ExecutablePathFinder;
import io.github.awidesky.ytdllp4j.util.YtdlpResultPrinter;

class YtdlpTest {

	static Ytdlp ytdlp = new Ytdlp(ExecutablePathFinder.findYtdlp(), ExecutablePathFinder.findFfmpeg());
	static String link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ytdlp.setIOExceptionHandler(e -> {
			e.printStackTrace();
			fail("yt-dlp process I/O failed");
		});
		ytdlp.addStdoutConsumer(System.out::println);
		ytdlp.addStderrConsumer(System.err::println);
		System.out.println("[YtdlpTest] yt-dlp path : " + ytdlp.getYtdlpPath());
		System.out.println("[YtdlpTest] ffmpeg path : " + ytdlp.getFfmpegPath());
		System.out.println();
	}
	
	@AfterEach
	void afterEach() {
		System.out.println();
	}

	@Test
	void getVideoNameTest() {
		System.out.println("[getVideoNameTest] Process start");
		assertEquals("Rick Astley - Never Gonna Give You Up (Official Music Video)", ytdlp.getVideoName(link, "%(title)s"));
		System.out.println("[getVideoNameTest] Process end");
	}
	
	@Test
	void getVersionTest() {
		System.out.println("[getVersionTest] Process start");
		String version = ytdlp.getVersion();
		System.out.println("[getVersionTest] Process end");
		System.out.println("[getVersionTest] yt-dlp version : " + version);
		assertNotNull(version);
	}
	
	@Test
	@Disabled
	void updateTest() {
		System.out.println("[updateTest] Process start");
		YtdlpResult res = ytdlp.update();
		System.out.println("[updateTest] Process end");
		YtdlpResultPrinter.print(res, "[updateTest]");
	}

}
