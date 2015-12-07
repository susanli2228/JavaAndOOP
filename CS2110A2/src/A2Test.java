import static org.junit.Assert.*;

import org.junit.Test;


public class A2Test {

	@Test
	public void palindromeTest() {
		assertEquals(A2.isPalindrome("babrrba"), false);
		assertEquals(A2.isPalindrome("zyxwwxyz"), true);
		assertEquals(A2.isPalindrome("b"), true);
		assertEquals(A2.isPalindrome(""), true);
		assertEquals(A2.isPalindrome("MadamImAdam"), false);
		assertEquals(A2.isPalindrome(" zyxw wxyz "), true);
		assertEquals(A2.isPalindrome(" zyxww xyz "), false);
		
	}
	
	@Test
	public void occurrencesTest() {
		assertEquals(A2.numOccurrences("abcabca", "z"), 0);
		assertEquals(A2.numOccurrences("abcabca", "abcab"), 1);
		assertEquals(A2.numOccurrences("abcabca", "a"), 3);	
		assertEquals(A2.numOccurrences("ababababa", "aba"), 4);
		
	}
	
	@Test
	public void nameFixerTest() {
		assertEquals(A2.fixName("    DAVID   ArThUr gries   "), "Gries, David A.");
		assertEquals(A2.fixName("sid  ChaudHuRi"), "Chaudhuri, Sid");
		
	}
	
	@Test
	public void anagramTest() {
		assertEquals(A2.areAnagrams("cat", "tac"), true);
		assertEquals(A2.areAnagrams("Cat", "Tac"), false);
		assertEquals(A2.areAnagrams("tom marvolo riddle", "i am lordvoldemort"), true);
		assertEquals(A2.areAnagrams("tommarvoloriddle", "i am lordvoldemort"), false);
		
	}
		
	@Test
	public void consonantTest() {
		assertEquals(A2.replaceConsonants("Minecraft"), "^i_e__a__");
		assertEquals(A2.replaceConsonants("M$inecraft"), "^$i_e__a__");
		assertEquals(A2.replaceConsonants("M$!.?ine  c raft"), "^$!.?i_e  _ _a__");
	}
	
	@Test
	public void decompressTest() {
		assertEquals(A2.decompress("3b1c5b2x0a"), "bbbcbbbbbxx");
		assertEquals(A2.decompress("4n0m1k2l2 3?"), "nnnnkll  ???");
		
	}

}
