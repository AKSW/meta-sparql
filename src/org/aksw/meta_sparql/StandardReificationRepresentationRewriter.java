package org.aksw.meta_sparql;


public class StandardReificationRepresentationRewriter extends AbstractRepresenationQueryRewriter
{
	
	public StandardReificationRepresentationRewriter(String query)
	{
		super(query);
		this.useSharedResources=true;
	}
	
	
	public String getTripleId(String id, String subject, String predicate, String object) 
	{
		return id+" a <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement>" 	+	";\n"+
		"<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> " 		+	subject		+ 	";\n"+
		"<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> "		+	predicate	+ 	";\n"+
		"<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> "			+	object		+ 	".\n";
	}
	
	
	

}
