package geo;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

class PolygonGeneratorHelper {

	private PolygonGeneratorHelper() {
		// static utility class
	}

	static Coordinate[] getCoordinatesAsSortedClosedArray(Collection<SortedCoordinate> currentOuterCoordinates) {
		List<Coordinate> outerCoordinateList = new ArrayList<>();
		currentOuterCoordinates.stream()
				.sorted(ComplexBoundingPolygonGenerator.ANGLE_COMPARE)
				.map(SortedCoordinate::getCoordinate)
				.forEachOrdered(outerCoordinateList::add);

		ensureCoordinatesFormAClosedBoundary(outerCoordinateList);

		return outerCoordinateList.toArray(Coordinate[]::new);
	}

	static void ensureCoordinatesFormAClosedBoundary(List<Coordinate> coordinateList) {
		Coordinate firstCoordinate = coordinateList.get(0);
		Coordinate lastCoordinate = coordinateList.get(coordinateList.size() - 1);
		if (!firstCoordinate.equals(lastCoordinate)) {
			coordinateList.add(firstCoordinate);
		}
	}

	static Coordinate calculateCenterPoint(List<Coordinate> coordinateList) {
		double averageX = coordinateList.stream().mapToDouble(Coordinate::getX).sum() / coordinateList.size();
		double averageY = coordinateList.stream().mapToDouble(Coordinate::getY).sum() / coordinateList.size();

		return new Coordinate(averageX, averageY);
	}

	static List<Segment> createSegmentsBetweenOuterCoordinates(Set<SortedCoordinate> currentOuterCoordinates) {
		List<SortedCoordinate> outerCoordinateList = new ArrayList<>(currentOuterCoordinates);
		outerCoordinateList.sort(ComplexBoundingPolygonGenerator.ANGLE_COMPARE);

		List<Segment> segmentList = new ArrayList<>();

		segmentList.add(new Segment(true,
				outerCoordinateList.get(outerCoordinateList.size() -1).getAngle(),
				outerCoordinateList.get(0).getAngle()));

		int numberOfCoordinatesInPolygon = outerCoordinateList.size();

		for (int index = 1; index < numberOfCoordinatesInPolygon; index++) {
			segmentList.add(new Segment(false, outerCoordinateList.get(index -1).getAngle(),
					outerCoordinateList.get(index).getAngle()));
		}

		return segmentList;
	}

	static void addCoordinatesToCorrectSegment(List<Segment> segmentList, List<SortedCoordinate> coordinateList) {
		for (SortedCoordinate sortedCoordinate : coordinateList) {
			for (Segment segment : segmentList) {
				if (segment.belongsInSegment(sortedCoordinate)) {
					segment.add(sortedCoordinate);
				}
			}
		}
	}
}
