package io.github.yeyuhl.novel.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 小说发布请求DTO
 *
 * @author yeyuhl
 * @date 2023/5/6
 */
@Data
public class BookAddReqDto {

    /**
     * 作品方向;0-男频 1-女频
     */
    @Schema(description = "作品方向;0-男频 1-女频")
    @Parameter(required = true)
    @NotNull(message = "作品方向不能为空！")
    private Integer workDirection;

    /**
     * 类别ID
     */
    @Schema(description = "类别ID")
    @Parameter(required = true)
    @NotNull(message = "类别ID不能为空！")
    private Long categoryId;

    /**
     * 类别名
     */
    @Schema(description = "类别名")
    @Parameter(required = true)
    @NotBlank(message = "小说类别不能为空！")
    private String categoryName;

    /**
     * 小说封面地址
     */
    @Schema(description = "小说封面地址")
    @Parameter(required = true)
    @NotBlank(message = "小说封面不能为空！")
    private String picUrl;

    /**
     * 小说名
     */
    @Schema(description = "小说名")
    @Parameter(required = true)
    @NotBlank(message = "小说名不能为空！")
    private String bookName;

    /**
     * 书籍描述
     */
    @Schema(description = "书籍描述")
    @Parameter(required = true)
    @NotBlank(message = "小说描述不能为空！")
    private String bookDesc;

    /**
     * 是否收费;1-收费 0-免费
     */
    @Schema(description = "是否收费;1-收费 0-免费")
    @Parameter(required = true)
    @NotNull(message = "请确定是否收费！")
    private Integer isVip;
}
