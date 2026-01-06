async function getData() {
    const tableBody = document.getElementById('tbody');
    const title_value = document.getElementById('search').value.trim();

    tbody.innerHTML = "";
    const url = `https://api.imdbapi.dev/search/titles?query=${title_value}`;

    if (!title_value) {
        alert("Please Enter Title")
    }

    try {

        const response = await fetch(url);

        if (!response.ok) {
            console.log(`Error: ${response.statusText}`)
        }

        const data = await response.json();
        const titles = data.titles;


        if (!titles || titles.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="loading">No results found</td></tr>`;
            return;
        }



        titles.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
            <td>${item.id || "-"} </td>
            <td>${item.type || "-"} </td>
            <td>${item.primaryTitle || "-"} </td>
            <td>${item.originalTitle || "-"} </td>
            <td>${item.startYear || "-"} </td>
            <td>${item.endYear || "-"} </td>
            <td>${item.rating ? `${item.rating.aggregateRating} (${item.rating.voteCount})` : "-"}</td>
        `;

            tableBody.appendChild(row);

        });


    } catch (error) {
        console.error('Error: ' + error.message);
        tableBody.innerHTML = `<tr><td colspan="8" class="loading"> Error loading data </td></tr>`;

    } finally {
        console.log("Fetch Completed")
    }
} 