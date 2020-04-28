package exceptions;


/*
Written by: @Sneha and @Prince
when date provided by user is 
invalid for fetching mini 
statement.
**/

public class StatementException extends Exception {
    public StatementException(String msg){
        super(msg);
    }
}
