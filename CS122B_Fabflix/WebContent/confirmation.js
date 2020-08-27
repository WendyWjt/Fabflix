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


function handleConfirmationResult(resultData) {
    consolePrint("handleMovieResult: populating handleConfirmationResult from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let confirmationBodyElement = jQuery("#confirmation_body");

    // Iterate through resultData, no more than 20 entries -> Top 20 rated movies
    let totalPrice = resultData[resultData.length - 1];
    document.getElementById("confirmationTitle").innerText = "Successfully purchased: (Total Price: $" +
        totalPrice["totalPrice"].toFixed(2) + ")";
    for (let i = 0; i < resultData.length-1; i++) {
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["num"] + "</th>";
        rowHTML += "<th>$" + resultData[i]["price"].toFixed(2) + "</th>";
        rowHTML += "<th>$" + (resultData[i]["price"] * resultData[i]["num"]).toFixed(2) + "</th>";
        rowHTML += "</tr>";

        consolePrint(rowHTML);
        // Append the row created to the table body, which will refresh the page
        confirmationBodyElement.append(rowHTML);
    }
}


function back(){
    window.location.href = "mainpage.html";
}


// #######################################################

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/confirmation", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleConfirmationResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});