fetch('api/main') // If genre searching breaks, check this
    .then(response => response.json())
    .then(data => {
        const container = document.getElementById('genre-list');

        data.forEach(item => {
            const movieGenre = item.movieGenreFront;
            const html = '<li onclick="sendGenre(\'' + movieGenre + '\')">' + movieGenre + '</li>';
            container.innerHTML += html;
        });
    })





