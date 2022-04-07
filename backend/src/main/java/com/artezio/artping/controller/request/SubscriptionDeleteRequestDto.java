package com.artezio.artping.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * ДТО для удаления подписки firebase
 */
@Getter
@Setter
public class SubscriptionDeleteRequestDto {

    /**
     * Подписка firebase
     */
    @ApiModelProperty(required = true)
    private String subscribeId;
}
