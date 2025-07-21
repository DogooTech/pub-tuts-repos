INSERT INTO account (
    customer_id,
    initial_balance,
    amount,
    event_type,
    version,
    created_at
) VALUES
      ('cust-12345', 1000.00, 0.00, 'INIT', 1, NOW());