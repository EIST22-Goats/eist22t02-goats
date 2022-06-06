package eist.tum_social.tum_social.location.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    public static String escapeQueryValue(String query) {
        return URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    public static JsonElement getRequest(String url, Map<String, String> params) {

        List<String> parameters = new ArrayList<>();
        for (String name: params.keySet()) {
            String param = name + "=" + escapeQueryValue(params.get(name));
            parameters.add(param);
        }

        if (!parameters.isEmpty()) {
            url += "?" + String.join("&", parameters);
        }
        return getRequest(url);
    }

    public static JsonElement getRequest(String url) {
        URL url_object;
        try {
            url_object = new URL(url);

            System.out.println(">>>>> "+url);

            URLConnection request = url_object.openConnection();
            request.connect();

            return JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
