package com.digital.school.service;

```java
package com.digital.school.service;

import com.digital.school.model.StudentGrade;
import java.util.List;
import java.util.Map;

public interface GradeService {
    List<StudentGrade> findByClasseAndSubject(Long classeId, Long subjectId);
    List<StudentGrade> saveBulk(List<StudentGrade> grades);
    byte[] generateClassReport(Long classeId);
    Map<String, Object> calculateClassStatistics(Long classeId);
    Double calculateStudentAverage(Long studentId, Long subjectId);
    Map<String, Double> calculateClassAverages(Long classeId);
    int calculateStudentRank(Long studentId, Long classeId);
}
```