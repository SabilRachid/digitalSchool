$(document).ready(function() {

    $("#classSelect").change(function() {
        let classId = $(this).val();
        if (classId) {
            $.get("/attendance/students/" + classId, function(data) {
                $("#studentList").empty();
                data.forEach(student => {
                    $("#studentList").append(`
                        <tr>
                            <td>${student.firstName} ${student.lastName}</td>
                            <td>
                                <select name="attendances[${student.id}]" class="statusSelect">
                                    <option value="PRESENT">Présent</option>
                                    <option value="ABSENT">Absent</option>
                                    <option value="RETARD">Retard</option>
                                </select>
                            </td>
                        </tr>
                    `);
                });
            });
        }
    });

    $("#markAllPresent").click(function() {
        $(".statusSelect").val("PRESENT");
    });

});
