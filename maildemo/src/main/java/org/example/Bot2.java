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

    };
    StringBuilder sb = new StringBuilder();
    // Список ответов пользователя (только текстовые ответы после первого)
    private List<String> answers = new ArrayList<>();
    private int currentQuestion = 0;

    // Переменная для хранения файла (первый ответ)
    private java.io.File fileAttachment = null;

    @Override
    public String getBotUsername() {
        return "";  // Замените на ваш username бота
    }

    @Override
    public String getBotToken() {
        return "";  // Замените на ваш токен бота
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
