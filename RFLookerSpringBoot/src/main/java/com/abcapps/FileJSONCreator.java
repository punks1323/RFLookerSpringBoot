package com.abcapps;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class FileJSONCreator {
    public static void main(String[] args) {
        File file = new File("/home/pankaj/Desktop/smartcc_release_QA");
        JSONArray dirJSONArr = new JSONArray();
        JSONObject fileJSONObj = new JSONObject();
        //p(createJSONTree(file, dirJSONArr, fileJSONObj).toString());
        createJSONTree(file, dirJSONArr);
        System.out.println(dirJSONArr);
    }

    static void p(String msg) {
        System.out.println(msg);
    }


    private static JSONArray createJSONTree(File rootFile, JSONArray fileJSONArray) {

        boolean isFile = rootFile.isFile();
        String name = rootFile.getName();

        if (isFile) {
            JSONObject fileJSONObj = new JSONObject();
            fileJSONObj.put("type", "F");
            fileJSONObj.put("size", rootFile.length());
            fileJSONObj.put("name", name);
            fileJSONArray.put(fileJSONObj);
        } else {

            JSONObject dirJSONObject = new JSONObject();
            dirJSONObject.put("type", "D");
            dirJSONObject.put("name", name);
            JSONArray dirJSONArr = new JSONArray();
            dirJSONObject.put(name, dirJSONArr);
            fileJSONArray.put(dirJSONObject);

            File[] files = rootFile.listFiles();

            if (files == null) {
                p("Access Denied: " + rootFile.getName());
            } else
                for (int i = 0; i < files.length; i++) {
                    createJSONTree(files[i], dirJSONArr);
                }

        }

        return fileJSONArray;
    }












    /*private static JSONObject createJSONTree(File rootFile, JSONArray fileJSONArray, JSONObject finalJSONObject ) {

        boolean isFile = rootFile.isFile();
        String name = rootFile.getName();

        if ( isFile ) {
            JSONObject fileJSONObj = new JSONObject();
            fileJSONObj.put("type", "F");
            fileJSONObj.put("size", rootFile.length());
            fileJSONObj.put("name", name);
            fileJSONArray.put(fileJSONObj);
        } else {
            finalJSONObject.put("type", "D");
            JSONArray dirJSONArr = new JSONArray();
            finalJSONObject.put(name, dirJSONArr);

            File[] files = rootFile.listFiles();


            for (int i = 0; i < files.length; i++) {
                System.out.println("Test: " + files[i].isFile());
                JSONObject dirJSONObj = new JSONObject();
                fileJSONArray.put(dirJSONObj);
                createJSONTree(files[i], dirJSONArr, dirJSONObj);
            }
        }

        return finalJSONObject;
    }*/
}
