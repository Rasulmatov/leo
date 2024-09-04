package uz.universes.leotelegrambot.Model;

import lombok.*;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserPatch {
        private String chat_id;
        private String name;
        private String phone;
        private Integer region;
        private String lang;
    }
