package io.github.awidesky.ytdllp4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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

import io.github.awidesky.ytdllp4j.outputConsumer.OutputConsumer;
import io.github.awidesky.ytdllp4j.util.ExecutablePathFinder;
import io.github.awidesky.ytdllp4j.util.YtdlpResultPrinter;

class YtdlpTest {

	Ytdlp ytdlp = new Ytdlp(ytdlpPath, ffmpegPath);
	static final String link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
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
				Arguments.of("yt-dlp -I 2 --get-filename -o %(title)s -- https://www.youtube.com/watch?v=C2xel6q0yao&list=PLlaN88a7y2_plecYoJxvRFTLHVbIVAOoc",
						(Consumer<YtdlpCommand>)c -> c.addOption("-I", 2).addOption("--get-filename").addOption("-o", "%(title)s").setUrl("https://www.youtube.com/watch?v=C2xel6q0yao&list=PLlaN88a7y2_plecYoJxvRFTLHVbIVAOoc"),
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
	void download() {
		
	}
}
