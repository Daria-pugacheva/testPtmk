package ru.pugacheva.testPtmk;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class MainApp {
    private static DatabaseConnection databaseConnection;
    private static Scanner scanner;
    private static String startInformation = "Привет!\n При начале работы с приложением в базе данных будет " +
            "создана таблица со столбцами ФИО, Дата рождения, Полю.\n Для начала работы введите 0";
    private static String moreInfoAboutActions = "Вам доступыне следующие действия при работе прилоежния:\n" +
            "1. Созданеие записи;\n" +
            "2. Вывод всех строк с уникальным значением ФИО+дата, отсортированным по ФИО (выводится ФИО, " +
            "Дата рождения, пол, кол-во полных лет);.\n" +
            "3. Автоматиеческое заполнение таблицы (1 000 000 рандомных строк + 100 строк, где фамилия" +
            "начинается на букву F и пол указан мужской;\n" +
            "4. Выборка из таблицы по критерию фамилия начинается на букву F и пол указан мужской;\n" +
            "5. Завершение работы.\n" +
            "Для выбора действия введите номер пункта (например, для создания записи необходимо ввести 1";


    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        System.out.println(startInformation);
        if (scanner.nextInt() != 0) {
            System.out.println("До свидания");
        } else {
            databaseConnection = new DatabaseConnection();
            databaseConnection.connect();
            do {
                System.out.println(moreInfoAboutActions);
                switch (scanner.nextInt()) {
                    case 1:
                        createLine(scanner, databaseConnection);
                        break;
                    case 2:
                        databaseConnection.chooseUniqueLinesByNameAndBirthday();
                        break;
                    case 3:
                        databaseConnection.addLinesAutomatically();
                        break;
                    case 4:
                        databaseConnection.selectByStartLetterOfNameAndGender();
                        break;
                    default:
                        databaseConnection.disconnect();
                        break;
                }
            } while (scanner.nextInt() < 1 || scanner.nextInt() > 4);
        }
        databaseConnection.disconnect();
        scanner.close();
    }

    private static void createLine(Scanner sc, DatabaseConnection dbConnection) {
        System.out.println("Введите ФИО на английском языке");
        String name = scanner.nextLine();
        System.out.println("Введите дату рождения в формате ДД.ММ.ГГГГ");
        String birthday = scanner.nextLine();
        System.out.println("Введите пол (выберите 1 или 0):\n" +
                "0 - женский\n" +
                "1 - мужской");
        int gender = sc.nextInt();
        dbConnection.createLineInTable(name, birthday, gender);
    }


}
