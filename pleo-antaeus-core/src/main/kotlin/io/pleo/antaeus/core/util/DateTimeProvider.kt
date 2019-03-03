package io.pleo.antaeus.core.util

import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import io.pleo.antaeus.models.Currency

class DateTimeProvider {

  fun isFirstDayOfMonth(currency: Currency): Boolean {
    lateinit var zoneId : ZoneId
    if(currency == Currency.USD) {
      //having country in Customer will provide better estimation current time in different zones
      zoneId = ZoneId.of("US/Central")
    }
    else if(currency == Currency.EUR) {
      //having country in Customer will provide better estimation current time in different zones
      zoneId = ZoneId.of("Europe/Kiev")
    }
    else if(currency == Currency.DKK) {
      zoneId = ZoneId.of("Europe/Copenhagen")
    }
    else if(currency == Currency.SEK) {
      zoneId = ZoneId.of("Europe/Stockholm")
    }
    else if(currency == Currency.GBP) {
      zoneId = ZoneId.of("GB")
    }
    else {
      return LocalDateTime.now().dayOfMonth == 1
    }
    val zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), zoneId)
    return zonedDateTime.dayOfMonth == 1
  }
}