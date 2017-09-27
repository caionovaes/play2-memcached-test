package filters;

import play.filters.gzip.GzipFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;

/**
 * Created by marcio on 10/08/15.
 */
public class Filters extends DefaultHttpFilters {
    @Inject
    public Filters(GzipFilter gzipFilter, ETagFilter eTagFilter) {
        super(gzipFilter, eTagFilter);
    }
}
