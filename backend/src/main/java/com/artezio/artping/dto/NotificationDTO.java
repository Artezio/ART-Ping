package com.artezio.artping.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDTO {

    private static final String ACTION_PATTERN = "[{\"action\": \"%s\", \"title\": \"%s\"}]";

    public static final Map<String, String> DATA_MAP = new HashMap<String, String>() {
        {
            put("body", "Тук-тук! Есть кто живой?");
            put("checkUrl", "/artping/check/");
            put("confirmDeliveryUrl", "/artping/iamhere/notification/");
            put("icon", "/artping/assets/icons/notifications.png");
            put("requireInteraction", Boolean.TRUE.toString());
            put("timestamp", "3600000");
            put("title", "ART-Ping");
            put("actions", String.format(ACTION_PATTERN, "goto:check", "Перейти к Art-ping"));
        }
    };

    public NotificationDTO(String subscribeId) {
        this.subscribeId = subscribeId;
    }

    public NotificationDTO(List<String> subscribeIds) {
        if (subscribeIds == null) {
            return;
        }
        Set<String> subscribersSet = new HashSet<>(subscribeIds);
        this.subscribeIds = new ArrayList<>(subscribersSet);
    }

    private String subscribeId;

    private List<String> subscribeIds;

}
