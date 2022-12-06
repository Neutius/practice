package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day9Task2 {

	static boolean performTask2 = false;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day9Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);
		List<String> stringList = Arrays.asList(inputLines);

		CoordinateTracker tracker = new CoordinateTracker();
		stringList.forEach(tracker::processInstruction);

		System.out.println("Unique coordinates visited by tail: " + tracker.visitedByTail.size());

		System.out.println();
	}

	public static class CoordinateTracker {
		private static final Coordinate START = new Coordinate(100, 100);
		private static final int HEAD_INDEX = 0;
		private static final int AMOUNT_OF_KNOTS = 10; // Set this to 2 to perform task 1

		private final Set<Coordinate> visitedByTail;
		private final List<Coordinate> currentKnotPositions;

		public CoordinateTracker() {
			currentKnotPositions = new ArrayList<>();
			for (int index = 0; index < AMOUNT_OF_KNOTS; index++) {
				currentKnotPositions.add(START);
			}
			visitedByTail = new HashSet<>();
			visitedByTail.add(currentKnotPositions.get(AMOUNT_OF_KNOTS - 1));
		}

		public void processInstruction(String instruction) {
			String[] splitInstruction = instruction.split(" ");
			String direction = splitInstruction[0];
			int times = Integer.parseInt(splitInstruction[1]);

			for (int index = 0; index < times; index++) {
				moveHeadOnce(direction);
				for (int knotIndex = 1; knotIndex < AMOUNT_OF_KNOTS; knotIndex++) {
					checkAndMoveNextKnot(knotIndex - 1, knotIndex);
				}
			}
		}

		private void checkAndMoveNextKnot(int precedingKnotIndex, int currentKnotIndex) {
			Coordinate precedingKnotLocation = currentKnotPositions.get(precedingKnotIndex);
			Coordinate currentKnotLocation = currentKnotPositions.get(currentKnotIndex);

			if (!currentKnotLocation.touches(precedingKnotLocation)) {
				Coordinate newKnotLocation = currentKnotLocation.moveTowards(precedingKnotLocation);

				currentKnotPositions.remove(currentKnotIndex);
				currentKnotPositions.add(currentKnotIndex, newKnotLocation);

				if (currentKnotIndex + 1 == AMOUNT_OF_KNOTS) {
					System.out.println("Tail position: " + newKnotLocation);
					visitedByTail.add(newKnotLocation);
				}
			}
		}

		private void moveHeadOnce(String direction) {
			Coordinate currentHeadPosition = currentKnotPositions.get(HEAD_INDEX);
			Coordinate newHeadPosition = null;
			if (direction.equals("L")) {
				newHeadPosition = new Coordinate(currentHeadPosition.x - 1, currentHeadPosition.y);
			}
			if (direction.equals("R")) {
				newHeadPosition = new Coordinate(currentHeadPosition.x + 1, currentHeadPosition.y);
			}
			if (direction.equals("U")) {
				newHeadPosition = new Coordinate(currentHeadPosition.x, currentHeadPosition.y + 1);
			}
			if (direction.equals("D")) {
				newHeadPosition = new Coordinate(currentHeadPosition.x, currentHeadPosition.y - 1);
			}
			if (newHeadPosition == null) {
				throw new IllegalStateException("New Head position cannot be null");
			}
			currentKnotPositions.remove(HEAD_INDEX);
			currentKnotPositions.add(HEAD_INDEX, newHeadPosition);
			System.out.println("Head position: " + newHeadPosition);
		}
	}

	public static class Coordinate {
		private final int x;
		private final int y;

		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public boolean touches(Coordinate other) {
			int xDiff = Math.abs(this.x - other.x);
			int yDiff = Math.abs(this.y - other.y);

			return xDiff <= 1 && yDiff <= 1;
		}

		public Coordinate moveTowards(Coordinate other) {
			int newX = getNewCoordinateValue(this.x, other.x);
			int newY = getNewCoordinateValue(this.y, other.y);

			return new Coordinate(newX, newY);
		}

		private int getNewCoordinateValue(int thisValue, int otherValue) {
			int newValue = thisValue;
			if (thisValue < otherValue) {
				newValue += 1;
			} else if (thisValue > otherValue) {
				newValue -= 1;
			}
			return newValue;
		}

		@Override
		public String toString() {
			return "Coordinate{" +
					"x=" + x +
					", y=" + y +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Coordinate that = (Coordinate) o;

			if (x != that.x) return false;
			return y == that.y;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}
	}


}
