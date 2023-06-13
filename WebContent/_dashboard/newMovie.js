let add_movie_form = $("#add-movie-form");

function displayMovieMessage(resultData){
    console.log(resultData);
    console.log(resultData["status"])
    let message = $("#successMovieMessage");
    let rowHTML = "";
    if(resultData["status"] === "success"){
        rowHTML += `<h2>Success! `;
        rowHTML += `movieID:${resultData["movie_id"]}, starID:${resultData["star_id"]}, genreID:${resultData["genre_id"]}</h2>`;
    } else if (resultData["status"] === "error") {
        rowHTML += `<h2>Error: Duplicate movie.</h2>`;
    }
    message.append(rowHTML);
}

function handleAddMovieForm(resultDataString) {
    resultDataString.preventDefault();
    $.ajax(
        "api/newMovie",{
            method: "POST",
            data: add_movie_form.serialize(),
            success: displayMovieMessage
        }
    )
}

add_movie_form.submit(handleAddMovieForm);