# Payment System Setup Guide

## Prerequisites Check

Before using the payment system, ensure the following are in place:

### 1. Database Setup
The Payment entity should already be defined in your project. If not, ensure you have:
```java
@Entity
@Table(name = "payments")
public class Payment {
    // Entity already exists in your project
}
```

### 2. Spring Security Configuration
Ensure `/api/payments/**` endpoints are accessible (either public or authenticated):

```java
// In SecurityConfig.java
.requestMatchers("/api/payments/**").authenticated()  // or .permitAll()
```

### 3. Email Service
The payment system sends confirmation emails. Ensure `EmailService` has:
```java
public void sendBookingConfirmation(Booking booking)
```

---

## Quick Start

### 1. Access Payment Page
Navigate to: `http://localhost:8080/payment?bookingId=1`

### 2. Select Payment Method
- **Credit Card** - Simulated payment (for testing)
- **Bank Transfer** - Manual verification required

### 3. Complete Payment
- Follow the form prompts
- Click "Pay Now" or "Confirm Transfer"
- System redirects to success/pending page

---

## Integration Checklist

### Backend Services (Verify Autowiring)
```java
@Autowired
private PaymentService paymentService;      // ✓ Should work

@Autowired
private BookingService bookingService;      // ✓ Already exists

@Autowired
private EmailService emailService;          // ✓ Already exists
```

### Repository Injection
```java
@Autowired
private PaymentRepository paymentRepository;  // ✓ Should auto-inject
```

### Controller Endpoints Registered
- POST `/api/payments/process` ✓
- GET `/api/payments/status/{bookingId}` ✓
- POST `/api/payments/verify-bank-transfer` ✓

### Page Routes Registered
- GET `/payment` ✓ (existing)
- GET `/payment-success` ✓ (new)
- GET `/payment-pending` ✓ (new)

---

## Configuration Options

### Modify Payment Method Options
In `payment.html`, you can add more payment methods:

```html
<!-- Add new payment method card -->
<div class="col-md-6">
    <div class="payment-method-card" data-method="WALLET" onclick="selectPaymentMethod('WALLET')">
        <div class="text-center p-4">
            <i class="fas fa-wallet fa-2x text-golden mb-3"></i>
            <h5>Digital Wallet</h5>
        </div>
    </div>
</div>
```

### Customize Bank Details
In `payment-pending.html`, update:
```html
<input type="text" class="form-control" value="Your Bank Name" readonly>
<input type="text" class="form-control" id="accountNumber" value="Your Account Number" readonly>
```

### Modify Styling
Colors are defined in `style.css`:
```css
:root {
    --accent-primary: #ffd700;      /* Golden */
    --bg-primary: #0a0a0a;          /* Dark background */
}
```

---

## Testing Payment Processing

### Test Credit Card Payment
```bash
# Request
POST http://localhost:8080/api/payments/process
Content-Type: application/json

{
    "bookingId": "1",
    "paymentMethod": "CREDIT_CARD",
    "cardholderName": "John Doe",
    "cardNumber": "4111111111111111",
    "expiryDate": "12/25",
    "cvv": "123"
}

# Expected Response
{
    "success": true,
    "paymentId": 1,
    "bookingId": 1,
    "paymentStatus": "PAID",
    "message": "Payment processed successfully"
}
```

### Test Bank Transfer
```bash
# Request
POST http://localhost:8080/api/payments/process
Content-Type: application/json

{
    "bookingId": "1",
    "paymentMethod": "BANK_TRANSFER"
}

# Expected Response
{
    "success": true,
    "paymentId": 2,
    "bookingId": 1,
    "paymentStatus": "PENDING",
    "message": "Payment processed successfully"
}
```

### Check Payment Status
```bash
GET http://localhost:8080/api/payments/status/1

# Expected Response
{
    "paymentId": 1,
    "bookingId": 1,
    "amount": 5000.00,
    "paymentStatus": "PAID",
    "paymentMethod": "CREDIT_CARD",
    "transactionId": "TXN-1234567890",
    "paymentDate": "2024-01-15T10:30:00"
}
```

---

## Troubleshooting

### Issue: "Payment endpoint not found"
**Solution:** Ensure `PaymentController.java` is in the correct package and Spring component scanning includes it.

### Issue: "PaymentService not found"
**Solution:** Verify `PaymentService.java` is in the service package with `@Service` annotation.

### Issue: "PaymentRepository not found"
**Solution:** Ensure `PaymentRepository.java` extends `JpaRepository` and is in the repository package.

### Issue: Payment form submission fails
**Solution:** 
1. Check browser console (F12) for JavaScript errors
2. Verify CSRF token is being sent
3. Check network tab to see actual error response
4. Verify authentication is working (user logged in)

### Issue: Email not sending on payment success
**Solution:**
1. Verify `EmailService` is properly configured
2. Check mail server settings in `application.properties`
3. Verify `sendBookingConfirmation()` method exists
4. Check console logs for email service errors

### Issue: Booking status not updating
**Solution:**
1. Verify `saveBooking()` method exists in BookingService
2. Check database for transaction issues
3. Verify booking ID is valid
4. Check console for SQL errors

---

## Production Deployment

### Before Going Live

1. **Integrate Real Payment Gateway**
   ```java
   // In PaymentController.processPayment()
   if ("CREDIT_CARD".equals(paymentMethod)) {
       // TODO: Integrate with Stripe/PayPal/Local gateway
       Payment result = stripeService.processPayment(paymentRequest);
   }
   ```

2. **Setup HTTPS**
   - Essential for payment security
   - Get SSL certificate
   - Update application.properties

3. **Configure Email Service**
   - Setup SMTP server
   - Test email sending
   - Create email templates

4. **Database Backup**
   - Regular backups of payments table
   - Test restore procedures

5. **Logging & Monitoring**
   - Setup payment failure alerts
   - Monitor transaction volumes
   - Track payment success rates

6. **Security Audit**
   - Run OWASP security tests
   - Check PCI compliance
   - Review access controls

7. **Load Testing**
   - Test with multiple concurrent payments
   - Monitor database performance
   - Optimize queries if needed

---

## API Reference

### Payment Methods
- `CREDIT_CARD` - Credit/Debit card payment
- `BANK_TRANSFER` - Direct bank transfer
- `CASH` - On-arrival cash payment (can be added)

### Payment Statuses
- `PENDING` - Payment awaiting processing
- `PAID` - Payment successfully received
- `FAILED` - Payment processing failed
- `CANCELLED` - Payment cancelled by user
- `REFUNDED` - Payment refunded to customer

### Booking Statuses Related to Payment
- `PENDING_PAYMENT` - Booking reserved, awaiting payment
- `CONFIRMED` - Payment received, booking confirmed
- `CANCELLED` - Booking cancelled

---

## Support & Maintenance

### Regular Tasks
- [ ] Monitor failed payments
- [ ] Process pending bank transfers
- [ ] Send payment reminders
- [ ] Generate payment reports
- [ ] Update payment gateway configurations

### Monthly Review
- [ ] Transaction volume analysis
- [ ] Payment method usage stats
- [ ] Failed payment investigation
- [ ] Customer support issues
- [ ] Security audit logs

---

## Additional Resources

- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Security: https://spring.io/projects/spring-security
- Thymeleaf: https://www.thymeleaf.org/
- Bootstrap 5: https://getbootstrap.com/docs/5.0/
