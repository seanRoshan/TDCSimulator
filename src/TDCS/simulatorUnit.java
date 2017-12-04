package TDCS;

import java.lang.Math;
import java.util.ArrayList;

public class simulatorUnit {
	
	 // Configuration Object to store Configurations
	 public configObject confObj;
	 
	 
	 // An array to store objects including operation type + address
	 public ArrayList<traceObject> traceArray = new ArrayList<traceObject>();
	
	 
	 // Simulation Unit constructor to create a new configuration object
	 public simulatorUnit(){
		   System.out.println("Simulation unit has been created!");
		   confObj = new configObject();
	 }
     
	 // Trace Object Constructor 
	 // Create a trace and initialize type and address
	 public traceObject creatTraceObject(int typeValue, long addressValue){
		 traceObject traceObj = new traceObject(typeValue, addressValue);
		 return (traceObj);
	 }
	 
	 // Print trace lines that passed the test [Actual trace to run]
	 public void printTrace(fileUnit resultFileunit) {
		 	int traceCount = traceArray.size();
		 	resultFileunit.addLine("Number of Approved Instructions: "+traceCount);
		 	/*
		 	for (int index=0; index<traceCount; index++){
		 		resultFileunit.addLine(traceArray.get(index).type+" "+Long.toHexString(traceArray.get(index).address));
		 	}
		 	*/
	 }
	 
	 public class traceObject {
		 private int   type;    // Operation type
		 private long  address; // Operation Address
		 
		 public traceObject(int typeText, long addressText) {
			type = typeText; 
			address = addressText;
		 }
		 
		 public int getType(){
			 return this.type;
		 }
		 
		 public long getAddress(){
			 return this.address;
		 }
		 
	 }

	 public class configObject {
		
		private int CacheBlockSize; // Byte
		private long CacheSize; // Byte
		private int PageSize; // Byte
		private int Associativity; // 0  Fully
		                           // 1  Direct
		private String ReplacementPolicies;
		private double ClockFrequency; // For future use 
		private int CacheHitTime;
		private int CacheMissPenalty;
		private int AddressLine;
		
		public configObject() {
			// Default Values
			CacheBlockSize = 4;    // 4 byte
			CacheSize = 1048576;   // 1 MB
			PageSize = 4096;       // 4 KB
			Associativity = 1;     // Direct Map
			ReplacementPolicies = "RANDOM";
			ClockFrequency = 1700 * (Math.pow(10,6)); // Hz 
			CacheHitTime = 1;
			CacheMissPenalty = 1;
			AddressLine = 32;
		}
		
		public void printConfig(fileUnit resultFileunit) {
			
			resultFileunit.addLine("##########################################################");
			resultFileunit.addLine("This Configuration has been used");
			resultFileunit.addLine("CacheBlockSize: "+CacheBlockSize);
			resultFileunit.addLine("CacheSize: "+CacheSize);
			resultFileunit.addLine("PageSize: "+PageSize);
			resultFileunit.addLine("Associativity: "+Associativity);
			resultFileunit.addLine("ReplacementPolicies: "+ReplacementPolicies);
			resultFileunit.addLine("ClockFrequency: "+ClockFrequency);
			resultFileunit.addLine("CacheHitTime: "+CacheHitTime);
			resultFileunit.addLine("CacheMissPenalty: "+CacheMissPenalty);
			resultFileunit.addLine("AddressLine: "+AddressLine);
			resultFileunit.addLine("##########################################################");
		}
		
		public void setCacheBlockSize (int value){
			CacheBlockSize = value;
		}
		
		public int getCacheBlockSize(){
			return CacheBlockSize;
		}
		
		public void setPageSize (int value){
			PageSize = value;
		}
		
		public int getPageSize(){
			return PageSize;
		}
		
		public void setCacheSize (long value){
			CacheSize = value;
		}
		
		public long getCacheSize(){
			return CacheSize;
		}
		
		public void setAssociativity (int value){
			Associativity = value;
		}
		
		public int getAssociativity(){
			return Associativity;
		}
		
		public void setCacheHitTime (int value){
			CacheHitTime = value;
		}
		
		public int getCacheHitTime(){
			return CacheHitTime;
		}
		
		public void setCacheMissPenalty (int value){
			CacheMissPenalty = value;
		}
		
		public int getCacheMissPenalty(){
			return CacheMissPenalty;
		}
		
		public void setClockFrequency (double value){
			ClockFrequency = value;
		}
		
		public double getClockFrequency(){
			return ClockFrequency;
		}
		
		public void setReplacementPolicies (String value){
			ReplacementPolicies = value;
		}
		
		public String getReplacementPolicies(){
			return ReplacementPolicies;
		}	
		
		public void setAddressLine (int value){
			AddressLine = value;
		}
		
		public int getAddressLine(){
			return AddressLine;
		}	
   }
}
