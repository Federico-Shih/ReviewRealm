package ar.edu.itba.paw.webapp.controller.helpers;

import ar.edu.itba.paw.models.Paginated;
import org.springframework.web.servlet.ModelAndView;

public class PaginationHelper {
    private static final int MAX_PAGES_PAGINATION = 6;
    private static final int INITIAL_PAGE = 1;

    private PaginationHelper() {

    }
    public static <T>void paginate(ModelAndView mav, Paginated<T> paginated, int maxPages, int initialPage) {
        if(paginated.getTotalPages() > maxPages){
            int offset = (paginated.getPage() <= (maxPages/2))? 0 : paginated.getPage() - (maxPages/2);
            mav.addObject("maxPages", Math.min(maxPages + offset, paginated.getTotalPages()));
            mav.addObject("initialPage", Math.min(initialPage + offset, (paginated.getTotalPages() - maxPages)));
        }else{
            mav.addObject("maxPages", paginated.getTotalPages());
            mav.addObject("initialPage", initialPage);
        }
        mav.addObject("currentPage", paginated.getPage());
    }
    public static <T>void paginate(ModelAndView mav, Paginated<T> paginated) {
        paginate(mav, paginated, MAX_PAGES_PAGINATION, INITIAL_PAGE);
    }

}
