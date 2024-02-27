package com.fs.entity

import java.time.ZonedDateTime

case class Expense(
  id: String,
  amount: BigDecimal,
  date: ZonedDateTime,
  budgetId: String
)
