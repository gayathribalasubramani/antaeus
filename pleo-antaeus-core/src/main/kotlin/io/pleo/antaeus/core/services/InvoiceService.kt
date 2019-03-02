/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
       return dal.fetchInvoices()
    }

    fun fetch(invoiceStatus: String): List<Invoice> {
        return dal.fetchInvoices(invoiceStatus)
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun pay(invoiceId: Int): Invoice {
        return dal.updateInvoiceStatus(fetch(invoiceId), InvoiceStatus.PAID.toString()) ?: throw InvoiceNotFoundException(invoiceId)
    }
}
