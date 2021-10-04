package geo;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.Length;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.util.Objects;

class SortedCoordinate {
	private static final GeometryFactory FACTORY = new GeometryFactory();

	private final Coordinate coordinate;
	private final Point point;
	private final double angle;
	private final double distance;

	private boolean onCurrentBoundary;
	private boolean withinCurrentBoundary;

	SortedCoordinate(Coordinate coordinate, Coordinate centerPoint) {
		this.coordinate = coordinate;
		point = FACTORY.createPoint(coordinate);
		angle = Angle.angle(centerPoint, coordinate);
		distance = Length.ofLine(new CoordinateArraySequence(new Coordinate[]{coordinate, centerPoint}));
	}

	Coordinate getCoordinate() {
		return coordinate;
	}

	double getAngle() {
		return angle;
	}

	double getDistance() {
		return distance;
	}

	Point asPoint() {
		return point;
	}

	boolean intersects(Geometry geometry) {
		return point.intersects(geometry);
	}

	boolean isOnCurrentBoundary() {
		return onCurrentBoundary;
	}

	void setOnCurrentBoundary(boolean onCurrentBoundary) {
		this.onCurrentBoundary = onCurrentBoundary;
	}

	boolean isWithinCurrentBoundary() {
		return withinCurrentBoundary;
	}

	boolean isOutsideCurrentBoundary() {
		return !isWithinCurrentBoundary();
	}

	void setWithinCurrentBoundary(boolean withinCurrentBoundary) {
		this.withinCurrentBoundary = withinCurrentBoundary;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return coordinate.equals(((SortedCoordinate) o).coordinate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(coordinate);
	}
}
