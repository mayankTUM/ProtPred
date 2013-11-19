import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;



public class HPO {
	ArrayList<String> HPOTerms = new ArrayList<String>();
	
	public ArrayList<String> getHPOfromGeneID(int geneID){
		Scanner input=null;
		String currentSentence=null;
		File file = new File("ALL_SOURCES_ALL_FREQUENCIES_genes_to_phenotype.txt");
		try{
			input = new Scanner(file);
		}catch(Exception e){
			System.out.println("Not found");
			
			
		}

		while(input.hasNext()) {
			
		    //or to process line by line
			currentSentence=input.nextLine();
			StringTokenizer st = new StringTokenizer(currentSentence);
				
			if(st.nextToken().equals((new Integer(geneID)).toString())){
				
				//System.out.println(currentSentence);
				while(st.hasMoreTokens()){
					
					String currentWord=st.nextToken();
					if(currentWord.startsWith("HP:")){
						//System.out.println(currentWord.substring(3));
						HPOTerms.add(currentWord.substring(3));
					}
				
				}
			}
		}

		input.close();
		return HPOTerms;
		
	}
	public void printHPOTerms(int geneID){
		ArrayList<String> HPOTerms2 = new ArrayList<String>();
		HPOTerms2 = getHPOfromGeneID(geneID);
		for (int i=0; i < HPOTerms2.size(); i++) {
			String HPOTerm = HPOTerms2.get(i);
			System.out.println(HPOTerm);
			}
		
		
		
		
	}

}
