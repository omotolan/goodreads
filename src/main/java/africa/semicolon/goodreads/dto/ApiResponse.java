package africa.semicolon.goodreads.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApiResponse {
    private String status;
    private String message;
    private Object data;
    private int result;
}
