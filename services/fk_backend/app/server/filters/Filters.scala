package server.filters

import play.api.http.{DefaultHttpFilters, EnabledFilters}
import play.filters.gzip.GzipFilter

import javax.inject.Inject

class Filters @Inject() (
    defaultFilters: EnabledFilters,
    gzip: GzipFilter,
    log: LoggingFilter,
) extends DefaultHttpFilters(defaultFilters.filters :+ gzip :+ log: _*)
