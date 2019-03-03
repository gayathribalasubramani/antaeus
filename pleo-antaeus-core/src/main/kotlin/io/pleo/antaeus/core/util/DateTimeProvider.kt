package io.pleo.antaeus.core.util

import java.time.LocalDateTime

// need to check if timezones can be accomodated

class DateTimeProvider {

  fun isFirstDayOfMonth(): Boolean {
    return LocalDateTime.now().dayOfMonth == 1
  }

}