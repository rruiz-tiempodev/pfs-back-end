package com.fs.entity

import com.fs.entity.Currency.Currency
import com.fs.entity.IncomeExpenseType.IncomeExpenseType

import java.time.ZonedDateTime


case class Expense(id:String,
  amount: BigDecimal,
  expenseType: IncomeExpenseType,
  description: String,
  currency: Currency,
  date: ZonedDateTime,
  fixedExpenseId: String, applied: Boolean
)
