// #######################################
// Helper functions

// debug
let allowConsolePrint = 1;
function consolePrint(tar){
    if(allowConsolePrint == 1) {
        console.log(tar);
    }
}

// #######################################

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated. Query: " + query);

    // TODO: if you want to check past query results first, you can do it here
    let queryStorage = sessionStorage.getItem("queryStorage");
    queryStorage = JSON.parse(queryStorage);
    let hasQuery = false;
    let result = undefined;

    if(queryStorage == null || queryStorage == undefined){
        consolePrint("queryStorage is Null")
        queryStorage = {};
        sessionStorage.setItem("queryStorage", JSON.stringify(queryStorage));
    }
    else{
        consolePrint("queryStorage is not null")
        result = queryStorage[query];
        if(result != null){
            hasQuery = true;
        }
    }

    if( !hasQuery ) {
        // sending the HTTP GET request to the Java Servlet endpoint title-suggestion
        // with the query data
        console.log("sending AJAX request to backend Java Servlet");
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "movie-suggestion?query=" + escape(query),
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback);
            },
            "error": function (errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }
    else{
        console.log("Use sessionStorage.");
        console.log("The cached list:");
        console.log(queryStorage);
        handleLookupAjaxSuccess(result, query, doneCallback);
    }
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    let curStorage = sessionStorage.getItem("queryStorage");
    curStorage = JSON.parse(curStorage);
    curStorage[query] = data;
    sessionStorage.setItem("queryStorage", JSON.stringify(curStorage));
    consolePrint("set after get");

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"]);
    this.window.location.href = "single-movie.html?id=" + suggestion["data"]["movieId"] + "&back=2";
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3
});
/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    let url = "index.html?search=true&fullTextSearchTitle=" + query.replace(" ", "%20");
    consolePrint(url);
    this.window.location.href = url;
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})


// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button
// #######################################

function handleGenreResult(resultData) {
    consolePrint("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let genreTable = jQuery("#browserGenre");
    let titleTable = jQuery("#browserTitle")
    let none = "none";
    for (let j = 0; j < 10; j++){
        let rowHTML2 = "";
        rowHTML2 += '<li class="titleLinkWrapper"><a class="link" href="index.html?startwith='+j+'">';
        rowHTML2 += "<span>"+j+"</span>"+ '</a></li>';
        titleTable.append(rowHTML2);
    }
    for (let j = 0; j < 26; j++){
        let rowHTML2 = "";
        rowHTML2 += '<li class="titleLinkWrapper"><a class="link" href="index.html?startwith='+ String.fromCharCode(97+j)+'">';
        rowHTML2 += "<span>"+String.fromCharCode(65+j)+"</span>"+ '</a></li>';
        titleTable.append(rowHTML2);
    }
    let rowHTML2 = "";
    rowHTML2 += '<li class="titleLinkWrapper"><a class="link" href="index.html?startwith='+ none +'">';
    rowHTML2 += "<span>*</span>"+ '</a></li>';
    titleTable.append(rowHTML2);
    // Iterate through resultData, no more than 20 entries -> Top 20 rated movies
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        if(resultData[i]['genre_id'] != undefined){
            rowHTML += '<li class="genreLinkWrapper"><a class="link" href="index.html?genreid='+resultData[i]['genre_id']+'">';
            rowHTML += "<span>"+resultData[i]['genre_name']+"</span>"+ '</a></li>';
        }
        // Append the row created to the table body, which will refresh the page
        genreTable.append(rowHTML);
    }
}


// #####################################################

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/mainpage", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});