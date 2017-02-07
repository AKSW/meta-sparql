package org.aksw.meta_sparql;


public class RdrRepresentationRewriter extends AbstractRepresenationQueryRewriter
{
	
	public RdrRepresentationRewriter(String query)
	{
		super(query);
		this.useSharedResources=true;
	}
	
	@Override
	public boolean isShared(String key)
	{
		return key.endsWith("Revision>") || key.endsWith("Revision");
	}
	
	@Override
	public String getTriple(String subject, String predicate, String object) {
		return getTiplePattern(subject, predicate, object);
	}
	
	@Override
	public String getTripleId(String id, String subject, String predicate, String object) 
	{
		String triple = subject +" "+predicate+" "+object;
		return
			"BIND( << "+ triple + " >> as "+id+"). ";
	}	

}
