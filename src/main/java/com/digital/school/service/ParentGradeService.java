package com.digital.school.service;

```java
package com.digital.school.service;

import com.digital.school.model.User;
import java.util.List;
import java.util.Map;

public interface ParentGradeService {
    List<Map<String, Object>> getChildrenGrades(User parent);
    Map<String, Object> getDetailedChildGrades(Long childId);
    byte[] generateChildReport(Long childId);
    Map<String, Object> getChildGradeStats(Long childId);
}
```