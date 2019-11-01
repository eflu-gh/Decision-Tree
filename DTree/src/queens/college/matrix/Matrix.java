/**
 * Class: Driver
 * @author Edgar Lizarraga
 * @date 05/11/2016
 */
package queens.college.matrix;

import java.util.ArrayList;
import java.util.HashMap;

public class Matrix {
	private int[][] matrix;
	private int[][] matrixCopy;
	private int[] type;
	private int[] typeCopy;
	private int[][] matrixKeys;
	private int[][] matrixKeysCopy;
	private int colType;
	private int totalCount;
	private int totalCountCopy;
	HashMap<Integer, String> labels;

	public Matrix(int[][] mat, int[] t, int[][] matrixK, int colT, int totCount, HashMap<Integer, String> l) {
		matrix = mat;
		type = t;
		matrixKeys = matrixK;
		setMatrixCopy(mat);
		setKeysCopy(matrixK);
		setTypeCopy(t);
		colType = colT;
		totalCount = totCount;
		labels = l;
	}

	public int getQuestion() {
		// TODO Auto-generated method stub
		double freq = 0;
		int colKey = 0; // References to the number row in matrixKeys.
		double prob = 0;
		double probQ;
		double subTotal = 0;
		double total = 0;
		double result = 0;
		int colToAsk = 0;
		ArrayList<Double> entropy = new ArrayList<>();
		ArrayList<Integer> colEntropy = new ArrayList<>();

		try {
			for (int i = 0; i < this.matrixKeys.length; i++) {
				for (int j = 0; j < this.matrixKeys[0].length; j++) {
					colKey = i; // Each row of matrixKeys represent a column in the initial matrix.

					if (this.matrixKeys[i][j] > 0) // In case it would be more options in one column and no in others.
					{
						probQ = calculateProbabilityPerQuestion(this.matrixKeys[i][j], colKey);
						for (int k = 0; k < type.length; k++) {
							freq = calculateFrequency(this.matrixKeys[i][j], type[k], colKey);
							prob = calculateProbability(this.matrixKeys[i][j], colKey);
							if (freq > 0.0 && prob > 0.0) {
								result = (freq / prob) * (Math.log10(prob / freq) / Math.log10(2));
								subTotal = result + subTotal;
							}
							result = 0;
						}
						System.out.println ("I (Q -> type): " + subTotal);
						subTotal = subTotal * probQ;
						
						total = subTotal + total;
						System.out.println("E (I (Q -> type)): " + labels.get(this.matrixKeys[i][j])
								+  " " + total);

						subTotal = 0;
					}
				}
				System.out.println("------");
				if (this.matrixKeys[i][0] > 0) {
					entropy.add(total); // Storing the minimun entropies.
					colEntropy.add(i);
				}
				total = 0;
			}
		} catch (Exception e) {
			System.out.println("Something happened... review your initial values ");
		}
		colToAsk = findMinimunEntropy(entropy, colEntropy);

		return colToAsk;
	}

	public int findMinimunEntropy(ArrayList<Double> entropy, ArrayList<Integer> colEntropy) {
		double minValue = entropy.get(0);
		int colToAsk = colEntropy.get(0);
		for (int i = 1; i < entropy.size(); i++) {
			if (entropy.get(i) < minValue) {
				minValue = entropy.get(i);
				colToAsk = colEntropy.get(i);
			}
		}
		return colToAsk;
	}

	public double calculateProbabilityPerQuestion(int nKey, int nColKey) {
		double probability = 0;
		double sumProb = 0;
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i][nColKey] == nKey) {
				probability = matrix[i][colType + 1];
				sumProb = probability + sumProb;
			}
		}
		return sumProb / totalCount;
	}

	public int calculateFrequency(int nkey, int ntype, int ncolKey) {
		int a, b = 0;
		int frequency = 0;
		int sumFrequency = 0;
		for (int i = 0; i < matrix.length; i++) {
			a = matrix[i][ncolKey];
			b = matrix[i][colType];
			if (ntype == b && nkey == a) {
				frequency = matrix[i][colType + 1];// Getting the value of the count or frequency.
				sumFrequency = frequency + sumFrequency;
			}
		}
		return sumFrequency;
	}

	public double calculateProbability(int nKey, int nColKey) {
		double probability = 0;
		double sumProb = 0;

		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i][nColKey] == nKey) {
				probability = matrix[i][colType + 1];
				sumProb = probability + sumProb;
			}
		}
		return sumProb;
	}

	public HashMap<Integer, String> getFeaturesOfQuestion(int colToAsk) {
		HashMap<Integer, String> features = new HashMap<>();
		for (int i = 0; i < this.matrix.length; i++) {
			features.put(this.matrix[i][colToAsk], this.labels.get(this.matrix[i][colToAsk]));
		}
		return features;
	}

	public void updateMatrixKeys(ArrayList<Integer> targetColumns) {
		int row;
		for (int index = 0; index < targetColumns.size(); index++) {
			row = targetColumns.get(index);
			for (int k = 0; k < this.matrixKeys.length; k++) {
				if (k == row) {
					for (int j = 0; j < this.matrixKeys[0].length; j++) {
						this.matrixKeys[row][j] = 0; // I am not considering this row of the matrix in order to not get
														// a
						// feature.
					}
				}
			}

		}
	}

	public void updateMatrix(ArrayList<Integer> targetColumns, ArrayList<Integer> targetK) {
		ArrayList<Integer> rowsToDelete = new ArrayList<>();
		int count = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length - 2; j++) {
				if (verify(matrix[i][j], targetK))
					count++;
			}
			if (count != targetK.size()) {
				rowsToDelete.add(i);
			}
			count = 0;
		}
		count = 0;
		boolean flag = false;
		int[][] temp = new int[matrix.length - rowsToDelete.size()][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			flag = verifyRowToDelete(i, rowsToDelete);
			if (!flag) {
				for (int j = 0; j < matrix[0].length; j++) // Copy the columns
					temp[count][j] = matrix[i][j];
				count++;
			}
		}
		matrix = temp;
		int sum = getTotalCount(matrix, colType + 1);
		totalCount = sum;
	}

	public boolean verify(int key, ArrayList<Integer> targetK) {
		for (int i = 0; i < targetK.size(); i++) {
			if (targetK.get(i) == key)
				return true;
		}
		return false;
	}

	public int getTypeOfNode(ArrayList<Integer> targetColumns, ArrayList<Integer> targetK, int[][] m) {
		int count = 0;
		int typeFound = 0;

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length - 2; j++) {
				if (verify(matrix[i][j], targetK))
					count++;
			}
			if (count == targetK.size()) {
				typeFound = matrix[i][colType];
				return typeFound;
			}
			count = 0;
		}
		return typeFound; // The nodes has not to split.
	}

	public int getTotalCount(int[][] mat, int colCount) {
		int sum = 0;
		for (int i = 0; i < mat.length; i++) {
			sum += mat[i][colCount];
		}
		return sum;
	}

	public boolean verifyRowToDelete(int row, ArrayList<Integer> rowsToDelete) {
		for (int i = 0; i < rowsToDelete.size(); i++) {
			if (row == rowsToDelete.get(i))
				return true;
		}
		return false;
	}

	public boolean requireSplit(int key, ArrayList<Integer> targetColumns, ArrayList<Integer> targetK) {
		int count = 0;
		int rowFound = 0;
		boolean flag = false;
		int typeTemp = matrix.length;
		if (typeTemp != 0) {
			typeTemp = matrix[0][colType];
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					flag = verifyColumInTargetColums(j, targetColumns);
					if (flag) {// It belongs to the attribute
						if (verifyKey(matrix[i][j], targetK, matrix[i][colType])) {
							count++;
							if (i > 0) {
								if (matrix[i][colType] == typeTemp)
									count--;
							}
						}
					}
				}
				if (count == targetK.size())
					rowFound++;
				count = 0;
			}
		}
		if (rowFound > 1)
			return true;

		return false; // The nodes has not to split.
	}

	public boolean verifyColumInTargetColums(int col, ArrayList<Integer> targetColumns) {

		for (int i = 0; i < targetColumns.size(); i++) {
			if (col == targetColumns.get(i))
				return true;
		}
		return false;

	}

	public boolean verifyKey(int key, ArrayList<Integer> targetKey, int type) {

		for (int i = 0; i < targetKey.size(); i++) {
			if (key == targetKey.get(i))
				return true;
		}
		return false;
	}

	public void updateType() {

		int atribute;
		ArrayList<Integer> temp = new ArrayList<>();
		for (int k = 0; k < matrix.length; k++) {
			atribute = matrix[k][colType];
			if (!verifyAtribute(atribute, temp))
				temp.add(atribute);
		}
		int arrayTemp[] = new int[temp.size()];
		for (int k = 0; k < temp.size(); k++) {
			arrayTemp[k] = temp.get(k);
		}
		type = arrayTemp;
	}

	public boolean verifyAtribute(int atribute, ArrayList<Integer> temp) {
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i) == null)
				return false;
			if (temp.get(i) == atribute)
				return true;
		}
		return false;
	}

	public void updateTotalCountInit() {
		totalCount = totalCountCopy;
	}

	public int[][] getMatrixKeys() {
		return matrixKeys;
	}

	public int[][] getMatrixKeysCopy() {

		return this.matrixKeysCopy;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int[][] getMatrixCopy() {
		return matrixCopy;
	}

	public int[] getType() {
		return type;
	}

	public int[] getTypeCopy() {
		return typeCopy;
	}

	public void printMatrix(int[][] matrix1) {
		for (int i = 0; i < matrix1.length; i++) {
			for (int j = 0; j < matrix1[0].length; j++) {
				System.out.print(matrix1[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public void printType() {
		for (int i = 0; i < type.length; i++) {
			System.out.print(type[i] + "\t");
			System.out.println();
		}
	}

	public void printTypeCopy(int[] copy) {
		for (int i = 0; i < copy.length; i++) {
			System.out.print(copy[i] + "\t");
			System.out.println();
		}
	}

	public void setKeysCopy(int[][] mk) {
		// TODO Auto-generated method stub
		int[][] matrixTemp = new int[mk.length][mk[0].length];
		for (int i = 0; i < mk.length; i++) {
			for (int j = 0; j < mk[0].length; j++) {
				matrixTemp[i][j] = mk[i][j];
			}
		}
		matrixKeysCopy = matrixTemp;
	}

	public void setMatrixCopy(int[][] m) {
		// TODO Auto-generated method stub
		int[][] matrixTemp = new int[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				matrixTemp[i][j] = m[i][j];

			}
		}
		matrixCopy = matrixTemp;
	}

	public void setTypeCopy(int[] t) {
		// TODO Auto-generated method stub
		int[] matrixTemp = new int[t.length];
		for (int i = 0; i < t.length; i++) {
			matrixTemp[i] = t[i];
		}
		typeCopy = matrixTemp;
	}

	public void updateMatrixKeysInit(int mk[][]) {
		int[][] matrixTemp = new int[mk.length][mk[0].length];
		for (int i = 0; i < mk.length; i++) {
			for (int j = 0; j < mk[0].length; j++) {
				matrixTemp[i][j] = mk[i][j];
			}
		}
		matrixKeys = matrixTemp;
	}

	public void updateMatrixInit(int m[][]) {
		int[][] matrixTemp = new int[m.length][m[0].length];

		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				matrixTemp[i][j] = m[i][j];
			}
		}
		matrix = matrixTemp;
	}

	public void updateTypeInit(int[] t) {
		// TODO Auto-generated method stub
		int[] arrayTemp = new int[t.length];
		for (int i = 0; i < t.length; i++) {
			arrayTemp[i] = t[i];
		}
		type = arrayTemp;
	}
}
