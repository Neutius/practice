package geo;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComplexBoundingPolygonGeneratorTest {

	/*
	These coordinates are roughly placed as follows:

	9					L		H
	8			B							D
	7
	6								O
	5		E				N					G
	4				M
	3		I									K
	2
	1			A							C
	0					J		F
	-1
	Y\X	-1	0	1	2	3	4	5	6	7	8	9

	*/
	private final Coordinate COORD_A = new Coordinate(100, 100);
	private final Coordinate COORD_B = new Coordinate(100, 800);
	private final Coordinate COORD_C = new Coordinate(800, 100);
	private final Coordinate COORD_D = new Coordinate(800, 800);

	private final Coordinate COORD_E = new Coordinate(0, 500);
	private final Coordinate COORD_F = new Coordinate(500, 0);
	private final Coordinate COORD_G = new Coordinate(900, 500);
	private final Coordinate COORD_H = new Coordinate(500, 900);

	private final Coordinate COORD_I = new Coordinate(0, 300);
	private final Coordinate COORD_J = new Coordinate(300, 0);
	private final Coordinate COORD_K = new Coordinate(900, 300);
	private final Coordinate COORD_L = new Coordinate(300, 900);

	private final Coordinate COORD_M = new Coordinate(200, 400);
	private final Coordinate COORD_N = new Coordinate(400, 500);
	private final Coordinate COORD_O = new Coordinate(600, 600);

	@Test
	void twoCoordinates_resultIsEmpty() {
		ComplexBoundingPolygonGenerator testSubject = new ComplexBoundingPolygonGenerator();

		Geometry result = testSubject.createComplexBoundingPolygon(List.of(COORD_A, COORD_B));

		assertThat(result).isNotNull();
		assertThat(result.getCoordinates()).isNotNull().isEmpty();
	}

	@Test
	void threeCoordinates_resultContainsAllThree() {
		ComplexBoundingPolygonGenerator testSubject = new ComplexBoundingPolygonGenerator();

		Geometry result = testSubject.createComplexBoundingPolygon(List.of(COORD_A, COORD_B, COORD_C));

		assertThat(result.getCoordinates()).hasSize(4).contains(COORD_A, COORD_B, COORD_C);
	}

	@Test
	void fourCoordinates_resultContainsOuterThree() {
		ComplexBoundingPolygonGenerator testSubject = new ComplexBoundingPolygonGenerator();

		Geometry result = testSubject.createComplexBoundingPolygon(List.of(COORD_A, COORD_B, COORD_C, COORD_M));

		assertThat(result.getCoordinates()).hasSize(4).contains(COORD_A, COORD_B, COORD_C);
	}

	@Test
	void fiveCoordinates_resultContainsOuterFour() {
		ComplexBoundingPolygonGenerator testSubject = new ComplexBoundingPolygonGenerator();

		Geometry result = testSubject.createComplexBoundingPolygon(List.of(COORD_A, COORD_B, COORD_C, COORD_D, COORD_M));

		assertThat(result.getCoordinates()).hasSize(5).contains(COORD_A, COORD_B, COORD_C, COORD_D);
	}

	@Test
	void sixCoordinates_resultContainsOuterFive() {
		ComplexBoundingPolygonGenerator testSubject = new ComplexBoundingPolygonGenerator();

		Geometry result = testSubject.createComplexBoundingPolygon(List.of(COORD_A, COORD_B, COORD_E, COORD_F, COORD_M, COORD_N));

		assertThat(result.getCoordinates()).hasSize(6).contains(COORD_A, COORD_B, COORD_E, COORD_F, COORD_N);
	}

	@Test
	void elevenCoordinates_resultContainsOuterEight() {
		ComplexBoundingPolygonGenerator testSubject = new ComplexBoundingPolygonGenerator();

		Geometry result = testSubject.createComplexBoundingPolygon(List.of(COORD_A, COORD_B, COORD_C, COORD_D, COORD_E, COORD_F,
				COORD_G, COORD_H, COORD_M, COORD_N, COORD_O));

		assertThat(result.getCoordinates()).hasSize(9).contains(COORD_A, COORD_B, COORD_C, COORD_D, COORD_E, COORD_F, COORD_G,
				COORD_H);
	}

	@Test
	void fifteenCoordinates_resultContainsOuterTwelve() {
		ComplexBoundingPolygonGenerator testSubject = new ComplexBoundingPolygonGenerator();

		Geometry result = testSubject.createComplexBoundingPolygon(List.of(COORD_A, COORD_B, COORD_C, COORD_D, COORD_E, COORD_F,
				COORD_G, COORD_H, COORD_I, COORD_J, COORD_K, COORD_L, COORD_M, COORD_N, COORD_O));

		assertThat(result.getCoordinates()).hasSize(13).contains(COORD_A, COORD_B, COORD_C, COORD_D, COORD_E, COORD_F, COORD_G,
				COORD_H, COORD_I, COORD_J, COORD_K, COORD_L);
	}


}