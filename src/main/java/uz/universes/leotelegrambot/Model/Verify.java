package uz.universes.leotelegrambot.Model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Verify {
    private Boolean success;
    private String message_uz;
    private String message_ru;
}
