package TDCS;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class fileUnit{
	
	   private String filePath = "";  // Store path of the input file
	   private ArrayList<String> linesDatabase = new ArrayList<String>(); // store lines that have been read from the trace file
	   private String type = "";
	   private String directory;
       
	   // File unit Constructor, also reads a file from the input
	   // This constructor is the only part that communicate with the user
	   public fileUnit (String inputType){
		
		   
		   if (inputType.equals("result")==false){
			   directory = System.getProperty("user.dir")+"/";
			   type = inputType;
			   String Message = "Please put your '."+type+"' file in '"+directory+"' directory\n" +
					   "Enter the "+type+" file name [Example."+type+"]:";
			   System.out.println(Message);
			   Scanner inputScanner = new Scanner(System.in);
			   filePath = inputScanner.next();
			   Message = "File Path: "+directory+filePath;
			   System.out.println(Message);
		   } else {
			   // For result
			   directory = System.getProperty("user.dir")+"/";
			   type = inputType;
			   
			   this.addLine("##########################################################");
			   this.addLine("###                                                    ###");
			   this.addLine("###        DRSVR TRACE DRIVEN SIMULATOR RESULTS        ###");
			   this.addLine("###                                                    ###");
			   this.addLine("##########################################################");
		   }
	   }
	   
	   public String getType () {
		   return (filePath.substring(0,filePath.length()-type.length()));
	   }
	   
	   // Add line to database for write to output file
	   public void addLine(String line){
		   if (type.equals("result")){
			   // Add line just for writes
			   linesDatabase.add(line);
		   }
		   
	   }
	   
	   // Return database
	   public ArrayList<String> getDatabase(){
		   return linesDatabase;
	   }
	   
	   // Write database's lines to the output file
	   void writeFile(String traceName) throws IOException{
		   
		   BufferedWriter myBufferedWriter = new BufferedWriter(new FileWriter(directory+type+"_"+traceName+"drsvr"));
		   
		   for (int index = 0; index<linesDatabase.size(); index++){
			   myBufferedWriter.write(linesDatabase.get(index)+"\n");
		   } 
		   myBufferedWriter.flush();
		   myBufferedWriter.close();
		   
		   System.out.println("Congratulations! You can find the Result File in: "+directory+type+"_"+traceName+"drsvr");
	   }
	   
	   // Read lines of the file to the database for later processing
	   int readFile() throws IOException{
		   
		  // Variable to read a new line for the file
		  String line = null; 
		  
		  
		  try {
			  //Creates a FileReader Object
		      FileReader myFileReader = new FileReader(filePath); 
		      BufferedReader myBufferedReader = new BufferedReader(myFileReader);
		      
		      // Add instruction from tracefile to the instruction database
		      if (type.equals(type)){
		    	  int index = 0;
		    	  while((line = myBufferedReader.readLine()) != null){
		    		  linesDatabase.add(line);
			    	  index++;
			      }
		      }
		      else{
		    	  System.out.println("Invalid File!"); 
		      }
		      myFileReader.close();
		      return 1;
		  } 
		  catch (IOException e) {
			     System.out.println("Error!: Find not find!");
		         return 0;
		  }
	   }
	}