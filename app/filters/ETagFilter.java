package filters;

import akka.stream.Materializer;
import org.apache.commons.codec.binary.Hex;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Created by marcio on 1/16/17.
 */
public class ETagFilter extends Filter {
    private MessageDigest messageDigest;

    @Inject
    public ETagFilter(Materializer mat) {
        super(mat);
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ignored) {
        }
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> next,
                                         Http.RequestHeader rh) {
        return next.apply(rh).thenApply(result -> {
            Optional<String> ifNoneMatch = rh.header("If-None-Match");
            Optional<String> resultContentType = result.contentType();
            if (resultContentType.isPresent() && resultContentType.get().matches("(.*/json)(;.*)?")) {
                String etag = "";
                try {
                    etag = result.body().consumeData(materializer).thenApply(data -> {
                        String s = data.decodeString("UTF-8");
                        messageDigest.reset();
                        messageDigest.update(s.getBytes());
                        return "\"" + new String(Hex.encodeHex(messageDigest.digest())) + "\"";
                    }).toCompletableFuture().get();
                } catch (InterruptedException | ExecutionException ignored) {
                }
                if (ifNoneMatch.isPresent() && !etag.isEmpty() && ifNoneMatch.get().equals(etag))
                    return Results.status(304);
                else
                    return result.withHeader("Etag", etag);
            } else {
                return result;
            }
        });
    }
}
