1. New columns added: 
  a. file_type (type of file eg: "BSP")
  b. transaction_type ("if the transaction is credit  or debit")
  c. ticket_restricted (as per opened bug)
 
2. Only transaction with type debit will be processed,other wise it will be skipped.
3. All file elements are configurable, please execute the script shared in the folder before executing the scheduler.
4. XML wlogs will be created in seperate logs as well, but they will also be in the main logs. 
