import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by nxphi on 2/9/2017.
 * The first line contains  space-separated integers,  and , respectively.
 The second line contains  space-separated integers (we'll refer to the  value as ) describing the unique values of the set.

 Print the size of the largest possible subset ().


 */
public class NonDivSubset extends RootProb {
	public NonDivSubset() {
		super();
		nonDivSubset_ex(scanner);
	}

	public void nonDivSubset_ex(Scanner sc) {
		int n = sc.nextInt();
		long k = sc.nextInt();
		long val;
		boolean hasDiv = false;
		List<Long> arr = new LinkedList<>();
		for (int i = 0; i < n; i++) {
			val = sc.nextLong();
			if (val % k == 0) {
				hasDiv = true;
				continue;
			}
//			System.out.printf("%s ", val % k);
			arr.add(val % k);
		}
		if (k == 1) {
			System.out.printf("1");
			System.exit(0);
		}
//		System.out.printf("size = %s\n", arr.size());
		nonDivSubset_cal(n, k, arr, hasDiv);
	}
	public long nonDivSubset_cal(int n, long k, List<Long> arr, boolean hasDiv) {
    	/*
    	6 9
		422346306 940894801 696810740 862741861 85835055 313720373
		out : 5

		4 3
		1 7 2 4
		out 3
    	 */

		long total = 0;
		// code goes here
		int solu = 0;

		// inefficient solutions, find all the number that has at least 1 other combine to make it divisible
		if (solu == 0) {
//			ArrayList<Integer> temp = new ArrayList<>();
//			int[][] arrs = new int[n][n];
			int max = 0;
			long val = 0;
//			boolean hasDiv = false;

			Collections.sort(arr);
//			System.out.printf("%s\n", arr);
//			System.out.printf("after sorted\n");
			Long[] array = new Long[arr.size()];
			array = arr.stream().toArray(Long[]::new);
//			arr.toArray(arr);
			int i = 0;
			int dI = 0;
			long vali;
			long valj;
			int dJ = 0;
			int j = arr.size() - 1;
			max = arr.size() + (hasDiv?1:0);
			int count = 0;
			while (i < j) {
				vali = array[i];
				valj = array[j];

				if (vali + valj == k && vali != valj) {
					dI = dJ = 1;
					while ((array[i + 1] == vali) && i < j) {
						dI++;
						i++;
					}
					while (array[j - 1] == valj && i < j) {
						dJ++;
						j--;
					}
//					System.out.printf("di = %d, vali = %d, dj = %d, valj = %d \n", dI, vali, dJ, valj);
					max -= (dI > dJ)?(dJ):dI;
				}
				else if (vali + valj == k && vali == valj){
					max -= 2;
				}
				else if (array[i + 1] + array[j] == k){
					i++;
					continue;
				}
				else if (array[i] + array[j - 1] == k) {
					j--;
					continue;
				}

//				else {
				i++;
				j--;
//				}
			}
//			System.out.printf("i = %d, j = %d\n", i, j);

//			// at least
			if (hasDiv && max == 1){
				max++;
			}
			else if (!hasDiv && max == 0) {
				max++;
			}

//			for (int i = 0; i < arr.size(); i++) {
//				long pivot = arr.get(i);
//				int sum = 1 + (hasDiv?1:0);
//				for (int j = 0; j < arr.size(); j++) {
////					arrs[i][j] = arr.get(i) + arr.get(j);
//					val = pivot + arr.get(j);
////					System.out.printf("%20d ", val);
//					if (i == j) {
//						continue;
//					}
//					if (val % k != 0) {
//						sum++;
//					}
//				}
////				System.out.printf(" sum = %s", sum);
//				if (max < sum) {
//					max = sum;
//				}
////				System.out.printf("\n");
//			}
////			System.out.printf("\n");
			System.out.printf("%s", max);
		}

		return total;
	}

}
