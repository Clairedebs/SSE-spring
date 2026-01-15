package me.clairedeborah.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SseEmitterManager {

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private static final Long TIMEOUT = 10 * 60 * 1000L; // 10 min

    public static void addEmitter(String subscriberId, SseEmitter emitter){
        emitters.put(subscriberId, emitter);
    }

    public static void removeEmitter(String subscriberId){
        emitters.remove(subscriberId);
        log.info("SSE completed for {}", subscriberId);
    }

    public static  void sendSseEventToClient(String subscriberId, Object data ){
        SseEmitter emitter = emitters.get(subscriberId);
        if (emitter == null){
            log.error("Emitter not found for {}", subscriberId);
            return;
        }
        try {
            Response response = new Response(data. toString());
            emitter.send(SseEmitter.event()
                    .data(response)
            );
        } catch (IOException e) {
            emitters.remove(subscriberId);
           log.warn("Error sending data to client : {} ", e.getMessage());
        }
    }

}
