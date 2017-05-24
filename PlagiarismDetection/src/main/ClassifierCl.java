package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.util.ArrayUtils;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.instance.RemoveRange;


public class ClassifierCl {

	public void learnClassifier() throws Exception {

		ArffLoader loader = new ArffLoader();
		loader.setFile(new File("result/train/1train-pan11/part1/suspicious-document00011.arff"));
		Instances trainDataInst = loader.getDataSet();
		trainDataInst.setClassIndex(trainDataInst.numAttributes() - 1);

		loader.setFile(new File("result/test/test1/suspious-docs/suspicious-document00001.arff"));
		Instances testDataInst = loader.getDataSet();
		testDataInst.setClassIndex(testDataInst.numAttributes() - 1);

		HoeffdingTree clasTree = new HoeffdingTree();
		// RandomForest clasTree = new RandomForest();

		clasTree.buildClassifier(trainDataInst);

		Instances labledInst = new Instances(testDataInst);

		double clsLabel;

		// test each instance using the model and return class label for each
		// test instance

		for (int i = 0; i < testDataInst.numInstances(); i++) {

			clsLabel = clasTree.classifyInstance(testDataInst.instance(i));
			System.out.println(clsLabel);
			labledInst.instance(i).setClassValue(clsLabel);
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter("result/suspicious-document00001-labled.arff"));
		writer.write(labledInst.toString());
		writer.newLine();
		writer.flush();
		writer.close();
	}

	
	public void learnClassifierIncremental() throws Exception {

		File folder = new File("result/train/1train-pan11/part1");
		File[] listOfFiles = folder.listFiles();

		HoeffdingTree classifierTree = new HoeffdingTree();
		// RandomForest classifierTree = new RandomForest();
		Instances strucInst;

		ArffLoader loader = new ArffLoader();

		// listOfFiles.length;
		for (int i = 0; i < 1; i++) {
			if (listOfFiles[i].getName().endsWith(".arff")) {

				// loader.setFile(new
				// File("result/train/1train-pan11/part1/"+listOfFiles[i].getName()));
				loader.setFile(new File("result/train/1train-pan11/part1/suspicious-document00011.arff"));

				strucInst = loader.getStructure();
				strucInst.setClassIndex(strucInst.numAttributes() - 1);

				classifierTree.buildClassifier(strucInst);

				Instance currentIns;

				while ((currentIns = loader.getNextInstance(strucInst)) != null) {
					System.out.println(currentIns);
					classifierTree.updateClassifier(currentIns);
				}

			}

		}

		// Testing with a test file
		loader.setFile(new File("result/test/test1/suspious-docs/suspicious-document00001.arff"));

		Instances testInst = loader.getDataSet();
		testInst.setClassIndex(testInst.numAttributes() - 1);

		Instances labledInst = new Instances(testInst);

		double clsLabel;

		for (int i = 0; i < testInst.numInstances(); i++) {

			clsLabel = classifierTree.classifyInstance(testInst.instance(i));
			System.out.println(clsLabel);

			labledInst.instance(i).setClassValue(clsLabel);
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter("result/updateClassifier.text"));
		writer.write(labledInst.toString());
		writer.newLine();
		writer.flush();
		writer.close();

	}

	
	public void mergeArff(String pt) throws Exception {

		List<Integer> indexArr = new ArrayList<Integer>();
		File file = new File(pt);
		File[] listOfFile = file.listFiles();

		for (int j = 0; j < listOfFile.length; j++) {
			if(!listOfFile[j].getName().endsWith(".arff")){
				listOfFile =(File[]) ArrayUtils.removeAt(listOfFile,j);
			}
        }
		
		ArffLoader tmpLoader;
		Instances tmpInst ;
		
		tmpLoader = new ArffLoader();
//		--------
//		tmpLoader.setFile(new File(pt+"/"+listOfFile[0].getName()));
		
//		-----------------------------------------------------
//		merge with the old file => then get out the condition if(j!=0) in loop
		tmpLoader.setFile(new File("result/mergedDataInst.arff"));
//		-----------------------------------------------------
		
		Instances mergedDataInst = tmpLoader.getDataSet();
		
//		-----------------------------------------------------
//		get last index from old file
		
		Scanner s = new Scanner(new File("result/index.txt"));
		ArrayList<Integer> indexArrOld = new ArrayList<Integer>();
		while (s.hasNext()){
			indexArrOld.add(s.nextInt());
		}
		s.close();
		int begin = indexArrOld.get(indexArrOld.size() - 1) + 1, end = indexArrOld.get(indexArrOld.size() - 1);
		
		
//		-----------------------------------------------------
//		int begin = 1, end = 0;
		
		if (listOfFile.length != 0) {
			for (int j = 0; j < listOfFile.length; j++) {
				tmpLoader = new ArffLoader();
				tmpLoader.setFile(new File(pt+"/"+listOfFile[j].getName()));
				tmpInst = tmpLoader.getDataSet();
				end =  end + tmpInst.numInstances();
				indexArr.add(begin);
				indexArr.add(end);
				begin =  end + 1;
//				if(j != 0){
					mergedDataInst.addAll(tmpInst);
					System.out.println("..merging file:" +j);
//				}
			}
		}
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(mergedDataInst);
		try {
			saver.setFile(new File("result/mergedDataInst.arff"));
			saver.writeBatch();
			System.out.println("Saved file arff: result/mergedDataInst.arff");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
//		PrintWriter pw = new PrintWriter(new FileOutputStream(new File("result/index.txt")));
		PrintWriter pw = new PrintWriter(new FileOutputStream(new File("result/index.txt"), true));
	    for (Integer index : indexArr)
	        pw.println(index.toString());
	    pw.close();
	    System.out.println("Saved file txt: result/index.txt");
		
		System.out.println("Total number of instances:"+mergedDataInst.numInstances());
		System.out.println("Done load index and Merged Data !!");
	}
	
	
	public void voteClassifier(String ptData, String ptIndex) throws Exception {

		int begin,end;

		List<Classifier> classifiersArr = new ArrayList<Classifier>();
		
		RandomForest RFclassifierTree = new RandomForest();
//		LibSVM svm = new LibSVM();

		RemoveRange rmRange = new RemoveRange();
		FilteredClassifier fc;
		
		Scanner s = new Scanner(new File(ptIndex));
		ArrayList<Integer> indexArr = new ArrayList<Integer>();
		while (s.hasNext()){
			indexArr.add(s.nextInt());
		}
		s.close();

		for(int i= 0; i < indexArr.size(); i=i+2){
			
			begin = indexArr.get(i);
			end = indexArr.get(i+1);
			
			rmRange = new RemoveRange();
			rmRange.setInstancesIndices(String.valueOf(begin) + "-" + String.valueOf(end));
			rmRange.setInvertSelection(true);
			
			fc = new FilteredClassifier();
			fc.setFilter(rmRange);
			fc.setClassifier(RFclassifierTree);

			classifiersArr.add(fc);
		}
		System.out.println("Number of Classifiers: " + classifiersArr.size());
		ArffLoader tmpLoader = new ArffLoader();
		tmpLoader.setFile(new File(ptData));
		Instances DataInst = tmpLoader.getDataSet();
		DataInst.setClassIndex(DataInst.numAttributes() - 1);
		
		Vote voter = new Vote();

		voter.setClassifiers(classifiersArr.toArray(new Classifier[classifiersArr.size()]));
		voter.buildClassifier(DataInst);
		
		weka.core.SerializationHelper.write("result/model/voterRF.model", voter);
		
		System.out.println("Saved model!");
		System.out.println();
	}
	
	
	public void renameFile(String pt) {
		
	  File folder = new File(pt);
      File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".arff")) {
                File f = new File(pt + "/" + listOfFiles[i].getName()); 
//                f.renameTo(new File(pt+"/test11-" + listOfFiles[i].getName() ));
                f.renameTo(new File(pt+"/" + listOfFiles[i].getName().substring(7,listOfFiles[i].getName().length()) ));
            }
        }
        System.out.println("conversion is done");
	}
}
