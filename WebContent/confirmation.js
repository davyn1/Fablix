async function printShoppingCart() {
    let scelement = jQuery("#confirmation_body");
    let totalPrice = jQuery("#totalPrice");
    let index = 0;
    let tot = 0;
    for (let i = 0; i < window.sessionStorage.length; i++) {
        if (!['director', 'title', 'year', 'current', 'limit', 'sort', 'genre', 'letter', 'name', 'length', 'total'].includes(window.sessionStorage.key(i)) &&!window.sessionStorage.key(i).includes("price_")) {
            let movieTitle = window.sessionStorage.key(i);
            let pr = window.sessionStorage.getItem("price_"+movieTitle);
            let quant = window.sessionStorage.getItem(window.sessionStorage.key(i))
            let saleId = await getSaleId(movieTitle);
            tot += Math.ceil(100 * (pr * quant)) / 100;
            tot = Math.ceil((100 * tot)) / 100;
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" + saleId + "</th>";
            rowHTML +=
                "<th>" +
                // Add a link to single-movie.html with id passed with GET url parameter
                '<a href="single-movie.html?id=' + movieTitle + '">' +
                // display movieTitle for the link text
                movieTitle + '</a>' +
                "</th>";
            window.sessionStorage.removeItem(movieTitle);
            rowHTML += "<th>" + quant + "</th>";
            rowHTML += "<th>" + pr + "</th>";
            rowHTML += "<th>" + tot + "</th>";
            scelement.append(rowHTML);
        }
    }
    window.sessionStorage.setItem("total", tot.toString());
    totalPrice.append(tot.toString());
}

function getSaleId(movieTitle, start) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: "api/confirmation",
            type: "POST",
            data: { "movieTitle": movieTitle},
            dataType: "json",
            success: function (data) {
                // process data returned from the server
                let saleId = data.saleId;
                resolve(saleId);
            },
            error: function (error) {
                reject(error);
            }
        });
    });
}

printShoppingCart();