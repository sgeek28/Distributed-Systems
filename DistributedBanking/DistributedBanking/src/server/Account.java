/*
* Written by: @Sneha and @Prince
* Account class which implements AccountInterface and Serializable
* It holds user account number, name, transactions list
* and current balance.
**/

package server;

import interfaces.*;
import client.*;
import runnable.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account implements AccountInterface,Serializable{
	
	private int accountNum;
	private String accountName;
	private double balance;

	// list of transaction associated with an account object
	private List<Transaction> transactions; 
	
	// Account class constructor
	public Account (int accountNum, String accountName, double openingBalance) {
				
		this.setAccountNum(accountNum);
		this.setAccountName(accountName);
		this.setBalance(openingBalance);
		transactions = new ArrayList<Transaction>();
	}
	
	// adding a transaction object to the list of transactions
	public void addTransaction(String type, double amount) {
		Transaction e = new Transaction(type, amount, getBalance()); 
		transactions.add(e);
	}
	
	// return all transactions 
	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	// return all transactions within a specified date range
	public List<Transaction> getTransactionsByDate(Date fromDate, Date toDate) {
		
		List<Transaction> statementList = new ArrayList<Transaction>();
		
		for (int i=0; i<transactions.size(); i++) {
			Transaction element = transactions.get(i);
			
			// check if the date value falls between the specified range 
			if (element.getTransactionDate().after(fromDate) && element.getTransactionDate().before(toDate)) {
				statementList.add(element); 
			}
		}
		return statementList;
	}

	//setters and getters
	public int getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(int accountNum) {
		this.accountNum = accountNum;
	}

	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}
