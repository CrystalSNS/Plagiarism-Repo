import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tagger.POS_tagger;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sentence sentence = new Sentence();
		Features features = new Features();
		readText();
		
//		sentence.setWord_Mean(features.findMean(setOfValue));
	}
	
	public static void readText () {
	    
//		try (BufferedReader br = new BufferedReader(new FileReader("/Users/noch/Documents/workspace/PlagiarismDetection/src/corpus/train/12Esample01.txt"))) {
//			String sCurrentLine;
//			String ts = null;
//			java.util.List myList = new ArrayList();
//			while ((sCurrentLine = br.readLine()) != null) {
//				
//				if (sCurrentLine.trim().isEmpty()){
//					myList.add(ts);
//					ts = null;
//				}else{
//					ts += sCurrentLine;
//				}
//				System.out.println(myList.get(0));
//				
//			}
//			System.out.println(ts);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		File file = new File("/Users/noch/Documents/workspace/PlagiarismDetection/src/corpus/train/12Esample01.txt");
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			try {
				fis.read(data);
				fis.close();
				String str = new String(data, "UTF-8");

				int paragraphCount = 0;
			   
				Pattern pattern = Pattern.compile("(?:[^\n][\n]?)+", Pattern.MULTILINE);
			    Matcher matcher = pattern.matcher(str);

			    while (matcher.find()) {
			      String paragraph = matcher.group();
//			      System.out.println("Paragraph: " + paragraph.trim() +"\n");
			      
			      paragraphCount++;
			    }
			    POS_tagger posTagger = new POS_tagger();
			    
				try {
					System.out.println(posTagger.builtPOS(str));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}

}
