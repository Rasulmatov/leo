package uz.universes.leotelegrambot.Model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UsersDto {
    private String chat_id;
    private String name;
    private String phone;
    private String region;
    private String lang;
    private Integer point;
}
