TRUNCATE TABLE transactions;

# SMALL
# CALL insert_data('transactions', 100000);

# MEDIUM
CALL insert_data('transactions', 1500000);

# LARGE
# CALL insert_data('transactions', 2500000);
