/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */
let current = 0; // current page
let limit = 20;
let max = 0;
var movieData;
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
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    movieData = resultData;
    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    var oldTable = document.getElementById("movie_table_body");
    var row = oldTable.getElementsByTagName("tr");
    let len = row.length;

    let empty = 0;
    if(len == 0){
        empty = 1;
    }
    limit = window.sessionStorage.getItem("limit");
    if(limit == "null" || limit == null) {
        limit = 20;
    } else {
        limit = parseInt(limit);
    }
    current = window.sessionStorage.getItem("current");
    if(current == "null" || current == null){
        current = 0;
    } else{
        current = parseInt(current);
    }
    max = Math.floor(resultData.length/limit);
    // how many rows in our table
    var count = limit + (limit*current);
    if(current == max && resultData.length % limit != 0){
        count = resultData.length - (max*limit);
    }

    // delete the rest of the rows if needed
    if(len > count || len > limit){
        for(let p = len - 1; p > count - 1; p--){
            oldTable.deleteRow(p);
        }
    }
    let stopping = count;
    if(count < limit){
        stopping = (limit*current) + count;
    }
    // starting point in table to loop
    let i = limit*current;
    let flag = 0;
    // Iterate through resultData, no more than 20 entries
    for (i; i < stopping; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]["movieTitle"] + '">' +
            // display movieTitle for the link text
            resultData[i]["movieTitle"] + '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movieDirector"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieYear"] + "</th>";
        rowHTML += "</th>";
        if(resultData[i]["movieRating"] == "0.0"){
            rowHTML += "<th>" + "N/A" + "</th>";
        } else{
            rowHTML += "<th>" + resultData[i]["movieRating"] + "</th>";
        }
        const genres = resultData[i]["movieGenre"].split(", ").sort();
        rowHTML += "<th>";
        for(let k = 0; k < genres.length; k++){
            rowHTML += // Add a link to single-movie.html with id passed with GET url parameter
                '<a href="index.html?genre=' + genres[k] + '">' +
                // display movieTitle for the link text
                genres[k];
            if(k < genres.length - 1){
                rowHTML += ", ";
            }
            rowHTML += '</a>';
        }
        rowHTML += "</th>";
        const stars = resultData[i]["movieStars"].split(", ").sort();
        rowHTML += "<th>";
        for(let j = 0; j < stars.length; j++){
            rowHTML += // Add a link to single-movie.html with id passed with GET url parameter
                '<a href="single-star.html?id=' + stars[j]+ '">' +
                // display movieTitle for the link text
                stars[j];
            if(j < stars.length - 1){
                rowHTML += ", ";
            }
            rowHTML += '</a>';
        }
        rowHTML += '<th><button type="button" onclick="addToCart(`' + resultData[i]["movieTitle"] +'`)">Add</button></th>';
        rowHTML += "</th>" + "</tr>";

        // Append the row created to the table body, which will refresh the page
        if(empty == 1 || flag > (len-1)){
            movieTableBodyElement.append(rowHTML);
        } else{
            row[flag].innerHTML = rowHTML;
        }
        flag++;

    }
}

function generalReset(){
    window.sessionStorage.setItem("current", "0");
    window.sessionStorage.setItem("limit", "20");
    window.sessionStorage.setItem("sort", "null");
    window.sessionStorage.setItem("genre", "null");
    window.sessionStorage.setItem("letter", "null");

}
function addToCart(movieTitle){
    var total;
    if(window.sessionStorage.getItem(movieTitle) == null || window.sessionStorage.getItem(movieTitle) == "null"){
        total = 0;
    } else{
        total = parseInt(window.sessionStorage.getItem(movieTitle));
    }
    total += 1;
    window.sessionStorage.setItem(movieTitle, total.toString());
    confirm("Added to Cart!")

}
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Get id from URL
// let sort = getParameterByName('sort')
var genres;
genres = getParameterByName("genre");
if(window.sessionStorage.getItem("genre") == null || window.sessionStorage.getItem("genre") == "null"){
    window.sessionStorage.setItem("genre", genres);
    if(genres != null){
        window.sessionStorage.setItem("current", "0");
        window.sessionStorage.setItem("limit", "20");
        window.sessionStorage.setItem("sort", "null");
        window.sessionStorage.setItem("letter", "null");
    }
} else{
    if(genres!= null && genres != window.sessionStorage.getItem("genre")){
        window.sessionStorage.setItem("current", "0");
        window.sessionStorage.setItem("limit", "20");
        window.sessionStorage.setItem("sort", "null");
        window.sessionStorage.setItem("title", "null");
        window.sessionStorage.setItem("director", "null");
        window.sessionStorage.setItem("year", "null");
        window.sessionStorage.setItem("name", "null");
        window.sessionStorage.setItem("letter", "null");
        window.sessionStorage.setItem("genre", genres);

    }
    genres = window.sessionStorage.getItem("genre");
}
var title, director, year, star, letter
letter = getParameterByName("letter");
title = getParameterByName("title");
director = getParameterByName("director");
year = getParameterByName("year");
name = getParameterByName("name");
if(title == ""){
    title = null;
}
if(director == ""){
    director = null;
}
if(year == ""){
    year = null;
}
if(name == ""){
    name = null;
}
var titleFlag = false;
var dirFlag = false;
var yearFlag = false;
var nameFlag = false;

if((window.sessionStorage.getItem("letter") == null || window.sessionStorage.getItem("letter") == "null")){
    window.sessionStorage.setItem("letter", letter);
    if(letter != null){
        window.sessionStorage.setItem("current", "0");
        window.sessionStorage.setItem("limit", "20");
        window.sessionStorage.setItem("sort", "null");
        window.sessionStorage.setItem("title", "null");
        window.sessionStorage.setItem("director", "null");
        window.sessionStorage.setItem("year", "null");
        window.sessionStorage.setItem("name", "null");
    }
} else{
    letter = window.sessionStorage.getItem("letter");
}

if((window.sessionStorage.getItem("title") == null || window.sessionStorage.getItem("title") == "null")){
    window.sessionStorage.setItem("title", title);
    if(title != null){
        titleFlag = true;
        generalReset();
    }
} else{
    if(title != null && title != window.sessionStorage.getItem("title")){
        titleFlag = true;
        generalReset();
        window.sessionStorage.setItem("title", title);
    }
    title = window.sessionStorage.getItem("title");
}
if((window.sessionStorage.getItem("director") == null || window.sessionStorage.getItem("director") == "null")){
    window.sessionStorage.setItem("director", director);
    if(director != null){
        dirFlag = true;
        generalReset();
    }
} else{
    if(director != null && director != window.sessionStorage.getItem("director")){
        dirFlag = true;
        generalReset();
        window.sessionStorage.setItem("director", director);
    }
    director = window.sessionStorage.getItem("director");
}
if((window.sessionStorage.getItem("year") == null || window.sessionStorage.getItem("year") == "null")){
    window.sessionStorage.setItem("year", year);
    if(year != null){
        yearFlag = true;
        generalReset();
    }
} else{
    if(year != null && year != window.sessionStorage.getItem("year")){
        yearFlag = true;
        generalReset();
        window.sessionStorage.setItem("year", year);
    }
    year = window.sessionStorage.getItem("year");
}

if((window.sessionStorage.getItem("name") == null || window.sessionStorage.getItem("name") == "null")){
    window.sessionStorage.setItem("name", name);
    if(name != "null"){
        nameFlag = true;
        generalReset();
    }
} else{
    if(name != null && name != window.sessionStorage.getItem("name")){
        nameFlag = true;
        generalReset();
        window.sessionStorage.setItem("name", name);
    }
    name = window.sessionStorage.getItem("name");
}
if(titleFlag || nameFlag || yearFlag || dirFlag){
    if(!titleFlag){
        window.sessionStorage.setItem("title", null);
    }
    if(!nameFlag){
        window.sessionStorage.setItem("name", null);
    }if(!yearFlag){
        window.sessionStorage.setItem("year", null);
    }if(!dirFlag){
        window.sessionStorage.setItem("director", null);
    }
}
pageSave(genres, true, false, false);




// }
