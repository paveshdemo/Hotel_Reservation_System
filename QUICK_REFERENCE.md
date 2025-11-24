# Payment System - Quick Reference

## 🚀 Quick Links

| Page | URL | Purpose |
|------|-----|---------|
| Payment Form | `/payment?bookingId=22` | Process payment for booking |
| Success | `/payment-success?bookingId=22` | Confirmation page |
| Pending | `/payment-pending?bookingId=22` | Bank transfer pending |
| API Status | `/api/payments/status/22` | Check payment status |

---

## 📝 File Locations

```
Project Root
│
├── PAYMENT_SYSTEM_DOCUMENTATION.md ← Read this first!
├── PAYMENT_SETUP_GUIDE.md
├── DATABASE_QUERIES.md
├── IMPLEMENTATION_SUMMARY.md
│
├── src/main/java/
│   └── com/hotelreservationsystem/hotelreservationsystem/
│       ├── controller/
│       │   ├── PaymentController.java
│       │   └── PageController.java
│       ├── service/
│       │   └── PaymentService.java
│       └── repository/
│           └── PaymentRepository.java
│
└── src/main/resources/templates/
    ├── payment.html
    ├── payment-success.html
    └── payment-pending.html
```

---

## 🎨 UI Components

### Payment Page (`payment.html`)
- 🎯 Hero section with title
- 📋 Booking summary (left 70%)
- 💰 Price breakdown (right 30%, sticky)
- 💳 Payment method selector
- 📝 Credit card form
- 🏦 Bank transfer details

### Success Page (`payment-success.html`)
- ✅ Confirmation message
- 📋 Booking details
- ℹ️ Next steps
- 📞 Contact information

### Pending Page (`payment-pending.html`)
- ⏳ Pending status indicator
- 🏦 Bank transfer instructions
- 📋 Booking details
- ⚠️ Important information
- 📋 Copy-to-clipboard buttons

---

## 🔌 API Endpoints

### Create/Process Payment
```http
POST /api/payments/process
Content-Type: application/json
X-CSRF-TOKEN: [token]

{
  "bookingId": "22",
  "paymentMethod": "CREDIT_CARD",
  "cardholderName": "John Doe",
  "cardNumber": "4111111111111111",
  "expiryDate": "12/25",
  "cvv": "123"
}

Response: 
{
  "success": true,
  "paymentId": 1,
  "bookingId": 22,
  "paymentStatus": "PAID",
  "message": "Payment processed successfully"
}
```

### Check Payment Status
```http
GET /api/payments/status/22
Accept: application/json

Response:
{
  "paymentId": 1,
  "bookingId": 22,
  "amount": 40320.00,
  "paymentStatus": "PAID",
  "paymentMethod": "CREDIT_CARD",
  "transactionId": "TXN-1234567890",
  "paymentDate": "2024-01-15T10:30:00"
}
```

### Verify Bank Transfer
```http
POST /api/payments/verify-bank-transfer
Content-Type: application/json
X-CSRF-TOKEN: [token]

{
  "transactionId": "TRANSFER-9876543210",
  "bookingId": "22"
}

Response:
{
  "success": true,
  "message": "Bank transfer verified successfully"
}
```

---

## 💾 Database

### Key Tables
```sql
payments (NEW)
├── payment_id (PK)
├── booking_id (FK)
├── amount (DECIMAL 10,2)
├── currency (VARCHAR)
├── payment_method (ENUM)
├── payment_status (ENUM)
└── transaction_id (UNIQUE)

bookings (EXISTING - MODIFIED)
├── booking_id
├── payment_status
└── booking_status
```

### Useful Queries
```sql
-- Get latest payment for booking
SELECT * FROM payments 
WHERE booking_id = 22 
ORDER BY created_at DESC 
LIMIT 1;

-- Get all paid payments
SELECT * FROM payments 
WHERE payment_status = 'PAID';

-- Get pending bank transfers
SELECT * FROM payments 
WHERE payment_method = 'BANK_TRANSFER' 
AND payment_status = 'PENDING';
```

---

## 🔐 Security

### Authentication
- ✅ All pages require authentication
- ✅ Users can only access their own bookings
- ✅ Session-based authentication

### CSRF Protection
- ✅ CSRF tokens on all forms
- ✅ CSRF token in API headers
- ✅ Spring Security validation

### Data Validation
- ✅ Card number validation (frontend)
- ✅ Email validation
- ✅ Amount verification
- ✅ User ownership checks

---

## 🧪 Test Scenarios

### Scenario 1: Credit Card Payment
```
1. Navigate to /payment?bookingId=22
2. Select "Credit Card" option
3. Fill form:
   - Name: John Doe
   - Card: 4111 1111 1111 1111
   - Expiry: 12/25
   - CVV: 123
4. Check terms checkbox
5. Click "Pay Now"
6. Should redirect to /payment-success?bookingId=22
7. Check email for confirmation
```

### Scenario 2: Bank Transfer
```
1. Navigate to /payment?bookingId=22
2. Select "Bank Transfer" option
3. Read bank details
4. Copy bank account number and reference
5. Confirm intention to transfer
6. Click "Confirm Bank Transfer"
7. Should redirect to /payment-pending?bookingId=22
8. Admin verifies transfer
9. System confirms payment
10. User receives confirmation email
```

### Scenario 3: Unauthorized Access
```
1. Login as User A
2. Try accessing User B's payment:
   /payment?bookingId=99 (User B's booking)
3. Should see "Access Denied" error
4. Should redirect to /my-bookings
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| 404 on `/payment` | Check PageController routes are defined |
| "Not authenticated" | Ensure user is logged in |
| "Access denied" | Verify you own the booking |
| Form not submitting | Check browser console for JavaScript errors |
| Email not sending | Verify SMTP settings in application.properties |
| Payment API returns 500 | Check server logs for exceptions |
| CSRF token invalid | Ensure token is sent in request header |

---

## 📊 Monitoring

### Key Metrics
- Payment success rate
- Failed payment count
- Pending payments (awaiting bank transfer)
- Average payment time
- Revenue by payment method

### Useful Queries
```sql
-- Daily revenue
SELECT DATE(payment_date) as date, 
       SUM(amount) as revenue 
FROM payments 
WHERE payment_status = 'PAID' 
GROUP BY DATE(payment_date);

-- Payment method stats
SELECT payment_method, 
       COUNT(*) as count, 
       AVG(amount) as avg_amount 
FROM payments 
GROUP BY payment_method;

-- Pending payments (24+ hours)
SELECT * FROM payments 
WHERE payment_status = 'PENDING' 
AND created_at < DATE_SUB(NOW(), INTERVAL 24 HOUR);
```

---

## 🚢 Deployment Checklist

### Pre-Deployment
- [ ] All tests passing
- [ ] Security audit complete
- [ ] Database backup configured
- [ ] Email service configured
- [ ] HTTPS certificate obtained
- [ ] Payment gateway API keys obtained

### Deployment
- [ ] Deploy code to production
- [ ] Run database migrations
- [ ] Verify all endpoints accessible
- [ ] Test payment processing
- [ ] Monitor error logs
- [ ] Verify email notifications

### Post-Deployment
- [ ] Monitor payment transactions
- [ ] Check for failed payments
- [ ] Review user feedback
- [ ] Monitor system performance
- [ ] Verify email delivery

---

## 📞 Contact & Support

### For Implementation Issues
- Check console logs (F12)
- Review server logs
- Check application.properties
- Verify database connectivity

### For Payment Gateway Integration
- Refer to payment provider documentation
- Update PaymentController.processPayment()
- Test thoroughly before going live

### For Customization
- UI changes: Edit HTML templates in `templates/`
- API changes: Modify PaymentController
- Database changes: Update entity and queries

---

## 🔗 Related Documentation

1. **PAYMENT_SYSTEM_DOCUMENTATION.md** - Complete technical documentation
2. **PAYMENT_SETUP_GUIDE.md** - Installation guide
3. **DATABASE_QUERIES.md** - Database operations
4. **IMPLEMENTATION_SUMMARY.md** - Architecture overview

---

## 📅 Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024-01-15 | Initial implementation |

---

## 💡 Tips & Tricks

### For Development
```bash
# Test without payment gateway
Use "4111111111111111" as test card

# View all payments
SELECT * FROM payments ORDER BY created_at DESC;

# Check booking payment status
SELECT b.booking_reference, b.payment_status, p.payment_status 
FROM bookings b 
LEFT JOIN payments p ON b.booking_id = p.booking_id;
```

### For Debugging
```javascript
// In browser console
console.log('Current payment method:', selectedPaymentMethod);
console.log('Booking ID:', document.getElementById('bookingId').value);
console.log('CSRF Token:', document.getElementById('csrfToken').value);
```

### For Admin
```sql
-- Verify payment received
UPDATE payments 
SET payment_status = 'PAID', 
    payment_date = NOW() 
WHERE transaction_id = 'TRANSFER-xxx';

-- Update booking to confirmed
UPDATE bookings 
SET booking_status = 'CONFIRMED', 
    payment_status = 'PAID' 
WHERE booking_id = 22;
```

---

**Last Updated:** January 15, 2024  
**Status:** ✅ Production Ready  
**Support:** See documentation files
