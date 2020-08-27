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
// Search Text
function searchText() {
    // Get request URL
    let title = document.getElementById("title").value.toString();
    let year = document.getElementById("year").value.toString();
    let director = document.getElementById("director").value.toString();
    let starname = document.getElementById("starname").value.toString();
    let name = new Array('title','year','director','starname');
    let array = new Array(title,year,director,starname);
    let url = "index.html";
    let anyElement = 0;
    for(let i = 0; i < name.length; i++){
        if(array[i]!=null&&array[i].length!=0){
            if(anyElement==0){
                anyElement++;
                url += "?search=true";
            }
            url += '&' + name[i] + '=' + array[i];
        }
    }
    // let request = new XMLHttpRequest();
    // request.open("GET",url);
    // request.send(null);
    window.location.href=url;
}