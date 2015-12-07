import static org.junit.Assert.*;

import org.junit.Test;

public class LinkedListTester {

	@Test
	public void testConstructor() { 
													
		/** CONSTRUCTOR */
		LinkedList<Integer> b = new LinkedList<Integer>();
		assertEquals("()", b.toString());
		assertEquals("()", b.toStringReverse());
		assertEquals(0, b.size());
		
	}	
		
	@Test
	public void testAppendPrepend() {
		
		/** APPEND */
		LinkedList<Integer> b = new LinkedList<Integer>();
		LinkedList<Integer>.Node n = b.append(3);
		assertEquals("(3)", b.toString());
		assertEquals("(3)", b.toStringReverse());
		assertEquals(1, b.size());
		assertEquals(b.getLast(), n); 

		LinkedList<Integer>.Node o = b.append(6); 
		assertEquals("(3, 6)", b.toString());
		assertEquals("(6, 3)", b.toStringReverse());
		assertEquals(2, b.size());
		assertEquals(b.getLast(), o); 

		/** PREPEND */
		LinkedList<Integer> a = new LinkedList<Integer>(); 
		LinkedList<Integer>.Node p = a.prepend(19);
		assertEquals("(19)", a.toString());
		assertEquals("(19)", a.toStringReverse());
		assertEquals(1, a.size());
		assertEquals(a.getFirst(), p); 

		LinkedList<Integer>.Node q = b.prepend(1); 
		assertEquals("(1, 3, 6)", b.toString());
		assertEquals("(6, 3, 1)", b.toStringReverse());
		assertEquals(3, b.size());
		assertEquals(b.getFirst(), q); 

	}
	
	@Test
	public void testInsertAfterBefore() {
		
		/** INSERT AFTER */
		LinkedList<Integer> b = new LinkedList<Integer>();
		b.append(1);
		b.append(3);
		b.append(6);
		LinkedList<Integer>.Node n = b.insertAfter(4, b.getLast());
		assertEquals("(1, 3, 6, 4)", b.toString());
		assertEquals("(4, 6, 3, 1)", b.toStringReverse());
		assertEquals(4, b.size());
		assertEquals(b.getLast(), n); 

		LinkedList<Integer>.Node o = b.insertAfter(7, b.getFirst());
		assertEquals("(1, 7, 3, 6, 4)", b.toString());
		assertEquals("(4, 6, 3, 7, 1)", b.toStringReverse());
		assertEquals(5, b.size());
		assertEquals(b.getFirst().next(), o); 

		/** INSERT BEFORE */
		LinkedList<Integer>.Node p = b.insertBefore(8, b.getFirst());
		assertEquals("(8, 1, 7, 3, 6, 4)", b.toString());
		assertEquals("(4, 6, 3, 7, 1, 8)", b.toStringReverse());
		assertEquals(6, b.size());
		assertEquals(b.getFirst(), p); 

		LinkedList<Integer>.Node q = b.insertBefore(5, b.getLast());
		assertEquals("(8, 1, 7, 3, 6, 5, 4)", b.toString());
		assertEquals("(4, 5, 6, 3, 7, 1, 8)", b.toStringReverse());
		assertEquals(7, b.size());
		assertEquals(b.getLast().prev(), q); 
		
	}
	
	@Test
	public void testRemove() {

		/** REMOVE */
		LinkedList<Integer> a = new LinkedList<Integer>(); 
		a.prepend(19);
		a.remove(a.getFirst()); // remove only node of a
		assertEquals("()", a.toString());
		assertEquals("()", a.toStringReverse());
		assertEquals(0, a.size());
		
		LinkedList<Integer> b = new LinkedList<Integer>();
		b.append(8);
		b.append(1);
		b.append(7);
		b.append(3);
		b.append(6);
		b.append(5);
		b.append(4);
		
		b.remove(b.getFirst()); //remove first node of b
		assertEquals("(1, 7, 3, 6, 5, 4)", b.toString());
		assertEquals("(4, 5, 6, 3, 7, 1)", b.toStringReverse());
		assertEquals(6, b.size());
		
		b.remove(b.getLast()); //remove last node of b
		assertEquals("(1, 7, 3, 6, 5)", b.toString());
		assertEquals("(5, 6, 3, 7, 1)", b.toStringReverse());
		assertEquals(5, b.size());

		b.remove(b.getLast().prev()); //remove 2nd to last node of b
		assertEquals("(1, 7, 3, 5)", b.toString());
		assertEquals("(5, 3, 7, 1)", b.toStringReverse());
		assertEquals(4, b.size());
		
		
	}

}
