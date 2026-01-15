package me.clairedeborah.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class FortuneTellerService {

    @Async
    public void tellingFuture(String subscriberId, String name){
        try {
            log.info("Sending future for : " + name);
            Thread.sleep(ThreadLocalRandom.current().nextInt(10_000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        log.info("Sent future for : " + name);
        String processedData = "You will have a beautiful future " + name + ", full of grace and blessings";
        SseEmitterManager.sendSseEventToClient(subscriberId, processedData);
    }

    public SseEmitter addEmitter(String subscriberId){
        SseEmitter emitter = new SseEmitter();
        log.info("Emitter created for subscriberId {}", subscriberId);
        SseEmitterManager.addEmitter(subscriberId, emitter);

        // Set a timeout for the SSE connection
        emitter.onTimeout(() -> {
            log.info("Emitter timed out for {}", subscriberId);
            emitter.complete();
            SseEmitterManager.removeEmitter(subscriberId);
        });

        // Set a handler for client disconnect
        emitter.onCompletion(() -> {
            log.info("SSE completed for {}", subscriberId);
            SseEmitterManager.removeEmitter(subscriberId);
        });

        emitter.onError(e -> {
            SseEmitterManager.removeEmitter(subscriberId);
            log.error("SSE error for {}",subscriberId, e);
        });

        return emitter;
    }
}
