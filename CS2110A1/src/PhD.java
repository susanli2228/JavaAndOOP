/** An instance maintains info about the PhD of a person. */

/** I checked the Javadoc output and it was okay.*/


public class PhD {

	private String name; // person's name, length >0
	private int year; // year PhD was awarded
	private int month; // month PhD was awarded, 1-12
	private char MF; // gender of person, M=male, F=female
	private PhD adv1; // first advisor of person, null if unknown
	private PhD adv2; // second advisor, null if unknown or nonexistent (if adv1
						// is null)
	private int num; // # advisees person had

	/**
	 * Constructor: an instance for a person with name n, gender g, PhD year y,
	 * and PhD month m. Its advisors are unknown, and it has no advisees.
	 * Precondition: n has at least 1 character, m is in 1..12, and g is 'M' for
	 * male or 'F' for female
	 */
	public PhD(String n, char g, int y, int m) {
		assert n.length() >= 1;
		assert m >= 1 && m <= 12;
		assert g == 'M' || g == 'F';

		name = n;
		MF = g;
		year = y;
		month = m;
	}

	/**
	 * Constructor: a PhD with name n, gender g, PhD year y, PhD month m, first
	 * advisor ad, and no second advisor. Precondition: n has at least 1 char, g
	 * is 'F' for female or 'M' for male, m is in 1..12, and ad is not null.
	 */
	public PhD(String n, char g, int y, int m, PhD ad) {
		assert n.length() >= 1;
		assert m >= 1 && m <= 12;
		assert g == 'M' || g == 'F';
		assert ad != null;

		name = n;
		MF = g;
		year = y;
		month = m;
		adv1 = ad;

		ad.num += 1;
	}

	/**
	 * Constructor: a PhD with name n, gender g, PhD year y, PhD month m, first
	 * advisor ad1, and second advisor ad2. Precondition: n has at least 1 char,
	 * g is 'F' for female or 'M' for male, m is in 1..12, ad1 and ad2 are not
	 * null, and ad1 and ad2 are different.
	 */
	public PhD(String n, char g, int y, int m, PhD ad1, PhD ad2) {
		assert n.length() >= 1;
		assert m >= 1 && m <= 12;
		assert g == 'M' || g == 'F';
		assert ad1 != null && ad2 != null;
		assert ad1 != ad2;

		name = n;
		MF = g;
		year = y;
		month = m;
		adv1 = ad1;
		adv2 = ad2;

		ad1.num += 1;
		ad2.num += 1;
	}

	/**
	 * @return name of person
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return year person got PhD
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @return month person got PhD
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @return the value of "this person is female"
	 */
	public boolean isFemale() {
		return MF == 'F';
	}

	/**
	 * @return this PhD's first advisor (null if unknown)
	 */
	public PhD getAdvisor1() {
		return adv1;
	}

	/**
	 * @return this PhD's second advisor (null if unknown or nonexistent)
	 */
	public PhD getAdvisor2() {
		return adv2;
	}

	/**
	 * @return the number of PhD advisees of this person
	 */
	public int numAdvisees() {
		return num;
	}

	/**
	 * Add p as this person's first PhD advisor. Precondition: this person's
	 * first advisor is unknown and p is not null
	 */
	public void addAdvisor1(PhD p) {
		assert adv1 == null;
		assert p != null;

		adv1 = p;
		p.num += 1;
	}

	/**
	 * Add p as this persons second advisor. Precondition: This person's first
	 * advisor is known, their second advisor is unknown, p is not null, and p
	 * is different from this person's first advisor
	 */
	public void addAdvisor2(PhD p) {
		assert adv1 != null;
		assert adv2 == null;
		assert p != null;
		assert p != adv1;

		adv2 = p;
		p.num += 1;
	}

	/**
	 * Precondition: p is not null.
	 * @return value of "this person got their PhD before p did".
	 */
	public boolean isOlderThan(PhD p) {
		assert p != null;

		return year < p.year || (year == p.year && month < p.month);
	}

	/**
	 * Precondition: p is not null.
	 * @return value of "this person and p are intellectual siblings".
	 */
	public boolean isPhDSibling(PhD p) {
		assert p != null;

		return (adv1 != null && adv1 == p.adv1 && this != p)
				|| (adv2 != null && adv2 == p.adv2 && this != p)
				|| (adv1 != null && adv1 == p.adv2 && this != p)
				|| (adv2 != null && adv2 == p.adv1 && this != p);
	}

}
