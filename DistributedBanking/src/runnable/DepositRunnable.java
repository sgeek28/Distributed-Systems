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

public class DepositRunnable implements Runnable
{
   /*
	private data members of thread
   */
   private int accountNum;
   private long sessionID;
   private double amount;
   private BankInterface bankInterface;
   private int repetitions;
   private String fileName;
   private static DecimalFormat precision2 = new DecimalFormat("#.##");

   /*
   *	constructor to initialize accountNumber, 
   *	amount to be deposited, 
   *    an active sessionID, 
   *    fileName to log deposit entries
   *    and number of times this thread to be called.
   */
   public DepositRunnable(BankInterface bankInterface,int accountNum,double amount,long sessionID,String fileName,int repetitions)
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
   *    run method to invoke deposit method of Bank class using client object connected to server 
   *	using Java RMI.
   */
   public void run()
   {
      try
      {
		String message="----------------------------------\nDEPOSIT\n----------------------------------\n";
	    for(int i=1;i<=repetitions;i++){	    
		    String threadName = Thread.currentThread().getName();
		    message+=threadName+" "+String.valueOf(i)+" is trying to deposit .... €"+String.valueOf(amount)+"\n";
 
		    FileWriter writer = new FileWriter(fileName, true);
	   	    writer.write(message);
	    	    writer.close();	   

		    double resultDeposit=bankInterface.deposit(accountNum,amount,sessionID,fileName,threadName,i);
		    
		              
		    if(repetitions==1)
		    	System.out.println("Current balance after deposit: €" + precision2.format(resultDeposit)); 
		    Thread.sleep(100);
	   }
      }
      catch(InvalidSessionException e){System.out.println(e.getMessage());}
      catch (InterruptedException exception) {}
      catch(RemoteBankingException e){System.out.println(e.getMessage());}
      catch (RemoteException e){}
      catch (Exception e){}
   }
}

