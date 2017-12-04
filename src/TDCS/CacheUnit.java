package TDCS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import TDCS.simulatorUnit.traceObject;

public class CacheUnit {
	
	
	private String replacementPolicy;
	
	private fileUnit resultFileunit;
	
	private int  missTime;
	private int  hitTime;
	
	private int  blockSize;
	private int  pageSize;
	private long cacheSize;
	private int  Associativity;
	private int  wordSize;
	
	private int numberOfBlocks; 
	private int numberOfSets;
	private int numberOfWays;
	
	private int offsetBits;
	private int indexBits;
	private int tagBits;
	private int addressBits;
	
	private int setCount;
	private int way;
	
	private int offset;
	private int index;
	private int tag; 
	
	private ArrayList<traceObject> traceArray ;
	private int cacheMemory [][]; // Create cacheMemory [numberOfSets][numberOfWays]
	private int nextFreeWay [];   // Store next way available position to avoid extra search in simulation to find a freeway 
	
	private replacementUnit replaceUnit; // Handle Replacements
	
	private statObject statObj;
	
	
	private class statObject {
		
		
		private int hit;
		private int miss;
		
		
		private int coldMiss;
		private int capacityMiss;
		private int conflictMiss;
		
		private int loadHit ;
		private int storeHit ;
	    private int loadMiss ;
		private int storeMiss ;
		
		private int loadColdMiss;
		private int storeColdMiss;
		
		private int loadConflicMiss;
		private int storeConflictMiss;
		
		private int loadCapacityMiss;
		private int storeCapacityMiss;
		
		private double missRate;
		private double AverageMemoryAccessTime;
		
		public statObject(){
			loadHit = 0;
			storeHit = 0;
		    loadMiss = 0;
			storeMiss = 0;
			capacityMiss = 0;
			coldMiss = 0;
			conflictMiss = 0;
			loadColdMiss = 0;
			storeColdMiss = 0;
			loadConflicMiss = 0; 
			storeConflictMiss = 0;
			loadCapacityMiss = 0;
			storeCapacityMiss = 0;
			missRate = 0;
			AverageMemoryAccessTime=0;
		}
		
		private void calculateColdMiss(){
			coldMiss = storeColdMiss + loadColdMiss;
		}
		
		private void calculateCapacityMiss(){
			capacityMiss = storeCapacityMiss + loadCapacityMiss;
		}
		
		private void calculateConflictMiss(){
			conflictMiss = storeConflictMiss + loadConflicMiss;
		}
		
		
		private void calculateMissRate(){
			missRate = (double)miss/(double)(hit+miss);
		}
		
		private void calculateAverageMemoryAccessTime(){
			AverageMemoryAccessTime = hitTime + (missRate*missTime) ; 
		}
		
		private void calculateHit () {
			hit = loadHit+storeHit;
		}
		
		private void calculateMiss (){
			miss = loadMiss+storeMiss;
		}
		
		public void loadHit() {
			loadHit++;
		}
		
		public void storeHit(){
			storeHit++;
		}
		
		public void loadMiss(){
			loadMiss++;
		}
		public void storeMiss(){
			storeMiss++;
		}
		
		public void loadColdMiss(){
			loadColdMiss++;
		}
		public void storeColdMiss(){
			storeColdMiss++;
		}
		public void loadConflicMiss(){
			loadConflicMiss++;
		}
		public void storeConflicMiss(){
			storeConflictMiss++;
		}
		public void loadCapacityMiss(){
			loadCapacityMiss++;
		}
		public void storeCapacityMiss(){
			storeCapacityMiss++;
		}
		public void printStat(){
			calculateMiss(); // Calculate Total Miss
			calculateHit(); // Calculate Total Hit
			calculateCapacityMiss(); //Calculate Capacity Miss
			calculateColdMiss(); //Calculate Cold Miss
			calculateConflictMiss();// Calculate Conflict Miss
			calculateMissRate();
			calculateAverageMemoryAccessTime();
			
			
			resultFileunit.addLine("Hits: "+hit);
			//resultFileunit.addLine("loadHit: "+loadHit);
			//resultFileunit.addLine("storeHit: "+storeHit);
			resultFileunit.addLine("Misses: "+miss);
			//resultFileunit.addLine("loadMiss: "+loadMiss);
			//resultFileunit.addLine("storeMiss: "+storeMiss);
			//resultFileunit.addLine("loadColdMiss: "+loadColdMiss);
			//resultFileunit.addLine("loadConflicMiss: "+loadConflicMiss);
			//resultFileunit.addLine("storeConflictMiss: "+storeConflictMiss);
			//resultFileunit.addLine("loadCapacityMiss: "+loadCapacityMiss);
			//resultFileunit.addLine("storeCapacityMiss: "+storeCapacityMiss);
			resultFileunit.addLine("ColdMisses: "+coldMiss);
			resultFileunit.addLine("ConflictMisses: "+conflictMiss);
			resultFileunit.addLine("CapacityMisses: "+capacityMiss);
			resultFileunit.addLine("MissRate: "+missRate);
			resultFileunit.addLine("AverageMemoryAccessTime: "+AverageMemoryAccessTime);
			resultFileunit.addLine("##########################################################");
		}
		
	}
	
	// Clash to do replacements
    private class replacementUnit {
    	
    	//private LinkedList accessHistoryList = new LinkedList();
    	
    	private ArrayList<LinkedList> accessHistoryArray = new ArrayList<LinkedList>();
    	
    	//private LinkedList<Integer>[] accessHistoryArray = new LinkedList<Integer>[5];
    	
    	public replacementUnit (int numberOfSets){
    		
    		//System.out.println("Number of Sets: "+numberOfSets);
    		
    		for (int i=0; i<numberOfSets; i++){
    			// For each set we keep track of Accesses
    			LinkedList<Integer> temp = new LinkedList<Integer>(); 
    			accessHistoryArray.add(temp);
    		}
    		
    		//System.out.println("Array Size: "+accessHistoryArray.size());
    		
    		/*
    		for (int i=0; i<numberOfSets; i++){
    			// For each set we keep track of Accesses
    			System.out.println("Size of Sets["+i+"] = "+accessHistoryArray.get(i).size());
    		}
    		*/
    		
    	}
    	
    	// Generate random number [0, number of ways(blocks) inside each set]
    	private int getRandom (int max){
    		
    		Random randomGenerator = new Random();
    		
    		int randomNumber = 0;
    		
    		randomNumber = randomGenerator.nextInt(max);
    		
    		return randomNumber;
    	}
    	
    	public void updateQueue (int setNumber, int wayNumber){
    		
    		int foundIndex =-1 ;
    		
    		
    		// System.out.println("HistoryArray Size: "+accessHistoryArray.get(setNumber).size());
    		// Search to see if the way is inside the queue
    		if (accessHistoryArray.get(setNumber).size()>0){
    			foundIndex = accessHistoryArray.get(setNumber).indexOf(wayNumber);
    		}
    		
    		//System.out.println(foundIndex);
    		
    		if (foundIndex==-1){
    			// NOT FOUND
    			//System.out.println("Size before: "+accessHistoryArray.get(setNumber).size());
    			//System.out.println("Way not found in the set queue! "+wayNumber+" added!");
    			accessHistoryArray.get(setNumber).addFirst(wayNumber);	
    			//System.out.println("Size After: "+accessHistoryArray.get(setNumber).size());
    		}
    		else {
    			// FIFO
    			if (replacementPolicy.equals("FIFO")){
    				// Found in the queue, so leave it as it was 
    				// The first time that we cache the line is important
    			}
    			// LRU
    			else if (replacementPolicy.equals("LRU")){
    				 // Remove found way from the linkedlist
    				 accessHistoryArray.get(setNumber).remove(foundIndex);
    				 // Add the way to the beginning of the linkedlist
    				 accessHistoryArray.get(setNumber).addFirst(wayNumber);	
    			}
    			else {
    				System.out.println("Something goes wrong with the replacement policy!");
    			}
    		}
    				
    	}
    	
    	public int getVictim (int index){
    		
    		int victimWay = 0; // Return the way we can read on it
    		
    		if (replacementPolicy.equals("RANDOM")){
    			System.out.println("'RANDOM' repleacement policy has been selected!");
    			victimWay = this.getRandom(numberOfWays);
    			//System.out.println("Number of ways: "+numberOfWays+" Number of Blocks: "+numberOfBlocks+" Victim: "+victimWay);
    			return victimWay;
    		}
    		else if (replacementPolicy.equals("FIFO")) {
    			//System.out.println("'FIFO' repleacement policy has been selected!");
    			Object temp = this.accessHistoryArray.get(index).getLast();
    			this.accessHistoryArray.get(index).removeLast();
    			victimWay = (int) temp;
    			return victimWay;
    		}
    		else if (replacementPolicy.equals("LRU")) {
    			//System.out.println("'LRU' repleacement policy has been selected!");
    			Object temp = this.accessHistoryArray.get(index).getLast();
    			this.accessHistoryArray.get(index).removeLast();
    			victimWay = (int) temp;
    			return victimWay;
    		}
    		else {
    			System.out.println("Something goes wrong with the replacement policy!");
    			return victimWay;
    		}
    	}
    	
    	public void printQueue (){
    		
    		for (int set=0; set<accessHistoryArray.size(); set++){
    			System.out.print("\nQueue:\t");
    			for (int way=0; way<accessHistoryArray.get(set).size(); way++){
					System.out.print(accessHistoryArray.get(set).get(way)+"\t");
			    }
    		}
    		System.out.println();
    			
    	}
    }
	
	
	// Initialize parameters of cache unit and the way that cache is configured
	public CacheUnit (simulatorUnit simUnitConfig, simulatorUnit simUnitTrace, fileUnit resultFileunitInput){
		
		resultFileunit = resultFileunitInput;
		
		hitTime = simUnitConfig.confObj.getCacheHitTime();
		missTime = simUnitConfig.confObj.getCacheMissPenalty();
		
		replacementPolicy = simUnitConfig.confObj.getReplacementPolicies();
		
		blockSize = simUnitConfig.confObj.getCacheBlockSize();
		pageSize = simUnitConfig.confObj.getPageSize();
		cacheSize = simUnitConfig.confObj.getCacheSize();
		Associativity = simUnitConfig.confObj.getAssociativity();
		addressBits = simUnitConfig.confObj.getAddressLine();
		numberOfBlocks = (int) (cacheSize/blockSize);
		wordSize = 1; // Byte order
		if (Associativity > 0){ // Not FullyAssociative
			numberOfSets = numberOfBlocks/Associativity; 
			numberOfWays = Associativity;
		}
		else {
			numberOfSets = 1 ; // Fully Associative
			numberOfWays = numberOfBlocks;
		}
				
		cacheMemory = new int [numberOfSets][numberOfWays] ;
		nextFreeWay = new int [numberOfSets];
		
		traceArray = simUnitTrace.traceArray;
		
		statObj = new statObject();
		
		replaceUnit = new replacementUnit(numberOfSets);
		
		this.calculateAddressBits(); // Calculate bits for each part of the address
		this.cacheInitialization(); // Initialize Cache
		this.addressDecoder(traceArray.get(0).getAddress());
		this.printCacheStructure();
		this.printAddressBits();
		this.runSimulation();
		statObj.printStat();
		
		
	}
	
	/*
	private String hexToBin (String Address){
		return (new BigInteger(Address,16).toString(2));
	}
	*/
	
	
	// Convert long to binary
	private String longToBin (long Address){
		String temp = Long.toBinaryString(Address);
		String binaryFormat = "";
				
		int length = temp.length();
		
		for (int i=0; i<addressBits-length; i++){  // Zero extension to fit address 
			binaryFormat = binaryFormat+"0";
		}
		//System.out.println("Address: "+Address);
		//System.out.println("Binary Format before: "+binaryFormat+" Length: "+binaryFormat.length());
		//System.out.println("TEMP before: "+temp+" Length: "+temp.length());		
		binaryFormat = binaryFormat+temp;
		//System.out.println("Binary Format before: "+binaryFormat+" Length: "+binaryFormat.length());	
		
		return (binaryFormat);
	}
	
	
	
	// Convert binary to long
	private long binToLong (String Address){
		if (Address.equals("")){
			return 0;
		}
		else {
			return ( new BigInteger(Address,2).longValue() );
		}
	}
	
	// Decode different part of each address and set bits
	private void addressDecoder (long Address){
		
		String Binary = this.longToBin(Address);
		
		//System.out.println("Long: "+Address+" Binary: "+Binary);
		
		int endPoint = Binary.length();
		int startPoint = endPoint-offsetBits;
		String Offset = Binary.substring(startPoint,endPoint); 
		//System.out.println("startPoint: "+startPoint+" endPoint: "+endPoint+" Offset: "+Offset);
		
		endPoint = startPoint;
		startPoint = endPoint - indexBits;
		String Index  = Binary.substring(startPoint,endPoint);
		//System.out.println("startPoint: "+startPoint+" endPoint: "+endPoint+" Index: "+Index);
		
		endPoint = startPoint;
		startPoint = 0;
		String Tag = Binary.substring(startPoint,endPoint);
		//System.out.println("startPoint: "+startPoint+" endPoint: "+endPoint+" Tag: "+Tag);
		
		offset = (int) this.binToLong(Offset);
		index = (int) this.binToLong(Index);
		tag = (int) this.binToLong(Tag);
	
		//System.out.println("Offset: "+offset+" index: "+index+" tag: "+tag);
		
	}
	
	// Search for a tag inside a specific set and return pos on detection or '-1'
	private int searchSet (int tag, int index, int wayPos ){
		for (int pos=0; pos<wayPos; pos++){
			if (cacheMemory[index][pos] == tag) {
				return pos;
			}	
		}
		return -1; // Not detected!
	}
	
	// Run simulation
	private void runSimulation(){
		
		
		//this.replaceUnit.getVictim(1);
		//this.replacementUnit(1);	
		
		int result = 0;
		
		int type;
		long address;
		
		for (int traceIndex=0; traceIndex<traceArray.size(); traceIndex++){
			
			type = traceArray.get(traceIndex).getType();
			address = traceArray.get(traceIndex).getAddress();
			
			result = this.referenceUnit(address);
			
			//System.out.println("Status: "+result);
		
			this.updateStatus(result, type); // Update Status 
			//this.replaceUnit.printQueue();
		}
	}
	
	// Update the status of the cache
	private void updateStatus (int result, int type){
	
		
		if (type == 0) { // LOAD
			
			switch (result) {
			
				case (0): {
					// Cold Miss
					statObj.loadColdMiss();	
					statObj.loadMiss();
					break;
				}
				
				case (1): {
					// Conflict Miss
					statObj.loadConflicMiss();
					statObj.loadMiss();
					break;
				}
				
				case (2):{
					// Capacity Miss
					statObj.loadCapacityMiss();
					statObj.loadMiss();
					break;
				}
				
				case(3):{
					// HIT
					statObj.loadHit();
					break;
				}
				
				default: {
				   System.out.println("Something is wrong with update status result!");
				   break;
				}
			
			}
				
		}
	
      if (type == 1) { // LOAD
			
			switch (result) {
			
				case (0): {
					// Cold Miss
					statObj.storeColdMiss();	
					statObj.storeMiss();
					break;
				}
				
				case (1): {
					// Conflict Miss
					statObj.storeConflicMiss();
					statObj.storeMiss();
					break;
				}
				
				case (2):{
					// Capacity Miss
					statObj.storeCapacityMiss();
					statObj.storeMiss();
					break;
				}
				
				case(3):{
					// HIT
					statObj.storeHit();
					break;
				}
				
				default: {
				   System.out.println("Something is wrong with update status result!");
				   break;
				}
			
			}
				
		}	
	}
	
	// Reference cache cells for read and write
	private int referenceUnit(Long Address){
		
		this.addressDecoder(Address); // Decode address and set index, tag, and offset
		
		int status = 0; // Cold Miss 0
		                // Conflict Miss 1
		                // Capacity Miss 2
		                // Hit 3
		
		int wayPos = 0;
		int currentContent = 0;
		
		// Direct Map
		if (numberOfWays == 1){ 
			
			currentContent = cacheMemory[index][0]; // Get the Content of each Set 
			
			// Initial Value is '-1' 
			// When current content equal to '-1' Cache does not have content
			
			if (currentContent == -1) { 
				// Cold Miss
				status = 0;
				cacheMemory[index][0] = tag; // I store just tag inside the memory, Data does not matter
				return status;
			} else {
				if (currentContent == tag){
					// HIT
					status = 3;
					return status;
				}
				else {
					// Conflict Miss 
					status = 1;
					cacheMemory[index][0] = tag;
					return status;
				}
			}
		} 
		// FullyAssociative 
		else if (Associativity == 0){
			
			wayPos = nextFreeWay[0]; // Get current Free way for specified set (We have only one set Index = 0)
			
			int findPos = this.searchSet(tag, 0, wayPos); // Check if you can find a tag in the set or not
			
			if ( (findPos==-1) && (wayPos!=numberOfWays)) {  // We have free black block in set 
				// NOT FOUND
				currentContent = cacheMemory[0][wayPos]; // Get current content 
				if (currentContent == -1) { 
					// Cold Miss
					status = 0;
					cacheMemory[0][wayPos] = tag;     // I store just tag inside the memory, Data does not matter
					this.replaceUnit.updateQueue(0, wayPos);
					nextFreeWay[0] = wayPos+1;        // Increase to the next waypos
					return status;
				}
				else {
					System.out.println("Should not enter this region! Something is wrong with the number of ways!");
					return status;
				}
			}
			else if ((findPos==-1) && (wayPos==numberOfWays) ) { //Not way left
					// Capacity Miss
					status = 2;
					int victimWay = this.replaceUnit.getVictim(0);
					System.out.println("Victim: "+victimWay);
					cacheMemory[0][victimWay] = tag;
					this.replaceUnit.updateQueue(0, victimWay);
					return status;
			}
			else {
					// HIT 
					status = 3;
					//cacheMemory[0][wayPos] = tag;     // I store just tag inside the memory, Data does not matter
					//nextFreeWay[0] = wayPos+1;        // Increase to the next waypos
					System.out.println("Find POS: "+findPos);
					this.replaceUnit.updateQueue(0, findPos);
					return status;	
				}
				
		}
		// Set Associative 
		else {
			
			wayPos = nextFreeWay[index]; // Get current Free way for specified set (We have only one set Index = 0)
			
			int findPos = this.searchSet(tag, index, wayPos); // Check if you can find a tag in the set or not
			
			if (findPos==-1 && (wayPos!=numberOfWays)){
				//NOT FIND
				currentContent = cacheMemory[index][wayPos]; // Get current content 
				if (currentContent == -1) { 
					// Cold Miss
					status = 0;
					cacheMemory[index][wayPos] = tag;     // I store just tag inside the memory, Data does not matter
					this.replaceUnit.updateQueue(index, wayPos);
					nextFreeWay[index] = wayPos+1;        // Increase to the next waypos
					return status;
				}
				else {
					System.out.println("Should not enter this region! Something is wrong with the number of ways!");
					return status;
				}
			}
			else if ((findPos==-1) && (wayPos==numberOfWays) ) { //Not way left
					// Conflict Miss
					status = 2;
					int victimWay = this.replaceUnit.getVictim(index);
					System.out.println("Victim: "+victimWay);
					cacheMemory[index][victimWay] = tag;
					this.replaceUnit.updateQueue(index, victimWay);
					return status;
			}
			else {
					// HIT 
					status = 3;
					//cacheMemory[index][wayPos] = tag;     // I store just tag inside the memory, Data does not matter
					//nextFreeWay[index] = wayPos+1;        // Increase to the next waypos
					System.out.println("Find POS: "+findPos);
					this.replaceUnit.updateQueue(index, findPos);
					return status;	
				}
			
		}
	}
	
	
	
	// Initialize cache cells with -1 to detect cold miss in future
	private void cacheInitialization (){
		
		for (int setIndex=0; setIndex<numberOfSets; setIndex++) {
			for (int wayIndex=0; wayIndex<numberOfWays; wayIndex++){
				cacheMemory [setIndex][wayIndex] = -1; // To detect cold misses
													   // on use we store tag to detect conflict  
			}
			nextFreeWay[setIndex] = 0; 
		}
	}
		
	// Calculate each part of address
	private void calculateAddressBits(){
		
		offsetBits = (int) (Math.log(blockSize) / Math.log(2));
		indexBits =  (int) (Math.log(numberOfSets) / Math.log(2));
		tagBits = addressBits - indexBits - offsetBits;   
	}

	
	// Print they way that our cache is organized
	private void printCacheStructure (){
		
		resultFileunit.addLine("numberOfWays: "+Associativity);
		resultFileunit.addLine("numberOfBlocks: "+numberOfBlocks);
		resultFileunit.addLine("numberOfSets: "+numberOfSets);
		resultFileunit.addLine("##########################################################");
		//System.out.println("Last cacheMemory: "+cacheMemory[numberOfSets-1][numberOfWays-1]);
		//System.out.println("Last nextFreeWay: "+nextFreeWay[numberOfSets-1]);
		
	}
    
	
	// Print each part of address represent what 
	private void printAddressBits(){
		
		resultFileunit.addLine("offsetBits: "+offsetBits);
		resultFileunit.addLine("indexBits: "+indexBits);
		resultFileunit.addLine("tagBits: "+tagBits);
		resultFileunit.addLine("##########################################################");
	}
}
