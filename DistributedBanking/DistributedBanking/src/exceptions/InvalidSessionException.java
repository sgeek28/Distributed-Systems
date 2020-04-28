package exceptions;


/*
Written by: @Sneha and @Prince
when user tries to perform bank 
operations using invalid session
which is dead now or belongs to
other ongoing transaction.
**/

public class InvalidSessionException extends Exception {
    public InvalidSessionException(String msg) {
        super(msg);
    }
}
