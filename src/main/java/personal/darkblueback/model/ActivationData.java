package personal.darkblueback.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ActivationData {

    private String token;
    private String code;
    private String email;


}
