package zgdx.xxaq.domain;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import org.jsoup.nodes.Document;
import java.io.IOException;

public class OkHttpClientTest {

    @Test
    public void requestTest() throws IOException {
        //String url = "http://www.hybjf.com";
        String url = "http://iface2.iqiyi.com";
        Request request = createRequest(url);

        try (Response response =  new OkHttpClient.Builder().build().newCall(request).execute()) {
            int statusCode = response.code();
            String ip = response.exchange().connection().socket().getInetAddress().getHostAddress();

            Document doc = Jsoup.parse(response.body().string());
            Elements head = doc.getElementsByTag("head");
            Elements meta = head.select("meta");
            String title = head.select("title").text();
            String keywords = meta.select("meta[name=keywords]").attr("content");
            String description = meta.select("meta[name=description]").attr("content");
            return;
        }
    }

    private Request createRequest(String url) {
        return new Request.Builder()
                .header("User-Agent", "*****")
                .addHeader("Accept", "*****")
                .url(url)
                .get()
                .build();
    }

}
