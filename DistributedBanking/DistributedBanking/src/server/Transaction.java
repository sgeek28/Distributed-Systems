/*
* Written by: @Sneha and @Prince
* Transaction object that implements Serializable
* stores all list of transactions
* for every Bank account.
**/

package server;

import interfaces.*;
import client.*;
import runnable.*;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

public class Transaction implements Serializable {

	private static final long serialVersionUID = -6841131027488692403L;
	
	// decimal formatting to 2 decimal places
	private DecimalFormat precision2 = new DecimalFormat("0.00");
	
	private String transactionType;
	private double transactionAmount;
	private double upToDateBalance;
	private Date transactionDate;
	
	public Transaction(String transactionType, double transactionAmount, double upToDateBalance){
		this.setTransactionType(transactionType);
		this.setTransactionAmount(transactionAmount);
		this.setUpToDateBalance(upToDateBalance);
		transactionDate = new Date();
	}
	
	public String toString() {
		return "Type: " + transactionType +
				"\nAmount: €" + precision2.format(transactionAmount) +
			    "\nBalance: €" + precision2.format(upToDateBalance) +
			    "\nDate: " + transactionDate.toString() + "\n";
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public double getUpToDateBalance() {
		return upToDateBalance;
	}

	public void setUpToDateBalance(double upToDateBalance) {
		this.upToDateBalance = upToDateBalance;
	}
}
