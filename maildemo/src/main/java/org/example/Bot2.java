package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class Bot2 extends TelegramLongPollingBot {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot2());
            System.out.println("Бот запущен...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    // Список вопросов
    private final String[] questions = new String[]{
                    "Я (ф.и.о), паспортные данные, даю свое согласие на оказание " +
                    "правовой помощи адвокатам Хисамову Рамилю Иршатовичу, " +
                    "рег.номер в реестре адвокатов Республики Татарстан 16/2389 и " +
                    "Ренскову Александру Михайловичу, рег.номер в реестре адвокатов Республики Татарстан 16/2376\n " +
                    "Фамилия имя отчество, подпись, дата \n" + "Когда напишите согласие, сканируйте лист и прикрепите его сюда в виде ФАЙЛА.\n" +
                    "\n",

            "1. Фамилия, имя, отчество –",
            "2. Дата рождения –",
            "3. Место рождения –",
            "4. Гражданство – ",
            "5. Образование –",
            "6. Место работы, должность, род занятий, телефон –",
            "7. Адрес регистрации -",
            "8. Место жительства -",
            "9. Семейное положение –",
            "10. Паспорт или иной документ, удостоверяющий личность – серия     №.     выдан - ",
            "11. Когда и от кого узнали про «RC Group» (указать полные данные, абонентский номер приглашавшего)?",
            "12. Известно ли Вам кто являлись владельцами и руководителями «RC Group», назовите их данные? Известен ли Вам круг общения данных лиц?",
            "13.Известны ли Вам юридические лица, осуществляющие деятельность под брендом «RC Group»?",
            "14. Имеются ли у Вас какие-либо аудио-, видеозаписи переговоров, выступлений представителей «RC Group», либо переписка и иные сведения о взаимодействии с представителями и иными лицами, действующими в интересах «RC Group»?",
            "15. Какие продукты «RC Group» Вам известны? Известно ли Вам, кто был инициатором, разработчиком продуктов «RC Group»? Если да, кто, назовите его ФИО, контактный телефон и иные имеющиеся у Вас данные?",
            "16. Известно ли Вам о местонахождении офисов, филиалов, представительств либо иных помещений, занимаемых «RC Group», либо его представителями, топ-менеджерами, если да, назовите адрес и иные имеющиеся у Вас сведения?",
            "17. По какому адресу и какую деятельность ведет «RC Group»? За счет какой деятельности «RC Group» может выплачивать вознаграждение пользователям? Кем сообщалось об указанной деятельности? Демонстрировались ли результаты указанной деятельности?",
            "18. При посещении офисов, филиалов и иных помещений, занимаемых «RC Group», какие лица находились, назовите их данные?",
            "19. Заключался ли договор, в какой форме, кем подписывался, что входило в обязанности по данному договору/соглашению?",
            "20. Посещали ли Вы презентации, вебинары, конференции и прочие собрания, проводимые представителями «RC Group»? Если да, когда это происходило, кто проводил мероприятие?",
            "21.Сообщалось ли представителями «RC Group», топ-лидерами RC Group, что прибыль RC Group не гарантирована, что имеются риски потерять вложенные средства?",
            "22. Кто и каким образом зарегистрировал личный кабинет (на какой сетевой ресурс обращались)? По чьей реферальной ссылке зарегистрировались? Каким образом происходила регистрация (адрес электронной почты, логин, ID пользователя, какой документ предоставлен для верификации)?",
            "23. Привязывались ли в приложения RC PAY расчетные счета?",
            "24. Какие тарифы Вам предлагали, по какой стоимости, каким образом, какую сумму и в какие сроки Вы должны были заработать с помощью данных тарифов?",
            "25. Приглашали ли лично Вы кого по своей реферальной ссылке, если да, то опишите механизм, какие бонусы получили за это?",
            "26. Использовали ли Вы кредитные средства для покупки данных тарифов, если да, то каким c кредитным учреждением заключен договор? Кто был инициатором использования кредитных средств? Кто подавал заявку в банк? Кто распоряжался кредитными средствами, то есть непосредственно проводил транзакции в банковском приложении?",
            "27. При использовании кредитных средств, погашали ли Вы кредит (указать дату кредитного договора, сумму кредита, сумму задолженности)?",
            "28. Какой продукт, предлагаемый «RC Group» приобретен? Условия приобретенного продукта/тарифа? На какие счета (номер счета) были перечислены денежные средства для покупки? По указанию кого были перечислены денежные средства?",
            "29. Устанавливали ли приложение RC PAY? Обменивали ли электронные денежные средства на какую-либо услугу посредством указанного приложения? Если да, то какой продукт/услуга, какова их стоимость?",
            "30.Осуществлялись ли покупки на других электронных площадках?",
            "31. Сколько внутренней валюты RC поступило в личный кабинет? Подробно опишите порядок поступления внутренней валюты?",
            "32. Сообщалось ли представителями «RC Group» о возможности использования внутренней валюты RC в качестве оплаты? Если да, то в каких компаниях и какой товар/услуга была приобретена на данную валюту?",
            "33.Заключались ли договоры с представителями «RC Group» на бумажном носителе?",
            "34. Привлекали ли в деятельность «RC Group» лиц из числа круга общения (своих знакомых, родственников)? Если да, то получали ли за их регистрацию какое-либо денежное вознаграждение?",
            "35. Состоите ли, подписаны ли на группы «RC Group» в приложениях телеграмм, Вотсап? Сколько участников? Кто администратор группы? Состоят ли в указанных группах топ-лидеры «RC Group»? Какова роль топ-лидеров в указанных группах?",
            "36. Производилась ли демонстрация имущества, приобретенного другими участниками «RC Group»? Кто и какое имущество демонстрировал? Что сообщалось о приобретении (адрес недвижимости, стоимость, марка-модель автомобиля, регистрационные данные, на какие средства приобретено имущество)?",
            "37. Известны ли лидеры «RC Group»? Если да, то кто известен, контакты указанных лиц?",
            "38. Назовите платежные системы (криптообменники криптобиржи) используемые представителями «RC Group»?",
    };
    StringBuilder sb = new StringBuilder();
    // Список ответов пользователя (только текстовые ответы после первого)
    private List<String> answers = new ArrayList<>();
    private int currentQuestion = 0;

    // Переменная для хранения файла (первый ответ)
    private java.io.File fileAttachment = null;

    @Override
    public String getBotUsername() {
        return "LawyerInformation_Bot\n";  // Замените на ваш username бота
    }

    @Override
    public String getBotToken() {
        return "7612416949:AAGaT9abqZXARlo2ep-vgYx945B5bCcXWoU";  // Замените на ваш токен бота
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null) {
            // Начало диалога /start
            if (message.hasText() && (message.getText().equals("/start") || message.getText().equals("Start"))) {
                currentQuestion = 0;
                answers.clear();
                sendMessage(message.getChatId().toString(), "Добрый день! для начала работы необходимо написать на листе бумаги от руки согласие по данному шаблону ");
                askNextQuestion(message);
            }

            // Обработка первого ответа с вложением
            if ((message.hasDocument() || message.hasPhoto() || message.hasVoice()) && currentQuestion == 0) {
                String fileId = message.getDocument() != null ? message.getDocument().getFileId() :
                        message.getPhoto() != null ? message.getPhoto().get(0).getFileId() :
                                message.getVoice() != null ? message.getVoice().getFileId() : null;

                if (fileId != null) {
                    try {
                        // Загружаем файл и сохраняем его
                        fileAttachment = downloadFileFromTelegram(fileId);
                        sendMessage(message.getChatId().toString(), "Спасибо! Теперь, пожалуйста, заполните анкету");
                        currentQuestion++; // Переходим к следующему вопросу
                        askNextQuestion(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendMessage(message.getChatId().toString(), "Произошла ошибка при получении файла.");
                    }
                }
            }

            // Обработка текстовых ответов после первого
            else if (message.hasText() && currentQuestion > 0) {
                String userMessage = message.getText();
                answers.add(userMessage);
                currentQuestion++;

                if (currentQuestion < questions.length) {
                    askNextQuestion(message);  // Переходим к следующему вопросу
                } else {
                    sendMessage(message.getChatId().toString(), "Спасибо , мы свяжемся с вами в ближайшее время.");
                    sendAnswers(message.getChatId().toString());
                    answers.clear(); // Очистим ответы после завершения
                    currentQuestion = 0;

                    // Отправляем письмо с ответами и вложением
                    try {

                        sendEmailWithAttachment("", "Ответы пользователя", sb.toString(), fileAttachment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Метод для отправки сообщения
    private void sendMessage(String chatId, String text) {
        org.telegram.telegrambots.meta.api.methods.send.SendMessage message = new org.telegram.telegrambots.meta.api.methods.send.SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message); // Отправка сообщения
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Метод для отправки следующего вопроса
    private void askNextQuestion(Message message) {
        if (currentQuestion < questions.length) {
            sendMessage(message.getChatId().toString(), questions[currentQuestion]);
        }
    }

    // Метод для формирования всех ответов
    private void sendAnswers(String chatId) {

        for (int i = 0; i < answers.size(); i++) {
            sb.append(questions[i]).append("\n").append(answers.get(i)).append("\n\n");
        }

    }

    // Метод для скачивания файла из Telegram
    private java.io.File downloadFileFromTelegram(String fileId) throws Exception {
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(fileId);
        org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);  // Получаем объект File

        String filePath = file.getFilePath();
        String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

        // Открываем поток для скачивания
        InputStream inputStream = new URL(fileUrl).openStream();

        String fileExtension = getFileExtension(filePath);
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;

        java.io.File downloadedFile = new java.io.File(fileName);
        try (OutputStream outputStream = new FileOutputStream(downloadedFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return downloadedFile;
    }

    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex > 0) {
            return filePath.substring(dotIndex + 1);
        } else {
            return "unknown";
        }
    }

    // Метод для отправки email с вложением
    private void sendEmailWithAttachment(String toEmail, String subject, String body, java.io.File file) throws Exception {
        String fromEmail = "";
        String password = "";  // Замените на ваш пароль

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            if (file != null) {
                MimeBodyPart filePart = new MimeBodyPart();
                FileDataSource fileDataSource = new FileDataSource(file.getAbsolutePath());
                filePart.setDataHandler(new DataHandler(fileDataSource));
                filePart.setFileName(file.getName());
                multipart.addBodyPart(filePart);
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Письмо с вложением отправлено на почту!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Инициализация бота

}
