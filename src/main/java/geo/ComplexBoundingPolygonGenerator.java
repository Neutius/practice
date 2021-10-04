package geo;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ComplexBoundingPolygonGenerator {
    static final Comparator<SortedCoordinate> ANGLE_COMPARE = Comparator.comparingDouble(SortedCoordinate::getAngle);
    static final Comparator<SortedCoordinate> DISTANCE_COMPARE = Comparator.comparingDouble(SortedCoordinate::getDistance);

    private static final Logger LOG = LoggerFactory.getLogger(ComplexBoundingPolygonGenerator.class);
    private static final GeometryFactory FACTORY = new GeometryFactory();

    private Coordinate centerPoint;
    private List<SortedCoordinate> sortedByAngle;

    private Set<SortedCoordinate> currentOuterCoordinates;
    private Polygon currentPolygon;

    private int iteration = 1;

    public Geometry createComplexBoundingPolygon(List<Coordinate> coordinateList) {
        if (coordinateList.size() < 3) {
            return FACTORY.createPolygon();
        }
        else {
            setFields(coordinateList);
            return createComplexBoundingPolygon();
        }
    }

    private void setFields(List<Coordinate> coordinateList) {
        centerPoint = PolygonGeneratorHelper.calculateCenterPoint(coordinateList);
        LOG.info("Calculated center point: {}", centerPoint);

        sortedByAngle = coordinateList.stream()
                .map(coordinate -> new SortedCoordinate(coordinate, centerPoint))
                .sorted(ANGLE_COMPARE)
                .collect(Collectors.toList());
        LOG.info("Sorted by angle - first: {}", sortedByAngle.get(0).getDistance());
        LOG.info("Sorted by angle - last: {}", sortedByAngle.get(sortedByAngle.size() - 1).getDistance());

        List<SortedCoordinate> sortedByDistance = sortedByAngle.stream().sorted(DISTANCE_COMPARE).collect(Collectors.toList());
        LOG.info("Sorted by distance - first: {}", sortedByDistance.get(0).getDistance());
        LOG.info("Sorted by distance - last: {}", sortedByDistance.get(sortedByDistance.size() - 1).getDistance());

        currentOuterCoordinates = new HashSet<>();
        currentOuterCoordinates.addAll(sortedByDistance.subList(sortedByDistance.size() - 3, sortedByDistance.size()));
        currentOuterCoordinates.forEach(sc -> sc.setOnCurrentBoundary(true));
    }

    private Polygon createComplexBoundingPolygon() {
        setCurrentPolygonForCurrentOuterCoordinates();

        if (notAllCoordinatesAreWithinCurrentPolygon()) {
            expandCurrentPolygon();
        }

        return currentPolygon;
    }

    private void expandCurrentPolygon() {
        iteration++;

        List<Segment> segmentList = PolygonGeneratorHelper.createSegmentsBetweenOuterCoordinates(currentOuterCoordinates);
        segmentList.forEach(segment -> LOG.info("Segment: {}", segment));
        PolygonGeneratorHelper.addCoordinatesToCorrectSegment(segmentList, sortedByAngle);
        segmentList.forEach(segment -> segment.addCoordinateToCurrentOuterCoordinates(currentOuterCoordinates));

        setCurrentPolygonForCurrentOuterCoordinates();

        if (iteration <= 10 && notAllCoordinatesAreWithinCurrentPolygon()) {
            expandCurrentPolygon();
        }
    }

    private void setCurrentPolygonForCurrentOuterCoordinates() {
        Coordinate[] coordinateArray = PolygonGeneratorHelper.getCoordinatesAsSortedClosedArray(currentOuterCoordinates);
        LOG.info("Coordinate array for iteration {}: {}", iteration, coordinateArray);
        currentPolygon = FACTORY.createPolygon(coordinateArray);
    }

    private boolean notAllCoordinatesAreWithinCurrentPolygon() {
        return !allCoordinatesAreWithinCurrentPolygon();
    }

    private boolean allCoordinatesAreWithinCurrentPolygon() {
        for (SortedCoordinate sortedCoordinate : sortedByAngle) {
            sortedCoordinate.setWithinCurrentBoundary(sortedCoordinate.intersects(currentPolygon));
        }
        return sortedByAngle.stream().allMatch(SortedCoordinate::isWithinCurrentBoundary);
    }

}
