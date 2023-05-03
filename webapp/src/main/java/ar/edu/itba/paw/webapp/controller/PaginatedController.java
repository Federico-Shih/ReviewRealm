package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Paginated;
import org.springframework.web.servlet.ModelAndView;

public class PaginatedController {
    private int MAX_PAGES_PAGINATION = 6;
    private int INITIAL_PAGE = 1;

    PaginatedController() {

    }

    PaginatedController(int max, int initial) {
        this.MAX_PAGES_PAGINATION = max;
        this.INITIAL_PAGE = initial;
    }
    <T>void paginate(ModelAndView mav, Paginated<T> paginated) {
        if(paginated.getTotalPages() > MAX_PAGES_PAGINATION){
            int offset = (paginated.getPage() <= (MAX_PAGES_PAGINATION/2))? 0 : paginated.getPage() - (MAX_PAGES_PAGINATION/2);
            mav.addObject("maxPages", Math.min(MAX_PAGES_PAGINATION + offset, paginated.getTotalPages()));
            mav.addObject("initialPage", Math.min(INITIAL_PAGE + offset, (paginated.getTotalPages() - MAX_PAGES_PAGINATION)));
        }else{
            mav.addObject("maxPages", paginated.getTotalPages());
            mav.addObject("initialPage", INITIAL_PAGE);
        }
        mav.addObject("currentPage", paginated.getPage());
    }
}
