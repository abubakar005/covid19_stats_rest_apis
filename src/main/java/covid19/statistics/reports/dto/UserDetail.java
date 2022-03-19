package covid19.statistics.reports.dto;

import java.time.LocalDateTime;

/**
 * This class has details of user
 */

public class UserDetail {

    private String userName;
    private String jwtToken;
    private LocalDateTime jwtCreationTime;
    private LocalDateTime dateUpdate;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public LocalDateTime getJwtCreationTime() {
        return jwtCreationTime;
    }

    public void setJwtCreationTime(LocalDateTime jwtCreationTime) {
        this.jwtCreationTime = jwtCreationTime;
    }

    public LocalDateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(LocalDateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }
}
