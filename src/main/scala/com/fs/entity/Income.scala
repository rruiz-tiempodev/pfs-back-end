package com.fs.entity

import com.fs.entity.Currency.Currency
import com.fs.entity.IncomeExpenseType.IncomeExpenseType

case class Income(id:String, amount: BigDecimal, incomeType: IncomeExpenseType, description: String, currency: Currency)
