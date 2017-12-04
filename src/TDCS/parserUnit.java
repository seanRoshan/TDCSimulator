package TDCS;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class parserUnit {
	
	private ArrayList<String> linesDatabase = new ArrayList<String>(); // store lines that have been read from the trace file
	private ArrayList<String> tokenArray = new ArrayList<String>();    // store token that have been read from the given line
	private int lineIndex;  // Index to the location of next line  in the linesDatabase 
	private int tokenIndex;  // Index to the location of next token in the line 
	
	private  simulatorUnit simUnit = new simulatorUnit(); 
	
	public simulatorUnit getSimUnit (){
		return (simUnit);
	}
	
	
	// Parser Unit constructor
	public  parserUnit (ArrayList<String> inputDataBase){
		   linesDatabase = inputDataBase; // Get Database from input
		   lineIndex = 0;
		   //System.out.println("Database Size: "+linesDatabase.size());
	   }
	
	// Read and return the nextline in the database
	// Create a free buffer for the line
	private  String getnextLine() {
		String line = linesDatabase.get(lineIndex);
		tokenArray = new ArrayList<String>();
		tokenIndex = 0;
		lineIndex++;
		return (line);
	}
	
	// Get a line and tokenize it and store tokens inside a buffer
	private void tokenizer(String line) {
		StringTokenizer ST = new StringTokenizer(line);
		int tokenCount = ST.countTokens();;
		for (int index = 0; index<tokenCount; index++){
			tokenArray.add(ST.nextToken());
		}
	}
	
	// Get the next token inside the buffer
	private String getnextToken() {
		String nextToken = tokenArray.get(tokenIndex);
		tokenIndex++;
		return (nextToken);
	}
	
	// Get total number of Lines in the databese
	private int getLineCount (){
		return linesDatabase.size();
	}
	
	// Get total number of tokens in buffer
	private int getTokenCount(){
		return tokenArray.size();
	}

	
	// Convert string to integer
    private Integer integerParser (String text, String type ) {
		  try {
			  		int value = Integer.parseInt(text);

			  		if (type.equals("Associativity")){
			  			
				  		if (value >= 0 ){
				  			return value;
				  		}
				  		else {
				  			return -1;
				  		}	
			  		}
			  		else {
			  			if (value >= 0){
			  				return value;
			  			}
			  			else {
			  				return -1;
			  			}
			  		}
			  		
			  		
			  		
		         
		  } 
		  catch (NumberFormatException e) {
		         return -1;
		  }
	}
    
    // Convert string to long
    private Long longParser (String text, String type) {
		  try {
		         return Long.parseLong(text,16);
		  } 
		  catch (NumberFormatException e) {
		         return -1L;
		  }
	}
    
    // Check values for the replacement Policy in the config
    private int checkReplacementPolicy (String text, String type){
    	
    	if ( text.equals("R") || text.equals("LRU") || text.equals("FIFO") )
    		return 1;
    	else {
    		System.out.println("Warning: Invalid "+type+" Value!");
    		return 0;
    	}
    		
    }
	
    // Parse the trace file
    public void parseTrace (fileUnit resultFileunit) {
    	System.out.println("Parsing Trace file has been started!");
    	int lineCount = this.getLineCount(); // Get number of lines inside the database
    	int currentCount = 0 ;
    	int operation = 0;
    	long address = 4294967295L; 
    	String token;
    		
    	while (currentCount<lineCount){
    		String currentLine = this.getnextLine();
    		this.tokenizer(currentLine); // tokenize the fetched line inside the database
    		if (this.getTokenCount()>=2){
    		token = this.getnextToken(); // first token if '0' or '1' or '2'
    		//System.out.println("token: "+token);
    		
    		if (this.integerParser(token,"Operation")!= -1) {
    			operation = this.integerParser(token,"Operation");
    			//System.out.println("operation: "+operation);
    			if ( (operation<0) && (operation>2) ){
    				System.out.println("Warning: Invalid Operation at Line "+(currentCount+1)+" and replaced by '0'!");
    				operation = 0; 
    			}
    		}
    		token = this.getnextToken(); // Second Token
    		
    		if (this.longParser(token,"Address")!= -1L) {
    			address = this.longParser(token,"Address");
    			if ( (address<0) && (address>4294967295L) ){
    				System.out.println("Warning: Invalid Address at Line "+(currentCount+1)+" and replaced by 'FFFFFFFF'!");
    				address = 4294967295L; 
    			}
    		}
    		}
    		else{
    			System.out.println("Warning: Invalid Operation at Line "+(currentCount+1)+" and replaced by '0'!");
				operation = 0;
				System.out.println("Warning: Invalid Address at Line "+(currentCount+1)+" and replaced by 'FFFFFFFF'!");
				address = 4294967295L;
    		}
    		currentCount++;
    		if ( (operation==0) || (operation==1) ){ // Ignore instruction cache [Operation ==2]
    		simUnit.traceArray.add(simUnit.creatTraceObject(operation, address));
    		}
    	}
    	simUnit.printTrace(resultFileunit);

    }
    
    // Parse the configuration file
	public void parseConfig () {
	
		System.out.println("Parsing Configuration file has been started!");
		int lineCount = this.getLineCount(); // Get number of lines inside the database
		int currentCount = 0 ; 
		
		
		while (currentCount<lineCount){
			
			String currentLine = this.getnextLine();
			this.tokenizer(currentLine); // tokenize the fetched line inside the database
			String token = this.getnextToken(); // first token if '$'
				if (token.equals("$")){
					token = this.getnextToken(); // next token after '$'
					int value = 0;
					switch (token){
						case "CacheBlockSize": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"CacheBlockSize")!= null) {
								value = this.integerParser(token,"CacheBlockSize");
								simUnit.confObj.setCacheBlockSize(value);
							}
							else {
								System.out.println("Simulation set default "+"CacheBlockSize"+" value : "+simUnit.confObj.getCacheBlockSize()+" byte!");
							}
							break;
						}
						case "PageSize": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"PageSize")!= null) {
								value = this.integerParser(token,"PageSize");
								simUnit.confObj.setPageSize(value*1024);
							}
							else {
								System.out.println("Simulation set default "+"PageSize"+" value : "+simUnit.confObj.getPageSize()+" byte!");
							}
							break;
						}
						case "CacheSize": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"CacheSize")!= null) {
								value = this.integerParser(token,"CacheSize");
								simUnit.confObj.setCacheSize(value*1024);
							}
							else {
								System.out.println("Simulation set default "+"CacheSize"+" value : "+simUnit.confObj.getCacheSize()+" byte!");
							}
							break;
						}
						case "Associativity": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"Associativity")!= null) {
								value = this.integerParser(token,"Associativity");
								simUnit.confObj.setAssociativity(value);
							}
							else {
								System.out.println("Simulation set default "+"Associativity"+" value : "+simUnit.confObj.getAssociativity()+"!");
							}
							break;
						}
						case "ReplacementPolicies": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.checkReplacementPolicy(token,"ReplacementPolicies") == 1) {
								simUnit.confObj.setReplacementPolicies(token);
							}
							else {
								System.out.println("Simulation set default "+"ReplacementPolicies"+" value : "+simUnit.confObj.getReplacementPolicies()+"!");
							}
							break;
						}
						case "ClockFrequency": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"ClockFrequency")!= null) {
								value = this.integerParser(token,"ClockFrequency");
								simUnit.confObj.setClockFrequency(value*1000000.0);
							}
							else {
								System.out.println("Simulation set default "+"ClockFrequency"+" value : "+simUnit.confObj.getClockFrequency()+"!");
							}
							break;
						}
						case "CacheHitTime": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"CacheHitTime")!= null) {
								value = this.integerParser(token,"CacheHitTime");
								simUnit.confObj.setCacheHitTime(value);
							}
							else {
								System.out.println("Simulation set default "+"CacheHitTime"+" value : "+simUnit.confObj.getCacheHitTime()+"!");
							}
							break;
						}
						case "CacheMissPenalty": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"CacheMissPenalty")!= null) {
								value = this.integerParser(token,"CacheMissPenalty");
								simUnit.confObj.setCacheMissPenalty(value);
							}
							else {
								System.out.println("Simulation set default "+"CacheMissPenalty"+" value : "+simUnit.confObj.getCacheMissPenalty()+"!");
							}
							break;
						}
						case "AddressLine": {
							this.getnextToken();         // get ':'
							token = this.getnextToken(); // get actual value  
							if (this.integerParser(token,"AddressLine")!= null) {
								value = this.integerParser(token,"AddressLine");
								simUnit.confObj.setAddressLine(value);
							}
							else {
								System.out.println("Simulation set default "+"AddressLine"+" value : "+simUnit.confObj.getAddressLine()+"!");
							}
							break;
						}
						default: {
						}
				    }
					
				}
		        currentCount++;
		}
		
	}
}
