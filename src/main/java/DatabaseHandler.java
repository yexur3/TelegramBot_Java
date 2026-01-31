import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:finance.db";

    public static void initDatabase(){
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()){

            String sql = "CREATE TABLE IF NOT EXISTS expenses(id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, category TEXT, amount REAL, date TEXT);";
            stmt.execute(sql);
            System.out.println("database is ready for work");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addExpense(long userId, String category, double amount){
        String sql = "INSERT INTO expenses (userId, category, amount, date) VALUES (?, ?, ?, date('now'));";

        try (Connection conn = DriverManager.getConnection(URL);
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setLong(1, userId);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);

            pstmt.execute();
            System.out.println("data writed into database");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Expense> getExpenses(long userId) {
        ArrayList<Expense> list = new ArrayList<>();
        String sql = "SELECT category, amount, date FROM expenses WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            java.sql.ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                // Створюємо об'єкт, витягуючи дані з колонок
                Expense exp = new Expense(
                        rs.getDouble("amount"),
                        rs.getString("category")
                );
                // Якщо у твого Expense є поле date, можна додати: exp.setDate(rs.getString("date"));
                list.add(exp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}


