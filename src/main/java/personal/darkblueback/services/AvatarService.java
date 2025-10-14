package personal.darkblueback.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarService {

    private final WebClient webClient;

    @Value("${storage.media.avatar-dir}")
    private String avatarDir;

    @Value( "${storage.media.url}")
    private String storageMediaUrl;

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
            Path dir = Paths.get(avatarDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Path filePath = dir.resolve(userId.split("@")[0] + ".png");
            Files.write(filePath, imageBytes);

            return storageMediaUrl + "/images/avatar/" + userId.split("@")[0] + ".png"; // esta será la URL que devuelves al frontend
        } catch (Exception e) {
            e.printStackTrace();
            return storageMediaUrl + "/images/avatar/default.png";
        }
    }
}