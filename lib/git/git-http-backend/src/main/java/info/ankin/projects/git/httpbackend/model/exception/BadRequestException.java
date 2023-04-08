package info.ankin.projects.git.httpbackend.model.exception;

public class BadRequestException extends GitHttpBackendException {
    public BadRequestException(String message) {
        super(message);
    }
}
