package pe.edu.utp.backend.content.dtos;

import lombok.Data;
import pe.edu.utp.backend.content.model.ContentActivity.ActivityType;

@Data
public class ContentActivityRequestDTO {
    private Long studentId;
    private Long contentId;
    private Long resourceId;
    private ActivityType activityType; // VIEW, DOWNLOAD, COMPLETE, FEEDBACK
    private Integer durationSeconds;
    private Integer progressPercentage;
    private String comment;
    private String deviceInfo;
    private String ipAddress;
}
