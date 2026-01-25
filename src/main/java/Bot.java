import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.Null;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {

    private ArrayList<Expense> expenses = new ArrayList<Expense>();
    private HashMap<Long, ArrayList<Expense>> expenseMap = new HashMap<>();


    public Bot(){
        super("8044753239:AAHMQyKlXJl7GwOK5BbLRqS6umqgmHqKWwM");
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sm = new SendMessage();

        Long userId = update.getMessage().getChatId();

        String text = update.getMessage().getText();
        sm.setChatId(userId);

        if (text.equals("/start")){
            sm.setText("Привіт, я твій персональний бот для трекінгу коштів і відслідковування витрат! Чим допомогти?");


        } else if(text.equals("/status")){
            sm.setText("Все працює стабільно!");
        } else if (text.startsWith("/add")){
            String[] expen = text.split(" ");
            if(expen.length < 3){
                sm.setText("Не правильно заданий формат. Коректний формат: /add Категорія Сума (Наприклад: /add Піцуня 500)");
            } else {
                String category = expen[1];
                double amount = Double.parseDouble(expen[2]);

                expenseMap.putIfAbsent(userId, new ArrayList<>());
                expenseMap.get(userId).add(new Expense(amount, category));

                sm.setText("Витрату додано: " + category + ": " + amount);
            }
        } else if(text.equals("/show")){
            ArrayList<Expense> userExpenses = expenseMap.get(userId);

            if(userExpenses == null || userExpenses.isEmpty()){
                sm.setText("Твій список витрат пустий");
            } else {
                String strtotal = "Твої витрати:\n";
                double total = 0;

                for(Expense e : userExpenses){
                    strtotal += "- " + e.getCategory() + ": " + e.getAmount() + " грн " + e.getDate() + "\n";
                    total += e.getAmount();
                }

                strtotal += "Сума всіх витрат: " + total + " грн";
                sm.setText(strtotal);
            }
        }
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBotUsername() {
        return "FinanceTraker";
    }
}
