package org.aksw.meta_sparql;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRepresenationQueryRewriter
{
	boolean useSharedResources = false;
	int varCount =0 ;
	String query;

	public AbstractRepresenationQueryRewriter(String query)
	{
		this.query=query;
	}
	
	//public abstract AbstractRepresenationQueryRewriter getRewriterForQuery (String query);
	
	public abstract String getTripleId(String id, String subject, String predicate, String object);
	
	public String getTriple(String subject, String predicate, String object) 
	{
		return getTripleId("?dummyVar_"+varCount++, subject, predicate, object);
	}
	
	public String getTiplePattern(String subject, String predicate, String object)
	{
		return subject +" "+predicate+" "+object+" .";
	}
	
	public String getMetadata(String id, String key, String value, boolean isReified)
	{
		if (isShared(key))
		{
			varCount++;
			return 
				getTiplePattern				 (id,					"<http://sdw.aksw.org/metardf/hasSharedMeta>",	"?shared_"+varCount)+
				getMetadataFactRepresentation("?shared_"+varCount,	key 										 ,	value					,isReified);
		}
		else
			return getMetadataFactRepresentation(id, key, value, isReified);
	}
	
	public String getMetadataFactRepresentation(String id, String key, String value, boolean isReified)
	{
		if (!isReified)
			return getTiplePattern(id, key, value);
		else
			return getTriple(id, key, value);
	}
	
	public boolean isShared(String key)
	{
		return useSharedResources;
	}
	
	public void rewriteQuery(PrintStream ps)
	{
		ps.println(this.rewriteQuery());
	}
	
	public String rewriteQuery()
	{
		Pattern p = Pattern.compile("#!data\\((?<s>[^,]+),(?<p>[^,]+),(?<o>.+?)\\)!#");
		Matcher data = p.matcher(query);
		String queryNew = query;
		int dummy = 0;
		while (data.find())
		{
			dummy++;
			queryNew = data
					.replaceFirst(getTriple(data.group("s"), data.group("p"), data.group("o")));
//					.replaceFirst(getTripleId("?dummyVar_"+dummy, data.group("s"), data.group("p"), data.group("o")));
			data = p.matcher(queryNew);
			// ps.println(getTripleId(data.group("id"),data.group("s"),
			// data.group("p"), data.group("o")));
			// ps.println(data.group("id"));
			// ps.println(data.group("s"));
			// ps.println(data.group("p"));
			// ps.println(data.group("o"));
			// ps.println("==");
		}
		
		Pattern p1 = Pattern.compile("#!reif\\((?<id>[^,]+),(?<s>[^,]+),(?<p>[^,]+),(?<o>.+?)\\)!#");
		Matcher reif = p1.matcher(queryNew);
		while (reif.find())
		{
			queryNew = reif
					.replaceFirst(getTripleId(reif.group("id"), reif.group("s"), reif.group("p"), reif.group("o")));
			reif = p1.matcher(queryNew);
			// ps.println(getTripleId(reif.group("id"),reif.group("s"),
			// reif.group("p"), reif.group("o")));
			// ps.println(reif.group("id"));
			// ps.println(reif.group("s"));
			// ps.println(reif.group("p"));
			// ps.println(reif.group("o"));
			// ps.println("==");
		}

		Pattern p2 = Pattern.compile("#!meta\\((?<id>[^,]+),(?<k>[^,]+),(?<v>.+?)\\)!#");
		Matcher meta = p2.matcher(queryNew);
		while (meta.find())
		{
			queryNew = meta.replaceFirst(getMetadata(meta.group("id"), meta.group("k"), meta.group("v"),false));
			meta = p2.matcher(queryNew);
			// ps.println(getMetadata(meta.group("id"),meta.group("k"),
			// meta.group("v")));
			// ps.println(meta.group("id"));
			// ps.println(meta.group("k"));
			// ps.println(meta.group("v"));
			// ps.println("==");
		}
		
		Pattern p3 = Pattern.compile("#!meta2\\((?<id>[^,]+),(?<k>[^,]+),(?<v>.+?)\\)!#");
		Matcher meta2 = p3.matcher(queryNew);
		while (meta2.find())
		{
			queryNew = meta2.replaceFirst(getMetadata(meta2.group("id"), meta2.group("k"), meta2.group("v"),true));
			meta2 = p3.matcher(queryNew);
//			 ps.println(getMetadata(meta2.group("id"),meta2.group("k"),
//			 meta2.group("v")));
//			 ps.println(meta2.group("id"));
//			 ps.println(meta2.group("k"));
//			 ps.println(meta2.group("v"));
//			 ps.println("==");
		}
		
		Pattern test = Pattern.compile("#!.*!#");
		Matcher tester = test.matcher(queryNew);
		if (tester.find())
		{
			System.err.println("found probably wrong meta-sparql pattern in query: "+queryNew);
		}
		return queryNew;
	}
	

}
