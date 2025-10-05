package personal.darkblueback.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarService {

    private final WebClient webClient;

    public AvatarService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public String saveAvatarFromUrl(String avatarUrl, String userId) {
        try {
            byte[] imageBytes = webClient.get()
                    .uri(avatarUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            // carpeta local para guardar
            Path dir = Paths.get("src/main/resources/static/media/images/avatar/");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Path filePath = dir.resolve(userId + ".png");
            Files.write(filePath, imageBytes);

            return "http://192.168.1.136:8080/media/images/avatar/" + userId + ".png"; // esta será la URL que devuelves al frontend
        } catch (Exception e) {
            e.printStackTrace();
            return "http://192.168.1.136:8080/media/images/avatar/default.png";
        }
    }
}