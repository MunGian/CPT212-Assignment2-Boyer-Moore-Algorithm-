import java.util.Scanner;

public class BoyerMoore {
    private final int R; // Radix to represents the size of the alphabet being used
    private final int[] right; // The bad-character skip array
    private final int[] suffix; // The suffix array for the good suffix rule
    private final boolean[] prefix; // The prefix array for the good suffix rule
    private final String pat; // The pattern string

    // Constructor
    public BoyerMoore(String pat) {
        this.R = 256; // Initializes the radix to 256 for ASCII character set
        this.pat = pat; // Assign the input pattern

        // Initialize the bad-character skip array.
        right = new int[R];
        for (int c = 0; c < R; c++) {
            right[c] = -1; // Initialize all values in right[] as -1
        }
        for (int j = 0; j < pat.length(); j++) {
            right[pat.charAt(j)] = j; // Fill the actual value of right[] for each character in pattern
        }

        // Initialize the good suffix arrays
        suffix = new int[pat.length()];
        prefix = new boolean[pat.length()];
        preprocessGoodSuffix();
    }

    // Function to preprocess the good suffix rule arrays
    private void preprocessGoodSuffix() {
        int size = pat.length();
        for (int i = 0; i < size; i++) {
            suffix[i] = -1;
            prefix[i] = false;
        }

        // Compute the suffix and prefix arrays
        for (int i = 0; i < size - 1; i++) {
            int j = i;
            int k = 0;

            // Find the longest suffix which is also a prefix of the pattern
            while (j >= 0 && pat.charAt(j) == pat.charAt(size - 1 - k)) {
                j--;
                k++;
                suffix[k] = j + 1;
            }

            // If the suffix reached the beginning of the pattern, then it is a prefix
            if (j == -1) {
                prefix[k] = true;
            }
        }
    }

    // Function to calculate the skip value for the good suffix rule
    private int moveByGoodSuffix(int j) {
        int size = pat.length();
        int k = size - 1 - j;

        // If the suffix exists in the pattern, return the shift value
        if (suffix[k] != -1) {
            return j - suffix[k] + 1;
        }

        // If the suffix does not exist, find the longest prefix which is also suffix
        for (int r = j + 2; r <= size - 1; r++) {
            if (prefix[size - r]) {
                return r;
            }
        }

        // If no such prefix exists, shift the entire pattern
        return size;
    }

    // Function to search for the pattern in the text
    public int[] search(String txt) {
        int N = pat.length();
        int M = txt.length();
        int skip;
        int[] occurrences = new int[M]; // Array to store the occurrences
        int count = 0; // Counter for occurrences

        // Loop over the text
        for (int i = 0; i <= M - N; i += skip) {
            skip = 0;

            // Loop over the pattern from right to left
            for (int j = N - 1; j >= 0; j--) {

                // If a mismatch is found
                if (pat.charAt(j) != txt.charAt(i + j)) {
                    // Calculate the bad character skip and the good suffix
                    int badCharSkip = j - right[txt.charAt(i + j)];
                    int goodSuffixSkip = (j < N - 1) ? moveByGoodSuffix(j) : 1;
                    // Choose the maximum skip value
                    skip = Math.max(badCharSkip, goodSuffixSkip);
                    break;
                }
            }
            // If no skip was needed, the pattern was found
            if (skip == 0) {
                occurrences[count++] = i; // Store the index and increment the counter
                skip = 1; // Move the search window one position to the right
            }
        }

        // Trim the occurrences array to the actual number of occurrences
        int[] result = new int[count];
        System.arraycopy(occurrences, 0, result, 0, count);
        return result; // Return the array of indices
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Create a Scanner object to read input from the console

        // Prompt the user to enter the text in which the pattern will be searched
        System.out.print("Enter the text: ");
        String txt = scanner.nextLine(); // Read the entire line of text entered by the user

        // Prompt the user to enter the pattern to search for within the text
        System.out.print("Enter the pattern to search for: ");
        String pat = scanner.nextLine(); // Read the entire line of pattern entered by the user

        // Create an instance of the BoyerMoore class with the user-provided pattern
        BoyerMoore bm = new BoyerMoore(pat);

        // Search for the pattern in the provided text
        int[] indices = bm.search(txt);

        // Output the result of the search
        if (indices.length > 0) {
            // If the pattern is found, print the indices at which it is found
            for (int index : indices) {
                System.out.println("Pattern found at index: " + index);
            }
        } else {
            // If the pattern is not found, inform the user
            System.out.println("Pattern not found");
        }

        scanner.close(); // Close the scanner to free up resources
    }
}
