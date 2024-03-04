package com.fs.entity

import com.fs.entity.Currency.Currency
import com.fs.entity.ExpenseType.ExpenseType

import java.time.ZonedDateTime


case class Expense(id:String,
  amount: BigDecimal,
  expenseType: ExpenseType,
  description: String,
  currency: Currency,
  date: ZonedDateTime,
  fixedExpenseId: String, applied: Boolean
)
