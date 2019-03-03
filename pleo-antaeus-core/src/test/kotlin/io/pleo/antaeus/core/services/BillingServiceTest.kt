package io.pleo.antaeus.core.services

import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.util.DateTimeProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

private val logger = KotlinLogging.logger {}
class BillingServiceTest {
    private val dal = mockk<AntaeusDal> {
        var money = Money(BigDecimal.ONE, Currency.USD)
        var pendingInvoice = Invoice(id = 1, customerId = 1, amount = money, status = InvoiceStatus.PENDING)
        var paidInvoice = Invoice(id = 2, customerId = 1, amount = money, status = InvoiceStatus.PAID)

        var listOfInvoices: MutableList<Invoice> = mutableListOf()
        listOfInvoices.add(pendingInvoice)
        every { fetchInvoice(404) } returns null
        every { fetchInvoices(InvoiceStatus.PENDING) } returns listOfInvoices.filter { it.status.equals(InvoiceStatus.PENDING) }
        every { fetchInvoice(1) } returns pendingInvoice
        every { fetchInvoices(InvoiceStatus.PAID) } returns listOfInvoices.filter { it.status.equals(InvoiceStatus.PAID) }
        every { updateInvoiceStatus(pendingInvoice, InvoiceStatus.PAID) } returns paidInvoice
    }

    private val paymentProvider = mockk<PaymentProvider> {
        val customer = Customer(id = 1, currency = Currency.USD)
        var money = Money(BigDecimal.ONE, Currency.USD)
        var pendingInvoice = Invoice(id = 1, customerId = 1, amount = money, status = InvoiceStatus.PENDING)
        every { charge(pendingInvoice) } returns true
    }

    private val customerService = mockk<CustomerService> {
        val customer = Customer(id = 1, currency = Currency.USD)
        every { fetch(1) } returns customer
    }

    private val invoiceService = InvoiceService(dal = dal)

    private val billingService = BillingService(dal = dal, paymentProvider = paymentProvider, customerService = customerService, invoiceService = invoiceService)

    @Test
    fun `invoice not paid if it is not first day of the month`() {
        val dateTimeProvider = mockk<DateTimeProvider> {
            every { isFirstDayOfMonth(Currency.USD) } returns false
        }
        var updatedInvoices: MutableList<Invoice?> = billingService.payInvoices(InvoiceStatus.PENDING)
        assert(updatedInvoices.count() == 0)
    }

    @Test
    fun `invoice not paid if it is the first day of the month`() {
        val dateTimeProvider = mockk<DateTimeProvider> {
            every { isFirstDayOfMonth(Currency.USD) } returns true
        }
        var updatedInvoices: MutableList<Invoice?> = billingService.payInvoices(InvoiceStatus.PENDING)
        updatedInvoices.forEach{
          assert(it?.status == InvoiceStatus.PAID)
        }
    }
}