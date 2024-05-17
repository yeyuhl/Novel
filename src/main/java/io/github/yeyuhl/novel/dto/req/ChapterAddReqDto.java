package io.github.yeyuhl.novel.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 章节发布请求DTO
 *
 * @author yeyuhl
 * @date 2023/5/6
 */
@Data
public class ChapterAddReqDto {

    /**
     * 小说ID
     */
    @Schema(description = "小说ID")
    @Parameter(required = true)
    private Long bookId;

    /**
     * 章节名
     */
    @Schema(description = "章节名")
    @Parameter(required = true)
    @NotBlank(message = "章节名不能为空！")
    private String chapterName;

    /**
     * 章节内容
     */
    @Schema(description = "章节内容")
    @Parameter(required = true)
    @NotBlank(message = "章节内容不能为空！")
    @Length(min = 50)
    private String chapterContent;

    /**
     * 是否收费;1-收费 0-免费
     */
    @Schema(description = "是否收费;1-收费 0-免费")
    @Parameter(required = true)
    @NotNull(message = "是否收费不能为空！")
    private Integer isVip;

}
