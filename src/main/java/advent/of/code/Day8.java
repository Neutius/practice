package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Day8 {

	public static final int MIN_INDEX = 0;
	public static final int MAX_INDEX = 98;

	static boolean performTask2 = false; // TODO perform task 2 - can't be bothered right now

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day8Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);
		List<String> stringList = Arrays.asList(inputLines);

		System.out.println("Amount of lines: " + stringList.size());
		System.out.println("Amount of characters in first line: " + stringList.get(MIN_INDEX).length());

		List<List<Tree>> treeMatrix = getTreeMatrix(stringList);

		setOuterTreesToVisible(treeMatrix);

		lookFromTheLeft(treeMatrix);
		lookFromTheRight(treeMatrix);
		lookFromTheTop(treeMatrix);
		lookFromTheBottom(treeMatrix);

		long count = treeMatrix.stream()
				.flatMap(Collection::stream)
				.filter(tree -> !tree.isVisible)
				.count();

		System.out.println("Amount of trees not visible: " + count); // WRONG!
		/*
		That's not the right answer; your answer is too high. If you're stuck, make sure you're using the full input data;
		there are also some general tips on the about page, or you can ask for hints on the subreddit.
		Please wait one minute before trying again. (You guessed 8005.) [Return to Day 8]
		 */

		long actualCount = treeMatrix.stream()
				.flatMap(Collection::stream)
				.filter(tree -> tree.isVisible)
				.count();
		System.out.println("Amount of trees visible: " + actualCount); // They wanted the visible trees - I read the question wrong

		System.out.println();
	}

	private static void lookFromTheLeft(List<List<Tree>> treeMatrix) {
		System.out.println();
		System.out.println("Look from the Left");
		for (int rowIndex = MIN_INDEX; rowIndex <= MAX_INDEX; rowIndex++) {
			int maxHeightInRow = 0;
			for (int columnIndex = MIN_INDEX; columnIndex <= MAX_INDEX; columnIndex++) {
				Tree tree = treeMatrix.get(rowIndex).get(columnIndex);
				if (rowIndex % 10 == 0 && columnIndex % 10 == 0) {
					System.out.println("Rowindex: " + rowIndex + " columnindex: " + columnIndex + " ");
				}
				if (tree.height > maxHeightInRow) {
					tree.setVisible(true);
				}
				maxHeightInRow = Math.max(maxHeightInRow, tree.height);
			}
		}
	}

	private static void lookFromTheRight(List<List<Tree>> treeMatrix) {
		System.out.println();
		System.out.println("Look from the Right");
		for (int rowIndex = MAX_INDEX; rowIndex >= MIN_INDEX; rowIndex--) {
			int maxHeightInRow = 0;
			for (int columnIndex = MAX_INDEX; columnIndex >= MIN_INDEX; columnIndex--) {
				Tree tree = treeMatrix.get(rowIndex).get(columnIndex);
				if (rowIndex % 10 == 0 && columnIndex % 10 == 0) {
					System.out.println("Rowindex: " + rowIndex + " columnindex: " + columnIndex + " ");
				}
				if (tree.height > maxHeightInRow) {
					tree.setVisible(true);
				}
				maxHeightInRow = Math.max(maxHeightInRow, tree.height);
			}
		}
	}

	private static void lookFromTheTop(List<List<Tree>> treeMatrix) {
		System.out.println();
		System.out.println("Look from the Top");
		for (int columnIndex = MIN_INDEX; columnIndex <= MAX_INDEX; columnIndex++) {
			int maxHeightInColumn = 0;
			for (int rowIndex = MIN_INDEX; rowIndex <= MAX_INDEX; rowIndex++) {
				Tree tree = treeMatrix.get(rowIndex).get(columnIndex);
				if (rowIndex % 10 == 0 && columnIndex % 10 == 0) {
					System.out.println("Rowindex: " + rowIndex + " columnindex: " + columnIndex + " ");
				}
				if (tree.height > maxHeightInColumn) {
					tree.setVisible(true);
				}
				maxHeightInColumn = Math.max(maxHeightInColumn, tree.height);
			}
		}
	}

	private static void lookFromTheBottom(List<List<Tree>> treeMatrix) {
		System.out.println();
		System.out.println("Look from the Bottom");
		for (int columnIndex = MAX_INDEX; columnIndex >= MIN_INDEX; columnIndex--) {
			int maxHeightInColumn = 0;
			for (int rowIndex = MAX_INDEX; rowIndex >= MIN_INDEX; rowIndex--) {
				Tree tree = treeMatrix.get(rowIndex).get(columnIndex);
				if (rowIndex % 10 == 0 && columnIndex % 10 == 0) {
					System.out.println("Rowindex: " + rowIndex + " columnindex: " + columnIndex + " ");
				}
				if (tree.height > maxHeightInColumn) {
					tree.setVisible(true);
				}
				maxHeightInColumn = Math.max(maxHeightInColumn, tree.height);
			}
		}
	}

	private static List<List<Tree>> getTreeMatrix(List<String> stringList) {
		List<List<Tree>> treeMatrix = new ArrayList<>();
		for (int rowIndex = MIN_INDEX; rowIndex <= MAX_INDEX; rowIndex++) {
			List<Tree> currentRow = new ArrayList<>();
			treeMatrix.add(currentRow);
			for (int columnIndex = MIN_INDEX; columnIndex <= MAX_INDEX; columnIndex++) {
				String asText = String.valueOf(stringList.get(rowIndex).charAt(columnIndex));
				int treeHeight = Integer.parseInt(asText);
				Tree tree = new Tree(rowIndex, columnIndex, treeHeight);
				currentRow.add(tree);
			}
		}
		return treeMatrix;
	}

	private static void setOuterTreesToVisible(List<List<Tree>> treeMatrix) {
		for (int index = MIN_INDEX; index <= MAX_INDEX; index++) {
			treeMatrix.get(index).get(MIN_INDEX).setVisible(true);
			treeMatrix.get(index).get(MAX_INDEX).setVisible(true);
			treeMatrix.get(MIN_INDEX).get(index).setVisible(true);
			treeMatrix.get(MAX_INDEX).get(index).setVisible(true);
		}
	}

	private static class Tree {
		private final int rowIndex;
		private final int columnIndex;
		private final int height;

		private boolean isVisible = false;

		public boolean isVisible() {
			return isVisible;
		}

		public void setVisible(boolean visible) {
			isVisible = visible;
		}

		public Tree(int rowIndex, int columnIndex, int height) {
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
			this.height = height;
		}
	}

}
