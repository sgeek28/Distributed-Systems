package exceptions;


/*
Written by: @Sneha and @Prince
when user tries to access operation
for a account which is not registered
with bank.
**/
public class InvalidAccountException extends Exception {
    public InvalidAccountException(int acnum){
        super("Account with account number: " + acnum + " does not exist");
    }
}
