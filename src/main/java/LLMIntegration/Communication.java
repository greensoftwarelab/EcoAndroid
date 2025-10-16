package LLMIntegration;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class Communication {

    /**
     * Communicates with a model through an API, returning its response.
     *
     * @param model model identification
     * @param apiUrl API's URL
     * @param apiKey API key
     * @return model's response
     */
    public static String queryModel(String model, String apiUrl, String apiKey, String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("messages", new JSONArray().put(message));

        RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response + ": " + Objects.requireNonNull(response.body()).string());
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
