package TDCS;
import java.io.*;

public class TDCS {
	
	public static void main(String[] args) throws IOException {
		
        // Simulator Welcome Messages
		System.out.println("**********************************************************");
        System.out.println("Welcome to DRSVR Trace Driven Cache Simulator!");
        System.out.println("Programmed by Shahriyar Valielahi Roshan");
        System.out.println("CS203 - Advanced Computer Architecture");
        System.out.println("Department of Computer Science");
        System.out.println("University of California, Riverside");
        System.out.println("Special Thanks to Professor Daniel Wong");
        System.out.println("Copyright © 2016 DRSVR Technologies. All rights reserved.");
        System.out.println("**********************************************************");
        
        
        
        
        // Create a Trace Object with the fileUnit type to ask user for trace file and store it
        fileUnit traceFileUnit;
        while (true){
        	traceFileUnit = new fileUnit("trace"); // Read TraceFile
            if (traceFileUnit.readFile()!=0){
            	// if we successfully read the file break
            	// otherwise ask user to re-enter the file path
            	break;
            }
        }
        
        
        fileUnit resultFileunit = new fileUnit("result");
        
        
        
        parserUnit traceParserUnit = new parserUnit(traceFileUnit.getDatabase()); // Create a parserUnit for a traceFile
        traceParserUnit.parseTrace(resultFileunit); // parse configFile
        
        
        // Create a Config Object with the fileUnit type to ask user for Config file and store it
        fileUnit configFileUnit;
        while (true){
        	configFileUnit = new fileUnit("config"); // Read TraceFile
            if (configFileUnit.readFile()!=0){
            	// if we successfully read the file break
            	// otherwise ask user to re-enter the file path
            	break;
            }
        }
        
       
        parserUnit configParserUnit = new parserUnit(configFileUnit.getDatabase()); // Create a parserUnit for a traceFile
        configParserUnit.parseConfig(); // parse configFile
        
        // Create Simulator Unit that contains required functions and variables for simulation [CacheUnit]
        simulatorUnit simUnitTrace = traceParserUnit.getSimUnit();
        simulatorUnit simUnitConfig = configParserUnit.getSimUnit();
        
        // Print Configuration
        simUnitConfig.confObj.printConfig(resultFileunit);
        
        // FileUnit resultFile // To write result.drsvr
        // I can also add .writeConfig(FileUnit resultFile)
        
        
        //Print number of trace lines = Load/Store instructions
        //System.out.println("Size TDCS: "+simUnitTrace.traceArray.size());
        
        CacheUnit myCacheUnit = new CacheUnit(simUnitConfig, simUnitTrace,resultFileunit);

        
        resultFileunit.writeFile(traceFileUnit.getType());
    }
	
}