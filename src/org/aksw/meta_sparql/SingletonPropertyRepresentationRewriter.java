package org.aksw.meta_sparql;


public class SingletonPropertyRepresentationRewriter extends AbstractRepresenationQueryRewriter
{
	
	public SingletonPropertyRepresentationRewriter(String query)
	{
		super(query);
		this.useSharedResources=true;
	}
	
	
	public String getTripleId(String id, String subject, String predicate, String object) 
	{
		return
			getTiplePattern(subject,	 id, 	object)+
            getTiplePattern(id, 		"<http://www.w3.org/1999/02/22-rdf-syntax-ns#singletonPropertyOf>", predicate);
	}	

}
