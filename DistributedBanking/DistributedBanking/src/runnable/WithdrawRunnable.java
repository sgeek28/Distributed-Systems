/*
* Written by: @Sneha and @Prince
* When user tries to deposit an amount
* this runnable is invoked from ATM class
**/

package runnable;

import interfaces.*;
import server.*;
import exceptions.*;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.*;
import java.rmi.*;
public class WithdrawRunnable implements Runnable
{

   /*
	private data members of thread
   */
   private int accountNum;
   private long sessionID;
   private double amount;
   private BankInterface bankInterface;
   private String fileName;
   private int repetitions;
   private static DecimalFormat precision2 = new DecimalFormat("#.##");


   /*
   *	constructor to initialize accountNumber, 
   *	amount to be withdrawn, 
   *    an active sessionID, 
   *    fileName to log deposit entries
   *    and number of times this thread to be called.
   */
   public WithdrawRunnable(BankInterface bankInterface,int accountNum,double amount,long sessionID,String fileName,int repetitions)
   {
        this.bankInterface=bankInterface;
 	this.accountNum=accountNum;
        this.amount = amount;
	this.sessionID=sessionID;
	this.fileName=fileName;
	this.repetitions=repetitions;
   }

   @Override
   /*
   *    run method to invoke withdraw method of Bank class using client object connected to server 
   *	using Java RMI.
   */
   public void run()
   {
      try
      {	 
		  String message="----------------------------------\nWITHDRAW\n----------------------------------\n";
		  for(int j=1;j<=repetitions;j++){
			  String threadName = Thread.currentThread().getName();
			    message+=threadName+" " +String.valueOf(j)+" is trying to withdraw..... €"+String.valueOf(amount)+"\n";
			     
			    FileWriter writer = new FileWriter(fileName, true);
		   	    writer.write(message);
		    	    writer.close();
			    double resultWithdrawal=bankInterface.withdraw(accountNum,amount,sessionID,fileName,threadName,j);	
			    if(repetitions==1)
				System.out.println("Current balance after withdrawal: €" + precision2.format(resultWithdrawal));
			    if(resultWithdrawal==0.00)
				throw new InsufficientFundsException();
			    Thread.sleep(200);
		}
	    
      }
      catch (InterruptedException e) {System.out.println(e.getMessage());}
      catch (RemoteException e){}
      catch (RemoteBankingException e){System.out.println(e.getMessage());}
      catch (InvalidSessionException  e){System.out.println(e.getMessage());}
      catch (InsufficientFundsException e){System.out.println(e.getMessage());}
      catch(Exception e){}
   }
}

