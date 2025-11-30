package usecase.portfolio;

/**
 * Input data for the Portfolio Analysis use case.
 *
 * This object is created by the Controller and passed into the Interactor.
 */
public class PortfolioInputData {

    private final String username;

    /**
     * @param username the username of the logged-in user
     */
    public PortfolioInputData(String username) {
        this.username = username;
    }

    /**
     * @return the username associated with this analysis request
     */
    public String getUsername() {
        return username;
    }
}

