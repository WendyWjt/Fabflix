// #######################################
// Helper functions

// debug
let allowConsolePrint = 1;
function consolePrint(tar){
    if(allowConsolePrint == 1) {
        console.log(tar);
    }
}


function handleDashboardResult(resultData) {
    consolePrint("handledashboardResult: populatingresultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let dashboardTable = jQuery("#dashboard_table_body");
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]['tablename'] + "</th>";
        rowHTML += "<th>" + resultData[i]['fieldname'] + "</th>";
        rowHTML += "<th>" + resultData[i]['type'] + "</th>";
        rowHTML += "</tr>";
        dashboardTable.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/dashboard", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleDashboardResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});