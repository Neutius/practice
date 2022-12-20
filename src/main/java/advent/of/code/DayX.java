package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class DayX {

	static boolean performTask2 = false;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day###Input.txt");
		String inputString = Files.readString(inputFile);
		List<String> inputLines = Arrays.asList(inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED));


		System.out.println(); // Set breakpoint for debugging
	}


}
