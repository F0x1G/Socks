package com.example.socks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorChooserExample {

    // Поле класу для зберігання вибраного колору
    private static Color selectedColor;

    public static Color showColorChooserDialog() {
            // Створюємо об'єкт JFrame (можна використовувати існуючий вікно)
            JFrame frame = new JFrame("Color Chooser Example");

            // Створюємо JButton, який викличе вікно палітри
            JButton chooseColorButton = new JButton("Choose Color");

            // Додаємо слухача подій до кнопки
            chooseColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Відображаємо вікно палітри та зберігаємо вибраний колір
                        selectedColor = JColorChooser.showDialog(frame, "Choose a Color", Color.BLACK);

                        // Перевіряємо, чи колір не є null (користувач скасував вибір)
                        if (selectedColor != null) {
                            // Викликаємо додаткові дії з вибраним кольором, якщо потрібно

                            // Наприклад, можна викликати інший метод з обраним колором
                            handleSelectedColor();

                            // Закриваємо вікно палітри
                            frame.dispose();
                        }
                    }catch (Exception t){

                    }
                }
            });

            // Додаємо кнопку до вікна
            frame.add(chooseColorButton);

            // Налаштовуємо параметри вікна
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);

            // Викликаємо showColorChooserDialog для виведення вікна палітри
            return selectedColor;

    }

    // Приклад методу, який обробляє вибраний колір
    private static void handleSelectedColor() {
        System.out.println("Selected Color: " + selectedColor);
        // Додаткові дії з вибраним колором можна виконати тут
    }
}
