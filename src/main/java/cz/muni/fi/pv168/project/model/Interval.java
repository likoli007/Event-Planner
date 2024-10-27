package cz.muni.fi.pv168.project.model;

public class Interval {
    private TimeUnit timeUnit;
    private int amount;

    public Interval(TimeUnit timeUnit, int amount) {
        this.timeUnit = timeUnit;
        this.amount = amount;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String format() {
        StringBuilder sb = new StringBuilder(amount + " " + timeUnit.getShortcut());

        if (timeUnit != TimeUnit.minute()) {
            sb
                    .append(" (")
                    .append(timeUnit.getMinutes() * amount)
                    .append(" ")
                    .append(TimeUnit.minute().getShortcut())
                    .append(")");
        }

        return sb.toString();
    }
}
