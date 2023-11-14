package com.cogent.system.domain.vo.bag;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ActionReq implements Serializable {


    private static final long serialVersionUID = 8651853352955706701L;

    @NotBlank
    private String sn;

    @NotBlank
    private String requestId;

    @NotNull
    private Boolean actionResult;
}
