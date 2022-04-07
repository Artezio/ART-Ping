package com.artezio.artping.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * ДТО для создания подписки firebase
 */
@Getter
@Setter
public class SubscriptionAddRequestDto {

    /**
     * Подписка firebase
     */
    @ApiModelProperty(required = true)
    private String subscribeId;
}
