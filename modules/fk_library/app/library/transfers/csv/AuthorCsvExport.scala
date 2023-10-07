package library.transfers.csv

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import library.dtos.AuthorDTO
import akka.stream.scaladsl.Concat

object AuthorCsvExport {

  def from(source: Source[AuthorDTO, NotUsed]): Source[ByteString, NotUsed] = {
    val csvHeader = Source.single(ByteString(""""First Name","Last Name","Year","BookTitle"""" + "\n"))
    val csvLines: Source[ByteString, NotUsed] = source.map(authorApi => {
      ByteString(s""""${authorApi.first_name.getOrElse("")}","${authorApi.last_name}","${authorApi.year_of_birth
        .getOrElse("")}","${authorApi.books match {
        case Some(books) => books.map(x => x.title).mkString("-")
        case None        => ""
      }}"""".stripMargin + "\n")
    })
    Source.combine(csvHeader, csvLines)(Concat[ByteString])
  }

}
