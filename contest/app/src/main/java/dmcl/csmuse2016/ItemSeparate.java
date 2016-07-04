package dmcl.csmuse2016;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemSeparate {
    private String response;
    private ArrayList<HashMap<String,String>> list;

    public ItemSeparate(String response){
        this.response=response;
        list=new ArrayList<HashMap<String,String>>();
    }

    public ArrayList<HashMap<String, String>> getItem(){
        String[] items=response.split("@@@@@");

        for(int i=0;i<items.length;i++){
            String[] content = items[i].split("###");
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("Surname", content[0]);
            map.put("Name", content[1]);
            map.put("Account", content[2]);
            map.put("Password", content[3]);
            map.put("sex", content[4]);
            map.put("birthType", content[5]);
            map.put("year", content[6]);
            map.put("month", content[7]);
            map.put("day", content[8]);
            map.put("hour", content[9]);
            list.add(map);
        }
        return this.list;
    }
/*	public boolean loginCheckingItem(String ans){

	}*/


}