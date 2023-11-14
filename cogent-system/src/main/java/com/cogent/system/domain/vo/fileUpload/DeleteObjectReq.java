package com.cogent.system.domain.vo.fileUpload;

import lombok.Data;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/24
 * {@code @description:}
 */
@Data
public class DeleteObjectReq {

    List<DeleteArgs> deleteArgs;
}
