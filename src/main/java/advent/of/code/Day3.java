package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

public class Day3 {

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day3Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);

		System.out.println(Arrays.toString(inputLines));

		List<Rucksack> rucksacks = new ArrayList<>();
		for (String string : inputLines) {
			Rucksack rucksack = new Rucksack(string);
			rucksacks.add(rucksack);
		}

		List<Character> duplicateItems = rucksacks.stream()
				.map(Rucksack::getDuplicateItem)
				.collect(Collectors.toList());

		int totalPriority = 0;
		for (Character item : duplicateItems) {
			if (isLowerCase(item)) {
				System.out.println("Lowercase item: " + item);
				totalPriority += (item - 96);
			}
			else if (isUpperCase(item)) {
				System.out.println("Uppercase item: " + item);
				totalPriority += (item - 38);
			}
			else {
				throw new IllegalStateException("Character is neither uppercase nor lower case: " + item);
			}
		}

		System.out.println("Total priority of misplaced items: " + totalPriority);

		// Second task from here on out

		int totalBadgeItemPriority = 0;
		for (int index = 0; index < 300; index += 3) {
			Rucksack e1 = rucksacks.get(index);
			Rucksack e2 = rucksacks.get(index + 1);
			Rucksack e3 = rucksacks.get(index + 2);

			Optional<Character> characterOptional = e1.getAllItems().stream()
					.filter(item -> e2.getAllItems().contains(item))
					.filter(item -> e3.getAllItems().contains(item))
					.findFirst();

			Character item = characterOptional.orElseThrow(() ->
					new IllegalStateException("No badge item found for elf group"));

			if (isLowerCase(item)) {
				totalBadgeItemPriority += (item - 96);
			}
			else if (isUpperCase(item)) {
				totalBadgeItemPriority += (item - 38);
			}
			else {
				throw new IllegalStateException("Character is neither uppercase nor lower case: " + item);
			}
		}

		System.out.println("Total priority of badge items: " + totalBadgeItemPriority);


	}

	private static class Rucksack {
		private final int compartmentSize;
		private final String firstCompartment;
		private final String secondCompartment;

		private Set<Character> allItems = new HashSet<>();

		public Rucksack(String inputString) {
			int length = inputString.length();
			compartmentSize = length / 2;
			firstCompartment = inputString.substring(0, compartmentSize);
			secondCompartment = inputString.substring(compartmentSize, length);
		}

		public Set<Character> getAllItems() {
			if (allItems.isEmpty()) {
				getDuplicateItem();
			}
			return allItems;
		}

		public Character getDuplicateItem() {
			Set<Character> itemsInFirstCompartment = new HashSet<>();
			Set<Character> itemsInSecondCompartment = new HashSet<>();

			for (int index = 0; index < compartmentSize; index++) {
				itemsInFirstCompartment.add(firstCompartment.charAt(index));
				allItems.add(firstCompartment.charAt(index));
				itemsInSecondCompartment.add(secondCompartment.charAt(index));
				allItems.add(secondCompartment.charAt(index));
			}

			Optional<Character> first = itemsInFirstCompartment.stream()
					.filter(itemsInSecondCompartment::contains)
					.findFirst();

			return first.orElseThrow(() ->
					new IllegalStateException("No common character in " + firstCompartment  + " and " + secondCompartment));
		}
	}

}
