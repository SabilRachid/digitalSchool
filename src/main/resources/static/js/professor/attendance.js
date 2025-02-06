$(document).ready(function() {
    let table = $('#groupedAttendanceTable').DataTable({
        "ajax": "/professor/attendance/grouped/data",
        "columns": [
            { "data": 0, "render": function(data) { return data.name; }},
            { "data": 0, "render": function(data) { return data.classroom.name; }},
            { "data": 1 },
            { "data": 2 },
            { "data": 0, "render": function(data, type, row) {
                    return `
                    <a href="/attendance/details?courseId=${data.id}&date=${row[1]}" class="btn btn-info btn-sm">
                        <i class="fa-solid fa-eye"></i> Voir
                    </a>
                    <a href="/attendance/edit?courseId=${data.id}&date=${row[1]}" class="btn btn-warning btn-sm">
                        <i class="fa-solid fa-edit"></i> Modifier
                    </a>
                `;
                }}
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.11.5/i18n/French.json"
        }
    });

    $("#applyFilters").click(function() {
        let classId = $("#classFilter").val();
        let startDate = $("#startDateFilter").val();
        let endDate = $("#endDateFilter").val();
        table.ajax.url(`/professor/attendance/grouped/data?classId=${classId}&startDate=${startDate}&endDate=${endDate}`).load();
    });

    window.attendancePage = {
        openNewAttendanceModal: function() {
            window.location.href = "/attendance/mark";
        }
    };
});
