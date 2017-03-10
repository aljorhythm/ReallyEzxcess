package utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpRequestResponse {

    private String body = "";
    private int responseCode;
    private URL url;
    private Exception e;

    public HttpRequestResponse(HttpURLConnection con) {
        this.setUrl(con.getURL());
        try {
            this.setResponseCode(con.getResponseCode());
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            this.setBody(response.toString());
        } catch (IOException e) {
            this.setE(e);
        }
    }

    public HttpRequestResponse(HttpPost post) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response;
        try {
            response = client.execute(post);
            this.setResponseCode(response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            setBody(result.toString());
        } catch (IOException e) {
            setE(e);
        }
    }

    public String toString() {
        return getBody();
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    private void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    private void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return the url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    private void setUrl(URL url) {
        this.url = url;
    }

    /**
     * @return the e
     */
    public Exception getException() {
        return e;
    }

    /**
     * @param e the e to set
     */
    private void setE(Exception e) {
        this.e = e;
    }
}
