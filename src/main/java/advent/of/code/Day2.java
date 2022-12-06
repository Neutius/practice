package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day2 {

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day2Input.txt");
		String inputString = Files.readString(inputFile);

		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);

		System.out.println(inputString);
		System.out.println(Arrays.toString(inputLines));

		List<RoundOfPlay> roundsOfPlay = new ArrayList<>();
		for (String string : inputLines) {
			RoundOfPlay roundOfPlay = new RoundOfPlay(string);
			roundsOfPlay.add(roundOfPlay);
		}

		int totalScore = roundsOfPlay.stream().mapToInt(RoundOfPlay::getScore).sum();
		System.out.println("Total score: " + totalScore);

		// below this point: Day 2 Task 2
		List<ActualRoundOfPlay> actualRoundsOfPlay = new ArrayList<>();
		for (String string : inputLines) {
			ActualRoundOfPlay actualRoundOfPlay = new ActualRoundOfPlay(string);
			actualRoundsOfPlay.add(actualRoundOfPlay);
		}

		int actualTotalScore = actualRoundsOfPlay.stream().mapToInt(ActualRoundOfPlay::getActualScore).sum();
		System.out.println("Actual total score: " + actualTotalScore);

		System.out.println();
	}

	static class ActualRoundOfPlay {
		final String inputString;
		final OpponentHand opponentHand;
		final ReactivePlay reactivePlay;

		public ActualRoundOfPlay(String inputString) {
			this.inputString = inputString;
			String[] splitInputString = inputString.split(" ");
			opponentHand = OpponentHand.valueOf(splitInputString[0]);
			reactivePlay = ReactivePlay.valueOf(splitInputString[1]);
		}

		int getActualScore() {
			switch (opponentHand) {
				case A: // Opponent plays Rock
					switch (reactivePlay) {
						case X: return 0 + 3; // I need to lose: play Scissors (3 points)
						case Y: return 3 + 1; // I need to draw: play Rock (1 point)
						case Z: return 6 + 2; // I need to win: play Paper (2 points)
					}
				case B: // Opponent plays Paper
					switch (reactivePlay) {
						case X: return 0 + 1; // I need to lose: play Rock (1 point)
						case Y: return 3 + 2; // I need to draw: play Paper (2 points)
						case Z: return 6 + 3; // I need to win: play Scissors (3 points)
					}

				case C: // Opponent plays Scissors
					switch (reactivePlay) {
						case X: return 0 + 2; // I need to lose: play Paper (2 points)
						case Y: return 3 + 3; // I need to draw: play Scissors (3 points)
						case Z: return 6 + 1; // I need to win: play Rock (1 point)
					}

			}


			throw new IllegalStateException("No score found for round with input: " + inputString);
		}
	}

	enum ReactivePlay {
		X("Lose"),
		Y("Draw"),
		Z("Win");

		final String description;

		ReactivePlay(String description) {
			this.description = description;
		}

	}

	static class RoundOfPlay {
		final String inputString;
		final OpponentHand opponentHand;
		final HandPlayed handPlayed;

		public RoundOfPlay(String inputString) {
			this.inputString = inputString;
			String[] splitInputString = inputString.split(" ");
			opponentHand = OpponentHand.valueOf(splitInputString[0]);
			handPlayed = HandPlayed.valueOf(splitInputString[1]);
		}

		int getScore() {
			switch(handPlayed) {
				case X: // I play Rock
					switch (opponentHand) {
						case A: return 1 + 3; // Opponent plays Rock: draw
						case B: return 1; // Opponent plays Paper: I lose
						case C: return 1 + 6; // Opponent plays Scissors: I win
					}
				case Y: // I play Paper
					switch (opponentHand) {
						case A: return 2 + 6; // Opponent plays Rock: I win
						case B: return 2 + 3; // Opponent plays Paper: draw
						case C: return 2; // Opponent plays Scissors: I lose
					}
				case Z: // I play Scissors
					switch (opponentHand) {
						case A: return 3; // Opponent plays Rock: I lose
						case B: return 3 + 6; // Opponent plays Paper: I win
						case C: return 3 + 3; // Opponent plays Scissors: draw
					}
			}
			throw new IllegalStateException("No score found for round with input: " + inputString);
		}
	}

	enum HandPlayed {
		X("Rock"),
		Y("Paper"),
		Z("Scissors");

		final String description;

		HandPlayed(String description) {
			this.description = description;
		}
	}

	enum OpponentHand {
		A("Rock"),
		B("Paper"),
		C("Scissors");

		final String description;

		OpponentHand(String description) {
			this.description = description;
		}
	}

}
