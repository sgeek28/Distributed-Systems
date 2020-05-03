package exceptions;


/*
Written by: @Sneha and @Prince
when user tries to provide name in 
invalid format.
**/
public class InvalidArgumentException extends Exception {
    public InvalidArgumentException(String msg){
        super(msg);
    }
}
