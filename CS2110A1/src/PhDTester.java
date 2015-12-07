import static org.junit.Assert.*;

import org.junit.Test;


public class PhDTester {

	
	@Test
	public void testConstructor1() {
		PhD one = new PhD("Alfred", 'M', 1995, 3);
		assertEquals(one.getName(), "Alfred");
		assertEquals(one.getYear(), 1995);
		assertEquals(one.getMonth(), 3);
		assertEquals(one.isFemale(), false);
		assertEquals(one.getAdvisor1(), null);
		assertEquals(one.getAdvisor2(), null);
		assertEquals(one.numAdvisees(), 0);
		
		PhD two = new PhD("Jane", 'F', 1995, 4);
		assertEquals(two.isFemale(), true);
		
	}
	
	@Test
	public void testSetters() {
		PhD one = new PhD("Alfred", 'M', 1995, 3);
		PhD p = new PhD("Anna", 'F', 1990, 12);
		PhD q = new PhD("John", 'M', 1991, 11);
		one.addAdvisor1(p);
		one.addAdvisor2(q);
		
		assertEquals(one.getAdvisor1(), p);
		assertEquals(one.getAdvisor2(), q);
		
		assertEquals(p.numAdvisees(), 1);
		assertEquals(q.numAdvisees(), 1);
		
	}
	
	@Test
	public void testMoreConstructors() {
		PhD p = new PhD("Anna", 'F', 1990, 12);
		PhD q = new PhD("John", 'M', 1991, 11);
		PhD one = new PhD("Alfred", 'M', 1995, 3, p);
		PhD two = new PhD("Emma", 'F', 1999, 2, p, q);
		assertEquals(one.getName(), "Alfred");
		assertEquals(one.getYear(), 1995);
		assertEquals(one.getMonth(), 3);
		assertEquals(one.isFemale(), false);
		assertEquals(one.getAdvisor1(), p);
		assertEquals(one.getAdvisor2(), null);
		assertEquals(one.numAdvisees(), 0);
		
		assertEquals(two.getName(), "Emma");
		assertEquals(two.getYear(), 1999);
		assertEquals(two.getMonth(), 2);
		assertEquals(two.isFemale(), true);
		assertEquals(two.getAdvisor1(), p);
		assertEquals(two.getAdvisor2(), q);
		assertEquals(two.numAdvisees(), 0);
		
		assertEquals(p.numAdvisees(), 2);
		assertEquals(q.numAdvisees(), 1);
		
	}
	
	@Test
	public void testOlderAndSiblings() {
		PhD p = new PhD("Anna", 'F', 1990, 12);
		PhD q = new PhD("John", 'M', 1991, 11);
		PhD one = new PhD("Alfred", 'M', 1995, 3, p);
		PhD two = new PhD("Emma", 'F', 1999, 2, p, q);
		PhD three = new PhD("Jack", 'M', 1999, 1, one, two);
		PhD four = new PhD("Bob", 'M', 1994, 3);
		PhD five = new PhD("Jane", 'F', 1995, 3);
		
		assertEquals(one.isOlderThan(two), true);
		assertEquals(three.isOlderThan(two), true);
		assertEquals(three.isOlderThan(three), false);
		assertEquals(one.isOlderThan(four), false);
		assertEquals(one.isOlderThan(five), false);
		
		assertEquals(one.isPhDSibling(two), true);
		assertEquals(four.isPhDSibling(five), false);
		assertEquals(three.isPhDSibling(one), false);
		assertEquals(one.isPhDSibling(five), false);
		assertEquals(one.isPhDSibling(one), false);
	}

}
