package kr.co.apiserver.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TodoDto {

    private Long tno;
    private String title;
    private Long userId;
    private String content;
    private boolean complete;
    private LocalDate dueDate;

    public void setTno(Long tno) {
        this.tno = tno;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
