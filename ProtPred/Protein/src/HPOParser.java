import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.logging.Logger;
import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class HPOParser {
	
	
	public class ParameterNameValue
	{
	  private final String name;
	  private final String value;

	  public ParameterNameValue(String name, String value)
	    throws UnsupportedEncodingException
	  {
	    this.name = URLEncoder.encode(name, "UTF-8");
	    this.value = URLEncoder.encode(value, "UTF-8");
	  }
	}
	
	  private   final String UNIPROT_SERVER = "http://www.uniprot.org/";
	  private   final Logger LOG = Logger.getAnonymousLogger();

	  private   String GeneId="";
	  			
		
	  
	public DirectedGraph<String, DefaultEdge> parseHPO( String ProteinID) 
	{
		List<HPOTerm> mycol = null;
		//System.out.println("No of items in Collection: " + mycol.size());
	
		//TODO: set the query field with desired protein ID
		///run method will retrieve the GeneID from the UniProt and
		/// put it in the GeneId variable
		
		 try {
			run("mapping", new ParameterNameValue[] {
				      new ParameterNameValue("from", "ACC"),
				      new ParameterNameValue("to", "P_ENTREZGENEID"),
				      new ParameterNameValue("format", "tab"),
				      new ParameterNameValue("query", ProteinID),
				      //new ParameterNameValue("limit", "100"),
				      //new ParameterNameValue("offset", "100"),
				    //  new ParameterNameValue("columns", "genes"),
				    });
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
//		 GeneId = "25";
		 System.out.println(GeneId);
		 if( GeneId.contains("\n"))
		 GeneId = GeneId.substring(0, GeneId.indexOf('\n'));
		 
		// Graph Data structures
		DirectedGraph<String, DefaultEdge > Graph = null;
		Graph = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
			 
		 ///if got the answer from the server get the HPO terms
		if(!GeneId.isEmpty())
		{
			///get the HPO(s) of the Gene 
			HPO Hpoobject = new HPO(); 
			ArrayList<String> HPOofGene = Hpoobject.getHPOfromGeneID(Integer.parseInt(GeneId));
			
			/// make sure that we have a collection to put the terms in
			mycol = new ArrayList<HPOTerm>();
			for (String HPOstring : HPOofGene) {
								
				/// get the related terms 
				try {
					
					HPOTerms(HPOstring, mycol);
							
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}		
			
//			System.out.println("No of items in Collection: " + mycol.size());
//			
//			System.out.println("Printing the Collection:");
			
			/// print out the collection
			for (HPOTerm item : mycol) {
				
				if( ! Graph.containsVertex(item.ParentID ))
				{
					Graph.addVertex(item.ParentID);
				}
				
				if( ! Graph.containsVertex(item.ID) )
				{
					Graph.addVertex(item.ID);
				}
				
				if( ! Graph.containsEdge(item.ParentID, item.ID))
				{
					Graph.addEdge(item.ParentID, item.ID);
				}
				
//				System.out.printf("HP Id:%S ParentID:%S", item.ID,item.ParentID);
//				System.out.println();
			}		
			
			System.out.println("Graph To String : ");
			System.out.println(Graph.toString() );
		}
		
		return Graph;
	}
	
	 private void run(String tool, ParameterNameValue[] params)
			    throws Exception
			  {
			    StringBuilder locationBuilder = new StringBuilder(UNIPROT_SERVER + tool + "/?");
			    for (int i = 0; i < params.length; i++)
			    {
			      if (i > 0)
			        locationBuilder.append('&');
			      locationBuilder.append(params[i].name).append('=').append(params[i].value);
			    }
			    String location = locationBuilder.toString();
			    URL url = new URL(location);
			    LOG.info("Submitting...");
			    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			    HttpURLConnection.setFollowRedirects(true);
			    conn.setDoInput(true);
			    conn.connect();

			    int status = conn.getResponseCode();
			    while (true)
			    {
			      int wait = 0;
			      String header = conn.getHeaderField("Retry-After");
			      if (header != null)
			        wait = Integer.valueOf(header);
			      if (wait == 0)
			        break;
			      LOG.info("Waiting (" + wait + ")...");
			      conn.disconnect();
			      Thread.sleep(wait * 1000);
			      conn = (HttpURLConnection) new URL(location).openConnection();
			      conn.setDoInput(true);
			      conn.connect();
			      status = conn.getResponseCode();
			    }
			    if (status == HttpURLConnection.HTTP_OK)
			    {
			      LOG.info("Got a OK reply");
			      InputStream reader = conn.getInputStream();
			      URLConnection.guessContentTypeFromStream(reader);
			      StringBuilder builder = new StringBuilder();
			      int a = 0;
			      while ((a = reader.read()) != -1)
			      {
			        builder.append((char) a);
			      }
			      
			      ///Get the geneID from the server and put it in the varaible for further requests
			      GeneId = builder.toString().substring(15,builder.length()-1);
			      
			    }
			    else
			      LOG.severe("Failed, got " + conn.getResponseMessage() + " for "
			        + location);
			    conn.disconnect();
			  }
	
	private String HPOTerms(String HPO, List<HPOTerm> mycol ) throws FileNotFoundException
	{
		File file = new File("hp.obo");
		Scanner input = new Scanner(file);
		while(input.hasNext()) {
		    String nextToken = input.next();
		    
		    ///reach one term
		    if(nextToken.equals("[Term]"))
		    {
		    	
		    	String idLine = input.nextLine();
		    	if(idLine.equals(null)||idLine.equals("")) idLine= input.nextLine();
		    	///next token is the id statement
		    	String idToken= idLine.substring(7);
		    	
		    	//check if we having the right hpo term
		    	if(idToken.equalsIgnoreCase(HPO))
		    	{
//		    		System.out.println(idLine);
		    		String ParentId="-1";
		    		///look for is_a term 
		    		while(input.hasNext() ){
		    			String isline=input.nextLine();
		    			if(isline.startsWith("is_a"))
		    			{
		    			ParentId = isline.substring(9,9+7);
		    			HPOTerm hpitem = new HPOTerm(idToken, ParentId);
			    		mycol.add(hpitem);
			    		
		    			HPOTerms(ParentId, mycol);
		    			///we might have multiple parents so no break
		    			//break;
		    			}
		    			/// check if there is no is a to break looking for parent
		    			else if(isline.startsWith("[Term]")||isline.isEmpty())break;
		    		}
		    		
		    		break;
		    	}
		    }
		    //or to process line by line
		   input.nextLine();
		    
		    
		}

		input.close();
		
		
		return "";
	}
}
