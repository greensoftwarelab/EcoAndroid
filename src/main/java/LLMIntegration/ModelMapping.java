package LLMIntegration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelMapping {
    public static class ModelInfo{
        private final String id;
        private final String apiUrl;
        private final String apiKey;

        public ModelInfo(String id, String apiUrl, String apiKey){
            this.id = id;
            this.apiUrl = apiUrl;
            this.apiKey = apiKey;
        }

        public String getId(){
            return id;
        }

        public String getApiUrl(){
            return apiUrl;
        }

        public String getApiKey(){
            return apiKey;
        }
    }

    private static final Map<String, ModelInfo> modelMap = new HashMap<>();


    /**
     * Returns all the information of the model.
     *
     * @param displayName the name of the model to search for on the HashMap
     * @return instance of class ModelInfo, with the information of the model
     */
    public static ModelInfo getModelInfo(String displayName){
        return modelMap.get(displayName);
    }


    /**
     * Lists the models' display names.
     *
     * @return the array with all display names.
     */
    public static String[] getDisplayNames() {
        if (modelMap.isEmpty()){
            return new String[] {"No models available"};
        }
        return modelMap.keySet().toArray(new String[0]);
    }


    /**
     * Adds a model to the HashMap of models.
     *
     * @param displayName display name of the model to add
     * @param info information of the model
     */
    public static void addModel(String displayName, ModelInfo info) {
        modelMap.put(displayName, info);
    }

    /**
     * Verifies if the HashMap has a certain models.
     *
     * @param displayName display name of the model to check
     * @return true if the model exists, false otherwise
     */
    public static boolean hasModel(String displayName){
        return modelMap.containsKey(displayName);
    }

    /**
     * Gets the HashMap.
     *
     * @return unmodifiableMap
     */
    public static Map<String, ModelInfo> getModelMap(){
        return Collections.unmodifiableMap(modelMap);
    }
}
