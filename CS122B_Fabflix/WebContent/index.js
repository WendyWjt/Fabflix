// #######################################
// Helper functions

// debug
let allowConsolePrint = 1;

function consolePrint(tar){
    if(allowConsolePrint == 1) {
        console.log(tar);
    }
}


function initUpper(s){
    return s[0].toUpperCase() + s.substring(1, s.length);
}


function getAllParameter(){
    let url = window.location.href;
    if(url.indexOf("?") == -1){
        return null;
    }
    url = url.split("?")[1];
    url = url.split("&");
    let result = {};
    for(let i = 0; i < url.length; i++){
        let temp = url[i].split("=");
        result[temp[0]] = temp[1];
    }
    return result;
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

// ################################################
// Global variable

let listSortOrder = ["noSort", -1, "noSort", -1];
let pageViewSet = {"offset": 0, "itemNum": 20}
let reachEnd = 0; // 0: false; 1: true

// ################################################
// Task 3: Previous/Next
function changePageViewItem(numItem){
    pageViewSet["itemNum"] = numItem;
    let currentPageNum = Number(document.getElementById("curPage").innerText);
    let offset = (currentPageNum-1) * numItem;
    pageViewSet["offset"] = offset;
    pageViewSet["itemNum"] = numItem;
    consolePrint(pageViewSet);
    sendCurAndMoreParam();
}


function jumpPrevPage(){
    let currentPageNum = Number(document.getElementById("curPage").innerText);

    consolePrint(reachEnd);
    if(reachEnd == 1){
        reachEnd = 0;
        document.getElementById("nextPage").disabled = false;
    }


    if(currentPageNum > 1){
        document.getElementById("prevPage").disabled = false;
        $("#curPage").text(currentPageNum-1);
        if(currentPageNum-1 <= 1){
            document.getElementById("prevPage").disabled = true;
        }
        changePageViewItem(pageViewSet["itemNum"]);
    }
    else{
        document.getElementById("prevPage").disabled = true;
    }
}


function jumpNextPage(){
    document.getElementById("prevPage").disabled = false;
    let currentPageNum = Number(document.getElementById("curPage").innerText);
    $("#curPage").text(currentPageNum+1);
    changePageViewItem(pageViewSet["itemNum"]);
}



// ################################################
// Task 3: Sort

function sortListToObject(){
    let result = {};
    if(listSortOrder[0] != "noSort"){
        result["firstSort"] = listSortOrder[0].toLowerCase();
        result["firstSortOrder"] = handleSortOrder(listSortOrder[1]);
        if(listSortOrder[2] != "noSort"){
            result["secondSort"] = listSortOrder[2].toLowerCase();
            result["secondSortOrder"] = handleSortOrder(listSortOrder[3]);
        }
    }
    consolePrint(result);
    return result;
}


function theOtherSort(item){
    if(item == "Rating"){
        return "Title";
    }
    else{
        return "Rating";
    }
}

function updateList(item, order){
    consolePrint("update: " + item + " " + order.toString());
    consolePrint(listSortOrder);
    if(item != "noSort") {
        if (order == 0) {
            $("#" + item).text(item + " ↓");
        } else {
            $("#" + item).text(item + " ↑");
        }
        if (listSortOrder[2] == "noSort") {
            $("#" + theOtherSort(item)).text(theOtherSort(item));
        }
    }
    // consolePrint(document.getElementById(item).textContent);
    // consolePrint(document.getElementById(theOtherSort(item)).textContent);
}


function sortByItem(item, sortOrder, order){
    listSortOrder[sortOrder*2] = item;
    listSortOrder[sortOrder*2 + 1] = order;
    consolePrint(listSortOrder);
    updateList(item, order);
    sendCurAndMoreParam();
}

function handleSortOrder(i){
    if(i == 0){
        return "desc";
    }
    else{
        return "asc";
    }
}


function handleSortOrderToInt(order){
    if(order == "desc"){
        return 0;
    }
    else{
        return 1;
    }
}



function handleSortResult(resultData){
    let starTableBodyElement = jQuery("#star_table_body");
    starTableBodyElement.text("");
    consolePrint("Clear");
    handleResult(resultData);
}


// ################################################
// Handle page

function handleResult(resultData) {
    consolePrint("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

        // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "<br><button class='butt' id='addToShoppingCart' onclick=\"addToCart(\'" + resultData[i]['movie_id'] + "\')\"> Add to Cart </button>" +
            "</th>";

        // add genres
        rowHTML += "<th>" + resultData[i]['movie_year'] + "</th>";
        rowHTML += "<th>" + resultData[i]['movie_director'] + "</th>";
        rowHTML += "<th><ul>";
        let genreHTML = "";
        for(let j = 1; j<4; j++){
            if(resultData[i]['movie_genres'][j] != undefined) {
                genreHTML += '<li><a href="index.html?genreid=' + resultData[i]['movie_genres'][j]['genreId'] + '">' +
                            resultData[i]['movie_genres'][j]['name'] + "</a>";
            }
        }
        rowHTML += genreHTML
        rowHTML += "</ul></th>";

        // add stars
        rowHTML += "<th><ul>" ;
        for(let j = 1; j < 4; j++){
            if(resultData[i]['movie_stars'][j] != undefined){
                rowHTML += '<li><a href="single-star.html?id=' + resultData[i]['movie_stars'][j]["id"] + '">'
                    + resultData[i]['movie_stars'][j]["name"] + '</a>';
            }
        }

        rowHTML += "</ul></th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }

    consolePrint(resultData.length);
    consolePrint(pageViewSet["itemNum"]);
    consolePrint(resultData.length <= pageViewSet["itemNum"].valueOf());

    if(resultData.length < pageViewSet["itemNum"].valueOf()){
        reachEnd = 1;
        document.getElementById("nextPage").disabled = true;
    }
    else{
        reachEnd = 0;
        document.getElementById("nextPage").disabled = false;
    }

    /*
    if(back == "1") {
        updateList(listSortOrder[0], listSortOrder[1]);
        updateList(listSortOrder[2], listSortOrder[3]);
    }
    */
}



// #############################
// add to shopping cart
function addToCart(movieId){
    $.ajax("api/movieList", {
        method: "POST",
        data: {"action": "addToCart", "movieId": movieId},
        success: resultData => addToCartAlert(resultData)
    });
}


function addToCartAlert(resultData){
    let resultDataJson = JSON.parse(resultData);
    if(resultDataJson["status"] == "success"){
        alert("Successfully add to cart.");
    }
    else{
        alert("Fail to add to cart.");
    }

}




// #############################
// jump
function handleBack(hasParameter){
    if(back == "1") {
        let firstSort_url = getParameterByName("firstSort");
        let firSortOrder_url = getParameterByName("firstSortOrder");
        let secondSort_url = getParameterByName("secondSort");
        let secondSortOrder_url = getParameterByName("secondSortOrder");
        let offset_url = getParameterByName("offset");
        let itemNum = getParameterByName("itemNum");
        let result = "";
        if(hasParameter == 0) {
            result = "?back=1";
        }
        else{
            result = "&back=1";
        }
        if(firSortOrder_url != null) {
            result += "&firstSort=" + firstSort_url;
            result += "&firstSortOrder=" + firSortOrder_url;
            listSortOrder[0] = firstSort_url;
            listSortOrder[1] = handleSortOrderToInt(firSortOrder_url);
            if(secondSortOrder_url != null) {
                result += "&secondSort=" + secondSort_url;
                result += "&secondSortOrder=" + secondSortOrder_url;
                listSortOrder[2] = secondSort_url;
                listSortOrder[3] = handleSortOrderToInt(secondSortOrder_url);
            }
        }
        if(offset_url != null) {
            result += "&offset=" + offset_url;
            result += "&itemNum=" + itemNum;
            pageViewSet["itemNum"] = Number(itemNum);
        }
        consolePrint("backUrl: " + result);
        updateList(initUpper(listSortOrder[0]), listSortOrder[1]);
        updateList(initUpper(listSortOrder[2]), listSortOrder[3]);
        //document.getElementById(initUpper(listSortOrder[0])).innerHTML = initUpper(listSortOrder[0]) + "↓";
        //document.getElementById(listSortOrder[2][0].toUpperCase() + listSortOrder[2].substring(1, listSortOrder[2].length)).innerHTML = listSortOrder[2] + "↓";
        return result;
    }
    else{
        return "";
    }

}


function sendCurAndMoreParam(){
    let parameterSet = getAllParameter();
    consolePrint(parameterSet);
    if(parameterSet != null) {
        if (parameterSet["offset"] != null) {
            parameterSet["offset"] = pageViewSet["offset"];
            parameterSet["itemNum"] = pageViewSet["itemNum"];
        } else {
            parameterSet = Object.assign(parameterSet, pageViewSet);
        }
        let result = sortListToObject();
        consolePrint(result);
        consolePrint(parameterSet["firstSort"]);
        if (parameterSet["firstSort"] != null) {
            let firstSort = result["firstSort"];
            consolePrint(firstSort);
            if (firstSort != null) {
                parameterSet["firstSort"] = firstSort;
                parameterSet["firstSortOrder"] = result["firstSortOrder"];
                consolePrint("paramterset after changing firstSort: " + parameterSet);
                let secondSort = result["secondSort"];
                if (secondSort != null) {
                    parameterSet["secondSort"] = secondSort;
                    parameterSet["secondSortOrder"] = result["secondSortOrder"];
                    consolePrint("paramterset after changing secondSort: " + parameterSet);
                }
            }
        } else {
            consolePrint("combine");
            parameterSet = Object.assign(parameterSet, sortListToObject());
        }
    }
    else{
        parameterSet = Object.assign(sortListToObject(), pageViewSet);
    }
    consolePrint(parameterSet);

    // update page
    if (parameterSet != {}) {
        $.ajax(
            "api/movieList", {
                method: "POST",
                data: parameterSet,
                success: (resultData) => handleSortResult(resultData)
            }
        );
    }
}



/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
consolePrint("javascript is here")
let genreid = getParameterByName('genreid');
let startwith = getParameterByName('startwith');
let search = getParameterByName('search');
let back = getParameterByName('back');
let offset = getParameterByName('offset');
let itemNum = getParameterByName('itemNum');
let targetUrl = "";
let num = 0;
// Update page number
if(offset!=null){
    num = Number(offset)/Number(itemNum);
    if(num + 1 > 1){
        document.getElementById('prevPage').disabled = false;
    }
}
document.getElementById('curPage').innerHTML = (num+1).toString();


if(search!=null){
    let url = "api/movieList?search=true"
    let title = getParameterByName('title');
    if(title!=null){
        url += "&title=" + title;
    }
    let year = getParameterByName('year');
    if(year!=null){
        url += "&year=" + year;
    }
    let director = getParameterByName('director');
    if(director!=null){
        url += "&director=" + director;
    }
    let starname = getParameterByName('starname');
    if(starname!=null){
        url += "&starname=" + starname;
    }
    let fullTextSearchTitle = getParameterByName("fullTextSearchTitle");
    if(fullTextSearchTitle != null){
        url += "&fullTextSearchTitle=" + fullTextSearchTitle;
    }
    url += handleBack(1);
    targetUrl = url;
}
else if(genreid!=null){
    targetUrl = "api/movieList?genreid="+genreid+handleBack(1);
}
else if(startwith!=null){
    targetUrl = "api/movieList?startwith="+startwith+handleBack(1);
}
else{
    consolePrint("everything is null");
    targetUrl = "api/movieList" + handleBack(0);
}

consolePrint(targetUrl);
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: targetUrl,
    success: (resultData) => handleResult(resultData)
});