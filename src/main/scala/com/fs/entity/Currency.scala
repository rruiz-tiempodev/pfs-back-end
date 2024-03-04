package com.fs.entity

import com.fs.entity.ExpenseType.Value

object Currency extends Enumeration {
  type Currency = Value
  val USD, MXP = Value
}
