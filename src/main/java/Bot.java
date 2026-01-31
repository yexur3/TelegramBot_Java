import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.HashMap;

public class Bot extends TelegramLongPollingBot {

    private ArrayList<Expense> expenses = new ArrayList<Expense>();
    private HashMap<Long, String> userStates = new HashMap<>();
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

        if(userStates.containsKey(userId) && userStates.get(userId).equals("WAITING_FOR_EXPENSE")){
            String[] expen = text.split(" ");
            if(expen.length < 2){
                sm.setText("Не правильно заданий формат. Коректний формат:  Категорія Сума (Наприклад: Піцуня 500)");
            } else {
                try{
                    String category = expen[0];
                    double amount = Double.parseDouble(expen[1]);

                    DatabaseHandler.addExpense(userId, category, amount);

                    userStates.remove(userId);
                    sm.setText("Витрату додано: " + category + ": " + amount);
                } catch (NumberFormatException e){
                    sm.setText("Enter a number");
                }
            }
            send(sm);
            return;
        }

        if (text.equals("/start")){
            sm.setText("Привіт, я твій персональний бот для трекінгу коштів і відслідковування витрат! Чим допомогти?");

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            keyboardMarkup.setResizeKeyboard(true);
            keyboardMarkup.setOneTimeKeyboard(false);
            keyboardMarkup.setSelective(true);

            ArrayList<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row1 = new KeyboardRow();
            row1.add("/status");
            row1.add("/show");

            KeyboardRow row2 = new KeyboardRow();
            row2.add("/add");

            keyboard.add(row1);
            keyboard.add(row2);

            keyboardMarkup.setKeyboard(keyboard);
            sm.setReplyMarkup(keyboardMarkup);

        } else if(text.equals("/status")){
            sm.setText("Все працює стабільно!");
        } else if (text.startsWith("/add")){
            sm.setText("Напиши, будь ласка, витрату. Формат:  Категорія Сума (Наприклад: Піцуня 500)");
            userStates.put(userId, "WAITING_FOR_EXPENSE");
        } else if(text.equals("/show")){

            // КРИТИЧНЕ МІСЦЕ: Читаємо з бази, а не з карти!
            ArrayList<Expense> userExpenses = DatabaseHandler.getExpenses(userId);

            if(userExpenses == null || userExpenses.isEmpty()){
                sm.setText("Твій список витрат у базі поки що пустий");
            } else {
                StringBuilder strtotal = new StringBuilder("Твої витрати з бази:\n");
                double total = 0;

                for(Expense e : userExpenses){
                    strtotal.append("- ").append(e.getCategory()).append(": ")
                            .append(e.getAmount()).append(" грн\n");
                    total += e.getAmount();
                }

                strtotal.append("\n💰 Разом: ").append(total).append(" грн");
                sm.setText(strtotal.toString());
            }
        } else if(text.equals("/delete")){

        }
        send(sm);

    }

    private void send(SendMessage sm) {
        try{
            execute(sm);
        } catch (TelegramApiException e ){
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "FinanceTraker";
    }
}
