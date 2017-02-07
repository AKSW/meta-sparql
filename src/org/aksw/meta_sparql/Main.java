package org.aksw.meta_sparql;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.ext.com.google.common.net.UrlEscapers;

	

public class Main 
{
    static String inputFilePath ;
    static String outputFilePattern ;
    static boolean templateInstances;
    static int numThreads = 0;
    static Map<Class<? extends AbstractRepresenationQueryRewriter>,PrintStream> representations = new HashMap<>();	
    static AtomicInteger linesCount = new AtomicInteger(0);

    static void printRewrittenQuery(PrintStream ps,String query,String name,int instanceCount) throws IOException
    {
//		ps.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% "+name+" #"+instanceCount);ps.println(query);
    	CSVPrinter tsv = new CSVPrinter(ps, CSVFormat.EXCEL);
		tsv.printRecord(name,instanceCount,UrlEscapers.urlFragmentEscaper().escape(query));
    }
    
    static void printQueryInstantiations(PrintStream ps, String query, String name) throws IOException
	{
    	Pattern p = Pattern.compile("\\|(?<pos>\\w+?)\\|");
    	Matcher template = p.matcher(query);
    	int instanceCount = 1;
    	if (templateInstances && template.find()) // if it is query with pattern
    	{
    		
    		for (CSVRecord record : loadInstantitationValues(name)) 
	    	{
    			
	    		String queryNew = query;
	    		template = p.matcher(queryNew);
	    		while (template.find())
	    		{
	    			queryNew = template
	    					.replaceFirst(record.get((template.group("pos"))));
	    			template = p.matcher(queryNew);
	    		}
	    		printRewrittenQuery(ps,queryNew,name,instanceCount);
	    		instanceCount++;
			}
    	}
    	else // if query has no template just print it
    	{
    		printRewrittenQuery(ps,query,name,instanceCount);
    	}
	}
    
    static Iterable<CSVRecord> loadInstantitationValues(String name)
    {
    	Iterable<CSVRecord> records = null;
		try
		{
			name = name.replaceFirst("-", "- - ");
			Reader in = new FileReader(name+".tsv");
			records = CSVFormat.TDF.withHeader().parse(in);
		} catch ( IOException e) { e.printStackTrace(); } 
    	return records;
    }
    
    public static void main(String[] args) 
	{
    	//System.out.println(testQuery);
    	try
		{
			parseArguments(args);
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try (BufferedReader br = Files.newBufferedReader(Paths.get(inputFilePath))) 
    	{
    		String line =null; StringBuilder query =null; String name=null;
    		do 
    		{
    			line = br.readLine();
    			if (line==null || line.startsWith("%%%%%%%%%%%%%%%%%%%"))
    			{
    				if (query!=null) // found new query ----> process old first
    				{
    					for ( Class<? extends AbstractRepresenationQueryRewriter> r: representations.keySet())
    					{
    						try {
    							//r.getConstructor(String.class).newInstance(query.toString()).rewriteQuery(representations.get(r));
    							printQueryInstantiations(representations.get(r),r.getConstructor(String.class).newInstance(query.toString()).rewriteQuery(),name);
    						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException| InvocationTargetException | NoSuchMethodException | SecurityException e) {e.printStackTrace();}
    					}
    				}
    				if (line!=null)
    				{
    					name = line.substring(line.lastIndexOf("%")+1).trim();//get name
        				query = new StringBuilder();
    				}
    			}
    			else
    				query.append(line+"\n");
    		} while (line!=null);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			for (Class<? extends AbstractRepresenationQueryRewriter> r : representations.keySet())
			{
				PrintStream ps = representations.get(r);
				ps.close();
			}
		}
    	
	}
    
	static void parseArguments(String[] args) throws IOException 
	{
		Options options = new Options();

		Option input = new Option("i", "input", true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file path pattern (file name pattern without file suffix)");
		//output.setRequired(true);
		options.addOption(output);
		

		Option disableTemplates = new Option("d", "disableTemplates", false, "when set the templates are not being replaced by its instantion values )");
		//output.setRequired(true);
		options.addOption(disableTemplates);
//		
//		Option threads = new Option("t", "threads", true, "the number of threads which should be used for parallel reading");
//		//output.setRequired(true);
//		options.addOption(threads);
//		
		Option format = new Option("f", "formats", false, "list of representation formats which should be used or all (default)");
		//format.setRequired(true); 
		format.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(format);
		
		Option properties  = OptionBuilder.withArgName( "property=value" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "use value for given property" )
                .create( "D" );
		options.addOption(properties);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("meta-sparql", options);

			System.exit(1);
			return;
		}

        inputFilePath = cmd.getOptionValue("input");
        outputFilePattern = cmd.getOptionValue("output");
        templateInstances = ! cmd.hasOption("disableTemplates") ; if (!templateInstances) System.out.println("Warning: templates are disabled. You need to replace them by your own!");
//        Meta.options =new org.aksw.sdw.meta_rdf.Options(cmd.getOptionProperties("D"));
//        numThreads = cmd.hasOption("threads") ? Integer.parseInt(cmd.getOptionValue("threads")) : 0;
//        
        String all[] = {"all"};  
        String formats[] = (cmd.getOptionValues("formats")!=null) ? cmd.getOptionValues("formats") : all;
        
        for (String f :  formats)
		{
        	Class<? extends AbstractRepresenationQueryRewriter> g;
			if (f.equalsIgnoreCase("ngraphs") || f.equalsIgnoreCase("all"))
				representations.put(	g = GraphRepresentationRewriter.class,   					createPS(outputFilePattern+	"-ngraphs.txt"	)   ); 
			if (f.equalsIgnoreCase("sgprop")  || f.equalsIgnoreCase("all"))
				representations.put(	g = SingletonPropertyRepresentationRewriter.class, 			createPS(outputFilePattern+	"-sgprop.txt"	)   );		
			if (f.equalsIgnoreCase("stdreif") || f.equalsIgnoreCase("all"))
				representations.put(	g = StandardReificationRepresentationRewriter.class, 		createPS(outputFilePattern+	"-stdreif.txt"	)   );		
			if (f.equalsIgnoreCase("rdr")     || f.equalsIgnoreCase("all"))
				representations.put(	g = RdrRepresentationRewriter.class, 						createPS(outputFilePattern+	"-rdr.txt"		)   );		
			if (f.equalsIgnoreCase("cpprop")  || f.equalsIgnoreCase("all"))
				representations.put(	g = CompanionPropertyRepresentationRewriter.class, 			createPS(outputFilePattern+	"-cpprop.txt"	)   );
			if (f.equalsIgnoreCase("nary")    || f.equalsIgnoreCase("all"))
				representations.put(	g = NaryRelationRepresentationRewriter.class, 			    createPS(outputFilePattern+	"-naryrel.txt"	)   );
			if (f.equalsIgnoreCase("data")    || f.equalsIgnoreCase("all"))
				representations.put(	g = RegularDataRepresentationRewriter.class, 			    createPS(outputFilePattern+	"-data.txt"		)   );
		}

    }
	
	static PrintStream createPS(String filename) throws IOException
	{
		return new PrintStream(Paths.get(filename).toFile());
	}
	
	 static String testQuery = 
	 		    "        # from all triples, query the birth dates for each distinct person,		"
			 +"\n        # that was modified recently.		"
			 +"\n         prefix dbo: <http://db.de/o/> 		"
			 +"\n         prefix meta: <http://meta.de/> 		"
			 +"\n         prefix dc: <http://dc.de/> 		"
			 +"\n         base <http://db.de/r/> 		"
			 +"\n        # from all triples, query the birth dates for each distinct person, that was modified recently.		"
			 +"\n        SELECT ?person ?birth {		"
			 +"\n            {  #!data(?g,?person,dbo:birthYear,?birth)!# 		"
			 +"\n              {#!meta(?g,dc:modified,?modified)!#}		"
			 +"\n            } FILTER NOT EXISTS {		"
			 +"\n                #!data(?g2,?person2,dbo:birthYear,?birth2)!#		"
			 +"\n                {#!meta(?g2,dc:modified,?modified2)!# }		"
			 +"\n                FILTER ( ?person = ?person2 && ?modified2 > ?modified )		"
			 +"\n            }		"
			 +"\n        } ";

//	
//	static void processLine(String line)
//	{
//		int thisLineNr = linesCount.getAndIncrement();
//		if (thisLineNr % 1000 == 0)
//			System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date())+" processed units: "+linesCount);
//		MetaStatementsUnit msu;
//		try
//		{
//			msu = Meta.readMetaStatementsUnit(line);
//			for (AbstractRepresentationFormat r : representations.keySet())
//			{
//			    PrintStream ps = representations.get(r);
//			    Collection<RdfQuad> l = r.getRepresenationForUnit(msu);
//				r.writeQuads(l, ps);
//			}
//		} catch (Exception e)
//		{
//			System.err.println("An Error occured during processing the metastatementsUnit #"+thisLineNr);
//			e.printStackTrace();
//			System.err.println(line);
//		}
//		
////		AbstractRepresentationFormat f = new GraphRepresentation();
////		Collection<RdfQuad> l = f.getRepresenationForUnit(msu);
////		Meta.writeQuads(l, ps);
////		try(PrintStream ps = new PrintStream(Paths.get(outputFilePath).toFile(),))
////		{
////			Meta.writeQuads(l, ps);//FileOutputStream(Paths.get(outputFilePath).toFile()));
////		} catch (FileNotFoundException e)
////		{
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} 
//	}
//
}
