package nz.ac.canterbury.seng302.portfolio.controller;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TestGitlabIntegration {
    public static void main(String[] args) throws GitLabApiException {
        String TOKEN = System.getenv("GITLAB_PROJECT_TOKEN");
        String PROJECT_ID = "13529";
        String GITLAB_URL = "https://eng-git.canterbury.ac.nz";

        GitLabApi gitLabApi = new GitLabApi(GITLAB_URL, TOKEN);

        List<Commit> commits = gitLabApi.getCommitsApi().getCommits(PROJECT_ID);
        System.out.println(commits);
    }
}
