package io.github.awidesky.ytdllp4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.awidesky.ytdllp4j.outputConsumer.DownloadProgressListener;
import io.github.awidesky.ytdllp4j.outputConsumer.OutputConsumer;
import io.github.awidesky.ytdllp4j.outputConsumer.PlaylistIndexListener;
import io.github.awidesky.ytdllp4j.util.ExecutablePathFinder;
import io.github.awidesky.ytdllp4j.util.ProgressFormatter;
import io.github.awidesky.ytdllp4j.util.TestDownloadUtil;
import io.github.awidesky.ytdllp4j.util.YtdlpResultPrinter;

class YtdlpTest {

	Ytdlp ytdlp = new Ytdlp(ytdlpPath, ffmpegPath);
	static final String link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
	static final String playlistLink = "https://www.youtube.com/watch?v=dQw4w9WgXcQ&list=OLAK5uy_nXAVoaa-qQkMOj5heXM-nlb3QbyPYjlvQ";
	static final String ytdlpPath = ExecutablePathFinder.findYtdlp();
	static final String ffmpegPath = ExecutablePathFinder.findFfmpeg();
	

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("[YtdlpTest] yt-dlp path : " + ytdlpPath);
		System.out.println("[YtdlpTest] ffmpeg path : " + ffmpegPath);
		System.out.println();
	}
	
	@BeforeEach
	void beforeEach() {
		ytdlp = new Ytdlp(ytdlpPath, ffmpegPath);
		ytdlp.setIOExceptionHandler(e -> {
			e.printStackTrace();
			fail("yt-dlp process I/O failed");
		});
		ytdlp.setStdoutConsumer(System.out::println);
		ytdlp.setStderrConsumer(System.err::println);
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
	

	@ParameterizedTest(name = "{0}")
	@MethodSource("variousCommands")
	void variousCommandsTest(String commandStr, Consumer<YtdlpCommand> addOption, String expectedOutput) throws IOException, InterruptedException {
		YtdlpCommand command = new YtdlpCommand(link);
		addOption.accept(command);
		assertEquals(commandStr, command.toString());
		String prefix = "[" + command.toString() + "]";
		
		System.out.println(prefix + " Process start");
		YtdlpResult res = ytdlp.execute(command);
		System.out.println(prefix + " Process end");
		
		YtdlpResultPrinter.print(res, prefix);
		
		if(expectedOutput != null) assertEquals(expectedOutput, res.getStdout().stream().collect(Collectors.joining("\n")));
	}

	static Stream<Arguments> variousCommands() throws Throwable {
		return Stream.of(
				Arguments.of("yt-dlp --get-filename -o %(title)s -- " + link,
						(Consumer<YtdlpCommand>)c -> c.addOption("--get-filename").addOption("-o", "%(title)s"),
						"Rick Astley - Never Gonna Give You Up (Official Music Video)"),
				Arguments.of("yt-dlp -I 1 --get-filename -o %(title)s -- " + playlistLink,
						(Consumer<YtdlpCommand>)c -> c.addOption("-I", 1).addOption("--get-filename").addOption("-o", "%(title)s").setUrl(playlistLink),
						"Rick Astley - Never Gonna Give You Up (Official Music Video)"),
				Arguments.of("yt-dlp --simulate -- " + link,
						(Consumer<YtdlpCommand>)c -> c.addOption("--simulate"),
						null)
	    );
	}

	@Test
	void outputConsumerExceptionHandlerTest() {
		AtomicReference<Exception> exeref = new AtomicReference<>();
		String msg = "outputConsumerExceptionHandlerTest message";
		Consumer<Exception> prev = OutputConsumer.getExceptionHandler();
		OutputConsumer.setExceptionHandler(exeref::set);
		
		ytdlp.setStdoutConsumer(s -> { throw new RuntimeException(msg); });
		assertNotNull(ytdlp.getVersion());
		Exception e = exeref.get();
		assertInstanceOf(RuntimeException.class, e);
		assertEquals(msg, e.getMessage());
		
		OutputConsumer.setExceptionHandler(prev);
	}
	
	@Test
	@DisabledIf("io.github.awidesky.ytdllp4j.util.TestDownloadUtil#downloadNotPermitted")
	void playlistIndexTest() throws IOException, InterruptedException {
		//yt-dlp -I 1:3,7 -s -- "https://www.youtube.com/watch?v=dQw4w9WgXcQ&list=OLAK5uy_nXAVoaa-qQkMOj5heXM-nlb3QbyPYjlvQ"
		YtdlpCommand command = new YtdlpCommand(playlistLink)
				.addOption("--playlist-items", "1:3,7")
				.addOption("--simulate")
				.setWorkingDir(TestDownloadUtil.testDownloadLocation());
		
		ytdlp.setPlaylistIndexListner(new PlaylistIndexListener() {
			private static int i = 1;
			@Override
			public void playListItemUpdated(int current, int playlistSize) {
				assertEquals(i++, current);
				assertEquals(4, playlistSize);				
			}
		});
		
		System.out.println("[playlistIndexTest] Process start");
		ytdlp.execute(command);
		System.out.println("[playlistIndexTest] Process end");
		
	}

	@Test
	@DisabledIf("io.github.awidesky.ytdllp4j.util.TestDownloadUtil#downloadNotPermitted")
	void downloadProgressTest() throws IOException, InterruptedException {
		YtdlpCommand command = new YtdlpCommand(link)
				.addOption("--output", "downloadProgressTest.%(ext)s")
				.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
				.setWorkingDir(TestDownloadUtil.testDownloadLocation());

		AtomicReference<String> line = new AtomicReference<>();
		ytdlp.setStdoutConsumer(s -> {
			System.out.println(s);
			line.setOpaque(s);
		});
		ytdlp.setProgressListner(new DownloadProgressListener() {
			
			@Override
			public void downloadProgressUpdated(String default_template, long downloaded_bytes, long total_bytes, long speed,
					int eta) {
				String out = line.getOpaque();
				assertEquals(out.substring(0, out.indexOf(" | raw:")).replaceAll("\\[download\\]\\s+", ""), default_template);
				assertEquals(out.replaceAll("(.+?)\\| raw: ", ""), downloaded_bytes + "/" + total_bytes + "/" + speed + "/" + eta);
				System.out.println("[test out] %5.1f%% of %10s at %10s/s ETA %s"
						.formatted(100.0 * downloaded_bytes / total_bytes,
								ProgressFormatter.formatBytes(total_bytes),
								ProgressFormatter.formatBytes(speed),
								ProgressFormatter.formatSeconds(eta)));
			}
		});
		
		System.out.println("[downloadProgressTest] Process start");
		ytdlp.execute(command);
		System.out.println("[downloadProgressTest] Process end");
		
		assertTrue(new File(TestDownloadUtil.testDownloadLocation(), "downloadProgressTest.mp4").exists());
		
	}
}
