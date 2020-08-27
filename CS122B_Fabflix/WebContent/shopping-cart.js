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

let isCartEmpty = true;



function handleShoppingCartResult(resultData) {
    consolePrint("handleMovieResult: populating handleShoppingCartResult from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let shoppingCartTableBodyElement = jQuery("#shopping_cart_body");

    consolePrint(resultData.length);

    if(resultData.length == 0){
        isCartEmpty = true;
    }
    else{
        isCartEmpty = false;
    }

    // CSS style
    let styleCss = "style='background-color: #F9F9F9; border:0px'";
    let addI = "<svg class=\"bi bi-caret-up-fill\" width=\"1em\" height=\"1em\" viewBox=\"0 0 16 16\" fill=\"currentColor\" xmlns=\"http://www.w3.org/2000/svg\">" +
               "<path d=\"M7.247 4.86l-4.796 5.481c-.566.647-.106 1.659.753 1.659h9.592a1 1 0 00.753-1.659l-4.796-5.48a1 1 0 00-1.506 0z\"/>" +
                "</svg>";
    let decI = "<svg class=\"bi bi-caret-down-fill\" width=\"1em\" height=\"1em\" viewBox=\"0 0 16 16\" fill=\"currentColor\" xmlns=\"http://www.w3.org/2000/svg\">" +
               "<path d=\"M7.247 11.14L2.451 5.658C1.885 5.013 2.345 4 3.204 4h9.592a1 1 0 01.753 1.659l-4.796 5.48a1 1 0 01-1.506 0z\"/>" +
               "</svg>";
    let delI = "<svg class=\"bi bi-dash\" width=\"1em\" height=\"1em\" viewBox=\"0 0 16 16\" fill=\"currentColor\" xmlns=\"http://www.w3.org/2000/svg\">" +
               "<path fill-rule=\"evenodd\" d=\"M3.5 8a.5.5 0 01.5-.5h8a.5.5 0 010 1H4a.5.5 0 01-.5-.5z\" clip-rule=\"evenodd\"/>" +
               "</svg>"

    // Iterate through resultData, no more than 20 entries -> Top 20 rated movies
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["num"] + "&nbsp;&nbsp;" +
            "<button " + styleCss + "onclick=\"changeItem('addItem', '" + resultData[i]["id"] + "')\">" + addI + "</button>" +
            "<button " + styleCss + "onclick=\"changeItem('decreaseItem', '" + resultData[i]["id"] + "')\">" + decI + "</button>" +
            "<button " + styleCss + "onclick=\"changeItem('deleteItem', '" + resultData[i]["id"] + "')\">" + delI + "</button>" +
            "</th>";
        rowHTML += "<th>$" + resultData[i]["price"].toFixed(2) + "</th>";
        rowHTML += "<th>$" + (resultData[i]["price"] * resultData[i]["num"]).toFixed(2) + "</th>";
        rowHTML += "</tr>";

        consolePrint(rowHTML);
        // Append the row created to the table body, which will refresh the page
        shoppingCartTableBodyElement.append(rowHTML);
    }
}


function changeItem(action, movieId){
    let shoppingCartTableBodyElement = document.getElementById("shopping_cart_body");
    shoppingCartTableBodyElement.innerText = "";
    consolePrint("in");
    consolePrint(shoppingCartTableBodyElement.innerText);
    $.ajax("api/shopping-cart", {
        method: "POST",
        data: {"action": action, "movieId": movieId},
        success: resultData => handleShoppingCartResult(resultData)
    });
}


function checkOut(){
    consolePrint(isCartEmpty);
    if(isCartEmpty == false) {
        window.location.href = "payment.html";
    }
    else{
        alert("The cart is empty.");
    }

}


// ##########################################

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shopping-cart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleShoppingCartResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});