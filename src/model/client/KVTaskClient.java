package model.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String apiToken = "";
    private int port = 0;

    //Конструктор принимает URL к серверу хранилища и регистрируется.
    // При регистрации выдаётся токен (API_TOKEN), который нужен при работе с сервером
    public KVTaskClient(int port) {
        try {
            this.port = port;
            URI uri = URI.create("http://localhost:" + port + "/register");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            apiToken = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    //должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
    public void put(String key, String json) throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:" + port + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        client.send(request, handler);
    }

    //должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:" + port + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        return response.body();
    }
}
