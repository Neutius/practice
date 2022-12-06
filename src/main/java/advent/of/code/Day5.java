package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Day5 {

	static boolean performTask2 = true;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day5Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);
		List<String> stringList = Arrays.asList(inputLines);

		List<Stack<Character>> stackList = new ArrayList<>();
		stackList.add(new Stack<>()); // add dummy stack at index 0 - actual crate stacks are labeled 1-9

		// Characters representing crates in the input file are 4 spaces apart - last one has index 33
		for (int columnIndexForStack = 1; columnIndexForStack <= 33; columnIndexForStack += 4) {
			stackList.add(createStack(stringList, columnIndexForStack));
		}

		System.out.println();
		stackList.forEach(System.out::println);

		for (int lineIndex = 10; lineIndex <= 510; lineIndex++) {
			String[] instructionLine = stringList.get(lineIndex).split(" ");
			moveCratesAsInstructed(stackList, instructionLine);
		}

		System.out.println();
		stackList.forEach(System.out::println);

		System.out.println();
		System.out.print("Crates on top of stacks: ");
		for (int crateStackIndex = 1; crateStackIndex <= 9; crateStackIndex++) {
			System.out.print(stackList.get(crateStackIndex).peek());
		}

		System.out.println();
	}

	private static void moveCratesAsInstructed(List<Stack<Character>> stackList, String[] instructionLine) {
		int numberOfOperations = Integer.parseInt(instructionLine[1]);
		int from = Integer.parseInt(instructionLine[3]);
		int to = Integer.parseInt(instructionLine[5]);

		Stack<Character> fromStack = stackList.get(from);
		Stack<Character> toStack = stackList.get(to);

		if (performTask2) {
			moveCratesAsForTask2(numberOfOperations, fromStack, toStack);
		}
		else {
			moveCratesAsForTask1(numberOfOperations, fromStack, toStack);
		}
	}

	private static void moveCratesAsForTask2(int numberOfOperations, Stack<Character> fromStack, Stack<Character> toStack) {
		Stack<Character> virtualTempStack = new Stack<>();
		for (int operationCount = 0; operationCount < numberOfOperations; operationCount++) {
			Character crate = fromStack.pop();
			virtualTempStack.push(crate);
		}
		for (int operationCount = 0; operationCount < numberOfOperations; operationCount++) {
			Character crate = virtualTempStack.pop();
			toStack.push(crate);
		}
	}

	private static void moveCratesAsForTask1(int numberOfOperations, Stack<Character> fromStack, Stack<Character> toStack) {
		for (int operationCount = 0; operationCount < numberOfOperations; operationCount++) {
			Character crate = fromStack.pop();
			toStack.push(crate);
		}
	}

	private static Stack<Character> createStack(List<String> stringList, int columnIndexForStack) {
		Stack<Character> stack = new Stack<>();
		for (int index = 7; index >= 0; index--) {
			addCrateCharacterToStack(stack, stringList.get(index), columnIndexForStack);
		}
		return stack;
	}

	private static void addCrateCharacterToStack(Stack<Character> stack, String inputString, int columnIndexForStack) {
		System.out.println(inputString);
		if (inputString.length() <= columnIndexForStack) {
			return; // IntelliJ seems to strip the spaces at the end of lines - thanks, IntelliJ
		}
		char crate = inputString.charAt(columnIndexForStack);
		if (crate != ' ') {
			stack.push(crate);
		}
	}

}
