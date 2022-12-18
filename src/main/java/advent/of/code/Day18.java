package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18 {

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	private static final Map<Coordinate, Voxel> coordinateVoxelMap = new HashMap<>();

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day18Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);
		List<String> stringList = Arrays.asList(inputLines);

		for (String inputLine : stringList) {
			String[] split = inputLine.split(",");
			Coordinate coordinate = new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			Voxel voxel = new Voxel(coordinate);
			coordinateVoxelMap.put(coordinate, voxel);
		}

		Collection<Voxel> voxels = coordinateVoxelMap.values();
		checkResultSize(stringList, voxels);

		for (Voxel voxel : voxels) {
			List<Coordinate> adjacent = voxel.calculateAllAdjacentCoordinates();
			for (Coordinate coordinate : adjacent) {
				Voxel other = coordinateVoxelMap.get(coordinate);
				processAdjacentVoxels(voxel, other);
			}
		}

		int totalAmountOfExposedSides = voxels.stream().mapToInt(Voxel::getExposedSides).sum();
		System.out.println("Total amount of exposed sides: " + totalAmountOfExposedSides); // answer to Task 1

		Set<ExposedSide> allExposedSides = new HashSet<>();
		for (Voxel voxel : voxels) {
			for (Coordinate empty : voxel.calculateEmptyAdjacentCoordinates()) {
				allExposedSides.add(new ExposedSide(voxel.coordinate(), empty));
			}
		}

		System.out.println("Total amount of exposed sides (created objects): " + allExposedSides.size()); // answer to Task 1, again

		ExposedSideConnector connector = new ExposedSideConnector(allExposedSides);
		connector.connectExposedSides();
		Set<ExposedSide> largestSetOfConnectedExposedSides = connector.getLargestSetOfConnectedExposedSides();

		System.out.println("Total amount of outer exposed sides: " + largestSetOfConnectedExposedSides.size()); // answer to Task 2
		// first attempt was 763 - was too low
		// second attempt was 4308 (same answer as Task 1) - was too high
		// third attempt was 4284 - still too high

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

		public List<Coordinate> calculateEmptyAdjacentCoordinates() {
			List<Coordinate> result = new ArrayList<>(calculateAllAdjacentCoordinates());
			result.removeAll(adjacent);
			return result;
		}

		public List<Coordinate> calculateAllAdjacentCoordinates() {
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

	public record ExposedSide(Coordinate filled, Coordinate empty) {
		public boolean isConnectedTo(ExposedSide other) {
			return (faceSameFilledSpace(other) && notBlockedByDiagonal(other))
					|| faceSameEmptySpace(other)
					|| areAdjacentOnSameSurface(other);
		}

		private boolean faceSameFilledSpace(ExposedSide other) {
			return this.filled.equals(other.filled);
		}

		private boolean notBlockedByDiagonal(ExposedSide other) {
			if (!faceSameFilledSpace(other)) {
				throw new IllegalArgumentException(
						"Only check for blocking diagonals when exposed sides share the same filled space");
			}

			int diagonalX = this.empty.x + other.empty.x - this.filled.x;
			int diagonalY = this.empty.y + other.empty.y - this.filled.y;
			int diagonalZ = this.empty.z + other.empty.z - this.filled.z;

			Coordinate diagonal = new Coordinate(diagonalX, diagonalY, diagonalZ);

			return coordinateVoxelMap.get(diagonal) == null;
		}

		private boolean faceSameEmptySpace(ExposedSide other) {
			return this.empty.equals(other.empty);
		}

		private boolean areAdjacentOnSameSurface(ExposedSide other) {
			return this.empty.isAdjacent(other.empty) && this.filled.isAdjacent(other.filled);
		}
	}

	public static class ExposedSideConnector {
		private final Set<ExposedSide> unconnectedExposedSides;
		private final List<Set<ExposedSide>> connectedExposedSides = new ArrayList<>();

		private Set<ExposedSide> currentlyConnectingExposedSides;

		public ExposedSideConnector(Set<ExposedSide> allExposedSides) {
			unconnectedExposedSides = new HashSet<>(allExposedSides);
		}

		public Set<ExposedSide> getLargestSetOfConnectedExposedSides() {
			connectedExposedSides.sort(Comparator.comparingInt(Set::size));
			return connectedExposedSides.get(connectedExposedSides.size() - 1);
		}

		public void connectExposedSides() {
			Optional<ExposedSide> any = unconnectedExposedSides.stream().findAny(); // how to get a single element from a Set?
			if (any.isEmpty()) {
				return;
			}

			currentlyConnectingExposedSides = new HashSet<>();
			connectedExposedSides.add(currentlyConnectingExposedSides);
			currentlyConnectingExposedSides.add(any.get());

			addConnectedSides();

			if (unconnectedExposedSides.size() > 0) {
				connectExposedSides();
			}
		}

		private void addConnectedSides() {
			Set<ExposedSide> currentIteration = this.currentlyConnectingExposedSides;
			boolean canConnectMore = true;
			while (canConnectMore) {
				Set<ExposedSide> newConnectedSidesFound = new HashSet<>();
				for (ExposedSide current : currentIteration) {
					newConnectedSidesFound.addAll(findNewConnectedSides(current));
				}

				this.currentlyConnectingExposedSides.addAll(newConnectedSidesFound);
				unconnectedExposedSides.removeAll(newConnectedSidesFound);

				canConnectMore = newConnectedSidesFound.size() > 0;
				currentIteration = newConnectedSidesFound;
			}
		}

		private Set<ExposedSide> findNewConnectedSides(ExposedSide current) {
			return unconnectedExposedSides.stream()
					.filter(side -> side.isConnectedTo(current))
					.collect(Collectors.toSet());
		}

	}

}
