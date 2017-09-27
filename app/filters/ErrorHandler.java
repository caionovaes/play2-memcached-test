package filters;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Application;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ErrorHandler implements HttpErrorHandler {

    private final Provider<Application> app;

    @Inject
    public ErrorHandler(Provider<Application> application) {
        this.app = application;
    }

    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        ObjectNode responseNode = Json.newObject();
        responseNode.put("error", message);
        return CompletableFuture.completedFuture(Results.status(statusCode, responseNode));
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        ObjectNode responseNode = Json.newObject();
        responseNode.put("error", exception.getClass().getSimpleName() + ": " + exception.getMessage());
        if (app.get().isDev())
            exception.printStackTrace();
        return CompletableFuture.completedFuture(Results.internalServerError(responseNode));
    }
}
