package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day20 {
	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";
	public static final int DECRYPTION_KEY = 811589153;

	static boolean performTask2 = true;

	private record NumberPosition(int originalIndex, long numberValue) {
	}

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day20Input.txt");
		String inputString = Files.readString(inputFile);
		List<String> inputLines = Arrays.asList(inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED));

		int inputSize = inputLines.size();
		System.out.println("Amount of lines in input: " + inputSize);
		int maxIndex = inputSize - 1;

		List<NumberPosition> workingList = new ArrayList<>();
		Map<Integer, NumberPosition> originalIndexMap = new HashMap<>();
		NumberPosition valueZero = null;

		for (int index = 0; index <= maxIndex; index++) {
			long numberValue = Integer.parseInt(inputLines.get(index));
			if (performTask2) {
				numberValue *= DECRYPTION_KEY; // for Task 2, the initial value must be multiplied with the decryption key
			}
			NumberPosition current = new NumberPosition(index, numberValue);
			workingList.add(current);
			originalIndexMap.put(index, current);
			if (numberValue == 0) {
				if (valueZero != null) {
					throw new IllegalStateException("More than one value zero found!");
				}
				valueZero = current;
			}
		}
		if (valueZero == null) {
			throw new IllegalStateException("Value zero not found!");
		}

		mixWorkingList(maxIndex, workingList, originalIndexMap);
		if (performTask2) { // List has been mixed once, must be mixed 10 times total for Task 2
			System.out.println("List has been mixed once, must be mixed 10 times total for Task 2");
			for (int iteration = 2; iteration <= 10; iteration++) {
				System.out.println("Start mixing iteration: " + iteration);
				mixWorkingList(maxIndex, workingList, originalIndexMap);
			}
		}

		int currentIndexOfValueZero = workingList.indexOf(valueZero);
		System.out.println("Current index of value zero: " + currentIndexOfValueZero);
		NumberPosition firstCoordinate = getCoordinate(workingList, currentIndexOfValueZero, 1000, "First");
		NumberPosition secondCoordinate = getCoordinate(workingList, currentIndexOfValueZero, 2000, "Second");
		NumberPosition thirdCoordinate = getCoordinate(workingList, currentIndexOfValueZero, 3000, "Third");

		long sum = firstCoordinate.numberValue + secondCoordinate.numberValue + thirdCoordinate.numberValue;
		System.out.println("Sum of three coordinate values: " + sum); // answer to Task 1 or Task 2
		// first answer to Task 1: 3339 - too low (no idea what I'm doing wrong)
		// second answer to Task 1 was correct! Thanks, Reddit
		// first answer to Task 2: 916533524 - too low
		// second answer to Task 2 was correct! I need to use long instead of int

		System.out.println(); // Set breakpoint for debugging
	}

	private static void mixWorkingList(int maxIndex, List<NumberPosition> workingList, Map<Integer, NumberPosition> originalIndexMap) {
		for (int index = 0; index <= maxIndex; index++) {
			NumberPosition current = originalIndexMap.get(index);
			int currentIndex = workingList.indexOf(current);
			int newIndex = getNewIndex(maxIndex, current, currentIndex);

			workingList.remove(current);
			workingList.add(newIndex, current);
			/*
			if previous index was 5, and value is 2, then new index is 7
			  	when removed, elements previously at [6,7,8,9,...] move to [5,6,7,8,...]
			  	when inserted at new index, elements at [5,6] stay in their new position,
			  		and elements at [7,8,...] move back to their original position at [8,9,...]
			 */

			int actualNewIndex = workingList.indexOf(current);

			if (index % 1000 == 0 || index == maxIndex) {
				System.out.println(String.format(
						"For value %d with original index %d - previous index: %d - new index: %d - actual new index: %d",
						current.numberValue, current.originalIndex, currentIndex, newIndex, actualNewIndex));
			}
		}
	}

	private static int getNewIndex(int maxIndex, NumberPosition current, int currentIndex) {
		long newIndex = currentIndex + current.numberValue;

		newIndex = newIndex % maxIndex;

		if (newIndex < 0) {
			newIndex += maxIndex;
		}
		return (int) newIndex;
		/* Calculate new index (with inputSize 5000 and maxIndex 4999):
		 	if current index is 2 and value is 2, then new index is 4

		 	WRONG (thanks to u/DrunkHacker for pointing at my mistake):
		 		if current index is 4999 and value is 1, then new index is 0 (so if above 4999, do -5000 to wrap around)
		 		if current index is 0 and value is -1, then new index is 4999 (so if below 0, do +5000 to wrap around)
		 	CORRECT:
		 		if current index is 4999 and value is 1, then new index is 1 (so if above 4999, do -4999 to wrap around)
		 		if current index is 0 and value is -1, then new index is 4998 (so if below 0, do +4999 to wrap around)
		 	the list loops around, so moving an item from index 0 to index 4999 (or vice versa) is not a move at all.
		 */
	}

	private static NumberPosition getCoordinate(List<NumberPosition> workingList, int currentIndexOfValueZero, int coordinateOffset, String ordinal) {
		int firstCoordinateIndex = getCoordinateIndex(workingList.size(), currentIndexOfValueZero, coordinateOffset);
		NumberPosition firstCoordinate = workingList.get(firstCoordinateIndex);
		System.out.println(ordinal + " coordinate " + firstCoordinate + " found at index: " + firstCoordinateIndex);
		return firstCoordinate;
	}

	private static int getCoordinateIndex(int inputSize, int currentIndexOfValueZero, int coordinateOffset) {
		return currentIndexOfValueZero + coordinateOffset < inputSize
				? currentIndexOfValueZero + coordinateOffset
				: currentIndexOfValueZero + coordinateOffset - inputSize;
	}

}
