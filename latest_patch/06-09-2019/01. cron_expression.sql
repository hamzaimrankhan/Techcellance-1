ALTER TABLE como_file_configurations ADD COLUMN cron_expression varchar(25);

Update como_file_configurations Set cron_expression = '0 0/5 9-11 * * ?' Where file_type = '1';
Update como_file_configurations Set cron_expression = '0 59 9 * * ?' Where file_type = '2';
Update como_file_configurations Set cron_expression = '0 58 9 * * ?' Where file_type = '3';