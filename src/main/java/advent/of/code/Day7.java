package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Day7 {

	static boolean performTask2 = true;

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day7Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);

		ElfFileSystemMapper mapper = new ElfFileSystemMapper();
		for (String currentLine : inputLines) {
			System.out.println(currentLine);
			String[] wordTokens = currentLine.split(" ");

			if ("$".equals(wordTokens[0])) {
				processCommand(mapper, wordTokens);
			} else if ("dir".equals(wordTokens[0])) {
				mapper.addChildDirectory(wordTokens);
			} else {
				mapper.addChildFile(wordTokens);
			}

		}

		System.out.println();
		if (!performTask2) {
			performTask1(mapper);
		}

		performTask2(mapper);

		System.out.println();
	}

	private static void performTask2(ElfFileSystemMapper mapper) {
		int totalDiskSpace = 70000000;
		int totalRequiredDiskSpace = 30000000;
		int rootDirectorySize = mapper.getRoot().getSize();
		int currentlyAvailableSpace = totalDiskSpace - rootDirectorySize;
		int diskSpaceToClear = totalRequiredDiskSpace - currentlyAvailableSpace;

		System.out.println("totalDiskSpace: " + totalDiskSpace);
		System.out.println("totalRequiredDiskSpace: " + totalRequiredDiskSpace);
		System.out.println("Root directory size: " + rootDirectorySize);
		System.out.println("currentlyAvailableSpace: " + currentlyAvailableSpace);
		System.out.println("diskSpaceToClear: " + diskSpaceToClear);

		List<ElfDirectory> directories = mapper.getChildParentMap().values().stream()
				.distinct()
				.filter(dir -> dir.getSize() >= diskSpaceToClear)
				.sorted(Comparator.comparingInt(ElfFileSystemNode::getSize))
				.collect(Collectors.toList());

		ElfDirectory first = directories.get(0);
		ElfDirectory last = directories.get(directories.size() - 1);

		System.out.println("Directory with name " + first.getName() + " has size " + first.getSize());
		System.out.println("Directory with name " + last.getName() + " has size " + last.getSize());
	}

	private static void processCommand(ElfFileSystemMapper mapper, String[] wordTokens) {
		if ("cd".equals(wordTokens[1])) {
			mapper.changeDirectory(wordTokens);
		} else if ("ls".equals(wordTokens[1])) {
			mapper.listFiles();
		} else {
			throw new IllegalArgumentException("Illegal argument: " + Arrays.toString(wordTokens));
		}
	}

	private static void performTask1(ElfFileSystemMapper mapper) {
		int maxSize = 100000;
		List<ElfDirectory> elfDirectoryList = mapper.getChildParentMap().values().stream()
				.distinct()
				.filter(dir -> dir.getSize() <= maxSize)
				.collect(Collectors.toList());

		int totalSize = 0;
		for (ElfDirectory directory : elfDirectoryList) {
			System.out.println("Directory with name " + directory.getName() + " has size " + directory.getSize());
			totalSize += directory.getSize();
		}
		System.out.println("Total size: " + totalSize);
	}


	private static class ElfFileSystemMapper {

		private final Map<ElfFileSystemNode, ElfDirectory> childParentMap = new HashMap<>();
		private final ElfDirectory root = new ElfDirectory("/");

		private ElfDirectory currentDirectory;
		private boolean currentlyListingFiles;

		public Map<ElfFileSystemNode, ElfDirectory> getChildParentMap() {
			return childParentMap;
		}

		public ElfDirectory getRoot() {
			return root;
		}

		public void changeDirectory(String[] wordTokens) {
			currentlyListingFiles = false;
			if ("/".equals(wordTokens[2])) {
				moveToRootDirectory();
			} else if ("..".equals(wordTokens[2])) {
				moveToParentDirectory();
			} else {
				moveToChildDirectory(wordTokens[2]);
			}
		}

		private void moveToRootDirectory() {
			currentDirectory = root;
		}

		private void moveToParentDirectory() {
			currentDirectory = childParentMap.get(currentDirectory);
		}

		private void moveToChildDirectory(String childName) {
			Optional<ElfDirectory> childDirectory = currentDirectory.getChildren().stream()
					.filter(child -> child.getName().equals(childName))
					.filter(child -> child instanceof ElfDirectory)
					.map(child -> (ElfDirectory) child)
					.findFirst();

			currentDirectory = childDirectory.orElseThrow();
		}

		public void listFiles() {
			currentlyListingFiles = true;
		}

		public void addChildDirectory(String[] wordTokens) {
			if (!currentlyListingFiles) {
				throw new IllegalStateException("Currently not listing files - current input: " + Arrays.toString(wordTokens));
			}
			ElfDirectory newChild = new ElfDirectory(wordTokens[1]);
			addChildElement(newChild);
		}

		public void addChildFile(String[] wordTokens) {
			if (!currentlyListingFiles) {
				throw new IllegalStateException("Currently not listing files - current input: " + Arrays.toString(wordTokens));
			}
			int childSize = Integer.parseInt(wordTokens[0]);
			ElfFile newChild = new ElfFile(childSize, wordTokens[1]);
			addChildElement(newChild);
		}

		private void addChildElement(ElfFileSystemNode newChild) {
			childParentMap.put(newChild, currentDirectory);
			currentDirectory.addChild(newChild);
		}
	}

	private interface ElfFileSystemNode {
		int getSize();

		String getName();
	}

	private static class ElfDirectory implements ElfFileSystemNode {
		private final List<ElfFileSystemNode> children = new ArrayList<>();
		private final String name;

		public ElfDirectory(String name) {
			this.name = name;
		}

		public void addChild(ElfFileSystemNode newChild) {
			children.add(newChild);
		}

		public List<ElfFileSystemNode> getChildren() {
			return children;
		}

		@Override
		public int getSize() {
			return children.stream().mapToInt(ElfFileSystemNode::getSize).sum();
		}

		@Override
		public String getName() {
			return name;
		}
	}

	private static class ElfFile implements ElfFileSystemNode {
		private final int size;
		private final String name;

		public ElfFile(int size, String name) {
			this.size = size;
			this.name = name;
		}

		@Override
		public int getSize() {
			return size;
		}

		@Override
		public String getName() {
			return name;
		}
	}

}
