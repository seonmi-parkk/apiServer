package kr.co.apiserver.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TodoDto {

    private Long tno;
    private String title;
    private String content;
    private boolean complete;
    private LocalDate dueDate;

}
