package org.aksw.meta_sparql;

public class GraphRepresentationRewriter extends AbstractRepresenationQueryRewriter
{

	public GraphRepresentationRewriter(String query)
	{
		super(query); 
	}
	
	@Override
	public String getTriple(String subject, String predicate, String object) {
		return getTiplePattern(subject, predicate, object);
	}
	
	@Override
	public String getTripleId(String id, String subject, String predicate, String object) 
	{
			return ("GRAPH "+id+" {"+getTiplePattern(subject, predicate, object) +"}");
	}

}
