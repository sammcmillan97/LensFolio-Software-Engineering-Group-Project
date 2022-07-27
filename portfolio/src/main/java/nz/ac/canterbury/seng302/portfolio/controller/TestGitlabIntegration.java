package nz.ac.canterbury.seng302.portfolio.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestGitlabIntegration {
    public static void main(String[] args) throws IOException {
        String TOKEN = System.getenv("GITLAB_PROJECT_TOKEN");
        String PROJECT_ID = "13529";
        URL url = new URL("https://eng-git.canterbury.ac.nz/api/v4/projects/" + PROJECT_ID + "/repository/commits");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("PRIVATE-TOKEN", TOKEN);

        int status = con.getResponseCode();
        System.out.println(status);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        System.out.println(content);
    }
}
