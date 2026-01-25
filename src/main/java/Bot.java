import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {

    private ArrayList<String> notes = new ArrayList<String>();

    SendMessage sm = new SendMessage();

    public Bot(){
        super("8044753239:AAHMQyKlXJl7GwOK5BbLRqS6umqgmHqKWwM");
    }

    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        sm.setChatId(chatId);

        if (text.equals("/start")){
            sm.setText("Привіт, я твій персональний бот для трекінгу коштів і відслідковування витрат! Чим допомогти?");
        } else if(text.equals("/status")){
            sm.setText("Все працює стабільно!");
        } else if (text.startsWith("/add")){
            String note = text.replace("/add", "").trim();

            if(note.isEmpty()){
                sm.setText("ви не написали нічого");
            } else {
                notes.add(note);
                sm.setText("текст додано: " + notes);
            }

        } else if(text.equals("/show")){
            if(notes.isEmpty()){
                sm.setText("список порожній");
            } else {
                String allNotes =  "Твої нотатки:\n";
                for(String note : notes){
                    allNotes += "- " + note + "\n";
                }
                sm.setText(allNotes);
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
