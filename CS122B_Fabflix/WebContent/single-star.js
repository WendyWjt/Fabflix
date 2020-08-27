// #######################################
// Helper functions

// debug
let allowConsolePrint = 1;

function consolePrint(tar){
    if(allowConsolePrint == 1) {
        console.log(tar);
    }
}


function handleReturnUrl(lastParam){
    let url = "index.html"
    consolePrint(lastParam);
    consolePrint(Object.keys(lastParam).length);
    if(Object.keys(lastParam).length <= 0){
        return url;
    }
    else{
        url += "?"
        consolePrint(lastParam);
        for(let item in lastParam){
            if(lastParam[item] != null) {
                url += item + "=" + lastParam[item] + "&";
            }
        }
        url += "back=1";
        consolePrint(url);
        return url;
    }
}


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


function reloadPage() {
    if(location.href.indexOf('#reloaded')==-1){
        location.href=location.href+"#reloaded";
        location.reload();
    }
}



// ######################################




/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {

    consolePrint("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    let lastParam = resultData[resultData.length-1];
    consolePrint(lastParam);
    let url = handleReturnUrl(lastParam);
    document.getElementById("returnPrevMain").href = url;
    consolePrint("Set url: " + url);

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    consolePrint("handleResult: populating movie table from resultData-----");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    consolePrint(resultData.length);

    for (let i = 0; i < resultData.length-1; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th><a href='single-movie.html?id=" + resultData[i]["movie_id"] + "'>" + resultData[i]["movie_title"] + "</a></th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    window.onload = reloadPage();
}



// ##################################

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});