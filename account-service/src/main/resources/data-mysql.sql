INSERT INTO CUSTOMER(id, cif_number, first_name, last_name, created_at, updated_at) values (1, 1453223564, 'Hamed', 'Mirzaei', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO CUSTOMER(id, cif_number, first_name, last_name, created_at, updated_at) values (2, 4154674165, 'Ali', 'Mirzaei', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ACCOUNT(id, account_number, customer_id, balance, status, created_at, updated_at) values (1, '003544343', 1, 1000000, 'IDEAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ACCOUNT(id, account_number, customer_id, balance, status, created_at, updated_at) values (2, '000453158', 2, 2000000, 'IDEAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
