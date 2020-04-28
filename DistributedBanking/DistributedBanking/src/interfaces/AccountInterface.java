package interfaces;

import server.*;
import client.*;
import java.util.Date;
import java.util.List;

/**
* Written by @Sneha and @Prince
* AccountInterface which has methods signatures
* which are implemented in Account class.
**/
public interface AccountInterface {
	
	public void addTransaction(String type, double amount);
	public List<Transaction> getTransactions();
	public List<Transaction> getTransactionsByDate(Date fromDate, Date toDate);
	public int getAccountNum();
	public void setAccountNum(int accountNum);
	public double getBalance();
	public void setBalance(double balance);
	public String getAccountName();
	public void setAccountName(String accountName);
	
}
