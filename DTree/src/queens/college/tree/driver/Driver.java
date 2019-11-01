/**
 * Class: Driver
 * @author Edgar Lizarraga
 * @date 05/11/2016
 */

package queens.college.tree.driver;
import java.util.ArrayList;
import java.util.HashMap;

import queens.college.Node.Node;
import queens.college.matrix.Matrix;

public class Driver {
	static HashMap<Integer, String> labels = new HashMap<>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Questions
		String question = "Overall Type: ";

		labels.put(1, question + "Simple");
		labels.put(2, question + "Compound");

		question = "Arrangment: ";
		labels.put(3, question + "Opposite");
		labels.put(4, question + "Alternate");

		question = "Margin: ";
		labels.put(5, question + "Entire");
		labels.put(6, question + "Toohed");

		// Type
		labels.put(7, "I");
		labels.put(8, "II");
		labels.put(9, "III");
		
		int[][] m = { 
						{ 1, 3, 5, 7, 5 }, 
						{ 2, 4, 5, 7, 5 }, 
						{ 1, 3, 6, 8, 11 }, 
						{ 1, 4, 6, 9, 7 },
						{ 2, 3, 6, 7, 2 }, 
						{ 2, 3, 5, 8, 7 }, 
						{ 1, 3, 6, 8, 6 }, 
						{ 2, 4, 6, 9, 15 } };
				
				// Matrixkeys: Create a row with each distinct question code.
				// i.e {1,2} belongs to Overall type - Simple 1 Compound 2
				int[][] mk = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
				
				// Setting the options for each questions plus the type and count frequency for
				// each type.
				int[] type = { 7, 8, 9 };
				int colType = 3; // Number of the column type. Initial column value = 0 then 1,2,3...
		
/*
		String question = "Outlook: ";
		labels.put(1, question + "Sunny");
		labels.put(2, question + "Overcast");
		labels.put(3, question + "Rain");

		question = "Temp: ";
		labels.put(4, question + "Hot");
		labels.put(5, question + "Mild");
		labels.put(6, question + "Cool");

		question = "Humidity: ";
		labels.put(7, question + "High");
		labels.put(8, question + "Normal");

		question = "Wind: ";
		labels.put(9, question + "Weak");
		labels.put(10, question + "Strong");

		// Type
		labels.put(11, "No");
		labels.put(12, "Yes");
		
		int[][] m = {
		{1,	4,	7	,9	,11,1},
		{1,	4,	7	,10	,11,1},
		{2,	4,	7	,9	,12,1},
		{3,	5,	7	,9	,12,1},
		{3,	6,	8	,9	,12,1},
		{3,	6,	8	,10	,11,1},
		{2,	6,	8	,10	,12,1},
		{1,	5,	7	,9	,11,1},
		{1,	6,	8	,9	,12,1},
		{3,	5,	8	,9	,12,1},
		{1,	5,	8	,10 ,12,1},
		{2,	5,	7	,10 ,12,1},
		{2,	4,	8	,9,	12,1},
		{3,	5,	7	,10 ,11,1}};
		
		int[][] mk = { { 1, 2,3 }, { 4, 5,6 }, { 7, 8,0 },{9,10,0} };
		int[] type = { 11, 12 };
		int colType = 4;
		
				*/
				
				//END OF SETTING VALUES////////////////////////////////////////////////
				
		int totalCount = 0;// Sum of the frequencies for each type.
		totalCount = getTotalCount(m, colType + 1);
		Matrix objMat;
		objMat = new Matrix(m, type, mk, colType, totalCount, labels);
		
		//Get the best question to split.
		int colToAsk = objMat.getQuestion();
		
		Node root = new Node("Tree", 0);
		HashMap<Integer, String> features = objMat.getFeaturesOfQuestion(colToAsk);
		
		// Creating the nodes of the root
		ArrayList<Integer> parents = new ArrayList<Integer>();
		parents.add(-1);
		
		for (HashMap.Entry<Integer, String> entry : features.entrySet()) 
			root.addChild(new Node(entry.getValue(), entry.getKey(), colToAsk));
		
		// For each child of the root, create nodes.
		for (int i = 0; i < root.getChildren().size(); i++) 
			createNodes(root.getChildren().get(i), objMat,m,mk,type);
		
		printTree(root, " ");
	}
	
	public static void createNodes(Node node, Matrix objMat,int [][] m, int [][] mk, int []t) {
		boolean result;
		int colToAsk = 0;

		objMat.updateMatrixInit(m);
		objMat.updateMatrixKeysInit(mk);
		objMat.updateTotalCountInit();
		objMat.updateTypeInit(t);
		
		objMat.updateMatrix(node.getTargetColumns(), node.getTargetKeys());
		result = objMat.requireSplit(node.getKey(), node.getTargetColumns(), node.getTargetKeys());

		if (result){//The actual node requires to split.
			objMat.updateMatrixKeys(node.getTargetColumns());
			objMat.updateType();
			colToAsk = objMat.getQuestion();
			HashMap<Integer, String> features = objMat.getFeaturesOfQuestion(colToAsk);

			for (HashMap.Entry<Integer, String> entry : features.entrySet()) {
				Node nodeChild = new Node(entry.getValue(), entry.getKey(), colToAsk);
				node.addChild(nodeChild);
			}
			// For each child of the root, create nodes.
			for (int i = 0; i < node.getChildren().size(); i++) {
				createNodes(node.getChildren().get(i), objMat,m,mk,t);
			}

		} else // The node does not split. Just create a final node with the class (type).
		{
			int type = 0;
			type = objMat.getTypeOfNode(node.getTargetColumns(), node.getTargetKeys(),m);
			Node nodeChild = new Node(labels.get(type), type, colToAsk);
			node.addChildType(nodeChild);
			objMat.updateMatrixInit(m);
			objMat.updateMatrixKeysInit(mk);
			objMat.updateTotalCountInit();
			objMat.updateTypeInit(t);
		}
	}
	
	private static void printTree(Node node, String appender) {
		System.out.println(appender + node.getData());
		node.getChildren().forEach(each -> printTree(each, appender + appender));
	}
	
	private static int getTotalCount(int[][] mat, int colCount) {
		int sum = 0;
		for (int i = 0; i < mat.length; i++) {
			sum += mat[i][colCount];
		}
		return sum;
	}
}