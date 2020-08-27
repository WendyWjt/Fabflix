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

function movieinsert() {
    // Get request URL
    let title = document.getElementById("title").value.toString();
    let director = document.getElementById("director").value.toString();
    let year = document.getElementById("year").value.toString();
    let genre = document.getElementById("genre").value.toString();
    let starname = document.getElementById("starname").value.toString();
    addmovie(title,director,year,genre,starname);
}

function addmovie(title,director,year,genre,starname){
    consolePrint("addmovie begin");
    $.ajax("api/addaction", {
        method: "POST",
        data: {"action": "addmovie", "title": title, "director": director, "year":year, "genre":genre, "starname":starname},
        success: resultData => addmovieAlert(resultData)
    });
}


function addmovieAlert(resultData){
    let resultDataJson = JSON.parse(resultData);
    if(resultDataJson["status"] == "success"){
        alert("Movieid="+resultDataJson["new_movie_id"]+" \n "
            +"Starid="+resultDataJson["new_star_id"]+" \n "
            +"Genreid="+resultDataJson["new_genre_id"]);
    }
    else{
        alert("Duplicate movie is not allowed to add.");
    }

}