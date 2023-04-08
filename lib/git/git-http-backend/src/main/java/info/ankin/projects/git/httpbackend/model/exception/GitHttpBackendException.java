package info.ankin.projects.git.httpbackend.model.exception;

public abstract class GitHttpBackendException extends RuntimeException {
    public GitHttpBackendException(String message) {
        super(message);
    }
}
