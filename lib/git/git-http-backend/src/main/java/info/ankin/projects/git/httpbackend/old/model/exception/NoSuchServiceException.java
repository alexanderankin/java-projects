package info.ankin.projects.git.httpbackend.old.model.exception;

public class NoSuchServiceException extends GitHttpBackendException{
    public NoSuchServiceException() {
        super("unsupported git service");
    }
}
