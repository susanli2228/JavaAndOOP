/* Time spent on a7:  5 hours and 23 minutes.

 * Name: Susan Li	
 * Netid: SZL5
 * What I thought about this assignment:
 * Not bad. 
 *
 */

package student;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import game.*;

/**
 * This class contains Dijkstra's shortest-path algorithm and some other
 * methods.
 */
public class Paths {

	/**
	 * Return a list of the nodes on the shortest path from start to end, or the
	 * empty list (a list of size 0) if a path from start to end does not exist.
	 */
	public static LinkedList<Node> dijkstra(Node start, Node end) {
		/*
		 * TODO Implement Dijkstras's shortest-path algorithm presented in the
		 * slides titled "Final Algorithm" in the slides for lecture 19. In
		 * particular, a min-heap (as implemented in assignment A6) should be
		 * used for the frontier set. We provide a declaration of the frontier.
		 * 
		 * Maintaining information about shortest paths will require maintaining
		 * for each node in the settled and frontier sets the backpointer for
		 * that node (as described in the handout) along with the length of the
		 * shortest path (thus far, for nodes in the frontier set). For this
		 * purpose, we provide static class NodeInfo. We leave it to you to
		 * declare the HashMap variable for this and describe carefully what it
		 * means.
		 * 
		 * Note 1: Do not attempt to create a data structure to contain the
		 * far-off set. Note 2: Read the list of notes on pages 2..3 of the
		 * handout carefully.
		 */

		// The frontier set, as discussed in lecture
		MinHeap<Node> frontier = new MinHeap<Node>();

		// TODO complete this method

		HashMap<Node, NodeInfo> seenMap = new HashMap<Node, NodeInfo>();
		//HashMap<Node, NodeInfo> frontMap = new HashMap<Node, NodeInfo>();

		NodeInfo startInfo = new NodeInfo(null, 0);

		seenMap.put(start, startInfo);

		frontier.add(start, startInfo.distance);

		while (!frontier.isEmpty()) {
			// poll from frontier node with shortest distance from v
			Node pulled = frontier.poll();
			
			if (pulled.equals(end)) { //THIS IS NEW. DOES IF WORK(&%*&%*@)R$R@)&)$(@
				break;
			}
			
			HashMap<Node, Integer> neighMap = pulled.getNeighbors();
			Iterator<Entry<Node, Integer>> neighIter = neighMap.entrySet()
					.iterator();
			// this iterator thing is weird...

			while (neighIter.hasNext()) { // add neighbors of polled node to
											// frontier
				Entry<Node, Integer> temp = neighIter.next();
				// if node is from far-off set, set distance
				//add to frontier, add to HashMap
				int newDist = temp.getValue() + seenMap.get(pulled).distance;
				
				if (!seenMap.containsKey(temp.getKey())) {
					frontier.add(temp.getKey(),
							newDist);
					NodeInfo tempInfo = new NodeInfo(pulled, newDist);
					seenMap.put(temp.getKey(), tempInfo);
				
				//else if node has already been seen, make sure info is of min path	
				} else {
					if (newDist < seenMap.get(temp.getKey()).distance) {
						//add to frontier and update info in HashMap
						frontier.updatePriority(temp.getKey(), newDist);
						NodeInfo shorterInfo = new NodeInfo(pulled, newDist);
						seenMap.put(temp.getKey(), shorterInfo);	
						
					}
					
				}
				
			}//end of while loop (adding neighbors to frontier)
			
		}//end of while loop (frontier not being empty)
		
		//So... once frontier is empty....make this LinkedList back to start
		
		/**LinkedList<Node> thePath = new LinkedList<Node>();
		
		Node thisNode = end;
		
		if (seenMap.get(thisNode).backPointer == null) {
			return thePath;
		}
		
		thePath.add(thisNode);
		while (thisNode != start) {
			thePath.add(seenMap.get(thisNode).backPointer);
			thisNode = seenMap.get(thisNode).backPointer;
		}
		

		return thePath;*/
		
		return buildPath(end, seenMap);
	}
	
	

	/**
	 * Return the path from the start node to end. Precondition: nodeInfo
	 * contains all the necessary information about the path.
	 */
	public static LinkedList<Node> buildPath(Node end,
			HashMap<Node, NodeInfo> nodeInfo) {
		LinkedList<Node> path = new LinkedList<Node>();
		Node p = end;
		while (p != null) {
			path.addFirst(p);
			p = nodeInfo.get(p).backPointer;
		}
		return path;
	}

	/** Return the sum of the weight of the edges on path p. */
	public static int pathLength(LinkedList<Node> path) { //I CHANGED THIS TO STATIC!!!!
		synchronized (path) {
			if (path.size() == 0)
				return 0;

			Iterator<Node> iter = path.iterator();
			Node p = iter.next(); // First node on path
			int s = 0;
			// invariant: s = sum of weights of edges from start up to p
			while (iter.hasNext()) {
				Node q = iter.next();
				s = s + p.getConnect(q).length; // TAKE NOTICE OF THIS!!!!
				p = q;
			}
			return s;
		}
	}

	/**
	 * An instance contains information about a node: the previous node on a
	 * shortest path from the start node to this node and the distance of this
	 * node from the start node.
	 */
	private static class NodeInfo {
		private Node backPointer;
		private int distance;

		/**
		 * Constructor: an instance with distance d from the start node and
		 * backpointer p.
		 */
		private NodeInfo(Node p, int d) {
			backPointer = p; // Backpointer on the path (null if start node)
			distance = d; // Distance from start node to this one.
		}

		/** Constructor: an instance with a null previous node and distance 0. */
		private NodeInfo() {
		}

		/** return a representation of this instance. */
		public String toString() {
			return "distance " + distance + ", bckptr " + backPointer;
		}
	}

}
