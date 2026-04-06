package pet.project.exception;

public class UserUpdatePasswordException extends RuntimeException{
    public UserUpdatePasswordException() {
        super("User update password failed");
    }
}
