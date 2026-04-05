package pet.project.exception;

public class UserRefreshRatingFailedException extends RuntimeException {
    public UserRefreshRatingFailedException() {
        super("User refresh rating failed");
    }
}
