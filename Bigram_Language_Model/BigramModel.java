package il.ac.tau.cs.sw1.ex5;

import java.io.*;
import java.util.Arrays;
import java.util.Locale;

public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14500;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;

	String[] mVocabulary;
	int[][] mBigramCounts;

	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException {
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);

	}


	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException { // Q 1
		File textF = new File(fileName);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(textF));
		String line;

		int vocIndex = 0;
		char currentCh;
		Boolean SomeNumAdded = false;
		Boolean boolNull = false;
		Boolean boolLetter = false;
		String[] vocabulary = new String[MAX_VOCABULARY_SIZE];

		while ((line = bufferedReader.readLine()) != null) {
			if (vocIndex == MAX_VOCABULARY_SIZE)
				continue;
			else if (line.equals(""))
				continue;

			else {
				String[] lineArr = line.split(" ");

				for (String word : lineArr) {
					StringBuilder wordBuild = new StringBuilder();

					for (int i = 0; i < word.length(); i++) {
						currentCh = word.charAt(i);
						if (isENGChar(currentCh)) {
							boolLetter = true;
							wordBuild.append((Character.toString(currentCh)).toLowerCase());
						} else if (!Character.isDigit(currentCh)) {
							boolNull = true;
							wordBuild.append(Character.toString(currentCh));
						} else {
							wordBuild.append(Character.toString(currentCh));
						}
					}

					if (boolLetter && vocIndex < MAX_VOCABULARY_SIZE) {
						if (notInVocabulary(wordBuild.toString(), vocabulary, vocIndex)) {
							vocabulary[vocIndex] = wordBuild.toString();
							vocIndex++;
						}
					} else if (boolNull || (wordBuild.toString().equals(""))) {
						continue;
					} else {
						if (SomeNumAdded) {
							continue;
						} else {
							if (vocIndex < MAX_VOCABULARY_SIZE) {
								vocabulary[vocIndex] = SOME_NUM;
								vocIndex++;
								SomeNumAdded = true;
							}
						}
					}

					boolNull = false;
					boolLetter = false;

				}
			}
		}
		bufferedReader.close();

		if (vocIndex == MAX_VOCABULARY_SIZE) {
			return vocabulary;
		} else {
			String[] vocabularyAdjs = Arrays.copyOf(vocabulary, vocIndex);
			return vocabularyAdjs;
		}
	}


	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Q - 2
		int[][] countsArray = new int[vocabulary.length][vocabulary.length];
		int i = 0;
		int j = 0;
		int occurCnt = 0;
		String word1;
		String word2;

		//index i is for word1, index j is for word2//
		//while loop word1, for loop word2. each can be exchanged...//
		while (i < vocabulary.length) {
			word1 = vocabulary[i];

			File textF = new File(fileName);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(textF));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] lineArr = line.split(" ");

				for (int m = 0; m < (lineArr.length - 1); m++) {
					if ((lineArr[m].equalsIgnoreCase(word1))) {
						word2 = lineArr[m + 1];
						for (int k = 0; k < vocabulary.length; k++) {
							if (vocabulary[k].equalsIgnoreCase(word2)) {
								j = k;
								countsArray[i][j]++;
								break;
							}
						}
					}
				} //finished to read the line//
			} //finished to read the text for word1//

			bufferedReader.close();
			i++;
		}
		return countsArray;
	}


	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException { // Q-3
		//mVocabulary//
		File toFileVoc = new File(fileName + VOC_FILE_SUFFIX);
		BufferedWriter bufferedWriterVoc = new BufferedWriter(new FileWriter(toFileVoc));
		bufferedWriterVoc.write(mVocabulary.length + " words" + "\n");
		for (int i = 0; i < mVocabulary.length; i++) {
			bufferedWriterVoc.write(i + "," + mVocabulary[i] + "\n");
		}
		bufferedWriterVoc.close();

		//mBigramCounts//
		File toFileCounts = new File(fileName + COUNTS_FILE_SUFFIX);
		BufferedWriter bufferedWriterCounts = new BufferedWriter(new FileWriter(toFileCounts));
		for (int i = 0; i < mBigramCounts.length; i++) {
			for (int j = 0; j < (mBigramCounts[i].length); j++) {
				if (mBigramCounts[i][j] != 0)
					bufferedWriterCounts.write(i + "," + j + ":" + mBigramCounts[i][j] + "\n");
			}
		}
		bufferedWriterCounts.close();
	}


	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException { // Q - 4
		//loading to mVoc//
		File fromFileVoc = new File(fileName + VOC_FILE_SUFFIX);
		BufferedReader bufferedReaderVoc = new BufferedReader(new FileReader(fromFileVoc));
		String lineVoc;
		lineVoc = bufferedReaderVoc.readLine();
		int vocLength = Character.getNumericValue(lineVoc.charAt(0));
		String[] vocabulary = new String[vocLength];
		int vocIndex = 0; 	//for index in vocabulary//

		while ((lineVoc= bufferedReaderVoc.readLine()) != null) {
			String[] lineArr = lineVoc.split(",");
			mVocabulary[vocIndex] = lineArr[1];
			vocIndex++;
		}
		bufferedReaderVoc.close();

		//loading to mCounts//
		int[][] counts = new int[mVocabulary.length][mVocabulary.length];
		File fromFileCounts = new File(fileName + COUNTS_FILE_SUFFIX);
		BufferedReader bufferedReaderCounts = new BufferedReader(new FileReader(fromFileCounts));
		String lineCounts;
		int i;
		int j;
		int occurNum;

		while ((lineCounts = bufferedReaderCounts.readLine()) != null) {
			String[] lineCountsArr = lineCounts.split("");
			i = Integer.parseInt(lineCountsArr[0]);
			j = Integer.parseInt(lineCountsArr[2]);
			occurNum = Integer.parseInt(lineCountsArr[4]);
			mBigramCounts[i][j] = occurNum;
		}
		bufferedReaderCounts.close();
	}


	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word) {  // Q - 5
		for (int i=0; i<mVocabulary.length; i++) {
			if (mVocabulary[i].equals(word))
				return i;
		}
		return ELEMENT_NOT_FOUND;
	}


	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2) { //  Q - 6
		int word1Index = -1;
		int word2Index = -1;

		for (int i=0; i<mVocabulary.length; i++) {
			if (mVocabulary[i].equals(word1)) {
				word1Index = i;
				break;
			}
		}
		for (int j=0; j<mVocabulary.length; j++) {
			if (mVocabulary[j].equals(word2)) {
				word2Index = j;
				break;
			}
		}

		if ((word1Index==-1) || (word2Index==-1)) {
			return 0;
		}
		else {
			int occurNum = mBigramCounts[word1Index][word2Index];
			return occurNum;
		}
	}


	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word) { //  Q - 7
		//extracting the location of the word in mBigramCounts//
		//we will run on inner array of the word//
		//return null if the inner array is fulled of zeroes entries//

		int wordIndex = -1;
		String wordCand = null;
		int wordCandCnt = 0;

		for (int i=0; i<mVocabulary.length; i++) {
			if (mVocabulary[i].equals(word)) {
				wordIndex = i;
				break;
			}
		}
		if (wordIndex == -1) //word is not in vocabulary//
			return null;

		for (int j=0; j<mVocabulary.length; j++) {
			if (mBigramCounts[wordIndex][j] > wordCandCnt) {
				wordCand = mVocabulary[j];
				wordCandCnt = mBigramCounts[wordIndex][j];
			}
		}
		return wordCand;
	}


	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence) {  //  Q - 8
		if (sentence.length()==0)
			return true;

		String[] sentenceArr = sentence.split(" ");
		boolean wordExist = false;
		String tmpWord;

		//case 1, word is not legal//
		for (int i=0; i<sentenceArr.length; i++) {
			tmpWord = sentenceArr[i];

			for (int k=0 ; k<mVocabulary.length; k++) { //looping in mVoc//
				if (mVocabulary[k].equalsIgnoreCase(tmpWord)) {
					wordExist = true;
					break;
				}
			}

			if (!wordExist)
				return false;
			else
				wordExist = false;

		}

		if (sentenceArr.length==1)
			return true;

		//case 2, word2 can't be written after word1 (according to BigramModel)//
		int word1Index=-1;
		int word2Index=-1;
		String word1;
		String word2;

		for (int i=0 ; i<(sentenceArr.length-1); i++) {
			 word1= sentenceArr[i];
			 word2= sentenceArr[i+1];

			for (int k=0; k<mVocabulary.length; k++) { //extracting the indices of word1 and word2//
				if (mVocabulary[k].equalsIgnoreCase(word1)) {
					word1Index = k;
				}
				if (mVocabulary[k].equalsIgnoreCase(word2)) {
					word2Index = k;
				}
				if ((word1Index!=-1) && (word2Index!=-1)) {
					break;
				}
			}

			if (mBigramCounts[word1Index][word2Index] < 1) {
				return false;
			}

			word1Index=-1;
			word2Index=-1;
		}

		return true;
	}


	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calcluates CosineSim
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2) { //  Q - 9
		//not dividing by zero//
		boolean arr1AllZero = true;
		boolean arr2AllZero = true;
		for (int num:arr1) {
			if (num>0) {
				arr1AllZero = false;
				break;
			}
		}
		for (int num:arr2) {
			if (num>0) {
				arr2AllZero = false;
				break;
			}
		}
		if (arr1AllZero || arr2AllZero)
			return -1;
		//////////////////
		int sum1=0;
		int sum2=0;
		int sum3=0;
		int tmpNum1;
		int tmpNum2;

		for (int i=0; i<arr1.length; i++) { //calculating sum1//
			tmpNum1 = arr1[i];
			tmpNum2 = arr2[i];
			sum1+= (tmpNum1*tmpNum2);
		}

		for (int i=0; i<arr1.length; i++) { //calculating sum2//
			tmpNum1 = arr1[i];
			tmpNum2 = arr1[i];
			sum2+= (tmpNum1*tmpNum2);
		}

		for (int i=0; i<arr2.length; i++) { //calculating sum3//
			tmpNum1 = arr2[i];
			tmpNum2 = arr2[i];
			sum3+= (tmpNum1*tmpNum2);
		}

		double returnValue = (sum1/((Math.sqrt(sum2))*(Math.sqrt(sum3))));
		return returnValue;
	}


	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized),
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word) { //  Q - 10

		//-->
		//CHECK LATER: WHAT IF THE WORD IS NOT IN VOCABULARY?//
		//<--

		if ((mVocabulary.length==1) && (mVocabulary[0].equals(word))) {
			return word;
		}

		int[] wordIntArr = new int[mVocabulary.length];
		for (int i=0; i<mVocabulary.length; i++) {
			if (mVocabulary[i].equals(word)) {
				wordIntArr = mBigramCounts[i];
				break;
			}
		}

		int[] wordCandIntArr = new int[mVocabulary.length]; //not really necessary to keep this//
		int[] wordCandIntArrTmp = new int[mVocabulary.length];
		String wordCand="";
		String wordCandTmp;
		double wordCandSimilarCnt=0.0;
		double wordCandSimilarCntTmp=0.0;

		for (int i=0; i< mBigramCounts.length; i++) {
			if (mVocabulary[i].equals(word)) {
				continue;
			}

			wordCandTmp = mVocabulary[i];
			wordCandIntArrTmp = mBigramCounts[i];
			wordCandSimilarCntTmp = calcCosineSim(wordIntArr,wordCandIntArrTmp);
			if (wordCandSimilarCntTmp > wordCandSimilarCnt) {
				wordCand = wordCandTmp;
				wordCandSimilarCnt = wordCandSimilarCntTmp;
				wordCandIntArr = wordCandIntArrTmp;
			}
		}

		return wordCand;
	}



	/*
	Additional methods
	 */

	private boolean isENGChar(char ch) {
		String abcStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < 52; i++) {
			if (ch == abcStr.charAt(i))
				return true;
		}
		return false;
	}

	private boolean notInVocabulary(String word, String[] vocabulary, int vocIndex) {
		for (int i = 0; i < vocIndex; i++) {
			if (vocabulary[i].equals(word)) {
				return false;
			}
		}
		return true;
	}

}