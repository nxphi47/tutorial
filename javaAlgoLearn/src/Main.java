import java.util.*;

/**
 * Created by nxphi on 3/11/2017.
 * given K (2 < K < 6) character with 0 and 1 forming a 2dim matrix NxM (1 < M,N < 10)
 * find the minimum number of bit to differentiate all K characters
 * test case:[N M K list of character string]
 * 2 3 2 111010 100100 -> 1
 * 2 4 4 00011000 10000000 00100010 01000000 -> 3
 * 2 4 6 00000000 00001000 01000010 10000000 10010000 10011000 -> 4
 * 2 4 6 00010000 00001000 01000010 10000000 10010000 10011000 -> 3
 */
class LeastBit {
	// number of character
	private int K;
	// N*M matrix for each character
	private int N;
	private int M;
	// store the data of each character ifself, a character N*M matrix is straighten to 1-dimensional arrayList() size N*M for
	// 	convenient manipulation
	private ArrayList<Integer>[] data;

	// store each comparison between 2 character,
	// for example, comparison[index] = [1,0,1,1,0] if 2 character is binary different in digit 0, 2, 3 as indicated as 1 in comparszon array
	// compare(1001, 0101) = 1100
	private int[][] comparisons;

	// if Debug mode is true, print out any debugging message
	private static final boolean isDebug = false;

	public LeastBit() {
		getInput();
		preprocess();
		execute();
	}

	public void debug(String str) {
		if (isDebug) {
			System.out.printf("\n%s", str);
		}
	}

	// retrieve the input
	public void getInput() {
		Scanner scanner = new Scanner(System.in);
		N = scanner.nextInt();
		M = scanner.nextInt();
		K = scanner.nextInt();
		// data is a array size K contain many Arraylist, each arraylist represent a character
		data = new ArrayList[K];
		String input;
		// use delimiter "" to get 1 character each on scanner input
		scanner.useDelimiter("");

		// loop through each character
		for (int i = 0; i < K; i++) {
			data[i] = new ArrayList<>();
			// each character is straighten to 1 dimensional array of size N*M
			for (int j = 0; j < N * M; j++) {
				do {
					input = scanner.next();
				} while (input.equals(" ") || input.equals("\n"));
				data[i].add(Integer.parseInt(input));
			}
		}
	}

	// preprocess will remove all bits that are the same for all K characters,
	// it also filter out all bits that are duplicate in a combination of K characters
	/* for example
	[110010]	 [1001]
	[001101]	 [0110]
	[110101] ==> [1010] because bit at index 1 and 5 is filter because is duplicate
	 */
	public void preprocess() {
		// filter bits that are all the same for k characters
		int current; // the current value that is evaluated
		boolean diff;
		for (int i = 0; i < data[0].size(); i++) {
			diff = false;
			current = data[0].get(i);
			for (int j = 1; j < data.length; j++) {
				if (current != data[j].get(i)) {
					// if not equal, this bit is not the same for all K character
					diff = true;
					break;
				}
			}
			if (!diff) {
				// remove this bit from all character arrayList
				for (ArrayList<Integer> arr: data) {
					arr.remove(i);
				}
				i--;
			}
		}
		// filter all bits that have the same pattern with K characters, only 1 left
		int[] pattern = new int[data.length]; // store the pattern of a bit in K character
		boolean samePattern;
		for (int i = 0; i < data[0].size(); i++) {
			// set the bit i's pattern
			for (int j = 0; j < data.length; j++) {
				pattern[j] = data[j].get(i);
			}

			// check if same pattern for all following bits
			for (int k = i + 1; k < data[0].size(); k++) {
				samePattern = true;
				for (int t = 0; t < data.length; t++) {
					if (pattern[t] != data[t].get(k)) {
						samePattern = false;
						break;
					}
				}
				if (samePattern) {
					// remove this bit
					for (int t = 0; t < data.length; t++) {
						data[t].remove(k);
					}
					k--;
				}
			}
		}
	}

	public String arrToStr(int[] arr) {
		StringBuilder builder = new StringBuilder("arr[");
		for (int i: arr) {
			builder = builder.append(String.valueOf(i) + ",");
		}
		return builder.substring(0, builder.length() - 1) + "]";
	}


	public void execute() {
		// the combination of 2 of K characters is the number of all comparison of K characters
		int cases = 1;
		for (int i = data.length; i > data.length - 2; i--) {
			cases *= i;
		}
		cases /= 2;

		// there are "cases" comparison and each take the size of 1 character's length
		comparisons = new int[cases][data[0].size()];
		int compareCount = 0; // counting for each comparison in loop
		for (int i = 0; i < data.length - 1; i++) {
			for (int j = i + 1; j < data.length; j++) {
				for (int k = 0; k < data[0].size(); k++) {
					// for data i and j, comparison is 1 if the bit is different and 0 if it is the same
					// this is equivalent when you get the XOR of 2 value
					comparisons[compareCount][k] = (data[i].get(k).equals(data[j].get(k)))?0:1;
				}
				debug(String.format("%s\n", arrToStr(comparisons[compareCount])));
				compareCount++;
			}
		}

		// finding the minimum required number of bit
		int min = Integer.MAX_VALUE;
		int temp;
		// for each starting point of all bit possible, start the recursively call recur_minBits to find the
		// minimum number of bit required for each starting bit
		for (int i = 0; i < data[0].size(); i++) {
			debug(String.format("Recur start %d", i));

			temp = recur_MinBits(i, new HashSet<>(), new int[comparisons.length]);
			debug(String.format("\n-------------------------RECUR end with %d and = %d--------------------------------\n", i, temp));

			if (temp < min) {
				min = temp;
			}
		}

		debug("FINAL RESULT\n");
		System.out.printf("%s", min);

	}
	// HashSet is the set of bits that are actually included for recognizing the characters
	// Occupied array store boolean value 0 or 1 indicating whether comparison[i] is differentiated by bits in Set
	// occupied array is initialized as all 0
	public int recur_MinBits(int index, HashSet<Integer> set, int[] occupied) {
		// if index can differentiate at least 1 comparison, put that element of occupied to 1, add index to HashSet
		boolean indexOccupied = false;
		for (int i = 0; i < occupied.length; i++) {
			// if index can recognize comparison i, and occupied == 0, then set 1 and add index into hash set
			if (comparisons[i][index] == 1 && occupied[i] == 0) {
				// marked  it 1
				occupied[i] = 1;
				indexOccupied = true;
				debug(String.format("assign to %d ", i));
			}
		}
		if (indexOccupied) {
			if (!set.contains(index)) {
				set.add(index);
				debug(String.format("Add %d to set %s occ = %s\n", index, set, arrToStr(occupied)));

			}
		}

		// check if all occupied element == 1, all comparisons are recognized, no need to find more bit, return size of the hash set
		boolean allOccupied = true;
		for (int i: occupied) {
			if (i == 0) {
				allOccupied = false;
			}
		}
		if (allOccupied) {
			debug(String.format("index = %s, set full: %s\n", index, set));
			return set.size();
		}

		// recursively start on each other bit on the right of index, find the minimum result and return
		int min = Integer.MAX_VALUE;
		int temp;
		for (int i = index + 1; i < data[0].size(); i++) {
			// make copy of occupied array to separately manipulate it
			int[] mirrorOccupied = new int[comparisons.length];
			for (int j = 0; j < comparisons.length; j++) {
				mirrorOccupied[j] = occupied[j];
			}
			debug(String.format("recur_MinBits at i = %s,  occ = %s, set = %s, ", i, arrToStr(occupied), set));
			temp = recur_MinBits(i, set, mirrorOccupied);
			if (set.contains(i))
				set.remove(i);

			if (temp < min) {
				min = temp;
			}
		}

		return min;
	}

}


public class Main{
	public static void main(String[] args) {
		// TODO: Implement your program
		LeastBit leastBit = new LeastBit();
	}
}
