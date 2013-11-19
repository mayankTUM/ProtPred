import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Parser {

	/**
	 * @param args
	 */
	public List<String> ParseSeqFile( String File, String Ngbour ) throws Exception{
		// TODO Auto-generated method stub
		
		String Inputfile = File;
		String ngbr = Ngbour;
		
		List<String> ProteinList = new ArrayList<String>();
		
		System.out.println("InputFile is :" + Inputfile);
		System.out.println(System.getProperty("user.dir"));
		int NoPar = 0;
		int ParRead = 0;
		String Protein;
		
		// parsing argument for nearest neigbrs
		ngbr = ngbr.trim();
		StringTokenizer st = new StringTokenizer( ngbr, "=" );
		st.nextToken();		
		String param = st.nextToken();
		
		NoPar = Integer.parseInt(param);		

		System.out.println("Finding out " + NoPar + " neighbours !");

		// logic to start process
		try {
			ProcessBuilder Prb= new ProcessBuilder("blastp", "-db","./db/firstdb", "-query", Inputfile, "-out", "file.txt");
			Process process = Prb.start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader brr = new BufferedReader(isr);

			String liner;
			while ((liner = brr.readLine()) != null) {
			  System.out.println(liner);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Error in executing blast process ! Exiting...");
		}

		// logic to parse output file
		String filename = "file.txt";

		System.out.println("Following are the results: ");
		
		try 
		{
			String Line;
			String Match = "Sequences producing significant alignments:";
			
			BufferedReader br = new BufferedReader( new FileReader(filename));
			
			while( true )
			{
				Line = br.readLine();
				if( Line.contains(Match))
				{
					break;
				}
			}
			
			// read sequences
			while( true )
			{
				Line = br.readLine();
				Line = Line.trim();
				if( Line.isEmpty())
				{
					continue;
				}
				
				if( Line.startsWith(">"))
				{
					if( ParRead < NoPar )
					{
						System.out.println("Only " + ParRead + " similar proteins found ! ");
					}
					break;					
				}
				
				if( ParRead >= NoPar )
				{
					break;
				}
				//Line = Line.replaceAll("\\s+", "");
				
				// we have a matching protein
				Protein = Line.substring(0, 6);
				System.out.println(Protein);
				ProteinList.add(Protein);
				ParRead++;
			}			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
		
		return ProteinList;
	}

}
