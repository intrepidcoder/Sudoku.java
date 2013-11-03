/****************************************************************************************************
 * Grid.java
 * by Daniel Moyer
 * 
 * Copywrite (C) 2012-2013
 * 
 * See Sudoku.java for description.
 ****************************************************************************************************/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

class Grid {
	private Cell[] cells;
	Puzzle puzzle;
	
	Grid(String values, Puzzle puzzle) {
		int length = values.length();
		int value;
		Grid.Cell currentCell;
		this.puzzle = puzzle;
		
		cells = new Cell[81];
		
		// Initialize cells.
		for (int i = 0; i < 81; i++) {
			cells[i] = new Cell(i, 0);
		}
		
		for (int i = 0; i < 81; i++) {
			if (i < length) {
				value = Integer.parseInt(values.charAt(i)+"");
			} else {
				value = 0;
			}
			
			currentCell = cells[i];
			
			if (value != 0) {
				puzzle.setSolvingMethod(i, 1); // 1 means cell is a given/clue.
				
				currentCell.candidates.clear();
				currentCell.candidates.add(value);
				currentCell.value = value;
				currentCell.processed = true;
				
				for (Cell v : currentCell.neighbors) {
					if (v.candidates.remove(value)) {
						v.processed = false;
					}
				}
			}
		}
		
		while (processSingles() || processHiddenSingles() || intersectionRemoval()) {}
		
	}
	
	class Cell {
		private int index = 0;
		private int value = 0;

		// // Remembers what method was used to solve this cell.
		// // 0 for unsolved, 1 for given/clue, 2 for single, 3 for hidden single, 4 for intersection, and 5 for guessing.
		// private int solvingMethod = 0;
		
		private HashSet<Cell> neighbors = new HashSet<Cell>(20);
		private HashSet<Cell> rowNeighbors = new HashSet<Cell>(8);
		private HashSet<Cell> columnNeighbors = new HashSet<Cell>(8);
		private HashSet<Cell> blockNeighbors = new HashSet<Cell>(8);
		
		private HashSet<Integer> candidates = new HashSet<Integer>(9);
		private boolean processed = true;
		
		Cell(int index, int value) {
			int[] blocks = {
				0, 0, 0, 1, 1, 1, 2, 2, 2,
				0, 0, 0, 1, 1, 1, 2, 2, 2,
				0, 0, 0, 1, 1, 1, 2, 2, 2,
				3, 3, 3, 4, 4, 4, 5, 5, 5,
				3, 3, 3, 4, 4, 4, 5, 5, 5,
				3, 3, 3, 4, 4, 4, 5, 5, 5,
				6, 6, 6, 7, 7, 7, 8, 8, 8,
				6, 6, 6, 7, 7, 7, 8, 8, 8,
				6, 6, 6, 7, 7, 7, 8, 8, 8
			};
			
			int[] blockIndeces = {0, 1, 2, 9, 10, 11, 18, 19, 20, 3, 4, 5, 12, 13, 14, 21, 22, 23, 6, 7, 8, 15, 16, 17, 24, 25, 26, 27, 28, 29, 36, 37, 38, 45, 46, 47, 30, 31, 32, 39, 40, 41, 48, 49, 50, 33, 34, 35, 42, 43, 44, 51, 52, 53, 54, 55, 56, 63, 64, 65, 72, 73, 74, 57, 58, 59, 66, 67, 68, 75, 76, 77, 60, 61, 62, 69, 70, 71, 78, 79, 80};
			
			Cell currentCell;
			int row = index / 9;
			int column = index % 9;
			int block = blocks[index];
			
			this.index = index;
			this.value = value;
			if (value == 0) {
				for (int i = 1; i <= 9; i++) {
					this.candidates.add(i);
				}
			} else {
				candidates.add(value);
			}
			
			// Set neighbors.
			
			// row
			for (int i = row * 9; i < (row + 1) * 9 && i < index; i++) {
				currentCell = cells[i];
				
				neighbors.add(currentCell);
				currentCell.neighbors.add(this);
				
				rowNeighbors.add(currentCell);
				currentCell.rowNeighbors.add(this);
			}
			
			// column
			for (int i = column; i < 81 && i < index; i += 9) {
				currentCell = cells[i];
				
				neighbors.add(currentCell);
				currentCell.neighbors.add(this);
				
				columnNeighbors.add(currentCell);
				currentCell.columnNeighbors.add(this);				
			}
			
			// block
			for (int i = block * 9; i < (block + 1) * 9; i++) {
				if (blockIndeces[i] < index) {				
					currentCell = cells[blockIndeces[i]];
					
					neighbors.add(currentCell);
					currentCell.neighbors.add(this);
					
					blockNeighbors.add(currentCell);
					currentCell.blockNeighbors.add(this);
				}
			}
			
		}
		
		ArrayList<Integer> getCandidates() {
			return new ArrayList<Integer>(new java.util.TreeSet<Integer>(candidates));
		}
		
		int getIndex() {
			return index;
		}
		
		public String toString() {
			String result = Integer.toString(this.value);
			return result;
		}
	}
	
	/**
	 * Searches for cells with only one candidate.
	 * Returns true if one or more cells were found.
	 **/
	private boolean processSingles() {
		Cell currentCell;
		boolean result = false;
		
		for (int i = 0; i < 81; i++) {
			currentCell = cells[i];
			
			if (currentCell.candidates.size() == 1 && !currentCell.processed) {
				// singlesCount++;
				result = true;
				
				puzzle.setSolvingMethod(i, 2); // 2 means cell was solved using singles method.
				
				currentCell.value = currentCell.candidates.iterator().next();
				currentCell.processed = true;
				
				for (Cell v : currentCell.neighbors) {
					if (v.candidates.remove(currentCell.value)) {
						v.processed = false;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Searches for cells that contain a candidate that appears only once in its row, column or block.
	 * Returns true if one or more cells were found.
	 **/
	private boolean processHiddenSingles() {
		Cell currentCell;
		HashSet<Integer> uniqueCandidates = new HashSet<Integer>();
		boolean result = false;
		
		for (int i = 0; i < 81; i++) {
			currentCell = cells[i];
			
			if (currentCell.value != 0) {
				continue;
			}
			
			// row
			uniqueCandidates.addAll(currentCell.candidates);
			
			for (Cell v : currentCell.rowNeighbors) {
				uniqueCandidates.removeAll(v.candidates);
			}
			
			
			if (uniqueCandidates.size() == 1) {
				puzzle.setSolvingMethod(i, 3); // 3 means cell was solved using hidden singles method.
				
				currentCell.value = uniqueCandidates.iterator().next();
				currentCell.candidates.clear();
				currentCell.candidates.add(currentCell.value);
				currentCell.processed = true;
				
				for (Cell v : currentCell.neighbors) {
					if (v.candidates.remove(currentCell.value)) {
						v.processed = false;
					}
				}
				
				// hiddenSinglesCount++;
				result = true;
				continue;
			}
			
			// column
			uniqueCandidates.addAll(currentCell.candidates);
			
			for (Cell v : currentCell.columnNeighbors) {
				uniqueCandidates.removeAll(v.candidates);
			}
			
			if (uniqueCandidates.size() == 1) {
				puzzle.setSolvingMethod(i, 3); // 3 means cell was solved using hidden singles method.
				
				currentCell.value = uniqueCandidates.iterator().next();
				currentCell.candidates.clear();
				currentCell.candidates.add(currentCell.value);
				currentCell.processed = true;
				
				for (Cell v : currentCell.neighbors) {
					if (v.candidates.remove(currentCell.value)) {
						v.processed = false;
					}
				}
				
				// hiddenSinglesCount++;
				result = true;
				continue;
			}
			
			// block
			uniqueCandidates.addAll(currentCell.candidates);
			
			for (Cell v : currentCell.blockNeighbors) {
				uniqueCandidates.removeAll(v.candidates);
			}
			
			if (uniqueCandidates.size() == 1) {
				puzzle.setSolvingMethod(i, 3); // 3 means cell was solved using hidden singles method.
				
				currentCell.value = uniqueCandidates.iterator().next();
				currentCell.candidates.clear();
				currentCell.candidates.add(currentCell.value);
				currentCell.processed = true;
				
				for (Cell v : currentCell.neighbors) {
					if (v.candidates.remove(currentCell.value)) {
						v.processed = false;
					}
				}
				
				// hiddenSinglesCount++;
				result = true;
				continue;
			}
		}
		
		return result;
	}
	
	/**
	 * Searches for numbers that must occur in a given row, column, or block. This may eliminate possibilities from intersecting rows, columns, or blocks.
	 **/
	private boolean intersectionRemoval() {
		Cell currentCell;
		HashSet<Integer> uniqueCandidates = new HashSet<Integer>();
		HashSet<Cell> intersection = new HashSet<Cell>(3);
		boolean result = false;
		int size;
		
		for (int i = 0; i < 81; i += 3) {
			currentCell = cells[i];
			
			intersection.clear();
			
			for (Cell v : currentCell.blockNeighbors) {
				if (v.value == 0 && currentCell.rowNeighbors.contains(v)) {
					intersection.add(v);
				}
			}
			
			if (intersection.size() <= 1) {
				continue;
			}
			
			for (Cell v : intersection) {
				uniqueCandidates.addAll(v.candidates);
			}
			
			if (uniqueCandidates.isEmpty()) {
				continue;
			}
			
			for (Cell v : currentCell.rowNeighbors) {
				if (! intersection.contains(v)) {
					uniqueCandidates.removeAll(v.candidates);
				}
			}
			
			if (uniqueCandidates.size() > 0 && intersection.size() >= uniqueCandidates.size()) {
				
				for (Cell v : currentCell.blockNeighbors) {
					if (v.value == 0 && ! intersection.contains(v)) {
						size = v.candidates.size();
						
						v.candidates.removeAll(uniqueCandidates);
						
						if (v.candidates.size() != size) {
							v.processed = false;
							puzzle.setSolvingMethod(v.index, 4); // 4 means that cell was solved using intersection method.
							
							// intersectionCount++;
							result = true;
						}
					} 
				}
			}
			
			uniqueCandidates.clear();
			
			for (Cell v : intersection) {
				if (v.value == 0) {
					uniqueCandidates.addAll(v.candidates);
				}
			}
			
			if (uniqueCandidates.isEmpty()) {
				continue;
			}
			
			for (Cell v : currentCell.blockNeighbors) {
				if (! intersection.contains(v)) {
					uniqueCandidates.removeAll(v.candidates);
				}
			}
			
			if (uniqueCandidates.size() > 0 && intersection.size() >= uniqueCandidates.size()) {

				for (Cell v : currentCell.rowNeighbors) {
					if (! intersection.contains(v)) {
						size = v.candidates.size();
						
						v.candidates.removeAll(uniqueCandidates);
						
						if (v.candidates.size() != size) {
							v.processed = false;
							
							puzzle.setSolvingMethod(v.index, 4); // 4 means that cell was solved using intersection method.
							// intersectionCount++;
							result = true;
						}
					}
				}
			}
		}

		for (int i = 0; i < 81; i = i % 9 == 8 ? i + 18 : i + 1) {
			currentCell = cells[i];
			
			intersection.clear();
			
			for (Cell v : currentCell.blockNeighbors) {
				if (v.value == 0 && currentCell.columnNeighbors.contains(v)) {
					intersection.add(v);
				}
			}
			
			if (intersection.size() <= 1) {
				continue;
			}
			
			for (Cell v : intersection) {
				uniqueCandidates.addAll(v.candidates);
			}
			
			if (uniqueCandidates.isEmpty()) {
				continue;
			}
			
			for (Cell v : currentCell.columnNeighbors) {
				if (! intersection.contains(v)) {
					uniqueCandidates.removeAll(v.candidates);
				}
			}
			
			if (uniqueCandidates.size() > 0 && intersection.size() >= uniqueCandidates.size()) {
				
				for (Cell v : currentCell.blockNeighbors) {
					if (v.value == 0 && ! intersection.contains(v)) {
						size = v.candidates.size();
						
						v.candidates.removeAll(uniqueCandidates);

						if (v.candidates.size() != size) {
							v.processed = false;
							
							puzzle.setSolvingMethod(v.index, 4); // 4 means that cell was solved using intersection method.
							// intersectionCount++;
							result = true;
						}
					} 
				}
			}
			
			uniqueCandidates.clear();
			
			for (Cell v : intersection) {
				if (v.value == 0) {
					uniqueCandidates.addAll(v.candidates);
				}
			}
			
			if (uniqueCandidates.isEmpty()) {
				continue;
			}
			
			for (Cell v : currentCell.blockNeighbors) {
				if (! intersection.contains(v)) {
					uniqueCandidates.removeAll(v.candidates);
				}
			}
			
			if (uniqueCandidates.size() > 0 && intersection.size() >= uniqueCandidates.size()) {
				
				for (Cell v : currentCell.columnNeighbors) {
					if (v.value == 0 && ! intersection.contains(v)) {
						size = v.candidates.size();
						
						v.candidates.removeAll(uniqueCandidates);
						
						if (v.candidates.size() != size) {
							v.processed = false;
							
							puzzle.setSolvingMethod(v.index, 4); // 4 means that cell was solved using intersection method.
							// intersectionCount++;
							result = true;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	Cell getGuessCell() {
		Cell guessCell = cells[0];
		Cell currentCell;
		
		// System.out.println(java.util.Arrays.toString(cells));
		for (int i = 1; i < 81; i++) {
			if (guessCell.value != 0) {
				guessCell = cells[i];
			} else {
				currentCell = cells[i];
				
				
				if (currentCell.value == 0 && currentCell.candidates.size() < guessCell.candidates.size()) {
					guessCell = currentCell;
				}
			}
		}
		
		return guessCell;
	}
	
	/**
	 * Returns true if there is no number repetition within a row, column, or block.
	 **/
	boolean isValid() {
		Cell currentCell;
		
		for (int i = 0; i < 81; i++) {
			currentCell = cells[i];
			
			if (currentCell.value != 0) {
				for (Cell v : currentCell.neighbors) {
					if (v.value == currentCell.value) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Returns true if the puzzle is filled. (Does not check for the vality of the solution.)
	 **/
	boolean isFilled() {
		for (int i = 0; i < 81; i++) {
			if (cells[i].value == 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public String toString() {
		String result = "";
		
		for (int i = 0; i < 81; i++) {
			result += cells[i].value+"";
		}
		
		return result;
	}
}