import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
//import org.jgrapht.*;


public class Main {

	/**
	 * @param args
	 */
	static HashMap<String, Integer> keyMap = new HashMap<String, Integer>();
	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub

		// parsing the file.txt
		List<String> ProteinList = new ArrayList<String>();
		Parser parser = new Parser( );
		ProteinList = parser.ParseSeqFile(args[0], args[1]);

		HPOParser hpoParse = new HPOParser();

		Iterator<String> it = ProteinList.iterator();

		String Prot;
		System.out.println();

		// Graph Data structures
		DirectedGraph<String, DefaultEdge > Graph1;			
		Graph1 = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

		Prot = it.next();
		Graph1 = hpoParse.parseHPO( Prot );
		iteratorMethod(Graph1);

		while( it.hasNext() )
		{
			Prot = it.next();
			DirectedGraph<String, DefaultEdge > graph = hpoParse.parseHPO( Prot );			
			Graphs.addGraph(Graph1,  graph);
			iteratorMethod(graph);
		}
		keyMap = sortByValues(keyMap);
		displayFrequency();
	}
	public static void iteratorMethod(DirectedGraph<String, DefaultEdge > Graph1)
	{
		Set<String> set = Graph1.vertexSet();
		Iterator<String> it = set.iterator();
		while(it.hasNext())
		{
			String key = it.next();
			if(!keyMap.containsKey(key))
			{
				keyMap.put(key, 1);
			}
			else
			{
				int count = keyMap.get(key);
				keyMap.put(key, count+1);
			}
		}
	}
	public static void displayFrequency()
	{
		Set<String> set = keyMap.keySet();
		Iterator<String> it = set.iterator();
		System.out.println("HPOTerm\tFrequeny");
		while(it.hasNext())
		{
			String key = it.next();
			System.out.println(key+"\t"+keyMap.get(key));
		}
	}
	@SuppressWarnings("rawtypes")
	public static <K extends Comparable,V extends Comparable> HashMap<K,V> sortByValues(Map<K,V> map){
	        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
	     
	        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

	            @SuppressWarnings("unchecked")
				@Override
	            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
	                return -(o1.getValue().compareTo(o2.getValue()));
	            }
	        });
	     
	        HashMap<K,V> sortedMap = new LinkedHashMap<K,V>();
	     
	        for(Map.Entry<K,V> entry: entries){
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	     
	        return sortedMap;
	    }


}
