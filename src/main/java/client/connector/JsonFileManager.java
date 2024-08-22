package client.connector;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

public class JsonFileManager {
    private final String filePath;
    @Getter
    private JSONObject jsonData;

    public JsonFileManager(String filePath) {
        this.filePath = filePath + "\\data.json";
        File file = new File(this.filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                jsonData = new JSONObject();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(this.filePath);
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONTokener tokener = new JSONTokener(sb.toString());
                jsonData = new JSONObject(tokener);
                br.close();
                fileInputStream.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addJsonData(int id, JSONObject jsonObject) {
        String key = String.valueOf(id);
        if (jsonData.has(key)) {

            JSONArray jsonArray = jsonData.getJSONArray(key);
            jsonArray.put(jsonObject);
            jsonData.put(key, jsonArray);
        } else {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            jsonData.put(key, jsonArray);
        }
    }

    public void saveJsonToFile() {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(jsonData.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public JSONObject getFirstJsonObjectFromFile(int id) {
        String key = String.valueOf(id);
        if (jsonData.has(key) && !jsonData.getJSONArray(key).isEmpty()) {
            return jsonData.getJSONArray(key).getJSONObject(0);
        }
        return null;
    }

    public void deleteFirstJsonObjectFromFile(int id) {
        String key = String.valueOf(id);
        jsonData.getJSONArray(key).remove(0);
        if (jsonData.getJSONArray(key).isEmpty()) {
            jsonData.remove(key);
        }
    }
}