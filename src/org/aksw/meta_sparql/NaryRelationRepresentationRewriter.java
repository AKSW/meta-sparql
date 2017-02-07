package org.aksw.meta_sparql;


public class NaryRelationRepresentationRewriter extends AbstractRepresenationQueryRewriter
{
	
	public NaryRelationRepresentationRewriter(String query)
	{
		super(query);
		this.useSharedResources=true;
	}
	
	/**
	 *  generic query generation: use BIND even when predicate is a constant value
	 */
	public String getTripleIdBind(String id, String subject, String predicate, String object) 
	{
		varCount++;
		String nary = "?nary_"+varCount;
		return
			getTiplePattern(subject,	predicate, 	id		)+
			" BIND(URI(CONCAT(STR("+predicate+"),\"-NARY-value\")) as "+nary+"). "+
            getTiplePattern(id, 		nary,		object	);
	}	
	
	/**
	 *  optimized query generation: if predicate is a constant no BIND is used, also as workaround for blazegraph where multiple BINDs result in wrong query results 
	 */
	@Override
	public String getTripleId(String id, String subject, String predicate, String object) 
	{
		if (predicate.startsWith("?"))
			return getTripleIdBind(id, subject, predicate, object);
		else
		{
			varCount++;
			String nary = (predicate.trim().endsWith(">")) ? predicate.substring(0,predicate.length()-1)+"-NARY-value"+'>' :predicate+"-NARY-value";
					return
							getTiplePattern(subject,	predicate, 	id		)+
							getTiplePattern(id, 		nary,		object	);
		}
	}	

}
