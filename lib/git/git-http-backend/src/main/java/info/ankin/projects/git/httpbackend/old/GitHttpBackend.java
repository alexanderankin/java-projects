package info.ankin.projects.git.httpbackend.old;

import info.ankin.projects.git.httpbackend.old.model.GitRequest;
import info.ankin.projects.git.httpbackend.old.model.Request;
import info.ankin.projects.git.httpbackend.old.model.exception.BadRequestException;
import info.ankin.projects.git.httpbackend.old.model.exception.NoSuchServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
public class GitHttpBackend {

    public static final Pattern PATH_HAS_RELATIVE_OR_PARENT = Pattern.compile("\\./|\\.\\.");
    public static final Pattern PATH_INFO_OR_REFS = Pattern.compile("/info/refs$");
    private static final Set<String> SUPPORTED_SERVICES = Set.of("git-upload-pack", "git-receive-pack");

    public void process(Request request) {
        process(toGitRequest(request));
    }

    private GitRequest toGitRequest(Request request) {
        GitRequest gitRequest = new GitRequest(request);

        String url = gitRequest.getRequest().getUrl();
        try {
            gitRequest.setUri(URI.create(url));
        } catch (Exception e) {
            throw new BadRequestException("invalid url: " + url);
        }

        return gitRequest;
    }

    protected void process(GitRequest gitRequest) {
        String path = gitRequest.getUri().getPath();
        if (hasRelativeOrParent(path))
            throw new BadRequestException("invalid git path");

        GitService gitService = getGitService(gitRequest, path);

        if (gitService.getName() == null) {
            log.warn("todo");
        }
    }

    protected GitService getGitService(GitRequest gitRequest, String path) {
        GitService gitService;
        boolean info;
        String service;
        if (PATH_INFO_OR_REFS.matcher(path).find()) {
            var params = UriComponentsBuilder.fromUri(gitRequest.getUri()).build().getQueryParams();
            List<String> list = params.get("service");
            service = CollectionUtils.lastElement(list);
            info = true;
        } else {
            info = false;
            service = CollectionUtils.lastElement(Arrays.asList(path.split("/")));
        }

        if (!SUPPORTED_SERVICES.contains(service)) throw new NoSuchServiceException();

        gitService = new GitService()
                .setInfo(info)
                .setName(service);
        return gitService;
    }

    boolean hasRelativeOrParent(String path) {
        return PATH_HAS_RELATIVE_OR_PARENT.matcher(path).find();
    }
}
