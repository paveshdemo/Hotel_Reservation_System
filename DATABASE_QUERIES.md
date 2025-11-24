# Database Schema & Query Examples

## Payment Table Schema

The `Payment` entity is mapped to the `payments` table with the following structure:

```sql
CREATE TABLE payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_provider VARCHAR(50),
    transaction_id VARCHAR(100) UNIQUE,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'LKR',
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_date DATETIME,
    gateway_response JSON,
    failure_reason TEXT,
    refund_amount DECIMAL(10, 2) DEFAULT 0.00,
    refund_date DATETIME,
    completed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_booking_id (booking_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
);
```

## Sample Data

### Insert a Test Payment (Credit Card)
```sql
INSERT INTO payments (
    booking_id,
    payment_method,
    payment_provider,
    transaction_id,
    amount,
    currency,
    payment_status,
    payment_date,
    completed_at
) VALUES (
    22,
    'CREDIT_CARD',
    'Simulated',
    'TXN-1234567890',
    40320.00,
    'LKR',
    'PAID',
    NOW(),
    NOW()
);
```

### Insert a Test Payment (Bank Transfer)
```sql
INSERT INTO payments (
    booking_id,
    payment_method,
    payment_provider,
    transaction_id,
    amount,
    currency,
    payment_status
) VALUES (
    23,
    'BANK_TRANSFER',
    'Bank',
    'TRANSFER-9876543210',
    25000.00,
    'LKR',
    'PENDING'
);
```

## Common Queries

### 1. Get All Payments for a Booking
```sql
SELECT * FROM payments 
WHERE booking_id = 22 
ORDER BY created_at DESC;
```

### 2. Get Latest Payment for a Booking
```sql
SELECT * FROM payments 
WHERE booking_id = 22 
ORDER BY created_at DESC 
LIMIT 1;
```

### 3. Find Payment by Transaction ID
```sql
SELECT * FROM payments 
WHERE transaction_id = 'TXN-1234567890';
```

### 4. Get All Paid Payments
```sql
SELECT * FROM payments 
WHERE payment_status = 'PAID' 
ORDER BY payment_date DESC;
```

### 5. Get All Pending Payments (for manual verification)
```sql
SELECT p.*, b.booking_reference, b.customer_name, b.total_amount
FROM payments p
JOIN bookings b ON p.booking_id = b.booking_id
WHERE p.payment_status = 'PENDING'
ORDER BY p.created_at ASC;
```

### 6. Get Payment Statistics for a Period
```sql
SELECT 
    DATE(payment_date) as payment_date,
    COUNT(*) as total_payments,
    SUM(amount) as total_amount,
    payment_method,
    payment_status
FROM payments
WHERE payment_date BETWEEN '2024-01-01' AND '2024-01-31'
GROUP BY DATE(payment_date), payment_method, payment_status
ORDER BY payment_date DESC;
```

### 7. Get Failed Payments
```sql
SELECT p.*, b.booking_reference, b.customer_name
FROM payments p
JOIN bookings b ON p.booking_id = b.booking_id
WHERE p.payment_status IN ('FAILED', 'CANCELLED')
ORDER BY p.created_at DESC;
```

### 8. Get Refunded Payments
```sql
SELECT * FROM payments 
WHERE refund_date IS NOT NULL 
ORDER BY refund_date DESC;
```

### 9. Calculate Total Refunds
```sql
SELECT 
    SUM(refund_amount) as total_refunded,
    COUNT(*) as refund_count
FROM payments
WHERE refund_date IS NOT NULL
AND DATE(refund_date) BETWEEN '2024-01-01' AND '2024-01-31';
```

### 10. Get Payment Method Statistics
```sql
SELECT 
    payment_method,
    COUNT(*) as count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount,
    payment_status
FROM payments
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY payment_method, payment_status
ORDER BY total_amount DESC;
```

### 11. Get Payments by Customer
```sql
SELECT p.*, b.booking_reference, b.customer_name, c.email
FROM payments p
JOIN bookings b ON p.booking_id = b.booking_id
JOIN customers c ON b.customer_id = c.customer_id
WHERE c.email = 'customer@example.com'
ORDER BY p.created_at DESC;
```

### 12. Check Payment Completion Rate
```sql
SELECT 
    COUNT(DISTINCT CASE WHEN p.payment_status = 'PAID' THEN b.booking_id END) as completed_payments,
    COUNT(DISTINCT b.booking_id) as total_bookings,
    ROUND(
        COUNT(DISTINCT CASE WHEN p.payment_status = 'PAID' THEN b.booking_id END) * 100.0 / 
        COUNT(DISTINCT b.booking_id), 2
    ) as completion_rate
FROM bookings b
LEFT JOIN payments p ON b.booking_id = p.booking_id
WHERE b.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY);
```

### 13. Find Unpaid Bookings (older than 24 hours)
```sql
SELECT b.*, p.payment_status
FROM bookings b
LEFT JOIN payments p ON b.booking_id = p.booking_id
WHERE b.booking_status = 'PENDING_PAYMENT'
AND b.created_at < DATE_SUB(NOW(), INTERVAL 24 HOUR)
ORDER BY b.created_at ASC;
```

### 14. Get Payment Success Rate by Method
```sql
SELECT 
    payment_method,
    COUNT(*) as total,
    SUM(CASE WHEN payment_status = 'PAID' THEN 1 ELSE 0 END) as successful,
    ROUND(
        SUM(CASE WHEN payment_status = 'PAID' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2
    ) as success_rate
FROM payments
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY payment_method;
```

### 15. Revenue Report
```sql
SELECT 
    DATE(payment_date) as date,
    payment_method,
    COUNT(*) as transactions,
    SUM(amount) as revenue,
    AVG(amount) as avg_transaction
FROM payments
WHERE payment_status = 'PAID'
AND payment_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(payment_date), payment_method
ORDER BY date DESC, revenue DESC;
```

## Useful Database Operations

### Update Payment Status to Paid
```sql
UPDATE payments 
SET payment_status = 'PAID', 
    payment_date = NOW(),
    completed_at = NOW()
WHERE transaction_id = 'TRANSFER-9876543210';
```

### Process Refund
```sql
UPDATE payments 
SET payment_status = 'REFUNDED',
    refund_amount = amount,
    refund_date = NOW()
WHERE payment_id = 1;
```

### Mark Payment as Failed
```sql
UPDATE payments 
SET payment_status = 'FAILED',
    failure_reason = 'Card declined'
WHERE transaction_id = 'TXN-failed-123';
```

### Delete Test Payments (for development only)
```sql
DELETE FROM payments 
WHERE payment_provider = 'Simulated' 
AND created_at < DATE_SUB(NOW(), INTERVAL 7 DAY);
```

## Maintenance Queries

### Cleanup Old Pending Payments (older than 30 days)
```sql
-- View what will be deleted
SELECT * FROM payments
WHERE payment_status = 'PENDING'
AND created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- Delete old pending payments
DELETE FROM payments
WHERE payment_status = 'PENDING'
AND created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

### Archive Completed Payments
```sql
-- Create archive table (one-time)
CREATE TABLE payments_archive AS
SELECT * FROM payments
WHERE payment_date < DATE_SUB(NOW(), INTERVAL 90 DAY)
AND payment_status IN ('PAID', 'REFUNDED');

-- Delete archived records
DELETE FROM payments
WHERE payment_date < DATE_SUB(NOW(), INTERVAL 90 DAY)
AND payment_status IN ('PAID', 'REFUNDED');
```

### Verify Data Integrity
```sql
-- Check for orphaned payments (no corresponding booking)
SELECT p.* FROM payments p
LEFT JOIN bookings b ON p.booking_id = b.booking_id
WHERE b.booking_id IS NULL;

-- Check for duplicate transactions
SELECT transaction_id, COUNT(*) as count
FROM payments
WHERE transaction_id IS NOT NULL
GROUP BY transaction_id
HAVING count > 1;

-- Verify amounts match
SELECT p.payment_id, p.amount, b.total_amount
FROM payments p
JOIN bookings b ON p.booking_id = b.booking_id
WHERE p.amount != b.total_amount
AND p.payment_status = 'PAID';
```

## Performance Optimization

### Add Indexes (if not using the schema above)
```sql
CREATE INDEX idx_payments_booking_date ON payments(booking_id, payment_date DESC);
CREATE INDEX idx_payments_status_date ON payments(payment_status, payment_date DESC);
CREATE INDEX idx_payments_method_date ON payments(payment_method, payment_date DESC);
```

### Analyze Query Performance
```sql
EXPLAIN SELECT * FROM payments 
WHERE booking_id = 22 
ORDER BY created_at DESC;
```

### Optimize Slow Queries
```sql
-- If finding by transaction ID is slow, ensure this index exists:
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);

-- If searching by date range is slow:
CREATE INDEX idx_payments_created_range ON payments(created_at, payment_status);
```

## Backup & Recovery

### Backup Payment Table
```bash
mysqldump -u username -p database_name payments > payments_backup.sql
```

### Restore from Backup
```bash
mysql -u username -p database_name < payments_backup.sql
```

### Create Payment Snapshot for Reporting
```sql
CREATE TABLE payments_snapshot AS
SELECT * FROM payments
WHERE DATE(payment_date) = '2024-01-15';
```

## Monitoring Queries

### Daily Revenue Summary
```sql
SELECT 
    CURDATE() as report_date,
    SUM(CASE WHEN p.payment_status = 'PAID' THEN p.amount ELSE 0 END) as total_revenue,
    COUNT(DISTINCT CASE WHEN p.payment_status = 'PAID' THEN p.payment_id END) as transactions,
    COUNT(DISTINCT CASE WHEN p.payment_status = 'PENDING' THEN p.payment_id END) as pending
FROM payments p;
```

### Alert: Unusually Low Transaction Volume
```sql
SELECT 
    DATE(payment_date) as date,
    COUNT(*) as transaction_count
FROM payments
WHERE payment_status = 'PAID'
AND payment_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(payment_date)
HAVING transaction_count < 5;
```

### Alert: Failed Payments Trend
```sql
SELECT 
    DATE(created_at) as date,
    COUNT(*) as failed_count
FROM payments
WHERE payment_status = 'FAILED'
AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(created_at)
ORDER BY date DESC;
```
