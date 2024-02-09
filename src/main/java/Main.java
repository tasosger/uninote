import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main {

    // OpenAI API Key
    private static final String API_KEY = "";

    // Function to encode the image
    private static String encodeImage(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        byte[] imageBytes = new byte[(int) imageFile.length()];
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            fis.read(imageBytes);
        }
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static void main(String[] args) throws IOException {
        String imagePath = "src/main/resources/uninote.jpg";

        String base64Image = encodeImage(imagePath);

        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "gpt-4-vision-preview");

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        Map<String, Object> content = new HashMap<>();
        content.put("type", "text");
        content.put("text", "What’s in this image?");

        Map<String, Object> imageUrl = new HashMap<>();
        imageUrl.put("url", "data:image/jpeg;base64," + base64Image);

        Map<String, Object> imageUrlWrapper = new HashMap<>();
        imageUrlWrapper.put("type", "image_url");
        imageUrlWrapper.put("image_url", imageUrl);

        Object[] contentArray = { content, imageUrlWrapper };
        message.put("content", contentArray);

        Object[] messagesArray = { message };
        payload.put("messages", messagesArray);
        payload.put("max_tokens", 300);

        okhttp3.RequestBody requestBody = RequestBody.create(JSON, new com.google.gson.Gson().toJson(payload));

        okhttp3.Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println(response.body().string());
            } else {
                System.err.println("Error: " + response.code() + " - " + response.message());
            }
        }
    }
}
