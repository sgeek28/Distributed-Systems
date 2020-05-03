Distributed Banking System with synchronization feature

This is menu-based distributed-banking system implemented with runnable threads for deposit and withdraw opeartion.Overview of the project can be found in report.

Follow these steps to compile the project

Make necessary changes in the path of deposit, withdrawal,get statement and
transfer operation of ATM class to get the log entries of deposit,
withdrawal and transfer.

1. javac interfaces/*.java
2. javac exceptions/*.java
3. javac runnable/*.java
4. javac client/*.java
5. javac server/*.java

Follow these steps to run the project

In Terminal 1
1. java server/Bank

In Terminal 2
1. java client/ATM

Run following commands to get the output in Terminal 2

1. Select option 1 to open account. Enter Name followed by account number between 1-100 in individual lines. If user doesn't want to provide account number, provide 0.

2. Select option 2 to deposit amount. Enter Account Number followed by amount followed by session ID which was generated previously in individual lines.For e.g.,
7
1000
234567

3. Select option 3 to withdraw amount. Enter Account Number followed by amount followed by session ID which was generated previously in individual lines.For e.g.,
7
1000
234567

4. Select option 4 to see how the deposit and withdraw threads run concurrently, if someone tries to access same account. Enter account number followed by amount want to transfer and session ID

5. Select option 5 to get the account current balance. Provide
ccount Number followed by session ID in individual lines.For e.g.,
7
234567

6. Select option 6 to get mini statement. Provide account number followed by from date(dd/mm/yyyy) format followed by to date(dd/mm/yyyy) format followed by session id generated earlier in individual lines. For e.g,
7
27/04/2020
29/04/2020
234567

7. Select option 7 to close the account. Provide account Number followed by session id in individual lines.

8. Select option 8 to exit the service and shit down the server. 

Output screenshots are provided in screenshot folder for reference.
Log files for transactions are generated in Logs folder for each account.
