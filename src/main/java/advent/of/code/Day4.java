package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day4 {

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day4Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);

		int enclosedSections = 0;
		int overlappingSections = 0;
		for (String string : inputLines) {
			ElfPair pair = new ElfPair(string);
			if (pair.sectionFullyContained()) {
				enclosedSections++;
			}
			if (pair.sectionsOverlap()) {
				overlappingSections++;
			}
		}

		System.out.println("Number of fully enclosed section assignments: " + enclosedSections);
		System.out.println("Number of fully or partially overlapping section assignments: " + overlappingSections);

	}

	private static class ElfPair {
		final String inputString;
		private final int firstMin;
		private final int firstMax;
		private final int secondMin;
		private final int secondMax;

		public ElfPair(String inputString) {
			this.inputString = inputString; // 71-71,42-72
			String[] twoElves = inputString.split(",");

			String firstElf = twoElves[0]; // 71-71
			String[] firstRange = firstElf.split("-");
			firstMin = Integer.parseInt(firstRange[0]); // 71
			firstMax = Integer.parseInt(firstRange[1]); // 71

			String secondElf = twoElves[1]; // 42-72
			String[] secondRange = secondElf.split("-");
			secondMin = Integer.parseInt(secondRange[0]); // 42
			secondMax = Integer.parseInt(secondRange[1]); // 72
		}

		public boolean sectionFullyContained() {
			return firstIsContainedBySecond() || secondIsContainedByFirst();
		}

		private boolean firstIsContainedBySecond() {
			// 71-71,42-72 -> 71 > 42 && 71 < 72
			return firstMin >= secondMin && firstMax <= secondMax;
		}

		private boolean secondIsContainedByFirst() {
			return firstMin <= secondMin && firstMax >= secondMax;
		}

		public boolean sectionsOverlap() {
			return !(firstEndBeforeSecondStarts() || firstStartsAfterSecondEnds());
		}

		private boolean firstStartsAfterSecondEnds() {
			return firstMin > secondMax;
		}

		private boolean firstEndBeforeSecondStarts() {
			return firstMax < secondMin;
		}
	}

}
