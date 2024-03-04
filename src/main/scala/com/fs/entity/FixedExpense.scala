package com.fs.entity

import com.fs.entity.Currency.Currency
import com.fs.entity.ExpenseType.ExpenseType



case class FixedExpense(id:String, amount: BigDecimal, expenseType: ExpenseType, description: String, currency: Currency)

