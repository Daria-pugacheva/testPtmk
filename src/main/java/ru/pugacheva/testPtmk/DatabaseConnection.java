package ru.pugacheva.testPtmk;

import java.nio.charset.Charset;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

public class DatabaseConnection {
    private Connection connection;
    private Statement statement;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Не удается подключиться к базе данных");
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTablePeople() {
        try {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS people (\n" +
                    "    id       INTEGER    PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name     TEXT (256),\n" +
                    "    birthday TEXT,\n" +
                    "    gender   INTEGER\n" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createLineInTable(String name, String birthday, int gender) {
        try {
            statement.executeUpdate("insert into people (name, birthday, gender) values ('"
                    + name + "', '" + birthday + "', " + gender + ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Запись в таблицу добавлена");
    }
//todo: запрос написан под случай хранения даты в формате Date  в MySQL. Не успела отформатировать под SQLlite
    public void chooseUniqueLinesByNameAndBirthday() {
        try (ResultSet resultSet = statement.executeQuery("SELECT DISTINCT concat(name, ' ; ', birthday)" +
                " as name_and_birthday,\n" +
                "name, birthday, gender, timestampdiff(year, birthday, CURDATE()) AS age\n" +
                "FROM people order by name")) {
            System.out.println("Список укникальных строк таблицы по ФИО + дата рождения:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name") + " " +
                        resultSet.getString("birthday") + " " +
                        resultSet.getInt("gender") + " " + resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLinesAutomatically() {
        try {
            connection.setAutoCommit(false);
            for (int i = 1; i <= 1000000; i++) {
                Random random = new Random();
                String name = generateName().toString();
                String birthday = generateBirthDay();
                int gender = random.nextInt(2);
                statement.executeUpdate(String.format("insert into people (name, birthday, gender)" +
                        " values ('%s', '%s', %d);", name, birthday, gender));
            }

            for (int i = 1; i <= 100; i++) {
                String name = new StringBuilder("F").append(generateName()).toString();
                String birthday = generateBirthDay();
                statement.executeUpdate(String.format("insert into people (name, birthday, gender)" +
                        " values ('%s', '%s', 1);", name, birthday));
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private StringBuilder generateName() {
        String alphabet[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(26);
            stringBuilder.append(alphabet[index]);
        }

        stringBuilder.append(" ");

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(26);
            stringBuilder.append(alphabet[index]);
        }

        stringBuilder.append(" ");

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(26);
            stringBuilder.append(alphabet[index]);
        }

        return stringBuilder;
    }

    private String generateBirthDay() {
        int date = (int) (Math.random() * 29) + 1;
        int month = (int) (Math.random() * 12) + 1;
        int year = (int) (Math.random() * 2023) + 1920;
        return date + "." + month + "." + year;
    }

    public void selectByStartLetterOfNameAndGender() {
        long start = System.currentTimeMillis();
        try (ResultSet resultSet = statement.executeQuery("select * from people where gender=1 and" +
                "name like 'F%'")) {
            System.out.println("Список строк таблицы, в которых ФИО начинается с буквы F и пол мужской:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name") + " " +
                        resultSet.getString("birthday") + " " +
                        resultSet.getInt("gender") + " " + resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Время выполенния задачи: " + (System.currentTimeMillis() - start));
    }


}

