package uz.universes.leotelegrambot.Model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CheckSMS {
    private String phone;
    private String code;
}
