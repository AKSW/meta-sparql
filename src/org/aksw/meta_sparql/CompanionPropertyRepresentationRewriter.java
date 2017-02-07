package org.aksw.meta_sparql;

public class CompanionPropertyRepresentationRewriter extends AbstractRepresenationQueryRewriter
{

	public CompanionPropertyRepresentationRewriter(String query)
	{
		super(query); 
	}
	
	@Override
	public String getTriple(String subject, String predicate, String object) {
		varCount++;
		String cp = "?cp_"+varCount+" ";
		return  subject +" " +cp+ object +". " +
				getTiplePattern(cp	,	"<http://www.w3.org/1999/02/22-rdf-syntax-ns#companionPropertyOf>"	, predicate	);
	}
	
	@Override
	public String getTripleId(String id, String subject, String predicate, String object) 
	{
		varCount++;
		String cp = "?cp_"+varCount+" ";
		String cpid = "?cpid_"+varCount+" ";
		return  subject +" " +cp+ object +"; " + cpid + id +". "+
				getTiplePattern(cp	,	"<http://www.w3.org/1999/02/22-rdf-syntax-ns#companionPropertyOf>"	, predicate	)+
				getTiplePattern(cpid,	"<http://www.w3.org/1999/02/22-rdf-syntax-ns#idPropertyOf>"			, cp		);
	}
	
	//@Override
	public String getTripleIdBind2(String id, String subject, String predicate, String object) 
	{
		varCount++;
		String cp = "?cp_"+varCount+" ";
		String cpid = "?cpid_"+varCount+" ";
		return  //subject +" " +cp+ object +". "+
				getTiplePattern(subject,	cp,		object	)+
		        "BIND(URI(CONCAT(STR("+cp+"),\".SID\")) as "+cpid+")."+
		        getTiplePattern(subject,	cpid,	id		); //TODO
		        //subject + cpid + id +". "+
				//getTiplePattern(cp	,	"<http://www.w3.org/1999/02/22-rdf-syntax-ns#companionPropertyOf>"	, predicate	)+
				//getTiplePattern(cpid,	"<http://www.w3.org/1999/02/22-rdf-syntax-ns#idPropertyOf>"			, cp		);
	}
	
	public String getTripleIdBindMix(String id, String subject, String predicate, String object) 
	{
		varCount++;
		String cp = "?cp_"+varCount+" ";
		String cpid = "?cpid_"+varCount+" ";
		return  
				getTiplePattern(subject,	cp,		object	)+
		        "BIND(URI(CONCAT(STR("+cp+"),\".SID\")) as "+cpid+")."+
		        getTiplePattern(subject,	cpid,	id		)+
				getTiplePattern(cp	,	"<http://www.w3.org/1999/02/22-rdf-syntax-ns#companionPropertyOf>"	, predicate	);
	}
	
	public String getTripleIdBind(String id, String subject, String predicate, String object) 
	{
		varCount++;
		String cp = "?cp_"+varCount+" ";
		String cpid = "?cpid_"+varCount+" ";
		return  
				getTiplePattern(subject,	cp,		object	)+
		        "BIND(URI(CONCAT(STR("+cp+"),\".SID\")) as "+cpid+")."+
		        getTiplePattern(subject,	cpid,	id		)+
				getTiplePattern(cp	,	"<http://www.w3.org/1999/02/22-rdf-syntax-ns#companionPropertyOf>"	, predicate	);
	}
	
	//@Override
	public String getTripleId2(String id, String subject, String predicate, String object) 
	{
		if (predicate.startsWith("?"))
			return getTripleIdBindMix(id, subject, predicate, object);
		else
		{
			//TODO
			return null;
		}
	}
	
//	@Override
//	public String getMetadata(String id, String key, String value)
//	{
//		return getTiplePattern(id, key, value);
//	}

}
