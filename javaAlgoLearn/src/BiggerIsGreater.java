import java.util.Arrays;
import java.util.Collections;
import java.util.StringJoiner;

/**
 * Created by nxphi on 2/9/2017.
 * Given a word , rearrange the letters of  to construct another word  in such a way that  is lexicographically greater than .
 * In case of multiple possible answers, find the lexicographically smallest one among them.
 *
 */
public class BiggerIsGreater extends RootProb{
	public BiggerIsGreater() {
		super();
	}

	public void execute() {
		int size = scanner.nextInt();
		String[] arr = new String[size];
		for (int i = 0; i < size; i++) {
			arr[i] = scanner.next();
		}
		cal(size, arr);
		for (String x :
				arr) {
			System.out.printf("%s\n", arr);
		}
	}

	public Character[] strToCharacters(String str) {
		Character[] out = new Character[str.length()];
		for (int i = 0; i < str.length(); i++) {
			out[i] = str.charAt(i);
		}
		return out;
	}
	public Character[] strToCharacters(StringBuilder stringBuilder) {
		return strToCharacters(stringBuilder.toString());
	}
	public String charactersToStr(Character[] ch) {
		StringBuilder builder = new StringBuilder(ch.length);
		for (Character x :
				ch) {
			builder.append(x);
		}
		return builder.toString();
	}
	public String charactersToStr(char[] ch) {
		StringBuilder builder = new StringBuilder(ch.length);
		for (char x :
				ch) {
			builder.append(x);
		}
		return builder.toString();
	}


	public boolean isMaxOrder(Character[] ch) {
		for (int i = 0; i < ch.length - 1; i++) {
			if (ch[i + 1] > ch[i]) {
				return false;
			}
		}
		return true;
	}

	public void cal(int size, String[] arr) {
		String[] out = new String[size];
		for (int i = 0; i < size; i++) {
			String target = arr[i];
			// do it on target
//			Character[] targetArr = strToCharacters(target);
			boolean found = false;
			for (int j = target.length() - 2; j >= 0; j--) {
				String sub = target.substring(j);
				Character[] subArr = strToCharacters(sub);
				if (!isMaxOrder(subArr)) {

					// finding the index of next higher
					int index = 1;
					Character min = subArr[1];
					for (int k = 1; k < subArr.length; k++) {
						if (subArr[k] > subArr[0] && min > subArr[k]) {
							index = k;
							min = subArr[k];
						}
					}
					Character temp = subArr[0];
					subArr[0] = subArr[index];
					subArr[index] = temp;
					Arrays.sort(subArr,  1, subArr.length);
					arr[i] = target.substring(0, j) + charactersToStr(subArr);
					found = true;
					break;
				}
			}
			if (!found) {
				arr[i] = "no answer";
			}

		}
	}

}
