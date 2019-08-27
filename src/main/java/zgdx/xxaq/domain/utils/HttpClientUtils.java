package zgdx.xxaq.domain.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ManagedHttpClientConnection;

public class HttpClientUtils {

    public static String getServerIp(HttpClientContext httpClientContext) {
        ManagedHttpClientConnection connection = httpClientContext.getConnection(ManagedHttpClientConnection.class);
        return connection.getSocket().getInetAddress().getHostAddress();
    }

    public static Integer getStatusCode(HttpResponse httpResponse){
        return httpResponse.getStatusLine().getStatusCode();
    }

}
