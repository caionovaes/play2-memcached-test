package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.cache.SyncCacheApi;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    private SyncCacheApi cacheApi;

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result set(String value) {
        ObjectNode responseJson = Json.newObject();
        String cacheValue = cacheApi.get("key");

        if (cacheValue != null) {
            responseJson.put("message", "theres something there, I won't set until you remove it");
            return status(409, responseJson);
        }

        cacheApi.set("key", value);
        responseJson.put("message", "your cache now has something");
        return ok(responseJson);
    }

    public Result get() {
        ObjectNode responseJson = Json.newObject();
        String cacheValue = cacheApi.get("key");

        if (cacheValue == null) {
            responseJson.put("message", "there is nothing here to get");
            return notFound(responseJson);
        }

        responseJson.put("message", "found something: " + cacheValue);
        return ok(responseJson);
    }

    public Result remove() {
        ObjectNode responseJson = Json.newObject();
        String cacheValue = cacheApi.get("key");

        if (cacheValue == null) {
            responseJson.put("message", "there is nothing here to remove");
            return notFound(responseJson);
        }

        cacheApi.remove("key");
        responseJson.put("message", "what we had we no longer have");
        return ok(responseJson);
    }
}
