package usecase.trends;

import java.time.LocalDate;

/**
 * Input data for the Trends use case.
 * This object is created by the Controller and passed into the Interactor.
 */
public class TrendsInputData {

    private final String username;
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Constructs a TrendsInputData.
     *
     * @param username the username of the logged-in user.
     * @param startDate the start date of the trend request.
     * @param endDate the end date of the trend request.
     */
    public TrendsInputData(String username, LocalDate startDate, LocalDate endDate) {
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the username.
     *
     * @return the username associated with this trend request.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the start date.
     *
     * @return the start date associated with this trend request.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the end date.
     *
     * @return the end date associated with this trend request.
     */
    public LocalDate getEndDate() {
        return endDate;
    }
}
