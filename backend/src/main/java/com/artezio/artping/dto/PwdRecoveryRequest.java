package com.artezio.artping.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ДТО для запроса смены пароля
 */
@Data
public class PwdRecoveryRequest {

    /**
     * Поле для сброса по логину
     */
    @ApiModelProperty(allowEmptyValue = true)
    private String login;

    /**
     * Поле для сбороса по tmail
     */
    @ApiModelProperty(allowEmptyValue = true)
    private String email;

}

