import java.util.List;


public class HPOCollection {
private List<HPOTerm> HPOItems= null;



public void addTerm(HPOTerm term){

	
	///if has the term don't add it
	
	
	HPOItems.add(term);
}



public List<HPOTerm> get_HPOTerms()
{
return HPOItems;
}
}
