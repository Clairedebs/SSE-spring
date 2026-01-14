package me.clairedeborah.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/teller")
@CrossOrigin(origins = "http://localhost:4200,http://localhost:4201")
@Slf4j
@RequiredArgsConstructor
public class FortuneTellerController {

    private final FortuneTellerService service;

    @GetMapping("/future/{name}/{subscriberId}")
    public Response tellFuture(@PathVariable String subscriberId, @PathVariable String name) {
        service.tellingFuture(subscriberId, name);
        return new Response("Your future is being told!");
    }

    @GetMapping("/subscribe/{subscriberId}")
    public SseEmitter streamSse(@PathVariable String subscriberId) {
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

record Response(String content){}
