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


function handlePriceResult(resultData) {
    consolePrint("handleMovieResult: populating movie table from resultData");
    consolePrint(resultData);
    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let priceLabelElement = document.getElementById("totalPrice");
    priceLabelElement.innerText = "$" + resultData[0]["price"].toFixed(2);
}

let payment_form = $("#payment_form");
payment_form.submit(submitPaymentForm);
function submitPaymentForm(formSubmitEvent){
    consolePrint("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: resultData => handlePaymentResult(resultData)
        }
    );
}


function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    consolePrint("handle login response");
    consolePrint(resultDataJson);
    consolePrint(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        alert("Successfully checkout :D");
        consolePrint("confirmation?");
        window.location.href = "confirmation.html";
    } else {
        // If payment fails, the web page will display
        // error messages on <div> with id "login_error_message"
        consolePrint("show error message");
        consolePrint(resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}


// ########################################################

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/payment",
    success: (resultData) => handlePriceResult(resultData)
});

