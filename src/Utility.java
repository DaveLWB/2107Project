
public class Utility {
	public static String trimZeros(String str) {
	    int pos = str.indexOf(0);
	    return pos == -1 ? str : str.substring(0, pos);
	}
}
