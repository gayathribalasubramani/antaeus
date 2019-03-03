package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.core.util.DateTimeProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService,
    private val customerService: CustomerService,
    private val dal: AntaeusDal
) {
   fun payInvoices(status: InvoiceStatus): MutableList<Invoice?>{
        if( status == InvoiceStatus.PENDING ) {
            var pendingInvoices: List<Invoice> = invoiceService.fetch(InvoiceStatus.PENDING)
            return charge(pendingInvoices)
        }
        else if( status == InvoiceStatus.FAILED) {
            var failedInvoices: List<Invoice> = invoiceService.fetch(InvoiceStatus.FAILED)
            return charge(failedInvoices)
        }
        else
            return ArrayList<Invoice?>()
   }

   fun charge(invoices: List<Invoice>): MutableList<Invoice?>{
        val dateTimeProvider = DateTimeProvider()
        var currentPaidInvoices: MutableList<Invoice?> = ArrayList<Invoice?>()
        invoices.forEach {
            try {
                val currency = customerService.fetch(it.customerId).currency;
                if( dateTimeProvider.isFirstDayOfMonth(currency) ) {
                    val isCustomerCharged = paymentProvider.charge(it)
                    if(isCustomerCharged) {
                        currentPaidInvoices.add(dal.updateInvoiceStatus(it, InvoiceStatus.PAID))
                    }
                    else {
                        // can be currency mismatch or customer not found or network error
                        currentPaidInvoices.add(dal.updateInvoiceStatus(it, InvoiceStatus.FAILED))
                    }
                }
            } catch (e: CustomerNotFoundException) {
                logger.error(e) { "CustomerNotFoundException" }
            } catch (e: CurrencyMismatchException) {
                logger.error(e) { "CurrencyMismatchException" }
            } catch (e: NetworkException) {
                logger.error(e) { "NetworkException" }
            }
        }
        return currentPaidInvoices
   }
}