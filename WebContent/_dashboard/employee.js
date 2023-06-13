function handleData(resultData) {
    let data_table = $("#data-table");
    let tableGroups = {};

    resultData.forEach(data => {
        if (data.table_name in tableGroups) {
            tableGroups[data.table_name].push(data);
        } else {
            tableGroups[data.table_name] = [data];
        }
    });

    let tableHTML = "";
    for (let tableName in tableGroups) {
        let columns = tableGroups[tableName];
        let columnHTML = columns
            .map(column => `<tr><td>${column.column_name}</td><td>${column.data_type}</td></tr>`)
            .join("");

        tableHTML += `
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th colspan="2">${tableName}</th>
                    </tr>
                    <tr>
                        <th>Column Name</th>
                        <th>Data Type</th>
                    </tr>
                </thead>
                <tbody>
                    ${columnHTML}
                </tbody>
            </table>
        </div>
        `;
    }

    data_table.append(tableHTML);
}

    $.ajax({
        url: "api/employee",
        method: "GET",
        success: handleData
    });