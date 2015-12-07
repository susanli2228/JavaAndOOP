package student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import game.*;
import student.Paths;




public class MyManager extends Manager {

	//WOULD THERE BE ANY CASE WHERE I WOULD HAVE TO CHECK THAT t OR p IS NOT NULL?????
	
	Board board;
	Set<Parcel> parcels; //will this cause an error?
	
	ArrayList<Truck> trucks; 
	
	boolean initRunCompleted = false;
	
	public MyManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//gather information about board
		//assign trucks to nearest packages, perhaps matching for color	
	
		board = getBoard();
		parcels = getParcels(); //already a synchronized set
		trucks = getTrucks();
		
		for (Truck t: trucks) {
			findNextParcel(t, board.getTruckDepot()); //assigns package to truck's user data
			
			if (t.getUserData() != null) {
				Parcel p = (Parcel)t.getUserData();
				t.setTravelPath(Paths.dijkstra(board.getTruckDepot(), p.destination));
			}
			
		}

		initRunCompleted = true;
	
	}
	

	@Override
	public void truckNotification(Truck t, Notification message) {
		// TODO Auto-generated method stub
		
		//really only need to act if TRUCK is WAITING
		
		if (message == Manager.Notification.WAITING){
		
			if (initRunCompleted == false) {return;}
			if (parcels.isEmpty() && t.getUserData()==null) {
				//nothing left to do, so return home
				t.setTravelPath(Paths.dijkstra(t.getLocation(), board.getTruckDepot()));
			}
			//there are still tasks (truck is assigned or there are still packages)
			
			if (t.getUserData()!=null) {//truck is assigned to package
				
				if (t.getLoad()!=null) {//truck is carrying the package
					
					if (t.getLocation() == t.getLoad().destination) {//at package destination
						
						t.dropoffLoad();
						t.setUserData(null);
						return;
							
					}//has package, not at destination = at pick-up place
					//so route it to package destination
					t.setTravelPath(Paths.dijkstra(t.getLocation(), t.getLoad().destination));
					return;
					
				}//not carrying package
				
				Parcel p = (Parcel) t.getUserData();
				
				if (t.getLocation() == p.start) {//then pick it up
					
					t.pickupLoad(p);
					return;
					
				}//assigned package but not at package start location
				
				t.setTravelPath(Paths.dijkstra(t.getLocation(), p.start));
				return;
				
			}//not assigned to a package
			
			findNextParcel(t, t.getLocation());
			//t.setTravelPath(Paths.dijkstra(t.getLocation(), t.getUserData().destination));
			return;
		
			
		}

		
		
	}
	
	/**This function assigns to an empty truck its next parcel (CHANGES t's USER DATA). 
	 * First, if no packages are left, return.
	 * If there is an unassigned package at truck's current node, pick it up and deliver,
	 * placing preference on same color packages.
	 * Then, it tries to find any package of the same color.
	 * If a color match is not available, it finds the closest parcel.
	 * 
	 * WOULD IT BE MORE EFFICIENT TO RANDOMLY ASSIGN A PARCEL INSTEAD OF FINDING
	 * THE CLOSEST ONE????*/
	public void findNextParcel(Truck t, Node currentLocus) {
		
		synchronized(parcels) {
			if (parcels.isEmpty()) {t.setUserData(null);} //in case of more trucks than parcels
			
			if (currentLocus.isParcelHere()) {
				HashSet<Parcel> parcelsHere = currentLocus.getParcels();
				for (Parcel p: parcelsHere) {
					if (parcels.contains(p)) { //if the packages are unassigned
						if (p.getColor()==t.getColor()) {//color match!
							t.setUserData(p);
							parcels.remove(p);
							parcelsHere.remove(p);
							
							return;
							
						}//no color matches available; assign to any one at spot
						/**t.setUserData(p);
						parcels.remove(p);
						parcelsHere.remove(p);
						return;
						*/
						
					}//are parcels at node are assigned, look elsewhere for parcels
				}
			
				
			}//look elsewhere for parcels, first look for (closest or no?) same color
			
			for (Parcel par: parcels){
				if (par.getColor()==t.getColor()){
					t.setUserData(par);
					parcels.remove(par);
					
					return;
					
				}
				
			}//no color matches, look for closest (iterate through all or BFS??)
			
			
			
			if (currentLocus.isParcelHere()) {
				HashSet<Parcel> parcelsHere = currentLocus.getParcels();
				for (Parcel p: parcelsHere) {
					if (parcels.contains(p)) { //if the packages are unassigned
						
						t.setUserData(p);
						parcels.remove(p);
						parcelsHere.remove(p);
						
						return;
						
					}
				}
				
			}
			
			
			//HashMap<Node, Integer> distances = new HashMap<Node, Integer>();
			
			for (Parcel pa: parcels){ 
				t.setUserData(pa);
				parcels.remove(pa);
				return;
				
			}
			
			
			//PUT STUFF HERE
			
			/**
			MinHeap<Node> frontier = new MinHeap<Node>();
			HashMap<Node, NodeInfo> seenMap = new HashMap<Node, NodeInfo>();

			NodeInfo startInfo = new NodeInfo(null, 0);
			seenMap.put(currentLocus, startInfo);
			frontier.add(currentLocus, startInfo.distance);

			Node end = null;
			
			while (!frontier.isEmpty()) {
				// poll from frontier node with shortest distance from v
				Node pulled = frontier.poll();
				
				
				//pulled is a node; p is a parcel
				if (pulled.isParcelHere()) { 
					
					HashSet<Parcel> parcelsHere = pulled.getParcels();
					for (Parcel p: parcelsHere) {
						if (parcels.contains(p)) {//here is the problem
							t.setUserData(p);
							parcels.remove(p);
							parcelsHere.remove(p);
							
							
							return;
						}
					}
					
					
					
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
				
			}
			*/	
			
			
			/**Integer shortest = Integer.MAX_VALUE;
			LinkedList<Node> bestPath = null;
			Parcel thisParcel = null;
			
			for (Parcel par: parcels) {//all parcels are undelivered at their start place
				LinkedList<Node> path = Paths.dijkstra(t.getLocation(), par.getLocation());
				int thisDist = Paths.pathLength(path); //NOTE: method changed to static by me
				if (thisDist < shortest) {
					shortest = thisDist;
					bestPath = path;
					thisParcel = par;
				}
				
				
				//distances.put(par.getLocation(), thisDist);
				
			}// now you have shortest and bestPath
			
			
			
			parcels.remove(thisParcel);
			t.setUserData(thisParcel);
			*/
			
		}
		
		
		
		
		
	}
	
	
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
	
	
	
	
	

}
