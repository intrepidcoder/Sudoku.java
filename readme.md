#Sudoku.java

##Description:
* Solves Sudoku puzzles.
* Takes input from console, ignoring all characters except the digits 0-9.
* Once 81 digits have been imputed, the solution will be outputted.

##Usage:
* To compile: javac Sudoku.java
* To run:     java Sudoku
* To run with file input: java Sudoku < file.txt

##Output description:
* If there is more than one solution, the program outputs one valid solution.
* Outputs whether a puzzle has no solution, a single solution, or multiple solutions.
* Outputs the number of cells that were solved with each technique if the puzzle has a solution.

###Techniques:
* Given - A cell that was inputted.
* Single - A cell that was solved because it only has one candidate.
* Hidden single - A cell that was solved because it had a candidate that appeared only once in its row, column or 3x3 block.
* Intersection - A cell that was solved because one or more of its candidates were eliminated because they had to occur in another intersecting row, column, or block.
* Guess - A cell that was solved by guessing.
 
If the puzzle cannot be solved using singles, hidden singles, or intersections, the program will make guesses until it reaches a solution.

See sampleinput.txt and sampleoutput.txt for example input and output.