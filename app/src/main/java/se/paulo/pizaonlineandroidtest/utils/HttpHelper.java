package se.paulo.pizaonlineandroidtest.utils;

/** * Created by Paulo Vila Nova on 2017-05-14.
 */

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHelper {

    public static String downloadFromFeed(RequestPackage requestPackage) throws IOException {

        String address = requestPackage.getEndpoint();
        String encodedParams = requestPackage.getEncodedParams();

        if (requestPackage.getMethod().equals("GET") &&  encodedParams.length() > 0) {
            address = String.format("%s?%s", address, encodedParams);
        }

        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder()
                .url(address);

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Exception: response code: " + response.code());
        }

    }

}