package exceptions;


/*
Written by: @Sneha and @Prince
when user tries to access account 
which is not registered with Bank.
**/
public class RemoteBankingException extends Exception {
    public RemoteBankingException(String msg){
        super(msg);
    }
}
