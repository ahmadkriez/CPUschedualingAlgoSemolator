
/**
 * Author : Team BG03
 * last update 2019/12/2
 * This class provides a random non-negative integer with a Poison distribution.
 * The Poison calculation is based on an expected number, which is a double real
 * number between 0 and 1.0.
 */

import java.util.Random; // Provides a random number with uniform distribution.

public class PoisonDistGenerator {
	private double expectedNumber; // Stores the expected number value
	private static Random rand; // Stores a random number sequence
	static {
		rand = new Random(); // Create the random number sequence
		rand.setSeed(System.currentTimeMillis()); // Initialize sequence from system current time
	}

	public PoisonDistGenerator() { // Default constructor
		expectedNumber = 0.5; // Default expected number value is 0.5
	}

	public PoisonDistGenerator(double e) { // Another constructor
		expectedNumber = e; // Initialize with the given value.
	}

	public void setExpectedNumber(double e) { // Changes the expected number value.
		expectedNumber = e;
	}

	public double getExpectedNumber() { // Returns the expected number value.
		return expectedNumber;
	}

	public int getNumber() { // Generates the next number of the random
		double em, x; // number sequence with the requested
		int n; // Poison distribution.

		em = Math.exp(-expectedNumber);
		x = rand.nextDouble();
		n = 0;
		while (x > em) {
			n++;
			x = x * rand.nextDouble();
		}
		return n;
	}
}
