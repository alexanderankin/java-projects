package info.ankin.projects.git.httpbackend.old.model.exception;

public class BadRequestException extends GitHttpBackendException {
    public BadRequestException(String message) {
        super(message);
    }
}
