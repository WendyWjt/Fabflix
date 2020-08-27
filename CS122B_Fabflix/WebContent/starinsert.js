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

function starInsert() {
    // Get request URL
    let starname = document.getElementById("starname").value.toString();
    let birthday = document.getElementById("birthyear").value.toString();
    addstar(starname,birthday);
}

function addstar(starname,birthyear){
    consolePrint("addstar begin");
    $.ajax("api/addaction", {
        method: "POST",
        data: {"action": "addstar", "starname": starname, "birthyear": birthyear},
        success: resultData => addstarAlert(resultData)
    });
}


function addstarAlert(resultData){
    let resultDataJson = JSON.parse(resultData);
    if(resultDataJson["status"] == "success"){
        let id = resultDataJson["id"];
        alert("add a star with id="+id);
    }
    else{
        alert("Fail to add to cart.");
    }

}