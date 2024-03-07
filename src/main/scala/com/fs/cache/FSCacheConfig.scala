package com.fs.cache

import com.google.common.cache.CacheBuilder
import scalacache.Entry
import scalacache.guava.GuavaCache

import scala.concurrent.Future


object FSCacheConfig {
  val underlyingGuavaCache = CacheBuilder.newBuilder()
    .maximumSize(10000L).build[String, Entry[Map[String, BigDecimal]]]
  implicit val scalaCacheGuava = new GuavaCache(underlyingGuavaCache)
}
