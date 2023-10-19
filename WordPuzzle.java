package il.ac.tau.cs.sw1.ex4;


import java.util.Random;
import java.util.Scanner;


public class WordPuzzle {
	public static final char HIDDEN_CHAR = '_';
	
	/*
	 * @pre: template is legal for word
	 */
	public static char[] createPuzzleFromTemplate(String word, boolean[] template) { // Q - 1
		int wordLength=word.length();
		char[] charPuzzle = new char[wordLength];

		for (int i=0; i<wordLength;i++) {
			if (template[i])
				charPuzzle[i] = HIDDEN_CHAR;
			else
				charPuzzle[i] = word.charAt(i);
		}
		return charPuzzle;
	}

	public static boolean checkLegalTemplate(String word, boolean[] template) { // Q - 2
		int wordLength = word.length();
		int templateLength = template.length;

		if (wordLength != templateLength)
			return false;

		int cntTrue=0;
		int cntFalse=0;

		for (int i=0; i<templateLength; i++) {
			if (template[i])
				cntTrue++;
			else
				cntFalse++;
		}
		if ( (cntFalse==0) || (cntTrue==0) )
			return false;

		// condition b, based on counting//

		char[] charArrayPuzzle = createPuzzleFromTemplate(word, template);
		String strPuzzle = new String(charArrayPuzzle);

		for (int i=0; i<wordLength; i++) {
			int wordCnt=0;
			int strPuzzleCnt=0;

			for (int j=0; j<wordLength; j++) {
				if (word.charAt(j)==word.charAt(i))
					wordCnt++;
			}

			if (wordCnt>=2) {
				for (int k=0; k<strPuzzle.length(); k++) {
					if (strPuzzle.charAt(k)==word.charAt(i))
						strPuzzleCnt++;
				}
				if ((strPuzzleCnt!=wordCnt) && (strPuzzleCnt!=0))
					return false;
			}
		}

		return true;
	}
	
	/*
	 * @pre: 0 < k < word.lenght(), word.length() <= 10
	 */
	public static int choose(int n, int k){
		if(n < k)
			return 0;
		if(k == 0 || k == n)
			return 1;
		return choose(n-1,k-1)+choose(n-1,k);
	}

	public static boolean[][] getAllLegalTemplates(String word, int k){  // Q - 3
		int n = word.length();
		int choose= choose(n,k);
		boolean[] tmpTemplate= new boolean[n];
		int templateCnt=0;
		boolean[][] possibles= new boolean[choose][n];
		int oneCnt=0;
		int maxNum= (int) (Math.pow(2,n)-1);
		boolean legalTemplate;


		for (int i=1; i<maxNum; i++) {
			String binNumStr= Integer.toBinaryString(i);

			while (binNumStr.length() < n) { //filling with zeroes//
				binNumStr = "0" +binNumStr;
			}

			for (int l=0; l<n; l++) {
				if (binNumStr.charAt(l)=='1')
					oneCnt++;
			}

			if (oneCnt==k) {
				for (int j=0; j<n; j++) { //generating a template//
					if (binNumStr.charAt(j)=='1') {
						tmpTemplate[j] = true;
					}
					else {
						tmpTemplate[j] = false;
					}
				}
				legalTemplate = checkLegalTemplate(word,tmpTemplate);
				if (legalTemplate) {
					for (int m=0 ; m<n ;m++) { //updating possibles//
						possibles[templateCnt][m]= tmpTemplate[m];
					}
					templateCnt++;
				}
			}
			oneCnt=0;
		}

		if (templateCnt==0) {
			boolean[][] emptyArr = {};
			return emptyArr;
		}

		else { //serving a squeezed array//
			boolean[][] finalArray = new boolean[templateCnt][n];
			for (int p = 0; p < templateCnt; p++) {
				for ( int q=0 ; q<n; q++) {
					finalArray[p][q]=possibles[p][q];
				}
			}
			return finalArray;
		}
	}
	
	
	/*
	 * @pre: puzzle is a legal puzzle constructed from word, guess is in [a...z]
	 */
	public static int applyGuess(char guess, String word, char[] puzzle) { // Q - 4

		for (int i=0; i<puzzle.length; i++) { //dumby try//
			if (puzzle[i]==guess)
				return 0;
		}

		if (word.contains(Character.toString(guess))) {
			int cnt=0;
			for (int j=0; word.length()>j; j++) {
				if (word.charAt(j)==guess) {
					puzzle[j]=guess;
					cnt++;
				}
			}
			return cnt;
		}

		else
			return 0;
	}
	

	/*
	 * @pre: puzzle is a legal puzzle constructed from word
	 * @pre: puzzle contains at least one hidden character. 
	 * @pre: there are at least 2 letters that don't appear in word, and the user didn't guess
	 */
	public static char[] getHint(String word, char[] puzzle, boolean[] already_guessed) { // Q - 5
		char[] arrFinal= new char[2];
		String abcStr= "abcdefghijklmnopqrstuvwxyz";
		String puzzleStr= new String(puzzle);
		String wrongStr= "";
		String correctStr= "";
		char chTmp;

		//creating a string of wrong chars//
		for (int i=0; i<26; i++) {
			chTmp = abcStr.charAt(i);
			if (!already_guessed[i]) {
				if (! (word.contains(Character.toString(chTmp)))) {
					wrongStr+= chTmp;
				}
			}
		}

		//creating a string of correct chars//
		for (int m=0; m<26; m++) {
			chTmp= abcStr.charAt(m);
			if (word.contains(Character.toString(chTmp))) {
				if (!puzzleStr.contains(Character.toString(chTmp))) {
					correctStr+= chTmp;
				}
			}
		}

		Random rand= new Random();
		int correctRand= rand.nextInt(correctStr.length());
		char randCChar= correctStr.charAt(correctRand);
		int wrongRand= rand.nextInt(wrongStr.length());
		char randWChar= wrongStr.charAt(wrongRand);

		if (randCChar>randWChar) {
			arrFinal[0] = randWChar;
			arrFinal[1] = randCChar;
		}

		else {
			arrFinal[0]=randCChar;
			arrFinal[1]=randWChar;
		}
		return arrFinal;
	}

	public static boolean checkIfSolved(char[] puzzle) {
		for (int i=0; i<puzzle.length; i++) {
			if (puzzle[i]==HIDDEN_CHAR)
				return false;
		}
		return true;
	}



	public static char[] mainTemplateSettings(String word, Scanner inputScanner) { // Q - 6
		//preparing tools//
		char[] randPuzzle = new char[word.length()];
		char[] userPuzzle= new char[word.length()];
		boolean boolPath1=false;

		printSettingsMessage();
		printSelectTemplate();
		//b//
		do {

			String option= inputScanner.next();

			if (option.equals("1")) {     //iii//
				printSelectNumberOfHiddenChars();
				int numOfHiddenChars= inputScanner.nextInt(); //2//
				boolean[][] optionalTemplates= getAllLegalTemplates(word,numOfHiddenChars);

				if (optionalTemplates.length==0){ //4//
					printWrongTemplateParameters();
					printSelectTemplate();
					continue;
				}
				else {  //3//
					Random rand= new Random();
					int numRand= rand.nextInt(optionalTemplates.length);
					boolean[] randTemplate = optionalTemplates[numRand];
					randPuzzle = createPuzzleFromTemplate(word,randTemplate);
					boolPath1=true;
					break;
				}
			}

			else {  //iv//
				boolean[] userTemplate = new boolean[word.length()];
				String tmpStr;
				char tmpCh;

				printEnterPuzzleTemplate(); //1//

				//2//
				String userInput = inputScanner.next(); //user writing//

				if (userInput.length() != (2*word.length()-1)) {
					printWrongTemplateParameters();
					printSelectTemplate();
					continue;
				}

				if ((!userInput.contains("X")) || (!userInput.contains("_"))) {
					printWrongTemplateParameters();
					printSelectTemplate();
					continue;
				}

				Scanner inputForTemplate = new Scanner(userInput).useDelimiter(","); //detached input//
				for (int i=0; i<word.length();i++) {
					tmpStr = inputForTemplate.next();
					tmpCh = tmpStr.charAt(0);
					if (tmpCh == HIDDEN_CHAR) {
						userTemplate[i]=true;
					}
				}

				if (checkLegalTemplate(word,userTemplate)) { //3//
					userPuzzle= createPuzzleFromTemplate(word,userTemplate);
					break;
				}

				else {  //4//
					printWrongTemplateParameters();
					printSelectTemplate();
					continue;
				}
			}

		} while (inputScanner.hasNext());

		if (boolPath1)
			return randPuzzle;
		else
			return userPuzzle;
	}

	public static void mainGame(String word, char[] puzzle, Scanner inputScanner){ // Q - 7
		//preparing tools//
		boolean[] already_guessed= new boolean[26];
		int guessedCharIndex;
		int revealNum;
		boolean boolSolved=false;

		//a//
		printGameStageMessage();

		//b//
		int cntHidden=0;
		int attemptsNum=3;
		for (int i=0; i<word.length();i++) {
			if (puzzle[i]==HIDDEN_CHAR)
				cntHidden++;
		}
		attemptsNum += cntHidden;

		//c//
		do {
			printPuzzle(puzzle);
			printEnterYourGuessMessage();

			//d//
			String userMessage = inputScanner.next();

			if (userMessage.equals("H")) { //i//
				char[] hintArr= getHint(word,puzzle,already_guessed);
				printHint(hintArr);
				continue;
			}

			else { //ii//
				revealNum = applyGuess(userMessage.charAt(0),word,puzzle);
				guessedCharIndex = (userMessage.charAt(0))-97;
				already_guessed[guessedCharIndex]=true;

				if (revealNum>0) { //1//
					if (checkIfSolved(puzzle)) { //1.1//
						boolSolved=true;
						break;
					}
					else {  //1.2//
						attemptsNum--;
						printCorrectGuess(attemptsNum);
						continue;
					}
				}

				else { //2//
					attemptsNum--;
					printWrongGuess(attemptsNum);
					continue;
				}
			}
		} while (attemptsNum>0);

		if (boolSolved)
			printWinMessage();
		else
			printGameOver();
	}
				
				


/*************************************************************/
/********************* Don't change this ********************/
/*************************************************************/

	public static void main(String[] args) throws Exception { 
		if (args.length < 1){
			throw new Exception("You must specify one argument to this program");
		}
		String wordForPuzzle = args[0].toLowerCase();
		if (wordForPuzzle.length() > 10){
			throw new Exception("The word should not contain more than 10 characters");
		}
		Scanner inputScanner = new Scanner(System.in);
		char[] puzzle = mainTemplateSettings(wordForPuzzle, inputScanner);
		mainGame(wordForPuzzle, puzzle, inputScanner);
		inputScanner.close();
	}


	public static void printSettingsMessage() {
		System.out.println("--- Settings stage ---");
	}

	public static void printEnterWord() {
		System.out.println("Enter word:");
	}
	
	public static void printSelectNumberOfHiddenChars(){
		System.out.println("Enter number of hidden characters:");
	}
	public static void printSelectTemplate() {
		System.out.println("Choose a (1) random or (2) manual template:");
	}
	
	public static void printWrongTemplateParameters() {
		System.out.println("Cannot generate puzzle, try again.");
	}
	
	public static void printEnterPuzzleTemplate() {
		System.out.println("Enter your puzzle template:");
	}


	public static void printPuzzle(char[] puzzle) {
		System.out.println(puzzle);
	}


	public static void printGameStageMessage() {
		System.out.println("--- Game stage ---");
	}

	public static void printEnterYourGuessMessage() {
		System.out.println("Enter your guess:");
	}

	public static void printHint(char[] hist){
		System.out.println(String.format("Here's a hint for you: choose either %s or %s.", hist[0] ,hist[1]));

	}
	public static void printCorrectGuess(int attemptsNum) {
		System.out.println("Correct Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWrongGuess(int attemptsNum) {
		System.out.println("Wrong Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWinMessage() {
		System.out.println("Congratulations! You solved the puzzle!");
	}

	public static void printGameOver() {
		System.out.println("Game over!");
	}

}
