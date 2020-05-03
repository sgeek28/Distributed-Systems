package exceptions;

/*
Written by: @Sneha and @Prince
when balance is 0.00 and
user is try to withdraw amount
**/

public class InsufficientFundsException extends Exception{
    public InsufficientFundsException(){
        super("Insufficient Funds");
    }
}
