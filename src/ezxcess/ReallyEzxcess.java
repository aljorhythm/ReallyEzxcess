package ezxcess;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import exceptions.InvalidCredentialsException;
import exceptions.SignInFailureException;
import utils.HttpRequestResponse;

public class ReallyEzxcess {
    public static class URLS {
        public static final String GOOGLE = "https://www.google.com";
        public static final String LOG_IN_UI = "https://ezxcess.nus.edu.sg/fs/customwebauth/al_login.html";
        public static final String LOG_IN_FORM = "https://ezxcess.nus.edu.sg/login.html";
        public static final String LOG_OUT_FORM = "https://ezxcess.nus.edu.sg/logout.html";
    }

    public static enum USER_AGENTS {
        MOZILLA
    }

    private USER_AGENTS userAgent;

    public ReallyEzxcess(USER_AGENTS userAgent) {
        this.userAgent = userAgent;
    }

    private String getUserAgentString() {
        return getUserAgentString(this.userAgent);
    }

    public static String getUserAgentString(USER_AGENTS ua) {
        switch (ua) {
        case MOZILLA:
            return "Mozilla/5.0";
        default:
            return "Mozilla/5.0";
        }
    }

    public static enum HTTP_METHOD {
        POST, GET
    }

    public static String getHTTPMethodString(HTTP_METHOD method) {
        switch (method) {
        case POST:
            return "POST";
        case GET:
            return "GET";
        default:
            return "GET";
        }
    }

    public HttpURLConnection getHttpUrlConnection(String url, HTTP_METHOD httpMethod, String urlParameters)
            throws IOException {
        String encodedData = URLEncoder.encode(urlParameters, "UTF-8");
        HttpURLConnection conn = getHttpUrlConnection(url, httpMethod);
        String type = "application/x-www-form-urlencoded";
        conn.setRequestProperty("Content-Type", type);
        conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
        return conn;
    }

    public HttpURLConnection getHttpUrlConnection(String url, HTTP_METHOD httpMethod) throws IOException {
        URL obj;
        obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(getHTTPMethodString(httpMethod));
        con.setRequestProperty("User-Agent", getUserAgentString());
        return con;
    }

    public boolean canAccessEzxcess() {
        try {
            return new HttpRequestResponse(getHttpUrlConnection(URLS.LOG_IN_UI, HTTP_METHOD.GET))
                    .getException() == null;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean canAccessGoogle() {
        try {
            return new HttpRequestResponse(getHttpUrlConnection(URLS.GOOGLE, HTTP_METHOD.GET)).getException() == null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 
     * @return true if logout
     */
    public HttpRequestResponse logoutOfNusNet() {
        HttpPost post = new HttpPost(URLS.LOG_OUT_FORM);
        post.setHeader("User-Agent", getUserAgentString());

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("Logout", "Logout"));

        HttpRequestResponse response;
        response = new HttpRequestResponse(post);
        return response;
    }

    public HttpRequestResponse signInToNusNet(String username, String password)
            throws InvalidCredentialsException, SignInFailureException {
        HttpPost post = new HttpPost(URLS.LOG_IN_FORM);

        post.setHeader("User-Agent", getUserAgentString());

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("username", "NUSSTU\\" + username));
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("domain", "NUSSTU"));
        urlParameters.add(new BasicNameValuePair("buttonClicked", "4"));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e1) {
            throw new InvalidCredentialsException("Invalid encoding of credentials");
        }

        HttpRequestResponse response;
        response = new HttpRequestResponse(post);
        return response;
    }
}
