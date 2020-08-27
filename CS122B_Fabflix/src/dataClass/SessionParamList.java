package dataClass;

import funcScripts.HelperFunc;

import javax.servlet.http.HttpServletRequest;

public class SessionParamList {
    public String search;
    public String genre;
    public String startwith;

    public String back;
    public String firstSort;
    public String firstSortOrder;
    public String secondSort;
    public String secondSortOrder;
    public String offset;
    public String itemNum;
    public String starname;
    public String title;
    public String year;
    public String director;

    public String fullTextSearchTitle;

    public SessionParamList(HttpServletRequest request){
        search = request.getParameter("search");
        genre =  request.getParameter("genreid");
        startwith = request.getParameter("startwith");

        back = request.getParameter("back");
        firstSort = request.getParameter("firstSort");
        firstSortOrder = request.getParameter("firstSortOrder");
        HelperFunc.printToConsole("firstOrder: " + firstSort);
        HelperFunc.printToConsole("firstSortOrder: " + firstSortOrder);
        secondSort = request.getParameter("secondSort");
        secondSortOrder = request.getParameter("secondSortOrder");
        offset = request.getParameter("offset");
        if(offset!=null&&offset.length()>=1){
            System.out.println("offset: " + offset);
        }
        itemNum = request.getParameter("itemNum");

        starname = getRidOfSearch(request.getParameter("starname"));
        title = getRidOfSearch(request.getParameter("title"));
        year = request.getParameter("year");
        director = getRidOfSearch(request.getParameter("director"));

        fullTextSearchTitle = getRidOfSearch(request.getParameter("fullTextSearchTitle"));
    }


    private String getRidOfSearch(String tar){
        if(tar != null){
            return tar.replace("%20", " ");
        }
        else{
            return tar;
        }
    }

    private String returnNullString(Object tar){
        if(tar == null){
            return "null(value)";
        }
        else{
            return tar.toString();
        }
    }


    public String toString(){
        return "search=" + returnNullString(search) + " | genre=" + returnNullString(genre) +
                " \n| startwith=" + returnNullString(startwith) + " | back=" + returnNullString(back) +
                " \n| firstSort=" + returnNullString(firstSort) + " | firstSortOrder=" + returnNullString(firstSortOrder) +
                " \n| back=" + returnNullString(back) + " | back=" + returnNullString(back) +
                " \n| secondSort=" + returnNullString(secondSort) + " | secondSortOrder=" + returnNullString(secondSortOrder) +
                " \n| secondSortOrder=" + returnNullString(secondSortOrder) + " | secondSortOrder=" + returnNullString(secondSortOrder) +
                " \n| offset=" + returnNullString(offset) + " | itemNum=" + returnNullString(itemNum) +
                " \n| starname=" + returnNullString(starname) + " | title=" + returnNullString(title) +
                " \n| year=" + returnNullString(year) + " | director=" + returnNullString(director) +
                " \n| fullTextSearchTitle=" + returnNullString(fullTextSearchTitle);
    }
}
