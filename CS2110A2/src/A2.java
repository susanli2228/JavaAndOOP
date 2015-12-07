import java.util.Arrays;

/** An instance contains static methods for assignment A2 */
public class A2 {

	/*
	 * NOTE: You will have to rely on methods that are declared in class String.
	 * Visit docs.oracle.com/javase/7/docs/api/java/lang/String.html, scroll
	 * down to the Method Summary, and peruse the available methods. Oft-used
	 * ones are charAt, length(), trim, substring, indexOf, isEmpty,
	 * lastIndexOf, startsWith, endsWith, split.
	 * 
	 * In all these methods, assume that String parameters are not null. Use
	 * assert statements to test all Preconditions. Note that in some cases, the
	 * assert statement cannot appear at the beginning of the method; some
	 * computation must be done first.
	 */

	/**
	 * Return true iff s is a palindrome, that is, it reads the same backwards
	 * and forwards.
	 *
	 * Examples: For s = "", return true For s = "ab", return false For s =
	 * "aba", return true. For s = "abba", return true. For s =
	 * "Madam, I'm Adam", return false. For s = "MadamImAdam", return false. For
	 * s = "madamimadam", return true.
	 */
	public static boolean isPalindrome(String s) {
		/*
		 * Do not visit each character of the string more than once each. Use a
		 * loop.
		 */

		int count = 0;
		for (int i = 0; i < s.length() / 2; i++) {
			if (s.charAt(i) != s.charAt(s.length() - 1 - i)) {
				count += 1;
			}

		}

		if (count > 0) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Return the number of times query occurs as a substring of src (the
	 * different occurrences may overlap). Precondition: query is not the empty
	 * string "". Examples: For src = "ab", query = "b", return 1. For src =
	 * "Luke Skywalker", query = "ke", return 2. For src = "abababab", query =
	 * "aba", return 3. For src = "abc", query = "", return 4
	 */
	public static int numOccurrences(String src, String query) {
		/*
		 * Actually, the case that query is the empty string "" should be
		 * included. For example, "" appears in the string "ab" three times:
		 * before the 'a', between the 'a' and 'b', and after the 'b'.
		 * 
		 * However, in testing our solution, we discovered a mistake in function
		 * indexOf(String, int)!. For example, according to the specification,
		 * 
		 * "ab".indexOf("", 4) should yield -1 but it yields 2
		 * 
		 * We have filed a bug report.
		 * 
		 * Therefore, this method has the Precondition that query is not the
		 * empty String, and you don't have to deal with this case.
		 * 
		 * Don't compare the strings character by character. Look through the
		 * methods of class String and see how you can directly jump from each
		 * occurrence of the query string to the next. Be sure you handle
		 * correctly the case that query does not occur in src.
		 */

		assert query != "";

		int first = src.indexOf(query);
		int last = src.lastIndexOf(query);

		if (first == -1) {
			return 0;

		} else if (last == first) {
			return 1;

		} else {
			int index = first + 1;
			int next = src.indexOf(query, index);
			int count = 2; // for first and last occurrences
			while (next < last) {
				count += 1;
				index = next + 1;
				next = src.indexOf(query, index);

			}

			return count;
		}

	}

	/**
	 * String s consists of a first name, an optional middle name, and a last
	 * name. The (two or three) names are separated by one or more blanks. There
	 * may be blank characters at the beginning of s and at the end of s. Return
	 * the correctly formatted name as Lastname, Firstname [MI.], where MI is
	 * the middle initial (first character of the middle name, if one was
	 * supplied). Note that in the answer, the first character of each name must
	 * be in uppercase and the rest of the characters in lowercase.
	 *
	 * Precondition: s has exactly 2 or 3 parts, as mentioned above.
	 *
	 * Examples: For s = "    David Gries" return "Gries, David" For s =
	 * "sid       chaudhuri " return "Chaudhuri, Sid" For s =
	 * "James Arthur Gosling" return "Gosling, James A."
	 */
	public static String fixName(String s) {
		/*
		 * As you know, String is a class. An object of class String is
		 * immutable -- you cannot change the sequence of chars that it
		 * contains. However, you can create new strings by catenating together
		 * parts of the original string.
		 * 
		 * Do NOT use a loop or recursion. Use only if-statements, assignments,
		 * and return statements.
		 * 
		 * Finally, this method can be written using an oft-used pattern: 1.
		 * Break the string into its parts 2. Modify the parts (How can you make
		 * the first letter of each part of the name uppercase? how can you
		 * extract the middle initial?) 3. Build the result from the modified
		 * parts.
		 */

		s = s.trim();
		String spaces = "[ ]+";
		String[] names = s.split(spaces);
		int num = names.length;

		assert num == 2 || num == 3; //name has 2 or 3 parts

		String first = names[0];
		first = first.substring(0, 1).toUpperCase()
				+ first.substring(1, first.length()).toLowerCase();

		if (num == 2) { // first and last name
			String last = names[1];

			last = last.substring(0, 1).toUpperCase()
					+ last.substring(1, last.length()).toLowerCase();

			String fixed2 = last + ", " + first;
			return fixed2;

		} else { // first, middle, and last name
			String middle = names[1];
			String last = names[2];

			String MI = middle.substring(0, 1).toUpperCase();

			last = last.substring(0, 1).toUpperCase()
					+ last.substring(1, last.length()).toLowerCase();

			String fixed3 = last + ", " + first + " " + MI + ".";
			return fixed3;
		}

	}

	/**
	 * Return true iff s and t are anagrams of each other. An anagram of a
	 * string is another string that has the same characters, but possibly in a
	 * different order. Like other methods of this class, this method too is
	 * case-sensitive, so 'a' and 'A' are considered different characters.
	 *
	 * Examples: For s = "mary", t = "army", return true. For s =
	 * "tom marvolo riddle", t = "i am lordvoldemort", return true. For s =
	 * "tommarvoloriddle", t = "i am lordvoldemort", return false. For s =
	 * "hello", t = "world", return false.
	 */
	public static boolean areAnagrams(String s, String t) {
		/*
		 * Do not use a loop or recursion! This is a tricky one but can in fact
		 * be done in a few lines. Hint: how can a sequence of characters be
		 * uniquely ordered? You might need to first convert the string to an
		 * array characters, and then use a function from class Arrays
		 * (http://docs.oracle.com/javase/7/docs/api/java/util/Arrays.html).
		 */

		char[] sChar = s.toCharArray();
		char[] tChar = t.toCharArray();

		Arrays.sort(sChar);
		Arrays.sort(tChar);

		return Arrays.equals(sChar, tChar);

	}

	/**
	 * Return a string that is s but with all lowercase consonants (letters of
	 * the English alphabet other than the vowels a, e, i, o, u) replaced with
	 * _, and all uppercase consonants replaced with ^.
	 *
	 * Examples: For s = "Minecraft" return "^i_e__a__". For s = "Alan Turing"
	 * return "A_a_ ^u_i__".
	 */
	public static String replaceConsonants(String s) {
		/*
		 * Writing a long list of 42 statements, one for each (uppercase or
		 * lowercase) consonant is not a great idea. Instead, put the 21
		 * consonants in a string; Then write a loop that sequences the chars in
		 * that string in turn, replacing the upper and lowercase versions of
		 * each letter in s. This should be the ONLY loop you write! You can use
		 * Character.toUpperCase() to convert a char to uppercase.
		 * 
		 * A for-loop to loop through a range b..c of integers can look like
		 * this: for (int i = b; i <= c ; i = i + 1) { ... }
		 */

		String small = "bcdfghjklmnpqrstvwxyz";
		String big = "BCDFGHJKLMNPQRSTVWXYZ";

		for (int i=0; i<small.length(); i++) {
			s = s.replace(small.charAt(i), '_');
			s = s.replace(big.charAt(i), '^');
			
		}
		
		return s;
		
		
	}

	/**
	 * String s is written in a form that looks something like this:
	 * "3b1c5b2x0a". For this s, return the decompressed string "bbbcbbbbbxx".
	 *
	 * More formally, s is in "compressed form", which means that it consists of
	 * a sequence of characters that are NOT digits 0..9 with each character
	 * preceded by a digit; the digit indicates how many times that character
	 * should appear. Return the uncompressed version of s.
	 */
	public static String decompress(String s) {
		/*
		 * To produce the integer that is in String s1 use function
		 * Integer.parseInt(s1). Remember that a character c is not a String,
		 * and to change c into a String you may have to catenate it with the
		 * empty String. This function will probably need a loop within a loop
		 */
		
		String fixed = "";
		
		
		for (int i=0; i<s.length(); i+=2) {

			int index = Integer.parseInt(s.substring(i, i+1));
			
			for (int j=0; j<index; j++) {
				fixed = fixed + s.charAt(i+1);
				
			}
			
		
		}

		
		return fixed;
	}
}
