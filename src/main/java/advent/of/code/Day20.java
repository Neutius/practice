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

	static boolean performTask2 = false;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	private record NumberPosition(int originalIndex, int numberValue) {
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
			int numberValue = Integer.parseInt(inputLines.get(index));
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

		for (int index = 0; index <= maxIndex; index++) {
			NumberPosition current = originalIndexMap.get(index);
			int currentIndex = workingList.indexOf(current);
			int newIndex = getNewIndex(inputSize, maxIndex, current, currentIndex);

			workingList.remove(current);
			workingList.add(newIndex, current); // not sure if this is correct
			/*
			if previous index was 5, and value is 2, then new index is 7
			  	when removed, elements previously at [6,7,8,9,...] move to [5,6,7,8,...]
			  	when inserted at new index, elements at [5,6] stay in their new position,
			  		and elements at [7,8,...] move back to their original position at [8,9,...]
			 */

			int actualNewIndex = workingList.indexOf(current);

			System.out.println(String.format(
					"For value %d with original index %d - previous index: %d - new index: %d - actual new index: %d",
					current.numberValue, current.originalIndex, currentIndex, newIndex, actualNewIndex));
		}

		int currentIndexOfValueZero = workingList.indexOf(valueZero);
		System.out.println("Current index of value zero: " + currentIndexOfValueZero);
		NumberPosition firstCoordinate = getCoordinate(workingList, currentIndexOfValueZero, 1000, "First");
		NumberPosition secondCoordinate = getCoordinate(workingList, currentIndexOfValueZero, 2000, "Second");
		NumberPosition thirdCoordinate = getCoordinate(workingList, currentIndexOfValueZero, 3000, "Third");

		int sum = firstCoordinate.numberValue + secondCoordinate.numberValue + thirdCoordinate.numberValue;
		System.out.println("Sum of three coordinate values: " + sum); // answer to Task 1
		// first answer: 3339 - too low (no idea what I'm doing wrong)

		System.out.println(); // Set breakpoint for debugging
	}

	private static int getNewIndex(int inputSize, int maxIndex, NumberPosition current, int currentIndex) {
		int newIndex = currentIndex + current.numberValue;
		while (newIndex < 0) {
			newIndex += maxIndex;
		}
		while (newIndex > maxIndex) {
			newIndex -= maxIndex;
		}
		return newIndex;
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
