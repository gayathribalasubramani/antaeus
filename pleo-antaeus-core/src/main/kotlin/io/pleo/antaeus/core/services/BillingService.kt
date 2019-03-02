package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService,
    private val dal: AntaeusDal
) {
   fun payInvoices(status: String): MutableList<Invoice?>{
        if( status.equals("PENDING") ) {
            var pendingInvoices: List<Invoice> = invoiceService.fetch(InvoiceStatus.PENDING.toString())
            var currentPaidInvoices: MutableList<Invoice?> = ArrayList<Invoice?>()

            pendingInvoices.forEach { 
                invoice -> currentPaidInvoices.add(dal.updateInvoiceStatus(invoice, InvoiceStatus.PAID))
            }
            return currentPaidInvoices
        }
        else 
            return ArrayList<Invoice?>()
   }
}