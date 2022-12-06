package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 {

	public static final String DOUBLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n\\r\\n)+";
	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day1Input.txt");
		String inputString = Files.readString(inputFile);

		String[] inputStringPerElf = inputString.split(DOUBLE_CARRIAGE_RETURN_LINE_FEED);
		List<Elf> elfList = new ArrayList<>();

		for (String string : inputStringPerElf) {
			Elf elf = new Elf(string);
			elfList.add(elf);
			elf.convertCalorieList();
		}

		List<Elf> sortedElfList = elfList.stream()
				.sorted(Comparator.comparingInt(Elf::getTotalCalories).reversed())
				.collect(Collectors.toList());

		System.out.println();
		sortedElfList.forEach(elf -> System.out.print(elf.getTotalCalories() + " "));

		day1Task2(sortedElfList);

		System.out.println();
	}

	private static void day1Task2(List<Elf> sortedElfList) {
		int caloriesCarriedByTopThree = 0;
		caloriesCarriedByTopThree += sortedElfList.get(0).getTotalCalories();
		caloriesCarriedByTopThree += sortedElfList.get(1).getTotalCalories();
		caloriesCarriedByTopThree += sortedElfList.get(2).getTotalCalories();

		System.out.println();
		System.out.println("Total calories carried by top 3 elves: " + caloriesCarriedByTopThree);
	}

	private static class Elf {
		final String calorieList;

		private int totalCalories;

		public Elf(String calorieList) {
			this.calorieList = calorieList;
		}

		public void convertCalorieList() {
			String[] inputStringPerListedItem = calorieList.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);

			totalCalories = 0;

			for (String string : inputStringPerListedItem) {
				totalCalories += Integer.parseInt(string);
			}
		}

		public int getTotalCalories() {
			return totalCalories;
		}
	}

}
