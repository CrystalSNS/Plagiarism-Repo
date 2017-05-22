package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import weka.classifiers.Classifier;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

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
//		RandomForest clasTree = new RandomForest();

		clasTree.buildClassifier(trainDataInst);

		Instances labledInst = new Instances(testDataInst);

		double clsLabel;

// test each instance using the model and return class label for each test instance
		
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
	
	public void voteClassifier() throws Exception {

		RandomForest classifierTree = new RandomForest();
		
		Classifier[] classifiers = {				
				(RandomForest) SerializationHelper.read(new FileInputStream("result/randomForest.model")),
				(RandomForest) SerializationHelper.read(new FileInputStream("result/randomForest1.model"))
		};
		
		
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File("result/test/test1/suspious-docs/suspicious-document00001.arff"));
		Instances testDataInst = loader.getDataSet();
		testDataInst.setClassIndex(testDataInst.numAttributes() - 1);
		
		Vote voter = new Vote();
		voter.setClassifiers(classifiers);
		voter.buildClassifier(testDataInst);
	}

	public void learnClassifierIncremental() throws Exception {

		File folder = new File("result/train/1train-pan11/part1");
		File[] listOfFiles = folder.listFiles();

		HoeffdingTree classifierTree = new HoeffdingTree();
//		RandomForest classifierTree = new RandomForest();
		Instances strucInst;

		ArffLoader loader = new ArffLoader();

		// listOfFiles.length;
		for (int i = 0; i < 1; i++) {
			if (listOfFiles[i].getName().endsWith(".arff")) {

// 				loader.setFile(new File("result/train/1train-pan11/part1/"+listOfFiles[i].getName()));
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

	//Testing with a test file 
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

}
