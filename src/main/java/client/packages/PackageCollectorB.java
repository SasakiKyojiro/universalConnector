package client.packages;

import config.json.Parameter;
import config.types.ParameterType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageCollectorB {
    public  static JSONObject incompleteAssembler (@NotNull JSONObject answerAServer){
        Map<String, JSONArray> resultMap = new HashMap<>();
        processJsonObject(answerAServer, resultMap);
        JSONObject tmp = new JSONObject(resultMap);
        resultMap.clear();
        processJson(tmp);
        return tmp;
    }

    public static JSONObject assembly(List<Parameter> parameters, JSONObject answerAServer) {
        JSONObject result = new JSONObject();
        JSONObject tmp = incompleteAssembler(answerAServer);
        JSONObject listObject = new JSONObject();
        listObjectAssembler(parameters, tmp, listObject);
        jsonPackageCollector(parameters, tmp, listObject, result);
        return result;
    }

    private static void jsonPackageCollector (@NotNull List<Parameter> parameters, JSONObject tmp,
                                              JSONObject listObject, JSONObject result){
        for (Parameter parameter : parameters) {
            if (parameter.getTypeParam().equals(ParameterType.LIST_OBJECT)) {
                result.put(parameter.getName(), listObject.getJSONArray(parameter.getName()));
                listObject.remove(parameter.getName());
            }
            else if (parameter.getTypeParam().equals(ParameterType.OBJECT)){
                JSONObject obj = new JSONObject();
                jsonPackageCollector(parameter.getParams(), tmp, listObject, obj);
                result.put(parameter.getName(), obj);
            }
            else {
                result.put(parameter.getName(), tmp.get(parameter.getName()));
            }
        }
    }


    private static void listObjectAssembler(@NotNull List<Parameter> parameters, JSONObject jsonObject,
                                            JSONObject listObject) {
        for (Parameter p : parameters) {
            if (p.getTypeParam().equals(ParameterType.LIST_OBJECT)) {
                JSONArray jsonArray = new JSONArray();
                int size = jsonObject.getJSONArray(p.getParams().get(0).getName()).length();
                for (int i = 0; i < size; i++) {
                    JSONObject obj = new JSONObject();
                    for (Parameter p1 : p.getParams())
                        obj.put(p1.getName(), jsonObject.getJSONArray(p1.getName()).get(i));
                    jsonArray.put(obj);
                }
                //удаляет уже использованные поля
                for (Parameter p1 : p.getParams())
                    jsonObject.remove(p1.getName());
                listObject.put(p.getName(), jsonArray);
            }
            if (p.getParams() != null)
                listObjectAssembler(p.getParams(), jsonObject, listObject);
        }
    }

    private static void processJson(@NotNull JSONObject json) {
        for (String key : json.keySet()) {
            Object value = json.get(key);

            if (value instanceof JSONObject)
                processJson((JSONObject) value); // Рекурсивный вызов для объектов
            else if (value instanceof JSONArray jsonArray)
                if (jsonArray.length() == 1) {
                    Object newValue = jsonArray.get(0);
                    json.put(key, newValue); // Заменяем JSON массив на его единственный элемент
                }
        }
    }

    private static void processJsonObject(@NotNull JSONObject jsonObject, Map<String, JSONArray> resultMap) throws JSONException {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject)
                processJsonObject((JSONObject) value, resultMap);
            else if (value instanceof JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object innerObj = jsonArray.get(i);
                    if (innerObj instanceof JSONObject innerJsonObject)
                        for (String innerKey : innerJsonObject.keySet()) {
                            if (resultMap.containsKey(innerKey))
                                resultMap.get(innerKey).put(innerJsonObject.get(innerKey));
                            else {
                                JSONArray newArray = new JSONArray();
                                newArray.put(innerJsonObject.get(innerKey));
                                resultMap.put(innerKey, newArray);
                            }
                        }
                     else {
                        resultMap.put(key, jsonArray);
                        break;
                    }
                }
            } else if (value instanceof String || value instanceof Number || value instanceof Boolean) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(value);
                resultMap.put(key, jsonArray);
            }
        }
    }
}
