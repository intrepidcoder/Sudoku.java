/****************************************************************************************************
 * Puzzle.java
 * by Daniel Moyer
 * 
 * Copywrite (C) 2012-2013
 * 
 * See Sudoku.java for description.
 ****************************************************************************************************/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
 
/** 
 * Sudoku puzzle object used by Sudoku.java
 **/
public class Puzzle {
	private Grid solution;
	
	private int givensCount = 0;
	private int singlesCount = 0;
	private int hiddenSinglesCount = 0;
	private int intersectionCount = 0;
	private int guessCount = 0;
	
	// Remembers what method was used to solve each cell.
	// 0 for unsolved, 1 for given/clue, 2 for single, 3 for hidden single, 4 for intersection, and 5 for guessing.
	private int[] solvingMethod = new int[81];
	
	private boolean singleSolution = false;
	
	public Puzzle (String values) {
		Grid lastSolution;
		
		solution = solve(new Grid(values, this), true);
		
		// System.out.println("line 36:" + solution.toString());
		
		lastSolution = solve(new Grid(values, this), false);
		
		singleSolution = lastSolution.toString().equals(solution.toString());
		
		// Update solving method variables.
		for (int i = 0; i < 81; i++) {
			switch(solvingMethod[i]) {
				case 1: givensCount++;
				break;
				
				case 2: singlesCount++;
				break;
				
				case 3: hiddenSinglesCount++;
				break;
				
				case 4: intersectionCount++;
				break;
				
				case 5: guessCount++;
				break;
			}
		}
	}
	
	/** 
	 * Returns the solution that occurs first in lexicographical order to the puzzle if firstSolution is true.
	 * Otherwise returns the solution that occurs last in lexicographical order.
	 **/
	private Grid solve(Grid givens, boolean firstSolution) {
		Stack<String> stack = new Stack<String>();
		int maxStackSize = 0;
		// Stack<String> stack = new Stack<char []>();
		
		Grid currentGrid;
		Grid.Cell guessCell;
		ArrayList<Integer> guessCellCandidates; // = new ArrayList<Integer>();
		char[] values;
		int length;
		
		stack.push(givens.toString());
		
		// stack.push(givens.values);
		
		
		while(!stack.isEmpty()) {
			// maxStackSize = Math.max(maxStackSize, stack.size());
			currentGrid = new Grid(stack.pop(), this);
			values = currentGrid.toString().toCharArray();
			
			if (currentGrid.isFilled()) {
				// System.out.println("line 581:" + currentGrid);
				// System.out.println(maxStackSize);
				return new Grid(new String(values), this);
			}
			
			if (currentGrid.isValid()) {
				
				guessCell = currentGrid.getGuessCell();
				guessCellCandidates = guessCell.getCandidates();
				length = guessCellCandidates.size();
				
				// System.out.println(solvingMethod[guessCell.getIndex()]);
				// System.out.println(firstSolution);
				// System.out.println(guessCellCandidates);
				// System.out.println(guessCell);
				
				setSolvingMethod(guessCell.getIndex(), 5); // 5 means that cell was solved by guessing.
				
				if (firstSolution) {
					for (int i = length - 1; i >= 0; i--) {
						values[guessCell.getIndex()] = (char)('0' + guessCellCandidates.get(i));
						stack.push(new String(values));
					
					}
				} else {
					for (int i = 0; i < length; i++) {
						values[guessCell.getIndex()] = (char)('0' + guessCellCandidates.get(i));
						stack.push(new String(values));
					}
				}
				
			}
		}
		
		return givens;
	}
	
	void setSolvingMethod(int index, int value) {
		solvingMethod[index] = solvingMethod[index] == 0 ? value : solvingMethod[index];
	}
	
	public int getGivensCount() {
		return givensCount;
	}
	
	public int getSinglesCount() {
		return singlesCount;
	}
	
	public int getHiddenSinglesCount() {
		return hiddenSinglesCount;
	}
	
	public int getIntersectionCount() {
		return intersectionCount;
	}
	
	public int getGuessCount() {
		return guessCount;
	}
	
	public boolean hasSolution() {
		boolean result = solution.isFilled() && solution.isValid();
		
		return result;
	}

	public boolean hasSingleSolution() {
		return singleSolution;
	}

	public String toString() {
		String result = solution.toString();
		
		return result;
	}
}