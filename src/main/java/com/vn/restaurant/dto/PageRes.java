package com.vn.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "PageRes", description = "Phan hoi phan trang chung")
public record PageRes<T>(
                @Schema(description = "Noi dung trang") List<T> content,
                @Schema(description = "Trang hien tai (bat dau tu 1)", example = "1") int page,
                @Schema(description = "Kich thuoc trang", example = "10") int size,
                @Schema(description = "Tong so phan tu", example = "25") long totalElements,
                @Schema(description = "Tong so trang", example = "3") int totalPages,
                @Schema(description = "Co trang tiep theo", example = "true") boolean hasNext) {
}