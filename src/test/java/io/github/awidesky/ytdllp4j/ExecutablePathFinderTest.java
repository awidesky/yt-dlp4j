package io.github.awidesky.ytdllp4j;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.awidesky.ytdllp4j.util.ExecutablePathFinder;
import io.github.awidesky.ytdllp4j.util.OSUtil;

class ExecutablePathFinderTest {
	
	@Test
	void validate_paths() {
		String[] paths = ExecutablePathFinder.path();
		System.out.println("[ExecutablePathFinderTest] Evaluated paths :");
		for(String p : paths) {
			System.out.println(p);
			File f = new File(p);
			assertTrue(f.exists(), p + " does not exist!");
			assertTrue(f.isDirectory(), p + " is not a directory!");
		}
		System.out.println("[ExecutablePathFinderTest] Evaluated paths end\n");
	}
	
	@Test
	void find_ytdlp() {
		String ytdlpPath = ExecutablePathFinder.findYtdlp();
		System.out.println("[ExecutablePathFinderTest] yt-dlp path : \"" + ytdlpPath + "\"");
		// Does not fail if null, because the test machine may not have yt-dlp in PATH
	}
	
	@Test
	void find_java() {
		String javaPath = ExecutablePathFinder.findFromPath("java", "--version");
		System.out.println("[ExecutablePathFinderTest] java path : \"" + javaPath + "\"");
		// Does not fail if null, because the test machine may not have java in PATH
	}
	
	@Test
	void find_shell() {
		List<String> shell = OSUtil.isUnix() ? List.of("bash", "-c", "echo find_shell") : List.of("cmd", "/c", "echo find_shell");
		String shellPath = ExecutablePathFinder.findFromPath(shell);
		System.out.println("[ExecutablePathFinderTest] " + shell.get(0) + " path : \"" + shellPath + "\"");
	}
	
	/**
	 * Test to find an executable that is common in most OS. 
	 */
	@Test
	void find_curl() {
		String curlPath = ExecutablePathFinder.findFromPath("curl", "--version");
		System.out.println("[ExecutablePathFinderTest] curl path : \"" + curlPath + "\"");
		assertNotNull(curlPath);
	}

}
