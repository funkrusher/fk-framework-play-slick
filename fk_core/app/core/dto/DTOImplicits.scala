package core.dto

import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class DTOImplicits {
  // Timestamp
  implicit val timestampReads: Reads[Timestamp] = {
    implicitly[Reads[Long]].map(epochMilli => {
      val instant = Instant.ofEpochMilli(epochMilli)
      val ldt     = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
      val ts      = Timestamp.valueOf(ldt)
      ts
    })
  }
  implicit val timestampWrites: Writes[Timestamp] = {
    implicitly[Writes[Long]].contramap(ts => {
      val utcLdt = ts.toLocalDateTime.atZone(ZoneId.of("UTC"))
      utcLdt.toInstant.toEpochMilli
    })
  }
  // LocalDate
  implicit val dateReads: Reads[LocalDate] = {
    implicitly[Reads[Long]].map(epochMilli => {
      val ld = Instant.ofEpochMilli(epochMilli).atZone(ZoneId.of("UTC")).toLocalDate
      ld
    })
  }
  implicit val dateWrites: Writes[LocalDate] = {
    implicitly[Writes[Long]].contramap(localDate => {
      val instant      = localDate.atStartOfDay(ZoneId.of("UTC")).toInstant
      val timeInMillis = instant.toEpochMilli
      timeInMillis
    })
  }
}
