/****************************************************************************************************
 * Sudoku.java
 * by Daniel Moyer
 * 
 * Copyright (C) 2012-2013
 * 
 * Description:
 * Solves Sudoku puzzles.
 * If there is more than one solution, the program outputs one valid solution.
 * Outputs whether a puzzle has no solution, a single solution, or multiple solutions.
 * Outputs the number of cells that were solved with each technique if the puzzle has a solution.
 * 
 * Techniques:
 * Given - A cell that had a known (inputted) value.
 * Single - A cell that was solved because it only has one candidate.
 * Hidden single - A cell that was solved because it had a candidate that appeared only once in its row, column or 3x3 block.
 * Intersection - A cell that was solved because one or more of its candidates were eliminated because they had to occur in another intersecting row, column, or block.
 * Guess - A cell that was solved by guessing.
 * 
 * If the puzzle cannot be solved using singles, hidden singles, or intersections, the program will make guesses until it reaches a solution.
 *
 * Takes input from console, ignoring all characters except the digits 0-9.
 * Once 81 digits have been imputed, the solution will be outputted.
 * 
 * Test cases:
 * 100920000524010000000000070050008102000000000402700090060000000000030945000071006 This sudoku puzzle has a single solution.
 * 530070000600195000098000060800060003400803001700020006060000280000419005000080079 This sudoku puzzle has a single solution.
 * 700010000010052000524600090907003500000020100030400000090500300002000800040080060 This sudoku puzzle has a single solution.
 * 380000000000400785009020300060090000800302009000040070001070500495006000000000092 This sudoku puzzle has a single solution.
 * 000003017015009008060000000100007000009000200000500004000000020500600340340200000 This sudoku puzzle has a single solution.
 * 300200000000107000706030500070009080900020004010800050009040301000702000000008006 This sudoku puzzle has a single solution.
 * 000000000000000000000000000000000000000000000000000000000000000000000000000000000 This sudoku puzzle has multiple solutions.
 * 120400300300010050006000100700090000040603000003002000500080700007000005000000098 This sudoku puzzle has a single solution.
 * 000000000000003085001020000000507000004000100090000000500000073002010000000040009 This sudoku puzzle has a single solution.
 * 000000039000001005003050800008090006070002000100400000009080050020000600400700000 This sudoku puzzle has a single solution.
 * 000345000300000001089000340050201090001603500090408070036000950500000008000567000 This sudoku puzzle has a single solution.
 * 007000600640000051800050007004803900000000000009107500400020006280000075005000800 This sudoku puzzle has a single solution.
 * 007000600640000051800050007004803900000000000009007500400020006280000075005000800 This sudoku puzzle has multiple solutions.
 * 007000600640000051800050007004803900001000000009107500400020006280000075005000800 This sudoku puzzle has no valid solution.
 ****************************************************************************************************/

import java.io.*;

public class Sudoku {
	public static void main(String[] args) throws IOException {
		String inputText = "";
		String solution;
		Puzzle p;
		int row = 0;
		
		// Create input stream from console.
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.printf("Enter a sudoku puzzle using zeros for empty cells.%nNon-numeric characters are ignored.%n");
		
		// Read input until there are values for empty cells.
		while (inputText.length() < 81) {
			inputText += input.readLine().replaceAll("\\D", "");
		}
		
		p = new Puzzle(inputText);
		
		if (p.hasSolution()) {
			System.out.printf("%nThe solution is:%n");
			
			solution = p.toString();
			
			// Print solution in a readable manner.
			for (int i = 0; i < 9; i++) {
				row = i * 9;
				System.out.printf("%s%s%s %s%s%s %s%s%s%n", solution.charAt(row), solution.charAt(row + 1), solution.charAt(row + 2), solution.charAt(row + 3), solution.charAt(row + 4), solution.charAt(row + 5), solution.charAt(row + 6), solution.charAt(row + 7), solution.charAt(row + 8));
				
				if (i == 2 || i == 5) {
					System.out.printf("%n");
				}
			}
			
			// Print solving techniques.
			System.out.printf("%nSolving techniques used:%n");
			System.out.printf("%-15s %2d%n", "Givens:", p.getGivensCount());
			System.out.printf("%-15s %2d%n", "Singles:", p.getSinglesCount());
			System.out.printf("%-15s %2d%n", "Hidden singles:", p.getHiddenSinglesCount());
			System.out.printf("%-15s %2d%n", "Intersections:", p.getIntersectionCount());
			System.out.printf("%-15s %2d%n", "Guesses:", p.getGuessCount());
			
			
			System.out.printf("This sudoku puzzle has %s.%n", p.hasSingleSolution() ? "a single solution" : "multiple solutions");
		} else {
			System.out.printf("%nThis sudoku puzzle has no valid solution.%n");
		}
	}
}
