function printShoppingCart(){
    for(let i = 0; i < window.sessionStorage.length; i++){
        if(!['director','title','year','current','limit','sort','genre','letter', 'name', 'length', 'total'].includes(window.sessionStorage.key(i))){
            let pr = Math.round(100*(Math.random() * (500 - 1) + 1))/100;
            if(!window.sessionStorage.key(i).includes("price_") && window.sessionStorage.getItem("price_" +window.sessionStorage.key(i)) == null){
                window.sessionStorage.setItem("price_"+window.sessionStorage.key(i), pr);
            }
        }
    }
    updateHTML();
}
// delete from cart
function remove(title){
    window.sessionStorage.removeItem(title);
    var tab = document.getElementById("shop_body");
    var tot = document.getElementById("totalPrice");
    tab.innerHTML = "";
    tot.innerHTML = "";
    updateHTML();
}

// increase quantity
function increase(title, index, price, total){
    let quan = window.sessionStorage.getItem(title);
    let newQ = parseInt(quan);
    newQ += 1;
    window.sessionStorage.setItem(title, newQ.toString());
    var tab = document.getElementById("shop_body");
    var tot = document.getElementById("totalPrice");
    tab.innerHTML = "";
    tot.innerHTML = "";
    updateHTML();

    // var oldTable = document.getElementById("shop_body");
    // var row = oldTable.getElementsByTagName("tr");
    // console.log("ROW: " + row[index]);
    // let newTotal = total + ((newQ - quan) * price);
    // let newRow = '';
    // newRow += "<tr>";
    // newRow +=
    //     "<th>" +
    //     // Add a link to single-movie.html with id passed with GET url parameter
    //     '<a href="single-movie.html?id=' + title + '">' +
    //     // display movieTitle for the link text
    //     title + '</a>' +
    //     "</th>";
    // newRow += "<th>" + '<button type="button" onclick="increase(`' + title + "`," + index + "," + price + "," + newTotal  +')">+</button>';
    // newRow += newQ;
    // newRow += '<button type="button" onclick="decrease(`' + title + "`," + index + "," + price + "," + newTotal  +')">-</button></th>';
    // newRow += '<th><button type="button" onclick="remove(`' + title +'`)">Delete</button></th>';
    // newRow += "<th>" + price + "</th>";
    // newRow += "<th>" + newTotal + "</th>";
    // row[index].innerHTML = newRow;
}

// decrease quantity
function decrease(title, index, price, total){
    let quan = window.sessionStorage.getItem(title);
    let newQ = parseInt(quan);
    newQ -= 1;
    window.sessionStorage.setItem(title, newQ.toString());
    if(newQ <= 0){
        window.sessionStorage.removeItem(title);
    }
    var tab = document.getElementById("shop_body");
    var tot = document.getElementById("totalPrice");
    tab.innerHTML = "";
    tot.innerHTML = "";
    updateHTML();

    // var oldTable = document.getElementById("shop_body");
    // var row = oldTable.getElementsByTagName("tr");
    // total = total - (price*quan);
    // let newTotal = total + (newQ * price);
    // let newRow = '';
    // newRow += "<tr>";
    // newRow +=
    //     "<th>" +
    //     // Add a link to single-movie.html with id passed with GET url parameter
    //     '<a href="single-movie.html?id=' + title + '">' +
    //     // display movieTitle for the link text
    //     title + '</a>' +
    //     "</th>";
    // newRow += "<th>" + '<button type="button" onclick="increase(`' + title + "`," + index + "," + price + "," + newTotal  +')">+</button>';
    // newRow += newQ;
    // newRow += '<button type="button" onclick="decrease(`' + title + "`," + index + "," + price + "," + newTotal  +')">-</button></th>';
    // newRow += '<th><button type="button" onclick="remove(`' + title +'`)">Delete</button></th>';
    // newRow += "<th>" + price + "</th>";
    // newRow += "<th>" + newTotal + "</th>";
    // row[index].innerHTML = newRow;
}

function updateHTML(){
    let scelement = jQuery("#shop_body");
    let totPr = jQuery("#totalPrice");
    let index = 0;
    let tot = 0;
    for (let i = 0; i < window.sessionStorage.length; i++) {
        if(!['director','title','year','current','limit','sort','genre','letter', 'name', 'length', 'total'].includes(window.sessionStorage.key(i)) && !window.sessionStorage.key(i).includes("price_")){
            let title = window.sessionStorage.key(i);
            let pr = window.sessionStorage.getItem("price_"+title);
            let quant = window.sessionStorage.getItem(window.sessionStorage.key(i))
            tot += Math.ceil(100*(pr*quant))/100;
            tot = Math.ceil((100*tot))/100;
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML +=
                "<th>" +
                // Add a link to single-movie.html with id passed with GET url parameter
                '<a href="single-movie.html?id=' + title + '">' +
                // display movieTitle for the link text
                title + '</a>' +
                "</th>";
            rowHTML += "<th>" + '<button type="button" onclick="increase(`' + title + "`," + index + "," + pr + "," + tot +')">+</button>';
            rowHTML += quant;
            rowHTML += '<button type="button" onclick="decrease(`' + title + "`," + index + "," + pr + "," + tot +')">-</button></th>';
            rowHTML += '<th><button type="button" onclick="remove(`' + title +'`)">Delete</button></th>';
            rowHTML += "<th>" + pr + "</th>";
            rowHTML += "<th>" + tot + "</th>";
            index += 1;
            scelement.append(rowHTML);
        }
    }
    window.sessionStorage.setItem("total", tot.toString());
    totPr.append(tot.toString());
}

printShoppingCart();