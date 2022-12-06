package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day9 {

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

		private final Set<Coordinate> visitedByTail;

		private Coordinate tailPosition;
		private Coordinate headPosition;

		public CoordinateTracker() {
			tailPosition = START;
			headPosition = START;
			visitedByTail = new HashSet<>();
			visitedByTail.add(tailPosition);
		}

		public void processInstruction(String instruction) {
			String[] splitInstruction = instruction.split(" ");

			String direction = splitInstruction[0];
			int times = Integer.parseInt(splitInstruction[1]);

			for (int index = 0; index < times; index++) {
				moveHeadOnce(direction);
				if (!tailPosition.touches(headPosition)) {
					tailPosition = tailPosition.moveTowards(headPosition);
					System.out.println("Tail position: " + tailPosition);
					visitedByTail.add(tailPosition);
				}
			}

		}

		private void moveHeadOnce(String direction) {
			if (direction.equals("L")) {
				headPosition = new Coordinate(headPosition.x - 1, headPosition.y);
			}
			if (direction.equals("R")) {
				headPosition = new Coordinate(headPosition.x + 1, headPosition.y);
			}
			if (direction.equals("U")) {
				headPosition = new Coordinate(headPosition.x, headPosition.y + 1);
			}
			if (direction.equals("D")) {
				headPosition = new Coordinate(headPosition.x, headPosition.y - 1);
			}
			System.out.println("Head position: " + headPosition);
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
