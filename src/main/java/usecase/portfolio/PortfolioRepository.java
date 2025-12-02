package usecase.portfolio;

import entity.Holding;
import java.util.List;

/**
 * Data access interface for user portfolio holdings.
 * Use case layer depends on this interface, not on a concrete implementation.
 */
public interface PortfolioRepository {

    /**
     * Returns all stock holdings for the given user.
     * @param username the username of the logged-in user
     * @return list of holdings (empty list if user has no investments)
     */
    List<Holding> findHoldingsByUser(String username);
}

