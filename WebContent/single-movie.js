/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    // let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    // starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
    //     "<p>Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movieTitle"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieYear"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieDirector"] + "</th>";
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
        const stars = resultData[i]["movieStars"].split(",");
        rowHTML += "<th>";
        // + resultData[i]["movieStars"] + "</th>";
        // rowHTML += "</tr>";
        for(let j = 0; j < stars.length; j++){
            if(j != 0){
                stars[j] = stars[j].slice(1);
            }
            rowHTML += // Add a link to single-movie.html with id passed with GET url parameter
                '<a href="single-star.html?id=' + stars[j]+ '">' +
                // display movieTitle for the link text
                stars[j];

            if(j < stars.length - 1){
                rowHTML += ", ";
            }
            rowHTML += '</a>';
        }
        rowHTML += "</th>";
        if(resultData[i]["movieRating"] == "0.0"){
            rowHTML += "<th>" + "N/A" + "</th>";
        } else{
            rowHTML += "<th>" + resultData[i]["movieRating"] + "</th>";
        }
        rowHTML += '<th><button type="button" onclick="addToCart(`' + resultData[i]["movieTitle"] +'`)">Add</button></th>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}
function addToCart(movieTitle){
    console.log(movieTitle);
    var total;
    if(window.sessionStorage.getItem(movieTitle) == null || window.sessionStorage.getItem(movieTitle) == "null"){
        total = 0;
    } else{
        total = parseInt(window.sessionStorage.getItem(movieTitle));
    }
    total += 1;
    window.sessionStorage.setItem(movieTitle, total.toString());
    confirm("Added to Cart!")
    console.log(window.sessionStorage);

}
/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});