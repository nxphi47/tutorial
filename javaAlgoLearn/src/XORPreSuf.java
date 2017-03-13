import java.util.Random;
import java.util.Scanner;

/**
 * Created by nxphi on 3/11/2017.
 * [exam4] maximum XOR with prefix and suffix array
 * Description
 We have N numbers as an array, you need to find a prefix array and a suffix array,
 which we can get the maximum xor value with all elements in them. Notice that for
 prefix[0, l] and suffix[r, n - 1], do not intersect (l < r), and they can be empty.

 Input
 The first line is one number N (1 <= N <= 100000)
 The second line contains N numbers ai(0 <= ai <= 1e12) separated by space,
 which represents the array

 Out: maximum xor

 test case
 3 1 2 3 -> 3
 4 1 2 3 6 -> 7
 10 234 543 216 232 543 22 441 252 657 4353 -> 5108
 20 234 543 216 232 543 22 441 252 657 4353 1234 54545 656 7657 23423 66878 56875 4534 8787 234324 -> 260422
 25 234 543 216 232 543 22 441 252 657 4353 1234 54545 656 7657 23423 66878 56875 4534 8787 234324 5656565656 12522423 242535 63 1634635343 -> 5663755056
 20 234 543 216 232 543 22 441 252 657 4353 32632 12512216 2323223 5156 56474 583482 2342343 745747 324231  534754 -> 12486329
 35 234 543 216 232 543 22 441 252 657 4353 1234 54545 656 7657 23423 66878 56875 4534 8787 234324 5656565 1252223 242535 63333 63463343 57548547
 		3476586545 57474835734 475674527 756746546 756865464 78678678 6346345454 6564854854 34634664545 -> 58903962872

 40 234 543 216 232 543 22 441 252 657 4353 1234 54545 656 7657 23423 66878 56875 4534 8787 234324 5656565 1252223 242535 63333 63463343 57548547
 		3476586545 57474835734 475674527 756746546 756865464 78678678 6346345454 6564854854 34634664545 87878778 342342 34534646 6757575
 		23423434 5436564 23423432 56457647 23423423 54654653 -> 58920620533

 conclusion:
 	This algorithm of using Trie will only become efficient when N > 100000, when N is small, brute force show it predominant
 	reason is perhaps because the creation and retrieval time of trie node is expensive that it is only effective when N very large
 */
// XORPReSuf will handle the problem programming
public class XORPreSuf {
	// debug mode, will print out debugging message as well as run brute force to check solution
	public static final boolean isDebug = false;

	// input mode is either random, it random, it will self generate number randomly, otherwise, user input them
	public static final boolean isRandom = false;

	// number of bit in one number, 40 is enough to evaluate up to 10^12
	public static final int numBit = 40;

	// the data it selft
	private long[] data;
	// prefixArray store XOR of all element starting from data[0] to data[i]
	private long[] prefixArr;
	// suffixArray store XOR of all element starting from data[i] to data[n - 1]
	private long[] suffixArr;
	private int N;
	// root of the binary trie
	private Node root;

	public XORPreSuf() {
		getInput();
		execute();
	}

	// class for a binary node (trie)
	public class Node {
		public Node zero; // the 0 branch,
		public Node one; // the 1 branch
//		public String represent; // 1 binary string representation of the node, e.g: 101 if it is from 1 - > 0 -> 1
		public Node() {
			zero = null;
			one = null;
		}
		public Node(Node zero, Node one, String represent) {
			this.zero = zero;
			this.one = one;
		}

		// just to return whether zero is empty
		public boolean is0empty() {
			return zero == null;
		}

		public boolean is1empty() {
			return one == null;
		}
	}

	// only print out message on debug mode
	public void debug(String string) {
		if (isDebug) {
			System.out.printf("\n%s", string);
		}
	}

	// getting the input from the user or from random mode
	public void getInput() {
		Scanner scanner = new Scanner(System.in);
		if (!isRandom) {
			N = scanner.nextInt();
			data = new long[N];
			for (int i = 0; i < data.length; i++ ){
				data[i] = scanner.nextLong();
			}
		}
		else {
			// In random, N random long number will be generated and assigned
			System.out.printf("Enter N: ");
			N = scanner.nextInt();
			Random random = new Random();
			data = new long[N];
			for (int i = 0; i < data.length; i++ ){
				data[i] = (long) (random.nextDouble() * 1E12);
			}
			System.out.printf("\nGeneration finished\n");
		}
	}


	// preProcess will create prefix array and suffix and generate the trie tree from suffix
	public void preProcess() {
		// we initialize prefix array and suffix array which contains XOR value up to and down from that index
		prefixArr = new long[data.length];
		suffixArr = new long[data.length];

		prefixArr[0] = data[0];
		for (int i = 1; i < prefixArr.length; i++) {
			prefixArr[i] = data[i] ^ prefixArr[i - 1];
		}
//		debug(String.format("Prefix: %s", arrToStr(prefixArr)));

		suffixArr[suffixArr.length - 1] = data[data.length - 1];
		for (int i = suffixArr.length - 2; i >= 0; i--) {
			suffixArr[i] = data[i] ^ suffixArr[i + 1];
		}

		// inserting all suffix, except the first one as we only count from second one, into a binary trie
		debug("Start inserting trie");
		long startTimt = System.currentTimeMillis();
		root = new Node();
		for (int i = 1; i < suffixArr.length; i++) {
			// except the first one
			insertTrieNode(longToBitStr(suffixArr[i]), root, 0);
		}
		debug(String.format("Trie entry in time: %s", (System.currentTimeMillis() - startTimt)));
		debug("Preprocess finished");
	}

	// convert long value to binary string (101101011), each string length 40 to fit up to 10^12
	public String longToBitStr(long val) {
		return String.format("%" + numBit + "s", Long.toBinaryString(val)).replace(" ", "0");
	}

	// convert binary string to long valuue
	public long bitStrToLong(String str) {
		return Long.parseLong(str, 2);
	}

	// recursive call: insert a binary number to a trie base on its '1' '0' sequence
	// index used to track the binary position of the bitStr when the number traverse inside the trie
	public void insertTrieNode(String bitStr, Node target, int index) {
		if (index >= bitStr.length()) {
			return;
		}
		// if bit at index is 0, create node 0 of target if not exists, then recursively insert into there
		if (bitStr.charAt(index) == '0') {
			// create a middle node if it is not exists at branch 0
			if (target.is0empty()) {
				target.zero = new Node();
			}
			insertTrieNode(bitStr, target.zero, ++index);
		}
		else {
			// create a middle node if it not exists at branch 1
			if (target.is1empty()) {
				target.one = new Node();
			}
			insertTrieNode(bitStr, target.one, ++index);
		}
	}

	// removing a trie node
	public void removeTrieNode(String bitStr, Node target, int index) {
		if (index >= bitStr.length()) {
			return;
		}
		// Index used to track the binary position of bitStr corresponding to each floor of the trie tree
		if (bitStr.charAt(index) == '0') {
			// when there is no leftOver, target.zero is the last position, remove it!
			if (index == bitStr.length() - 1) {
				target.zero = null;
			}
			else {
				// else if zero is not empty, go the leftOver to its
				if (!target.is0empty()) {
					removeTrieNode(bitStr, target.zero, ++index);
				}
			}
		}
		else {
			// when there is no leftOver, target.zero is the last position, remove it!
			if (index == bitStr.length() - 1){
				target.one = null;
			}
			else {
				// else if zero is not empty, go the leftOver to its
				if (!target.is1empty()) {
					removeTrieNode(bitStr, target.one, ++index);
				}
			}
		}
	}

	// find the suffix value from the trie "node" that combine with "value" to produce max XOR
	/*
	The more the binary digit is different between 2 number and the more close the most significant bit (left side)
		-> the more XOR become
	 */
	public long maxXORsuffix(String value, Node node, int index) {
		if (node == null || index >= value.length()) {
			// reach the end of the string value or node tree, return the node whether we found the maximum
			return 0;
//			return null;
		}
		else {
//			String leftOver = value.substring(1);
			if (value.charAt(index) == '0') {
				// if 0 at first digit, find on '1' side to achieve maximum distinction
				if (!node.is1empty()) {
					// prepending '1' to its binary represent means plus 2 powers to 63 - index
					return (long) Math.pow(2, numBit - 1 - index) + maxXORsuffix(value, node.one, ++index);
				}
				else {
					// no '1' side, have no choice to go to '0' side
					return maxXORsuffix(value, node.zero, ++index);
				}
			}
			else {
				// if 1 at first digit, find on '0' side to achieve maximum distinction
				if (!node.is0empty()) {
					return maxXORsuffix(value, node.zero, ++index);
				}
				else {
					// '0' side, have no choice to go to '1' side
					return (long) Math.pow(2, numBit - 1 - index) + maxXORsuffix(value, node.one, ++index);
				}
			}
		}
	}

	public void execute() {
		debug("Start process max XOR");
		long startTime = System.currentTimeMillis();

		preProcess();
		long maxXOR = 0;
		// finding the set of prefix and suffix that combines to make maximum XOR
		/*
		algorithm:
			looping through each value of prefix array:
				finding the element in suffix array that is most binary distinct from the most significant bit (largest XOR)
				with the reference prefix element
				calculate the maximum XOR for each iteration.
			prefix or suffix array can be empty, so we loop through prefix and suffix array to find the maximum XOR
				compare with one found above to achieve the global maximum XOR
		 */

		// loop prefix array except last point
		for (int i = 0; i < prefixArr.length - 1; i++) {
			// taking prefix itself as it can take empty suffix
			if (prefixArr[i] > maxXOR) {
				maxXOR = prefixArr[i];
			}
			// retrieve the maximum node with value that combines with prefixArr[i] to produce max XOR
			long nodeVal = maxXORsuffix(longToBitStr(prefixArr[i]), root, 0);
//			if (!nodeStr.equals("")) {
//				long nodeVal = bitStrToLong(nodeStr);
			long xor = prefixArr[i] ^ nodeVal;
			if (xor > maxXOR) {
				maxXOR = xor;
			}
//			}
			// remove the suffixArr[i + 1] from the trie because prefix and suffix must not overlapp
			removeTrieNode(longToBitStr(suffixArr[i + 1]), root, 0);
		}
		if (prefixArr[prefixArr.length - 1] > maxXOR) {
			maxXOR = prefixArr[prefixArr.length - 1];
		}

		for (int i = 0; i < suffixArr.length; i++) {
			if (suffixArr[i] > maxXOR) {
				maxXOR = suffixArr[i];
			}
		}
		// take the suffix itself as it can take a empty prefix
		debug(String.format("Optimized max %s, overall time = %s\n", maxXOR, (System.currentTimeMillis() - startTime)));
		System.out.printf("%s", maxXOR);
		if (isDebug) {
			if (maxXOR == bruteExecute()) {
				debug("Algorithm correct");
			}
			else {
				debug("Algorithm incorrect");
			}
		}
	}

	public long bruteExecute() {
		long maxXOR = 0;
		long startTime = System.currentTimeMillis();

		// TODO: taking prefix itself as it can take empty suffix
		for (int i = 0; i < prefixArr.length; i++) {
			if (prefixArr[i] > maxXOR) {
				maxXOR = prefixArr[i];
			}
		}

		// TODO: taking suffix itself as it can take empty prefix
		for (int i = 0; i < suffixArr.length; i++) {
			if (suffixArr[i] > maxXOR) {
				maxXOR = suffixArr[i];
			}
		}

		// if taking both prefix and suffix, find all possible pair so that it make XOR max
		// this take O(n^2) time
		for (int i = 0; i < prefixArr.length - 1; i++) {
			for (int j = i + 1; j < suffixArr.length; j++) {
				long xor = prefixArr[i] ^ suffixArr[j];
				if (xor > maxXOR) {
//					debug(String.format("brute:xor found at %d and %d", i, j));
					maxXOR = xor;
				}
			}
		}

		debug(String.format("Brute: %s, overall time = %s\n", maxXOR, (System.currentTimeMillis() - startTime)));
		return maxXOR;
	}
}
