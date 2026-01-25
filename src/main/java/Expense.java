import java.time.LocalDate;
import java.util.Date;

public class Expense {
    private String category;
    private double amount;
    private LocalDate date;

    public Expense(double amount, String category){
        this.amount = amount;
        this.category = category;
        date = LocalDate.now();
    }

    public String getCategory(){
        return category;
    }

    public double getAmount(){
        return amount;
    }

    public LocalDate getDate(){
        return date;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setAmount(double amount){
        this.amount = amount;
    }
}
