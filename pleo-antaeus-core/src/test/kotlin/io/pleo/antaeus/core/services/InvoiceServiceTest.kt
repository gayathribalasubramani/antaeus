package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InvoiceServiceTest {
    private val dal = mockk<AntaeusDal> {
        var money = Money(BigDecimal.ONE, Currency.GBP)
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

    private val invoiceService = InvoiceService(dal = dal)

    @Test
    fun `will throw if invoice is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.fetch(404)
        }
    }

    @Test
    fun `will return invoice based on status`() {
        var invoices: List<Invoice> = invoiceService.fetch(InvoiceStatus.PENDING)
        assert(invoices.count() == 1)
    }

    @Test
    fun `will update invoice based on status`() {
        var invoice: Invoice = invoiceService.updateInvoiceStatus(1, InvoiceStatus.PAID)
        assert(invoice.status == InvoiceStatus.PAID)
    }
}