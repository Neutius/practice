package geo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Segment {
	private final boolean wrapAround;
	private final double fromAngle;
	private final double toAngle;
	private final List<SortedCoordinate> coordinates = new ArrayList<>();

	Segment(boolean wrapAround, double fromAngle, double toAngle) {
		this.wrapAround = wrapAround;
		this.fromAngle = fromAngle;
		this.toAngle = toAngle;
	}

	boolean belongsInSegment(SortedCoordinate coordinate) {
		return wrapAround
				? fromAngle <= coordinate.getAngle() || coordinate.getAngle() <= toAngle
				: fromAngle <= coordinate.getAngle() && coordinate.getAngle() <= toAngle;
	}

	void add(SortedCoordinate coordinate) {
		coordinates.add(coordinate);
	}

	void addCoordinateToCurrentOuterCoordinates(Set<SortedCoordinate> currentOuterCoordinates) {
		List<SortedCoordinate> sortedCoordinateList = this.coordinates.stream()
				.filter(SortedCoordinate::isOutsideCurrentBoundary)
				.sorted(ComplexBoundingPolygonGenerator.DISTANCE_COMPARE)
				.collect(Collectors.toList());

		if (!sortedCoordinateList.isEmpty()) {
			SortedCoordinate sortedCoordinate = sortedCoordinateList.get(sortedCoordinateList.size() - 1);
			currentOuterCoordinates.add(sortedCoordinate);
			sortedCoordinate.setOnCurrentBoundary(true);
		}

	}

	@Override
	public String toString() {
		return "Segment{" +
				"reversed=" + wrapAround +
				", fromAngle=" + fromAngle +
				", toAngle=" + toAngle +
				", coordinates=" + coordinates +
				'}';
	}
}
