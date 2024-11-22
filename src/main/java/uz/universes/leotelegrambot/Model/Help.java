package uz.universes.leotelegrambot.Model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Help {
    private Integer id;
    private String file_id;
    private Integer message_id;
    private String message_lang;
    private String message_text;
    private String message_type;
    private String from_id;
}
