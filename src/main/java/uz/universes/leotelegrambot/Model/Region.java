package uz.universes.leotelegrambot.Model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Region {
    private Integer id;
    private String name_uz;
    private String name_ru;
}
