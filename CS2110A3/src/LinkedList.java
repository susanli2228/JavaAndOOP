/* Time spent on a2:  3 hours and 55 minutes.

 * Name: Susan Li
 * Netid: SZL5
 * What I thought about this assignment:
 * It was an interesting concept. Not too too hard but requires attention 
 * to detail when replacing the prev/next/first/last fields.
 * Syntax was also challenging at times when going in between the LinkedList
 * and Node classes.
 */

/** An instance is a doubly linked list. */
public class LinkedList<E> {
	private int size; // Number of values in the linked list.
	private Node first; // first node of linked list (null if none)
	private Node last; // last node of linked list (null if none)

	/** Constructor: an empty linked list. */
	public LinkedList() {
	}

	/** Return the number of values in this list. */
	public int size() {
		return size;
	}

	/** Return the first node of the list (null if the list is empty). */
	public Node getFirst() {

		return first;
	}

	/** Return the last node of the list (null if the list is empty). */
	public Node getLast() {
		return last;
	}

	/**
	 * Return the value of node n of this list. Precondition: n is a node of
	 * this list; it may not be null.
	 */
	public E valueOf(Node n) {
		assert n != null;
		return n.value;
	}

	/**
	 * Return a representation of this list: its values, with adjacent ones
	 * separated by ", ", "(" at the beginning, and ")" at the end. <br>
	 * 
	 * E.g. for the list containing 6 3 8 in that order, the result it
	 * "(6, 3, 8)".
	 */
	public String toString() {
		// TODO: Write this method first. Do NOT use fields size

		String result = "(";

		if (first == null) { 
			return "()";

		} else {
			result = result + valueOf(first);
			Node temp = first;
			while (temp.next != null) {
				result = result + ", " + valueOf(temp.next);
				temp = temp.next;
				
			}
			result = result + ")";
			return result;

		}

	}

	/**
	 * Return a representation of this list: its values in reverse, with
	 * adjacent ones separated by ", ", "(" at the beginning, and ")" at the
	 * end. <br>
	 * 
	 * E.g. for the list containing 6 3 8 in that order, the result it
	 * "(8, 3, 6)".
	 */
	public String toStringReverse() {
		// TODO: Write this method second. Do NOT use fields size

		String result = "(";

		if (last == null) { 
			return "()";

		} else {
			result = result + valueOf(last);
			Node temp = last;
			while (temp.prev != null) {
				result = result + ", " + valueOf(temp.prev);
				temp = temp.prev;
			}
			result = result + ")";
			return result;

		}

	}

	/**
	 * add value v in a new node at the end of the list and return the new node.
	 */
	public Node append(E v) {
		// TODO:This is the third method to write. Then you can begin testing
		// this method AND toStringand toString reverse at the same time.

		if (first == null) {
			LinkedList<E>.Node n = new Node(null, null, v);
			first = n;
			last = n;
			size = 1;

			return n;

		} else {
			Node prevLast = last;
			LinkedList<E>.Node n = new Node(prevLast, null, v);
			last = n;
			size = size + 1;
			prevLast.next = n;
			

			return n;
		}

	}

	/**
	 * add value v in a new node at the beginning of the list and return the new
	 * node
	 */
	public Node prepend(E v) {
		// TODO: This is the fourth method to write and test

		if (first == null) {
			LinkedList<E>.Node n = new Node(null, null, v);
			first = n;
			last = n;
			size = 1;
			

			return n;

		} else {
			Node prevFirst = first;

			LinkedList<E>.Node n = new Node(null, prevFirst, v);
			first = n;
			size = size + 1;
			
			prevFirst.prev = first;

			return n;

		}

	}

	/**
	 * Insert value v in a new node after node n and return the new node.
	 * Precondition: n must be a node of this list; it may not be null.
	 */
	public Node insertAfter(E v, Node n) {
		// TODO: This is the fifth method to write and test
		assert n != null;

		if (n == last) { 
			LinkedList<E>.Node newN = new Node(n, null, v);

			size = size + 1;
			n.next = newN;
			last = newN;

			return newN;

		} else {
			Node afterN = n.next;

			LinkedList<E>.Node newN = new Node(n, afterN, v);

			size = size + 1;
			n.next = newN;
			afterN.prev = newN;

			return newN;

		}

	}

	/**
	 * Insert value v in a new node before node n and return the new node.
	 * Precondition: n must be a node of this list; it may not be null.
	 */
	public Node insertBefore(E v, Node n) {
		// TODO: This is the sixth method to write and test
		assert n != null;

		if (n == first) {
			LinkedList<E>.Node newN = new Node(null, n, v);

			first = newN;
			size = size + 1;
			n.prev = newN;

			return newN;

		} else {
			Node beforeN = n.prev;

			LinkedList<E>.Node newN = new Node(beforeN, n, v);
			size = size + 1;
			beforeN.next = newN;
			n.prev = newN;

			return newN;

		}

	}

	/**
	 * Remove node n from this list. Precondition: n must be a node of this
	 * list; it may not be null.
	 */
	public void remove(Node n) {
		// TODO: This is the seventh method to write and test
		assert n != null;

		if (first == last) { // n is only node
			first = null;
			last = null;
			size = 0;

		} else if (n == first) {
			Node newFirst = n.next;
			first = newFirst;
			size = size - 1;
			newFirst.prev = null;

		} else if (n == last) {
			Node newLast = n.prev;
			last = newLast;
			size = size - 1;
			newLast.next = null;

		} else {
			Node before = n.prev;
			Node after = n.next;
			size = size - 1;
			before.next = after;
			after.prev = before;
		}

	}

	/*********************/

	/** An instance is a node of this list. */
	public class Node {
		/** Previous node on list (null if this is the first node). */
		private Node prev;

		/** The value of this element. */
		private E value;

		/** Next node on list. (null if is the last node). */
		private Node next;

		/**
		 * Constructor: an instance with previous node p (can be null), next
		 * node n (can be null), and value v.
		 */
		private Node(Node p, Node n, E v) {
			prev = p;
			next = n;
			value = v;
		}

		/** Return the value of this node. */
		public E getValue() {
			return value;
		}

		/**
		 * Return the node previous to this one (null if this is the first node
		 * of the list).
		 */
		public Node prev() {
			return prev;
		}

		/**
		 * Return the next node in this list (null if this is the last node of
		 * this list).
		 */
		public Node next() {
			return next;
		}
	}

}
