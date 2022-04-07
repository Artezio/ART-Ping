package com.artezio.artping.service;

import com.artezio.artping.dto.NotificationDTO;
import com.artezio.artping.dto.NotificationResponseDto;
import com.artezio.artping.dto.NotificationsResponseDto;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * Сервис для рассылки оповещений посредством firebase
 */
@Service
public class FirebaseNotificationService {

    /**
     * Отправка оповещения сотруднику при его проверке
     *
     * @param notification содержимое оповещения
     * @return статус отправки
     */
    public NotificationsResponseDto sendNotifications(NotificationDTO notification) {
        NotificationsResponseDto responseDto = new NotificationsResponseDto();
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(notification.getSubscribeIds())
                .putAllData(NotificationDTO.DATA_MAP)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            List<NotificationResponseDto> employeeSubscriptionIds = response.getResponses()
                    .stream()
                    .map((SendResponse sendResponse) -> new NotificationResponseDto(
                            sendResponse.getException() == null,
                            sendResponse.getMessageId(),
                            sendResponse.getException())
                    )
                    .collect(Collectors.toList());
            responseDto.setNotificationResponseDtoList(employeeSubscriptionIds);
            responseDto.setSuccess(true);
        } catch (FirebaseMessagingException e) {
            responseDto.setException(e);
            responseDto.setSuccess(false);
            e.printStackTrace();
        }
        return responseDto;

    }

}
