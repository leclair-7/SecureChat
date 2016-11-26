import java.net.*;
import java.io.*;
import java.util.*;
import java.io.IOException;


public class testthings
{

	public static void main(String[] args) throws IOException {
	        
	    BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in));
        String userInput = ""; 
        String line = "";

		try{

			line = "April Ron Leslie Kimberly";
	        String[] namesArray = line.split("\\s+");
	        List<String> list = Arrays.asList(namesArray); 
	        Set <String> nameList = new HashSet<String>(list);
	        System.out.printf("Names: %s", nameList);

	        Boolean selected = false;
	        while( !selected )
	        {
	        	userInput = stdIn.readLine();

	        	/* in its current setup, you'll have to loop through the entire hashset */
	        	if ( nameList.contains(userInput) )
	        	{
	        		System.out.println("The thing works how you wanted it to");
	        		selected = true;
	        	}
	        	
	        }




		}catch( Exception ex){}


	    }


}