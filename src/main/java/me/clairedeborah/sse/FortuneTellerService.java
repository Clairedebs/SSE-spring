package me.clairedeborah.sse;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class FortuneTellerService {

    @Async
    public void tellingFuture(String subscriberId, String name){
        Random rand = new Random();
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
}
