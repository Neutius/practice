package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day6 {
	static boolean performTask2 = true;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day6Input.txt");
		String inputString = Files.readString(inputFile);

		if (performTask2) {
			performTask(inputString, 13, "2");
		}
		else {
			performTask(inputString, 3, "1");
		}

	}

	private static void performTask(String inputString, int startingIndexForTask, String taskNumber) {
		for (int index = startingIndexForTask; index < inputString.length(); index++) {
			List<Character> currentCharacterList = new ArrayList<>();

			for (int innerIndex = (index - startingIndexForTask); innerIndex <= index; innerIndex++) {
				currentCharacterList.add(inputString.charAt(innerIndex));
			}

			System.out.println("currentCharacterList: " + currentCharacterList);

			if (areAllUnique(currentCharacterList)) {
				System.out.println("Unique characters found: " + inputString.substring(index - startingIndexForTask, index + 1));
				System.out.println("Indices of unique characters: " + (index - startingIndexForTask) + "-" + (index));
				System.out.println("Answer for task " + taskNumber + " is " + (index + 1));
				return;
			}

		}
	}

	private static boolean areAllUnique(List<Character> charList) {
		Set<Character> charSet = new HashSet<>(charList);
		return charList.size() == charSet.size();
	}

}
