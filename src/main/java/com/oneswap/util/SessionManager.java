package com.oneswap.util;

import com.oneswap.dto.EstimateRequest;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class SessionManager {

    // make sessionId as key to save every user's EstimateRequest
    private static final Map<String, EstimateRequest> sessions = new ConcurrentHashMap<>();

    // save user's EstimateRequest
    public static void addSession(String sessionId, EstimateRequest estimateRequest) {
        sessions.put(sessionId, estimateRequest);
        log.info("request sessionId: " + sessionId);
    }

    // get EstimateRequest by sessionId
    public static EstimateRequest getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    // remove session by sessionId
    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("remove sessionId: " + sessionId);
    }

    // get all Sessions
    public static Map<String, EstimateRequest> getAllSessions() {
        return sessions;
    }

    // get users who subscribe the specific token info
    public static Map<String, EstimateRequest> getSubscribers(String tokenA, String tokenB) {
        Map<String, EstimateRequest> subscribers = new ConcurrentHashMap<>();
        for (Map.Entry<String, EstimateRequest> entry : sessions.entrySet()) {
            EstimateRequest estimateRequest = entry.getValue();
            if (estimateRequest.getTokenIn().equalsIgnoreCase(tokenA) && estimateRequest.getTokenOut().equalsIgnoreCase(tokenB)) {
                subscribers.put(entry.getKey(), estimateRequest);
            } else if (estimateRequest.getTokenIn().equalsIgnoreCase(tokenB) && estimateRequest.getTokenOut().equalsIgnoreCase(tokenA)) {
                subscribers.put(entry.getKey(), estimateRequest);
            }
        }
        return subscribers;
    }
}

