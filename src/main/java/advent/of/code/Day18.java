package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day18 {

	static boolean performTask2 = false;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day18Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);
		List<String> stringList = Arrays.asList(inputLines);

		Map<Coordinate, Voxel> coordinateVoxelMap = new HashMap<>();

		for (String inputLine : stringList) {
			String[] split = inputLine.split(",");
			Coordinate coordinate = new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			Voxel voxel = new Voxel(coordinate);
			coordinateVoxelMap.put(coordinate, voxel);
		}

		Collection<Voxel> voxels = coordinateVoxelMap.values();
		checkResultSize(stringList, voxels);

		for (Voxel voxel : voxels) {
			List<Coordinate> adjacent = voxel.getAdjacent();
			for (Coordinate coordinate : adjacent) {
				Voxel other = coordinateVoxelMap.get(coordinate);
				processAdjacentVoxels(voxel, other);
			}
		}

		int totalAmountOfExposedSides = voxels.stream().mapToInt(Voxel::getExposedSides).sum();
		System.out.println("Total amount of exposed sides: " + totalAmountOfExposedSides);

		System.out.println(); // Set breakpoint for debugging
	}

	private static void processAdjacentVoxels(Voxel voxel, Voxel other) {
		if (other == null) {
			return;
		}
		if (!other.coordinate.isAdjacent(voxel.coordinate)) {
			throw new IllegalStateException("Non-adjacent voxel is found as adjacent voxel!");
		}
		voxel.addAdjacent(other);
	}

	private static void checkResultSize(List<String> stringList, Collection<Voxel> voxels) {
		if (voxels.size() != stringList.size()) {
			throw new IllegalStateException(
					"The size of the input is different from the size of generated object - check for Hash collisions!");
		}
	}

	public record Coordinate(int x, int y, int z) {
		public boolean isAdjacent(Coordinate other) {
			return adjacentOnX(other) || adjacentOnY(other) || adjacentOnZ(other);
		}

		private boolean adjacentOnX(Coordinate other) {
			return valueIsDifferentByOne(this.x, other.x) && valueIsTheSame(this.y, other.y) && valueIsTheSame(this.z, other.z);
		}

		private boolean adjacentOnY(Coordinate other) {
			return valueIsTheSame(this.x, other.x) && valueIsDifferentByOne(this.y, other.y) && valueIsTheSame(this.z, other.z);
		}

		private boolean adjacentOnZ(Coordinate other) {
			return valueIsTheSame(this.x, other.x) && valueIsTheSame(this.y, other.y) && valueIsDifferentByOne(this.z, other.z);
		}

		private boolean valueIsDifferentByOne(int thisValue, int otherValue) {
			return Math.abs(thisValue - otherValue) == 1;
		}

		private boolean valueIsTheSame(int thisValue, int otherValue) {
			return thisValue == otherValue;
		}
	}

	public record Voxel(Coordinate coordinate, Set<Coordinate> adjacent) {
		public Voxel(Coordinate coordinate) {
			this(coordinate, new HashSet<>());
		}

		public void addAdjacent(Voxel other) {
			this.adjacent.add(other.coordinate);
			other.adjacent.add(this.coordinate);
		}

		public int getExposedSides() {
			return 6 - adjacent().size();
		}

		public List<Coordinate> getAdjacent() {
			return List.of(
					new Coordinate(coordinate.x - 1, coordinate.y, coordinate.z),
					new Coordinate(coordinate.x + 1, coordinate.y, coordinate.z),
					new Coordinate(coordinate.x, coordinate.y - 1, coordinate.z),
					new Coordinate(coordinate.x, coordinate.y + 1, coordinate.z),
					new Coordinate(coordinate.x, coordinate.y, coordinate.z - 1),
					new Coordinate(coordinate.x, coordinate.y, coordinate.z + 1)
			);
		}
	}


}
