package interfaces;

import server.*;
import client.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import exceptions.*;


/*
Written by: @Sneha and @Prince
BankInterface has method signatures which
are implemented in Bank class.
**/
public interface BankInterface extends Remote {
	
	public String startingBalance(int accountNum,String name) throws RemoteException,RemoteBankingException,InvalidArgumentException;
	public double closeAccount(int accountNum,long sessionID) throws RemoteException,RemoteBankingException,InvalidSessionException;
	public double deposit(int accountNum, double amount,long sessionID,String fileName,String threadName,int x) throws RemoteException, InvalidSessionException,RemoteBankingException;
	public double withdraw(int accountNum, double amount,long sessionID,String fileName,String threadName,int x) throws RemoteException, InvalidSessionException,RemoteBankingException;
	public double inquiry(int accountNum,long sessionID) throws RemoteException,InvalidSessionException,RemoteBankingException;
	public double getTotalBalance() throws RemoteException;
	public void stopServer() throws RemoteException;
	public java.util.List<Transaction> getStatement(int accountNum, Date from, Date to,long sessionID) throws RemoteException,InvalidSessionException,StatementException,RemoteBankingException;
}
