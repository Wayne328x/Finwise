package usecase.portfolio;

import data.usecase5.PortfolioRepository;
import entity.Holding;

import java.util.ArrayList;
import java.util.List;

public class FakePortfolioRepository implements PortfolioRepository {
    private List<Holding> holdings = new ArrayList<>();

    public void setHoldings(List<Holding> h) {
        holdings = h;
    }

    @Override
    public List<Holding> findHoldingsByUser(String username) {
        return holdings;
    }
}