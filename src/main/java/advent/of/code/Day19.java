package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static advent.of.code.Day19.RobotBuildingOption.BUILD_CLAY_ROBOT;
import static advent.of.code.Day19.RobotBuildingOption.BUILD_GEODE_ROBOT;
import static advent.of.code.Day19.RobotBuildingOption.BUILD_OBSIDIAN_ROBOT;
import static advent.of.code.Day19.RobotBuildingOption.BUILD_ORE_ROBOT;
import static advent.of.code.Day19.RobotBuildingOption.NONE;
import static advent.of.code.Day19.RobotBuildingOption.WAIT_FOR_CLAY_ROBOT;
import static advent.of.code.Day19.RobotBuildingOption.WAIT_FOR_GEODE_ROBOT;
import static advent.of.code.Day19.RobotBuildingOption.WAIT_FOR_OBSIDIAN_ROBOT;
import static advent.of.code.Day19.RobotBuildingOption.WAIT_FOR_ORE_ROBOT;

public class Day19 {

	static boolean performTask2 = false;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path exampleFile = Path.of("D:\\dev\\advent\\Day19Input.txt");
		String exampleString = Files.readString(exampleFile);
		List<String> exampleLines = Arrays.asList(exampleString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED));

		processInput(exampleLines);

		System.out.println(); // Set breakpoint for debugging
	}

	private static void processInput(List<String> inputLines) {
		List<BluePrint> bluePrints = inputLines.stream().map(BluePrint::ofInputLine).collect(Collectors.toList());

		int totalQualityLevel = 0;

		for (BluePrint bluePrint : bluePrints) {
			RobotBuildingSimulator simulator = new RobotBuildingSimulator(bluePrint);
			simulator.calculate();
			int geodesOpened = simulator.geodesOpened();

			int qualityLevel = bluePrint.blueprintNumber * geodesOpened;
			System.out.println("Blueprint number: " + bluePrint.blueprintNumber + " - Geodes opened: " + geodesOpened
					+ " - Quality level: " + qualityLevel);

			totalQualityLevel += qualityLevel;
		}

		System.out.println("Total quality level (answer to Task 1): " + totalQualityLevel);

	}

	public enum RobotBuildingOption {
		BUILD_ORE_ROBOT,
		BUILD_CLAY_ROBOT,
		BUILD_OBSIDIAN_ROBOT,
		BUILD_GEODE_ROBOT,
		WAIT_FOR_ORE_ROBOT,
		WAIT_FOR_CLAY_ROBOT,
		WAIT_FOR_OBSIDIAN_ROBOT,
		WAIT_FOR_GEODE_ROBOT,
		NONE
	}

	public record ProcessNode(int time, RobotBuildingOption option, BluePrint bluePrint, ResourceInventory inventory) {
		public int getOpenedGeodes() {
			return inventory.geodesOpened;
		}
	}

	public static class RobotBuildingSimulator {
		private final BluePrint bluePrint;

		private final Queue<ProcessNode> processNodeQueue = new PriorityQueue<>(Comparator.comparingInt(ProcessNode::time));
		private final Set<ProcessNode> finishedNodes = new HashSet<>();

		private final int[] maxGeodeRobots;
		private final int[] maxGeodesOpened;

		private int geodesOpened;

		public RobotBuildingSimulator(BluePrint bluePrint) {
			this.bluePrint = bluePrint;
			maxGeodeRobots = new int[25];
			Arrays.fill(maxGeodeRobots, 0);
			maxGeodesOpened = new int[25];
			Arrays.fill(maxGeodesOpened, 0);
		}

		public void calculate() {
			processNodeQueue.add(new ProcessNode(0, NONE, bluePrint, new ResourceInventory()));

			while (!processNodeQueue.isEmpty()) {
				ProcessNode current = processNodeQueue.poll();
				if (mightStillBeViable(current)) {
					List<RobotBuildingOption> possibleSteps = calculateNextSteps(current);
					performPossibleSteps(current, possibleSteps);
				}
			}

			List<ProcessNode> sortedNodes = finishedNodes.stream()
					.sorted(Comparator.comparingInt(ProcessNode::getOpenedGeodes).reversed())
					.toList();

			geodesOpened = sortedNodes.get(0).getOpenedGeodes();
		}

		private boolean mightStillBeViable(ProcessNode current) {
			boolean twoOrLessRobotsBehind = current.inventory.geodeRobots + 2 >= maxGeodeRobots[current.time];
			boolean twoOrLessGeodesBehind = current.inventory.geodesOpened + 2 >= maxGeodesOpened[current.time];
			return twoOrLessRobotsBehind && twoOrLessGeodesBehind;
		}

		public int geodesOpened() {
			return geodesOpened;
		}

		private static List<RobotBuildingOption> calculateNextSteps(ProcessNode current) {
			return switch (current.option) {
				case WAIT_FOR_ORE_ROBOT, WAIT_FOR_CLAY_ROBOT, WAIT_FOR_OBSIDIAN_ROBOT, WAIT_FOR_GEODE_ROBOT -> List.of(current.option);
				default -> calculateNewNextSteps(current);
			};
		}

		private static List<RobotBuildingOption> calculateNewNextSteps(ProcessNode current) {
			ResourceInventory inventory = current.inventory;
			BluePrint bluePrint = current.bluePrint;
			List<RobotBuildingOption> result = new ArrayList<>();

			if (isExtraOreRobotUseful(inventory, bluePrint)) {
				if (canAffordOreRobot(inventory, bluePrint)) {
					result.add(BUILD_ORE_ROBOT);
				} else {
					result.add(WAIT_FOR_ORE_ROBOT);
				}
			}

			if (isExtraClayRobotUseful(inventory, bluePrint)) {
				if (canAffordClayRobot(inventory, bluePrint)) {
					result.add(BUILD_CLAY_ROBOT);
				} else {
					result.add(WAIT_FOR_CLAY_ROBOT);
				}
			}

			if (isExtraObsidianRobotUseful(inventory, bluePrint)) {
				if (canAffordObsidianRobot(inventory, bluePrint)) {
					result.add(BUILD_OBSIDIAN_ROBOT);
				} else if (inventory.clayRobots > 0) {
					result.add(WAIT_FOR_OBSIDIAN_ROBOT);
				}
			}

			if (canAffordGeodeRobot(inventory, bluePrint)) {
				result.add(BUILD_GEODE_ROBOT);
			} else if (inventory.obsidianRobots > 0) {
				result.add(WAIT_FOR_GEODE_ROBOT);
			}

			return result;
		}

		private void performPossibleSteps(ProcessNode original, List<RobotBuildingOption> possibleSteps) {
			for (RobotBuildingOption option : possibleSteps) {
				ResourceInventory newInventory = original.inventory.copy();
				BiConsumer<ResourceInventory, BluePrint> buildTask = null;
				RobotBuildingOption newOption = NONE;

				switch (option) {
					case BUILD_ORE_ROBOT:
					case WAIT_FOR_ORE_ROBOT:
						if (canAffordOreRobot(newInventory, bluePrint)) {
							newInventory.ore -= bluePrint.oreRobotOreCost;
							buildTask = RobotBuildingSimulator::buildOreRobot;
						} else {
							newOption = WAIT_FOR_ORE_ROBOT;
						}
						break;
					case BUILD_CLAY_ROBOT:
					case WAIT_FOR_CLAY_ROBOT:
						if (canAffordClayRobot(newInventory, bluePrint)) {
							newInventory.ore -= bluePrint.clayRobotOreCost;
							buildTask = RobotBuildingSimulator::buildClayRobot;
						} else {
							newOption = WAIT_FOR_CLAY_ROBOT;
						}
						break;
					case BUILD_OBSIDIAN_ROBOT:
					case WAIT_FOR_OBSIDIAN_ROBOT:
						if (canAffordObsidianRobot(newInventory, bluePrint)) {
							newInventory.ore -= bluePrint.obsidianRobotOreCost;
							newInventory.clay -= bluePrint.obsidianRobotClayCost;
							buildTask = RobotBuildingSimulator::buildObsidianRobot;
						} else {
							newOption = WAIT_FOR_OBSIDIAN_ROBOT;
						}
						break;
					case BUILD_GEODE_ROBOT:
					case WAIT_FOR_GEODE_ROBOT:
						if (canAffordGeodeRobot(newInventory, bluePrint)) {
							newInventory.ore -= bluePrint.geodeRobotOreCost;
							newInventory.obsidian -= bluePrint.geodeRobotObsidianCost;
							buildTask = RobotBuildingSimulator::buildGeodeRobot;
						} else {
							newOption = WAIT_FOR_GEODE_ROBOT;
						}
						break;
				}

				newInventory.letRobotsWork(); // let robots gather more resources
				if (buildTask != null) { // finish building a robot
					buildTask.accept(newInventory, bluePrint);
				}
				createNextNode(original, newInventory, newOption);
			}
		}

		private void createNextNode(ProcessNode original, ResourceInventory newInventory, RobotBuildingOption newOption) {
			int newTime = original.time + 1;
			maxGeodeRobots[newTime] = Math.max(maxGeodeRobots[newTime], newInventory.geodeRobots);
			maxGeodesOpened[newTime] = Math.max(maxGeodesOpened[newTime], newInventory.geodesOpened);

			ProcessNode nextNode = new ProcessNode(newTime, newOption, bluePrint, newInventory);
			if (nextNode.time >= 24) {
				finishedNodes.add(nextNode);
			} else {
				processNodeQueue.add(nextNode);
			}
		}

		private static boolean isExtraOreRobotUseful(ResourceInventory inventory, BluePrint bluePrint) {
			return inventory.oreRobots < bluePrint.highestOreCost;
		}

		private static boolean isExtraClayRobotUseful(ResourceInventory inventory, BluePrint bluePrint) {
			return inventory.clayRobots < bluePrint.obsidianRobotClayCost
					&& inventory.clay < bluePrint.obsidianRobotClayCost; // prevent over-production
		}

		private static boolean isExtraObsidianRobotUseful(ResourceInventory inventory, BluePrint bluePrint) {
			return inventory.obsidianRobots < bluePrint.geodeRobotObsidianCost
					&& inventory.obsidian < bluePrint.geodeRobotObsidianCost; // prevent over-production
		}

		private static boolean canAffordOreRobot(ResourceInventory inventory, BluePrint bluePrint) {
			return inventory.ore >= bluePrint.oreRobotOreCost;
		}

		private static boolean canAffordClayRobot(ResourceInventory inventory, BluePrint bluePrint) {
			return inventory.ore >= bluePrint.clayRobotOreCost;
		}

		private static boolean canAffordObsidianRobot(ResourceInventory inventory, BluePrint bluePrint) {
			return inventory.ore >= bluePrint.obsidianRobotOreCost
					&& inventory.clay >= bluePrint.obsidianRobotClayCost;
		}

		private static boolean canAffordGeodeRobot(ResourceInventory inventory, BluePrint bluePrint) {
			return inventory.ore >= bluePrint.geodeRobotOreCost
					&& inventory.obsidian >= bluePrint.geodeRobotObsidianCost;
		}

		public static void buildOreRobot(ResourceInventory inventory, BluePrint bluePrint) {
			inventory.oreRobots++;
		}

		public static void buildClayRobot(ResourceInventory inventory, BluePrint bluePrint) {
			inventory.clayRobots++;
		}

		public static void buildObsidianRobot(ResourceInventory inventory, BluePrint bluePrint) {
			inventory.obsidianRobots++;
		}

		public static void buildGeodeRobot(ResourceInventory inventory, BluePrint bluePrint) {
			inventory.geodeRobots++;
		}
	}

	public static class ResourceInventory {
		int ore = 0;
		int clay = 0;
		int obsidian = 0;
		int geodesOpened = 0;
		int oreRobots = 1;
		int clayRobots = 0;
		int obsidianRobots = 0;
		int geodeRobots = 0;

		public ResourceInventory copy() {
			ResourceInventory copy = new ResourceInventory();
			copy.ore = this.ore;
			copy.clay = this.clay;
			copy.obsidian = this.obsidian;
			copy.geodesOpened = this.geodesOpened;
			copy.oreRobots = this.oreRobots;
			copy.clayRobots = this.clayRobots;
			copy.obsidianRobots = this.obsidianRobots;
			copy.geodeRobots = this.geodeRobots;
			return copy;
		}

		public void letRobotsWork() {
			ore += oreRobots;
			clay += clayRobots;
			obsidian += obsidianRobots;
			geodesOpened += geodeRobots;
		}

		@Override
		public String toString() {
			return String.format(
					"ore: %d - clay: %d - obsidian: %d - geodesOpened: %d - oreRobots: %d - clayRobots: %d - obsidianRobots: %d - geodeRobots: %d -",
					ore, clay, obsidian, geodesOpened, oreRobots, clayRobots, obsidianRobots, geodeRobots);
		}
	}

	public record BluePrint(int blueprintNumber, int oreRobotOreCost, int clayRobotOreCost, int obsidianRobotOreCost,
							int obsidianRobotClayCost, int geodeRobotOreCost, int geodeRobotObsidianCost, int highestOreCost) {

		/* example line 1:
		Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
		0         1  2    3   4     5     6 7    8    9    10    11    12 13  14   15       16    17    18 19 20  21 22    23   24    25    26    27 28 29  30 31
		*/
		public static BluePrint ofInputLine(String inputLine) {
			String[] wordTokens = inputLine.replace(":", "").split(" ");
			int blueprintNumber = Integer.parseInt(wordTokens[1]);
			int oreRobotOreCost = Integer.parseInt(wordTokens[6]);
			int clayRobotOreCost = Integer.parseInt(wordTokens[12]);
			int obsidianRobotOreCost = Integer.parseInt(wordTokens[18]);
			int obsidianRobotClayCost = Integer.parseInt(wordTokens[21]);
			int geodeRobotOreCost = Integer.parseInt(wordTokens[27]);
			int geodeRobotObsidianCost = Integer.parseInt(wordTokens[30]);

			int highestOreCost = Math.max(
					Math.max(oreRobotOreCost, clayRobotOreCost),
					Math.max(obsidianRobotOreCost, geodeRobotOreCost));

			return new BluePrint(blueprintNumber, oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost,
					obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost, highestOreCost);
		}

	}

}
