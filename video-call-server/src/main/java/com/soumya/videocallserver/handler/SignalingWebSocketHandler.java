package com.soumya.videocallserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.soumya.videocallserver.model.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingWebSocketHandler extends TextWebSocketHandler {
    private static final List<UserSession> users = new ArrayList<>();
    public static final Map<WebSocketSession,String> sessionUserMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        // Log the new connection
        System.out.println("New WebSocket connection established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payLoad = message.getPayload();
        Map<String,Object> messageMap = objectMapper.readValue(payLoad,Map.class);
        String type = (String)messageMap.get("type");

        System.out.println("Received message: " + payLoad);

        switch (type) {
            case "STORE_USER":
                storeUser(session, messageMap);
                break;
            case "START_CALL":
                startCall(session, messageMap);
                break;
            case "CREATE_OFFER":
                createOffer(session, messageMap);
                break;
            case "CREATE_ANSWER":
                createAnswer(session, messageMap);
                break;
            case "ICE_CANDIDATE":
                iceCandidate(session, messageMap);
                break;
            case "CALL_ENDED":
                callEnded(session, messageMap);
                break;
        }


    }

    private void storeUser(WebSocketSession session, Map<String, Object> messageMap) throws Exception {
        String name = (String) messageMap.get("name");
        UserSession user = findUser(name);

        if (user != null) {
            return;
        }

        UserSession newUser = new UserSession(name, session);
        users.add(newUser);
        sessionUserMap.put(session, name);
    }

    private void startCall(WebSocketSession session, Map<String, Object> messageMap) throws Exception{
        String target = (String) messageMap.get("target");
        UserSession userToCall = findUser(target);

        if (userToCall != null) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("type", "CALL_RESPONSE", "data", "user is ready for call")
            )));
        } else {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    Map.of("type", "CALL_RESPONSE", "data", "user is not online")
            )));
        }
    }

    private void createOffer(WebSocketSession session, Map<String, Object> messageMap) throws Exception{
        String target = (String) messageMap.get("target");
        String name = (String) messageMap.get("name");
        String sdp = (String) ((Map) messageMap.get("data")).get("sdp");



        UserSession userToReceiveOffer = findUser(target);

        if(userToReceiveOffer!=null){
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "OFFER_RECEIVED");
            response.put("name", name);
            response.put("data",sdp);

            userToReceiveOffer.getSession().sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));

        }


    }


    private void createAnswer(WebSocketSession session, Map<String, Object> messageMap) throws Exception {
        String target = (String) messageMap.get("target");
        String name = (String) messageMap.get("name");
        String sdp = (String) ((Map) messageMap.get("data")).get("sdp");

        System.out.println("answer send by "+name+"for "+target);

        UserSession userToReceiveAnswer = findUser(target);
        if (userToReceiveAnswer != null) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "ANSWER_RECEIVED");
            response.put("name", name);
            response.put("data",sdp);

            userToReceiveAnswer.getSession().sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    private void iceCandidate(WebSocketSession session, Map<String, Object> messageMap) throws Exception{
        String target = (String) messageMap.get("target");
        String name = (String) messageMap.get("name");
        Map data = (Map) messageMap.get("data");

        UserSession userToReceiveIceCandidate = findUser(target);
        if (userToReceiveIceCandidate != null) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "ICE_CANDIDATE");
            response.put("name", name);
            response.set("data", objectMapper.createObjectNode()
                    .put("sdpMLineIndex", (int) data.get("sdpMLineIndex"))
                    .put("sdpMid", (String) data.get("sdpMid"))
                    .put("sdpCandidate", (String) data.get("sdpCandidate"))
            );

            userToReceiveIceCandidate.getSession().sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    private void callEnded(WebSocketSession session, Map<String, Object> messageMap) throws Exception{
        String target = (String) messageMap.get("target");
        String name = (String) messageMap.get("name");

        UserSession userToNotifyCallEnded = findUser(target);
        if (userToNotifyCallEnded != null) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("type", "CALL_ENDED");
            response.put("name", name);

            userToNotifyCallEnded.getSession().sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    private UserSession findUser(String target) {
        for (UserSession userSession : users) {
            if (userSession.getName().equals(target)) {
                return userSession;
            }
        }
        return null;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userName = sessionUserMap.get(session);
        users.removeIf(user -> user.getName().equals(userName));
        sessionUserMap.remove(session);
    }
}
