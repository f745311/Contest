package dmcl.csmuse2016;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by chenhaowei on 16/4/3.
 */
public class Write_and_Read {
    public File file;
     public Write_and_Read(String Filename,File file){

        this.file = new File(file,Filename);
    }
    public void WritetoFile( String data) {
        FileOutputStream osw = null;
        try {
            //String tem = new String( te +"1"+ "/n");
            osw = new FileOutputStream(file,true);
            //true -> not clean originally
            //osw = openFileOutput(tem,MODE_APPEND);
            osw.write(data.getBytes());
            osw.flush();
        } catch (Exception e) {

        } finally {
            try {
                osw.close();
            } catch (Exception e) {

            }
        }
    }
    public void WritetoFile_clear(String data) {
        FileOutputStream osw = null;
        try {
            //String tem = new String( te +"1"+ "/n");
            osw = new FileOutputStream(file);
            //not use true or false is false and clean originally
            //osw = openFileOutput(tem,MODE_APPEND);
            osw.write(data.getBytes());
            osw.flush();
        } catch (Exception e) {

        } finally {
            try {
                osw.close();
            } catch (Exception e) {

            }
        }
    }
    public String ReadFromFile() {
        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);

                //(concat)
            }
        } catch (Exception e) {

        } finally {
            try {
                reader.close();
            } catch (Exception e) {

            }
        }
        return data.toString();
    }
    public Boolean ifLogin(){
        if(ReadFromFile()==""){
            return false;
        }
        else
            return true;
    }
}
