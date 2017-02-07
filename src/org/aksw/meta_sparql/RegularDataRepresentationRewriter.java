package org.aksw.meta_sparql;


public class RegularDataRepresentationRewriter extends AbstractRepresenationQueryRewriter
{
	
	public RegularDataRepresentationRewriter(String query)
	{
		super(query);
		this.useSharedResources=true;
	}
	
	
	public String getTripleId(String id, String subject, String predicate, String object) 
	{
		return
			getTiplePattern(subject,	 predicate, 	object);
	}	

}
