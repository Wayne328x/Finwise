package entity;

public class Expense {

    private final long id;
    private final String username;
    private final String datetime;
    private final String type;
    private final double amount;

    public Expense(long id, String username, String datetime, String type, double amount) {
        this.id = id;
        this.username = username;
        this.datetime = datetime;
        this.type = type;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}
