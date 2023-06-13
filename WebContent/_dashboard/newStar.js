let add_star_form = $("#add-star-form");

function displayStarMessage(resultData){
    console.log(resultData)
    let messageTag = $("#successStarMessage");
    let rowHTML = `<h3>Success! starID:${resultData['starId']}</h3>`;
    messageTag.append(rowHTML);
}

function handleAddStarForm(resultDataString) {
    resultDataString.preventDefault();
    $.ajax(
        "api/newStar",{
            method: "POST",
            data: add_star_form.serialize(),
            success: displayStarMessage
        }
    )
}

add_star_form.submit(handleAddStarForm);