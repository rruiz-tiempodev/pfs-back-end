package com.fs.entity

import com.fs.entity.Currency.Currency
import com.fs.entity.IncomeExpenseType.IncomeExpenseType




case class FixedExpense(id:String, amount: BigDecimal, expenseType: IncomeExpenseType, description: String, currency: Currency)

