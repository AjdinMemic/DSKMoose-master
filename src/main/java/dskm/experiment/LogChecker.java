package dskm.experiment;

import java.io.*;

public class LogChecker {

    // Increments the number.txt file for every start of the application
    public static String retNumbLog() throws IOException {

        if (logList().length == 1) {
            checkIflogEmpty();
        }

        String retVal = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader("..\\DSKMoose-master\\testlogs\\number.txt"));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                retVal += strLine;
            }

            int append = Integer.parseInt(retVal);
            append++;
            String appendStr = String.valueOf(append);

            FileWriter writer = new FileWriter("..\\DSKMoose-master\\testlogs\\number.txt");
            BufferedWriter buffer = new BufferedWriter(writer);

            buffer.write(appendStr);
            buffer.close();

            //Close the input stream
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //if(retVal.equals("0")){retVal="1";}
        return retVal;
    }

    public static String[] logList() {
        //Creating a File object for directory
        File directoryPath = new File("..\\DSKMoose-master\\testlogs");
        //List of all files and directories
        String contents[] = directoryPath.list();

        return contents;
    }

    public static void checkIflogEmpty() throws IOException {
        String[] lengthOfLogsFold = logList();
        FileWriter fileWriter = new FileWriter("..\\DSKMoose-master\\testlogs\\number.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String numb = "1";
        if (lengthOfLogsFold.length == 1) {
            bufferedWriter.write(numb);
        }
        bufferedWriter.close();
        fileWriter.close();
    }
}
