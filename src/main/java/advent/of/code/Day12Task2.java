package advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Day12Task2 {

	public static final String SINGLE_CARRIAGE_RETURN_LINE_FEED = "(\\r\\n)";

	private static final Map<Coordinate, GridSquare> redundantMapping = new HashMap<>();

	private static final Set<GridSquare> initialUnvisitedNodes = new HashSet<>();

	private static final List<GridSquare> allStartingNodes = new ArrayList<>();

	static boolean performTask2 = true;

	public static void main(String... args) throws IOException {
		Path inputFile = Path.of("D:\\dev\\advent\\Day12Input.txt");
		String inputString = Files.readString(inputFile);
		String[] inputLines = inputString.split(SINGLE_CARRIAGE_RETURN_LINE_FEED);
		List<String> stringList = Arrays.asList(inputLines);

		GridSquare end = null;

		for (int y = 0; y < stringList.size(); y++) {
			String currentLine = stringList.get(y);
			for (int x = 0; x < currentLine.length(); x++) {
				char currentChar = currentLine.charAt(x);
				Coordinate coordinate = new Coordinate(x, y);
				GridSquare square = new GridSquare(coordinate, getHeight(currentChar), currentChar);
				redundantMapping.put(coordinate, square);
				initialUnvisitedNodes.add(square);
				if (currentChar == 'S') {
					allStartingNodes.add(square);
				}
				if (performTask2 && currentChar == 'a' && x <= 2) {
					allStartingNodes.add(square);
				}
				if (currentChar == 'E') {
					end = square;
				}
			}
		}
		if (allStartingNodes.isEmpty() || end == null) {
			throw new IllegalStateException();
		}

		System.out.println("Amount of start nodes: " + allStartingNodes.size());

		List<VisitedNode> allEndNodes = new ArrayList<>();

		for (GridSquare startNode : allStartingNodes) {
			NodeTraverser traverser = new NodeTraverser(startNode, new HashSet<>(initialUnvisitedNodes));
			traverser.visitFirstNode();
			traverser.visitAdjacentNodes();

			System.out.println("End node reached: " + traverser.endNodeReached.size());
			System.out.println("Start node: " + startNode);
			for (VisitedNode endNode : traverser.endNodeReached) {
				System.out.println("End node reached with distance: " + endNode.distance);
				System.out.println("End node reached: " + endNode);
				allEndNodes.add(endNode);
			}
		}

		allEndNodes.sort(Comparator.comparingInt(VisitedNode::distance));

		System.out.println("End node reached with distance: " + allEndNodes.get(0).distance);
		System.out.println("End node reached with distance: " + allEndNodes.get(allEndNodes.size() - 1).distance);

		System.out.println();
	}

	private static int getHeight(char currentChar) {
		if (currentChar == 'S') {
			return 1;
		}
		if (currentChar == 'E') {
			return 26;
		}
		return currentChar - 96;
	}

	private static class NodeTraverser {
		private final Map<Coordinate, VisitedNode> visitedNodes = new HashMap<>();
		private final Queue<NodeToVisit> nodesToVisit = new PriorityQueue<>();
		private final List<VisitedNode> endNodeReached = new ArrayList<>();

		private final GridSquare startNode;
		private final Set<GridSquare> unvisitedNodes;

		public NodeTraverser(GridSquare startNode, Set<GridSquare> unvisitedNodes) {
			this.startNode = startNode;
			this.unvisitedNodes = unvisitedNodes;
		}

		private void visitFirstNode() {
			var visitedNode = new VisitedNode(startNode, List.of(startNode), 0);
			visitedNodes.put(visitedNode.node.coordinate(), visitedNode);
			unvisitedNodes.remove(visitedNode.node);

			addNodesToVisit(visitedNode);
		}

		private void addNodesToVisit(VisitedNode visitedNode) {
			List<GridSquare> adjacentNodes = getAdjacentSquares(visitedNode.node.coordinate.x, visitedNode.node.coordinate.y);
			for (GridSquare adjacentNode : adjacentNodes) {
				if (visitedNode.node.canTravelTo(adjacentNode)) {
					if (unvisitedNodes.contains(adjacentNode)) {
						nodesToVisit.add(new NodeToVisit(visitedNode, adjacentNode));
					} else if (visitedNodes.get(adjacentNode.coordinate).distance > visitedNode.distance + 1) {
						nodesToVisit.add(new NodeToVisit(visitedNode, adjacentNode));
					}
				}
			}
		}

		private static List<GridSquare> getAdjacentSquares(int x, int y) {
			List<GridSquare> result = new ArrayList<>();
			addSquareIfPresent(result, x - 1, y);
			addSquareIfPresent(result, x + 1, y);
			addSquareIfPresent(result, x, y - 1);
			addSquareIfPresent(result, x, y + 1);
			return result;
		}

		private static void addSquareIfPresent(List<GridSquare> result, int x, int y) {
			GridSquare square = redundantMapping.get(new Coordinate(x, y));
			if (square != null) {
				result.add(square);
			}
		}

		private void visitAdjacentNodes() {
			while (nodesToVisit.peek() != null) {
				NodeToVisit nextNode = nodesToVisit.poll();
				if (unvisitedNodes.contains(nextNode.nextNode)) {
					visitUnvisitedNode(nextNode);
				} else {
					revisitNode(nextNode);
				}
			}
		}

		private void visitUnvisitedNode(NodeToVisit nodeToVisit) {
			unvisitedNodes.remove(nodeToVisit.nextNode);
			visitNode(nodeToVisit);
		}

		private void revisitNode(NodeToVisit nodeToRevisit) {
			VisitedNode visitedNode = visitedNodes.get(nodeToRevisit.nextNode.coordinate);
			if (visitedNode == null) {
				throw new IllegalArgumentException("Node to visit has not been visited before! NodeToVisit: " + nodeToRevisit);
			}

			if (visitedNode.distance > nodeToRevisit.previousNode.distance + 1) {
				visitNode(nodeToRevisit);
			}
		}

		private void visitNode(NodeToVisit nodeTovisit) {
			VisitedNode newVisitedNode = getNewVisitedNode(nodeTovisit);
			visitedNodes.put(newVisitedNode.node.coordinate, newVisitedNode);

			if (newVisitedNode.node.original == 'E') {
				endNodeReached.add(newVisitedNode);
			} else {
				addNodesToVisit(newVisitedNode);
			}
		}

		private static VisitedNode getNewVisitedNode(NodeToVisit nodeToVisit) {
			List<GridSquare> pathToNode = new ArrayList<>(nodeToVisit.previousNode.pathToNode);
			pathToNode.add(nodeToVisit.nextNode);
			return new VisitedNode(nodeToVisit.nextNode, pathToNode, nodeToVisit.previousNode.distance + 1);
		}
	}

	public record Coordinate(int x, int y) {
		public boolean isAdjacent(Coordinate other) {
			return (this.x == other.x && Math.abs(this.y - other.y) == 1)
					|| (this.y == other.y && Math.abs(this.x - other.x) == 1);
		}
	}

	public record GridSquare(Coordinate coordinate, int height, char original) {
		public boolean canTravelTo(GridSquare other) {
			return (this.height + 1) >= other.height && this.coordinate.isAdjacent(other.coordinate);
		}
	}

	public record VisitedNode(GridSquare node, List<GridSquare> pathToNode, int distance) {
	}

	private record NodeToVisit(VisitedNode previousNode, GridSquare nextNode) implements Comparable<NodeToVisit> {
		@Override
		public int compareTo(NodeToVisit other) {
			return this.previousNode.distance - other.previousNode.distance;
		}
	}

}
