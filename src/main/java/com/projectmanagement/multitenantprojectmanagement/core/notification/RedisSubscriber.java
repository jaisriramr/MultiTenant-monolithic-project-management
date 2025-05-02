package com.projectmanagement.multitenantprojectmanagement.core.notification;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonMessage = new String(message.getBody());
        System.out.println("Received message: " + jsonMessage);
        messagingTemplate.convertAndSend("/topic/notifications", jsonMessage);
    }

    public void sendNotification(String messageContent) {
        System.out.println("Received message: " + messageContent);
        messagingTemplate.convertAndSend("/topic/notifications", messageContent);
    }

    // private final NotificationWebSocketHandler notificationWebSocketHandler;

    // @Override
    // public void onMessage(@NonNull Message message, @Nullable byte[] pattern) {
    //     String data = new String(message.getBody());
    //     String[] parts = data.split("\\|", 2);
    //     String tenantId = parts[0];
    //     String payload = parts[1];
    //     try {
    //         notificationWebSocketHandler.sendToTenant(tenantId, payload);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }


    // }

}
