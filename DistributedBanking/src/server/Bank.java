/*
* Written by: @Sneha and @Prince
* Bank class which acts as server,
* will accept the client connectio request
* and process all the operations requested by ATM class.
**/

package server;

import interfaces.*;
import client.*;
import runnable.*;
import java.rmi.Naming;
import exceptions.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;

// bank acts as the remote server that the client (ATM) connects to
public class Bank extends UnicastRemoteObject implements BankInterface {
	
	protected static final long serialVersionUID = -8317765732411101420L;
	protected Registry registry = null;
	

	// users accounts
	protected static List<Account> accounts = new ArrayList<Account>();
	protected List<Session> sessions,deadSessions;

	private Lock balanceChangeLock;
	private Condition sufficientFundsCondition;
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	
	Map accounts_map= new HashMap();
	public Bank () throws RemoteException{
		
		super();

		registry = LocateRegistry.createRegistry(1099);
		System.setProperty("java.security.policy","file:test.policy");	
			
		//initial accounts added
		sessions=new ArrayList<>();
		deadSessions=new ArrayList<>();
		accounts_map.put(1,"Prince");
		accounts_map.put(2,"Pranav");
		accounts_map.put(3,"Vishesh");
		accounts_map.put(4,"Chnadranshu");
		accounts_map.put(5,"Deepak");
		accounts_map.put(6,"Abhishek");

		//to ensure synchronization due to multithreading implementation
		balanceChangeLock = new ReentrantLock();
		sufficientFundsCondition = balanceChangeLock.newCondition();
	}
	
	@Override
	// to  open account of a client with provided accountNumber(which is optional) and Name of a new user.
	public synchronized String startingBalance(int accountNum,String name) throws RemoteException, RemoteBankingException,InvalidArgumentException
	{
		long session_id=9999;
		String acc_session=null;
		if(accountNum>100)
		{
			acc_session="Invalid"+":"+String.valueOf(-2);
			return acc_session;
		}
		if(accountNum>0 && accountNum<7){
			acc_session="Invalid"+":"+String.valueOf(-1);
			return acc_session;
					
		}
		else if(accountNum>6)
		{
			if(accounts_map.get(accountNum)!=null)
				throw new RemoteBankingException("Account already exists.");
			if(isNumeric(name))
				throw new InvalidArgumentException("Error:Invalid name. Failed attempt to open account.");
			Account acc = new Account(accountNum,name,100);
			accounts_map.put(accountNum,name);
			accounts.add(acc);
			Session s=new Session(acc);
			sessions.add(s);
			session_id=s.sessionId;
			
		}
		else
		{
				Random rand = new Random(); 
        			// Generate random accountNumbers in range 0 to 100 
        			accountNum = rand.nextInt(101); 
				for (int i=0;i<accounts.size();i++) {
					Account acc=accounts.get(i);
        				if (acc.getAccountNum()== accountNum) {
            					accountNum = rand.nextInt(101); 
        				}
					else{

						if(accounts_map.get(accountNum)!=null)
								throw new RemoteBankingException("Account already exists.");
						if(isNumeric(name))
								throw new InvalidArgumentException("Error:Invalid name. Failed attempt to open account.");
						Account acct=new Account(accountNum,name,100);
						accounts_map.put(accountNum,name);
						accounts.add(acct);
						Session s=new Session(acct);
						sessions.add(s);
						session_id=s.sessionId;
						break;
					}
				}
		}
		acc_session=String.valueOf(session_id)+":"+String.valueOf(accountNum);
		return acc_session;
	}

	//to check whether the provided string contains digits
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
		if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}

	//to close account for a user with provided accountNumber and an active session ID
	@Override
	public synchronized double closeAccount(int accountNum,long sessionID) throws RemoteException, RemoteBankingException,InvalidSessionException{
		double balance=0;
		if(!checkSessionActive(sessionID))
				throw new InvalidSessionException("Invalid Session!!!");
		if(accounts_map.get(accountNum)==null)
				throw new RemoteBankingException("Account doesn't exists.");
		for (int i=0;i<accounts.size();i++) {
			Account acc=accounts.get(i);
			if (acc.getAccountNum()== accountNum){
				if(acc.getBalance()==0) {
					accounts_map.remove(accountNum);
					accounts.remove(new Account(accountNum,acc.getAccountName(),acc.getBalance()));
					break;
				}
				else{
					balance=acc.getBalance();
				}
			}
		}	
		return balance;
	}

	//to deposit amount into user account with provided account Number, amount, active session ID 
	// filename to log deposit entries, thread which is running this method
	@Override
	public double deposit(int accountNum, double amount,long sessionID,String fileName,String threadName,int x) throws RemoteException, InvalidSessionException, RemoteBankingException{	

		balanceChangeLock.lock();
		double bal=0.00;
		try{
			if(!checkSessionActive(sessionID))
				throw new InvalidSessionException("Invalid Session!!!");

			for (int i=0; i<accounts.size(); i++){
				Account element = accounts.get(i);
				if (element.getAccountNum() == accountNum){
						element.setBalance(element.getBalance() + amount); 
						element.addTransaction("Deposit", amount);
						bal=inquiry(accountNum,sessionID);					
						String mes=threadName+" "+String.valueOf(x)+" completed deposit...Current balance after deposit: €"+String.valueOf(bal)+"\n";						try{
						FileWriter writer = new FileWriter(fileName, true);
		   			    	writer.write(mes);
		    			    	writer.close();
						sufficientFundsCondition.signalAll();
						}
						catch(Exception e){}		
						return bal;
				}
			}
		}
		finally{
			balanceChangeLock.unlock();
		}
		
		throw new RemoteBankingException("Account doesn't exists!!!");
		
	}

	//to withdraw amount from user account if sufficient balance founds, with provided account Number, amount, active session ID 
	// filename to log withdraw entries, thread which is running this method
	@Override
	public double withdraw(int accountNum, double amount,long sessionID,String fileName,String threadName,int x) throws RemoteException, InvalidSessionException,RemoteBankingException{
		
		balanceChangeLock.lock();
		double balance=0.09;
		try{
		// reduce the amount in the account with account number 'accountNum' by 'amount'
			if(!checkSessionActive(sessionID))
					throw new InvalidSessionException("Invalid Session!!!");
			for (int i=0; i<accounts.size(); i++){
				Account element = accounts.get(i);
				if (element.getAccountNum() == accountNum){
					balance=element.getBalance();
					while(balance<amount)
					{
						sufficientFundsCondition.await();
					}
					if(element.getBalance() > 0 && element.getBalance()-amount >= 0){
						element.setBalance(element.getBalance() - amount); 
						element.addTransaction("Withdraw", amount);
		
						balance=inquiry(accountNum,sessionID);
						String message=threadName+" "+String.valueOf(x)+" completed withdrawal...Current balance after withdrawal: €"+String.valueOf(balance)+"\n";
						
						FileWriter writer = new FileWriter(fileName, true);
	   			    		writer.write(message);
	    			    		writer.close();
						return balance;
					}				
				}
			}
		}
		catch(InvalidSessionException e){
			throw new InvalidSessionException("Invalid Session!!!");	
		}
		catch(Exception e)
		{System.out.println(e.getMessage());}
		finally{
			balanceChangeLock.unlock();
		}		 
		throw new RemoteBankingException("Account doesn't exists.");
	}
	
        //to fetch total balance for entire account
	@Override
	public double getTotalBalance() throws RemoteException
	{
		balanceChangeLock.lock();
		double total=0.00;
		try
		{	
			 for (int i=0; i<accounts.size(); i++){
					Account element= accounts.get(i);
						total+=element.getBalance();
			 }
			 return total;	
			
		}
		catch(Exception e){

		}
		finally{
			balanceChangeLock.unlock();
		}
		return total;
	}

	// to fetch balance for an existing account provided account Number and an active session ID which is valid for ongoing transaction.
	@Override
	public synchronized double inquiry(int accountNum,long sessionID) throws RemoteException, InvalidSessionException,RemoteBankingException {
		// returns the balance of the account with account number 'accountNum'
		balanceChangeLock.lock();
		try{			
			if(!checkSessionActive(sessionID))
					throw new InvalidSessionException("Invalid Session!!!");
				
			for (int i=0; i<accounts.size(); i++){
				Account element = accounts.get(i);
				if (element.getAccountNum() ==accountNum){
					return element.getBalance(); 
					
				}
			}	
		}
		finally{
			balanceChangeLock.unlock();
		}
			throw new RemoteBankingException("Account doesn't exists.");
	}

	//to fetch the mini statement provided accountNumber, from Date(dd/mm/yyyy) , toDate(dd/mm/yyyy), and an active session ID.
	@Override
	public List<Transaction> getStatement(int accountNum, Date fromDate, Date toDate,long sessionID) throws RemoteException, InvalidSessionException, StatementException,RemoteBankingException {
		List<Transaction> statementList = new ArrayList<Transaction>();
		
		if(!checkSessionActive(sessionID))
			throw new InvalidSessionException("Invalid Session!!!");
		for (int i=0; i<accounts.size(); i++){
			Account element = accounts.get(i);
			if (element.getAccountNum() == accountNum){
					return element.getTransactionsByDate(fromDate, toDate);
			}
		}				
		throw new StatementException("Could not generate statement for given account and date");
	}

	//to stop server, if client uses exit command.
	@Override
	public void stopServer() throws RemoteException
	{
		System.out.println("Stopping server......");
		try
		{
			registry.unbind("BankInterface");
			UnicastRemoteObject.unexportObject(registry,true);
			System.exit(0);
			
		}
		catch(RemoteException e){}
		catch(Exception e){}
	}

	 // to check whether session is active or not.
	 private boolean checkSessionActive(long sessID) throws InvalidSessionException{
         for(Session s : sessions){

            //Checks if the sessionID passed from client is in the sessions list and active
            if(s.getClientId() == sessID && s.isAlive()) {
                //Prints session details and returns true if session is alive
                System.out.println(">> Session " + s.getClientId() + " running for " + s.getTimeAlive() + "s");
                System.out.println(">> Time Remaining: " + (s.getMaxSessionLength() - s.getTimeAlive()) + "s");
                return true;
            }

            //If session is in list, but timed out, add it to deadSessions list
            //This flags timed out sessions for removeAll
            //They will be removed next time this method is called
            if(!s.isAlive()) {
                System.out.println("\n>> Cleaning up timed out sessions");
                System.out.println(">> SessionID: " + s.getClientId());
                deadSessions.add(s);
            }
        }
        System.out.println();

        // cleanup dead sessions by removing them from sessions list
        sessions.removeAll(deadSessions);

        //throw exception if sessions passed to client is not valid
        throw new InvalidSessionException("Invalid Session!!!");
	}
       
	
	public static void main(String[] args) throws Exception {
		try{
		
		@SuppressWarnings("unused")

		// Create an instance of the local object 
		Bank bankServer = new Bank(); 
		//System.out.println("Bank Instance created!!!"); 

		// Put the server object into the Registry
		Naming.rebind("BankInterface", bankServer);  
		System.out.println("-------------------------------------\nBank listening for incoming requests\n-------------------------------------\n"); 
		
		// setup some test accounts with various balances.
		Account account1 = new Account(1, "Prince", 11000);
		Account account2 = new Account(2, "Pranav", 15000);
		Account account3 = new Account(3, "Vishesh", 7500);
		Account account4 = new Account(4, "Chandranshu", 20000);
		Account account5 = new Account(5, "Deepak", 32900);
		Account account6 = new Account(6, "Abhishek", 3900);
		
		accounts.add(account1);
		accounts.add(account2);
		accounts.add(account3);
		accounts.add(account4);
		accounts.add(account5);
		accounts.add(account6);
				
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
	}
}
