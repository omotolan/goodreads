package africa.semicolon.goodreads.dto;

import africa.semicolon.goodreads.enums.AgeRate;
import africa.semicolon.goodreads.enums.Category;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class BookItemUploadRequest {
    @NotNull
    @NotBlank
    private String title;
    @NotNull @NotBlank
    private String author;
    @NotNull @NotBlank
    private String description;
    @NotNull @NotBlank
    private String coverImageFileName;
    @NotNull @NotBlank
    private String fileName;
    @NotNull
    private AgeRate ageRate;
    @NotNull @NotBlank
    private String uploadedBy;
    @NotNull
    private Category category;
}
