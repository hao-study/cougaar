import java.io.*;
import java.util.*;


class WordScore implements Comparable<WordScore> {
	String word;
	int score;
	
	public WordScore(String word, int score) {
		this.word = word;
		this.score = score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	/* used to sort */
	public int compareTo(WordScore to) {
		if (score != to.score) {
			/* Note: ascending order (used to form a min Heap) */
			return score - to.score;
		}
		else {
			/* sorted by native string */
			return to.word.compareTo(word);
		}
	}
	
	@Override
	public String toString() {
		return word + "," + String.valueOf(score);
	}
}

public class WordSearch {
	private static final int TOP_COUNT = 10;
	
	
    public static boolean found = false;

	/* prepare a priority queue to save top 10 words */
    private static PriorityQueue<WordScore> minHeap = null;
    private static int heapSize = 0;
    
    private static void saveTop(WordScore ws)
    {
    	if (minHeap == null) {
    		minHeap = new PriorityQueue<WordScore>();
    		heapSize = 0;
    	}
    	
		if (heapSize < TOP_COUNT) {
			minHeap.add(ws);
			heapSize++;
		}
		else {
			minHeap.add(ws);
			/* keep queue in the size of TOP_COUNT */
			minHeap.poll();
		}    	
    }
    
    /* recursive function (should be OK given the length up to 100) 
     *   DFS search from all possible path starting from the same point.
     */
    private static void searchInMatrix(
    		String word, int charAt, char[][] matrix, int N, 
    		int m, int n,
    		int prev_m0, int prev_n0,
    		int prev_m1, int prev_n1,
    		int turns, 
    		boolean[][] flags) {

    	int wordLen = word.length();

    	if (charAt >= wordLen) {
    		System.out.println("Warning: something wrong");
    		return;
    	}

    	if ((m < 0) || (m >= N) || (n < 0) || (n >= N)) {
            return;
        }
        if (word.charAt(charAt) != matrix[m][n]) {
            return;
        }
                
        // if this element has been used, do not used again
        if (flags[m][n]) {
            return;
        }

        /* set flag to prevent revisiting in a path */
        flags[m][n] = true;
        
        /* check if a turn along a searching path */
        if ((prev_m0 >= 0) && (prev_n0 >= 0)) {
        	/* turn */
        	if ((0 != (prev_m0 - m)) && (0 != (prev_n0 - n))) {
        		turns++;
        	}
        }
        
        if (charAt == (wordLen - 1)) {
    		/* a path is found */
    		WordScore ws = new WordScore(word, wordLen - turns);
    		saveTop(ws);
    		found = true;
    		flags[m][n] = false;
    		// System.out.println(word + ", " + String.valueOf(word.length() - turns));
    		return;
        }

        prev_m0 = prev_m1;
        prev_n0 = prev_n1;
        prev_m1 = m;
        prev_n1 = n;
        /* up */
        searchInMatrix(word, charAt + 1, matrix, N, m - 1, n    , prev_m0, prev_n0, prev_m1, prev_n1, turns, flags);
        /* to the left */
        searchInMatrix(word, charAt + 1, matrix, N, m    , n - 1, prev_m0, prev_n0, prev_m1, prev_n1, turns, flags);
        /* to the right */
        searchInMatrix(word, charAt + 1, matrix, N, m    , n + 1, prev_m0, prev_n0, prev_m1, prev_n1, turns, flags);
        /* down */
        searchInMatrix(word, charAt + 1, matrix, N, m + 1, n    , prev_m0, prev_n0, prev_m1, prev_n1, turns, flags);
        
        /* clear flag to search other path */
        flags[m][n] = false;
    }
	
	/* search a word in the dictionary */
	private static void searchWord(String word, char[][] dictMatrix, int N) {
		boolean[][] flags;
		
		found = false;
	    for (int m = 0; m < N; m++) {
	        for (int n = 0; n < N; n++) {
	            if (word.charAt(0) == dictMatrix[m][n]) {
	                flags = new boolean[N][N];	                
	                searchInMatrix(word, 0, dictMatrix, N, m, n, -1, -1, -1, -1, 0, flags);
	            }
	        }
	    }
	    if (!found) {
    		WordScore ws = new WordScore(word, 0);
    		saveTop(ws);
    		//System.out.println(word + ", " + 0);
	    }
	}

	public static void main(String[] args) throws FileNotFoundException {

		File dictFile = new File("dictionary.txt");		
		File wordFile = new File("word-search.txt");
		
		/* Form N x N dictionary matrix 
		 * (assume the input is correct)
		 */
		Scanner wordSc = new Scanner(wordFile);
		int N = 0;
		char[][] dictMatrix = null;
		while (wordSc.hasNextLine()) {
			String line = wordSc.nextLine();
			if (dictMatrix == null) {
				dictMatrix = new char[line.length()][line.length()];
			}
			if (line.length() <= 0) {
				continue;
			}
			for (int n = 0; n < line.length(); n++) {
				dictMatrix[N][n] = line.charAt(n);
			}
			N++;
		}
		wordSc.close();
				
		/* process word file */
		Scanner dictSc = new Scanner(dictFile);
		String word;
		while (dictSc.hasNextLine()) {
			word = dictSc.nextLine();
			if (word.length() <= 0) {
				continue;
			}
			searchWord(word, dictMatrix, N);
		}
		dictSc.close();
		
		/* output top 10 (or all if less then 10) by descending order */
		String outStr = null;
		while (!minHeap.isEmpty()) {
			WordScore temp = minHeap.poll();
			if (outStr == null) {
				outStr = temp.toString();
			}
			else {
				outStr = temp.toString() + "\n" + outStr;
			}
		}
		PrintWriter outputFile = new PrintWriter("output.txt");
		outputFile.println(outStr);
		outputFile.close();
		//System.out.println(outStr);
	}
}