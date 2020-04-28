package client;

import interfaces.*;
import server.*;
import exceptions.*;
import runnable.*;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Locale;
import java.io.*;

/**
 * ATM class is to represent a client process in a RMI banking application. The client
 * loads a set of account transactions from a account object and makes RMI calls on
 * corresponding remote account objects. The client is multi-threaded.
 */

public class ATM {

	private static BankInterface bankInterface;
	static long sessionID;
	
	// formatting decimal to decimal places
	private static DecimalFormat precision2 = new DecimalFormat("0.00");

	public static void main(String[] args) throws Exception{

		String option="0";
		
		try{
	
			// connecting to remote server
			bankInterface = (BankInterface) Naming.lookup("rmi://127.0.0.1/BankInterface");	
			System.out.println("\n----------------------------------------------------------\n Client Connected"+"\n----------------------------------------------------------\n");	
		
		//menu based banking
		while(Integer.parseInt(option)!=8)
		{
			System.out.println("------------------------------------------------------------");
			System.out.println("Please select one of the below operations to proceed further");
			System.out.println("\n1.Open Account  \n2.Deposit Cash  \n3.Withdraw Cash \n4.Transfer Cash \n5.Inquiry Balance \n6.Mini Statement \n7.Close Account \n8.Exit");
			System.out.println("------------------------------------------------------------");
			Scanner in=new Scanner(System.in);
 			option=in.nextLine();
			switch (option) {

			//open account
			case "1":
				try{
					System.out.println("Please provide your registration details. e.g.,Name, Account Number");
					System.out.println("If doesn't want to choose Account Number, please enter 0");
					String name=in.nextLine();
					
					int acc=in.nextInt();
					
					String[] account_str=bankInterface.startingBalance(acc,name).split(":");
					int accountNumber=Integer.parseInt(account_str[1]);
					
					if(accountNumber==-1)
						System.out.println("Please provide a valid account Number (6-100), as already customer exists");
					else if(accountNumber==-2)
						System.out.println("Please provide a valid account Number (6-100)");
					else
					{
						
						sessionID=Long.parseLong(account_str[0]);
						System.out.println("Welcome to KBK Bank!!!"); 

						//Print account details
			    			System.out.println("--------------------------\nAccount Details:\n--------------------------\n" +
				               "Account Number: " + accountNumber +
				               "\nSessionID: " + account_str[0] +
				               "\nUsername: " + name +
				               "\nBalance: " +  100.00+
				               "\n--------------------------\n");
			    			System.out.println("Session active for 5 minutes");
			    			System.out.println("Please use this session id " + sessionID + " for all other bank transactions");
					}
				}
				catch(RemoteException e)
				{
					System.out.println(e.getMessage());
				}
				catch(InvalidArgumentException e)
				{
					System.out.println(e.getMessage());
				}
				catch(RemoteBankingException e)
				{
					System.out.println(e.getMessage());
				}
				break;

			//deposit amount
			case "2":
				try{
					System.out.println("Please provide deposit related details. e.g.,Account Number, Amount, SessionID");
					String accNum=in.nextLine();
					String amount=in.nextLine();
					sessionID=Long.parseLong(in.nextLine());
					
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
					Date curDate = new Date();
					String strDate = sdf.format(curDate);
					String fileName = "transactionLogs_" + accNum + "_" +strDate;	

					//change this path for getting results				
					String path="/home/sneha/Downloads/DistributedBanking/DistributedBanking/src/Logs/"+fileName;
					
					File newFile = new File(path);	

					DepositRunnable dt = new DepositRunnable(bankInterface,Integer.parseInt(accNum),Double.parseDouble(amount),sessionID,path,1);	
					Thread d=new Thread(dt);
					d.setName("Prince..........(Deposit Thread)");
					d.start();
					Thread.sleep(2000);

				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
				break;

			//withdraw amount
			case "3":
				try
				{
					System.out.println("Please provide withdraw related details. e.g.,Account Number, Amount, SessionID");
					String accN=in.nextLine();
					String amt=in.nextLine();
					sessionID=Long.parseLong(in.nextLine());
					
					//SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hhmmss");
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
					Date curDate = new Date();
					String strDate = sdf.format(curDate);
					String fileName = "transactionLogs_" + accN + "_" +strDate;

					//change this path to get results
					String path="/home/sneha/Downloads/DistributedBanking/DistributedBanking/src/Logs/"+fileName;
					File newFile = new File(path);	

					WithdrawRunnable wt=new WithdrawRunnable(bankInterface,Integer.parseInt(accN),Double.parseDouble(amt),sessionID,path,1);
						
					Thread w=new Thread(wt);
					w.setName("Sneha..........(Withdraw Thread)");
					w.start();
					Thread.sleep(2000);
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());		
				}
				break;

			//transfer amount 
			case "4":
					try{

						System.out.println("Please provide money transfer related details. e.g.,Account Number, Amount, SessionID");
						String acN=in.nextLine();
						String amnt=in.nextLine();
						sessionID=Long.parseLong(in.nextLine());
						
						SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hhmmss");
						Date curDate = new Date();
						String strDate = sdf.format(curDate);
						String fileName = "transferlogFile_" + acN + "_" + strDate;

						//change this path to get results
						String path="/home/sneha/Downloads/DistributedBanking/DistributedBanking/src/Logs/"+fileName;
						File newFile = new File(path);	

						System.out.println("Total balance before transfer €"+precision2.format(bankInterface.getTotalBalance()));
						System.out.println("Transferring...... Please wait!!!");

						for(int i=1;i<=100;i++)
						{

							DepositRunnable dw = new DepositRunnable(bankInterface,Integer.parseInt(acN),Double.parseDouble(amnt),sessionID,path,10);
							WithdrawRunnable wt=new WithdrawRunnable(bankInterface,Integer.parseInt(acN),Double.parseDouble(amnt),sessionID,path,10);
						
							Thread one=new Thread(dw);
							Thread two=new Thread(wt);
							one.setName("Prince............................................................(Deposit Thread)");
							two.setName("Sneha.............................................................(Withdraw Thread)");
							one.start();
							Thread.sleep(1000);
							two.start();
						}
						Thread.sleep(3000);
						System.out.println("Total balance after successful transfer €" +precision2.format(bankInterface.getTotalBalance()));

					}
					catch (Exception e)
					{
						System.out.println(e.getMessage());
					}
				break;

			//inquiry bank balance
			case "5":
				System.out.println("Please provide account related details. e.g.,Account Number, SessionID");
				String AccountNum=in.nextLine();
				sessionID=Long.parseLong(in.nextLine());
				try{	
					double resultInquiry = bankInterface.inquiry(Integer.parseInt(AccountNum),sessionID);
					System.out.println("Current balance: €" + precision2.format(resultInquiry));
		       		} 
				//Catch exceptions that can be thrown from the server
				catch (InvalidSessionException e)
				{
					System.out.println(e.getMessage());
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
				}
				break;

			//get statement
			case "6":
				try{
					System.out.println("Please provide account related details to fetch statement. e.g.,Account Number, From Date(dd/mm/yyyy), ToDate(dd/mm/yyyy), SessionID");
					String actNumber=in.nextLine();
					String fromD=in.nextLine();
					String toD=in.nextLine();
					sessionID=Long.parseLong(in.nextLine());

					Date fromDate = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(fromD);
					Date toDate = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(toD);

					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
					Date curDate = new Date();
					String strDate = sdf.format(curDate);
					String fileName = "transactionLogs_" + actNumber + "_" +strDate;

					//change this path to get results
					String path="/home/sneha/Downloads/DistributedBanking_DAIICT/DistributedBanking_DAIICT/src/Logs/"+fileName;
					
					java.util.List<Transaction> statementList = 
							bankInterface.getStatement(Integer.parseInt(actNumber), fromDate, toDate,sessionID);
					String message="----------------------------------\nMINI STATEMENT\n----------------------------------\n";
					
					FileWriter writer = new FileWriter(path, true);
					writer.write(message);
	
					for (int i=0; i<statementList.size(); i++) {
						Transaction element = statementList.get(i);
	   	    				writer.write(element.toString());
						writer.write("\n");
					}
					writer.close();	   
					System.out.println("Balance Statement generated!!!");
				}
				catch(RemoteException e)
				{
					System.out.println(e.getMessage());
				}
				catch(InvalidSessionException e)
				{	
					System.out.println(e.getMessage());
				}
				catch(StatementException e)
				{	
					System.out.println(e.getMessage());
				}
				break;

			//close account
			case "7":
				System.out.println("Please provide account related details to close account. e.g.,Account Number, SessionID");
				String aN=in.nextLine();
				sessionID=Long.parseLong(in.nextLine());
				try{
					double closed=bankInterface.closeAccount(Integer.parseInt(aN),sessionID);				
					if(closed>0){
						System.out.println("Error: Account can't be closed as positive Balance found amounts to €"+closed);
					}
					else
						System.out.println("ok. Account closed successfully!!!");
					}
				catch(RemoteBankingException e)
				{
					System.out.println(e.getMessage());
				}
				catch(InvalidSessionException e)
				{
					System.out.println(e.getMessage());
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
				break;

			//to start the menu
			case "0":
				break;
			}
		    }
		    if(Integer.parseInt(option)==8){
				
				bankInterface.stopServer();
		    }		
		    System.exit(0);
		}
		catch(Exception e)
		{
			if(Integer.parseInt(option)==8)
				System.out.println("Thanking you for visiting us!!!");
			else
				System.out.println("Server shut down. Can't connect.");
		}
		
	}
}
