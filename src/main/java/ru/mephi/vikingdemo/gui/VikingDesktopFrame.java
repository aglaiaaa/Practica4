package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;
import ru.mephi.vikingdemo.service.VikingAnalyticsService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final VikingAnalyticsService analytics;
    private final VikingTableModel tableModel = new VikingTableModel();

    public VikingDesktopFrame(VikingService vikingService, VikingAnalyticsService analytics) {
        this.vikingService = vikingService;
        this.analytics = analytics;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 480));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JTable vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(28);
        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JButton singleButton = new JButton("Create random viking");
        singleButton.addActionListener(e -> {
            Viking v = vikingService.createRandomViking();
            tableModel.addViking(v);
        });

        JButton manyButton = new JButton("Create many vikings 50");
        manyButton.addActionListener(e -> {
            List<Viking> newVikings = vikingService.generateManyRandomVikings(50);
            newVikings.forEach(tableModel::addViking);
            JOptionPane.showMessageDialog(this, "Создано " + newVikings.size() + " викингов");
        });

        JButton analyticsButton = new JButton("Analytics");
        analyticsButton.addActionListener(e -> showAnalyticsDialog());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(singleButton);
        bottomPanel.add(manyButton);
        bottomPanel.add(analyticsButton);
        add(bottomPanel, BorderLayout.SOUTH);
        loadExisting();
    }

    private void loadExisting() {
        List<Viking> all = vikingService.findAll();
        all.forEach(tableModel::addViking);
    }

    public void addNewViking(Viking viking) {
        tableModel.addViking(viking);
    }

    private void showAnalyticsDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ButtonGroup group = new ButtonGroup();

        JRadioButton opt1 = new JRadioButton("1) Старше 20 лет");
        JRadioButton opt2 = new JRadioButton("2) Младше 20 лет");
        JRadioButton opt3 = new JRadioButton("3) От 20 до 40 лет");
        JRadioButton opt4 = new JRadioButton("4) Вне диапазона 20-40");
        JRadioButton opt5 = new JRadioButton("5) Рыжие с длинной бородой");
        JRadioButton opt6 = new JRadioButton("6) Владеют ровно 1 топором");
        JRadioButton opt7 = new JRadioButton("7) Владеют ровно 2 топорами");
        JRadioButton opt8 = new JRadioButton("8) Случайный великан (рост > 180 см)");
        JRadioButton opt9 = new JRadioButton("9) С легендарным снаряжением");
        JRadioButton opt10 = new JRadioButton("10) Рыжие, сортировка по возрасту");
        JRadioButton opt11 = new JRadioButton("11) Максимальный ID");
        JRadioButton opt12 = new JRadioButton("12) Все чётные ID");

        group.add(opt1);
        group.add(opt2);
        group.add(opt3);
        group.add(opt4);
        group.add(opt5);
        group.add(opt6);
        group.add(opt7);
        group.add(opt8);
        group.add(opt9);
        group.add(opt10);
        group.add(opt11);
        group.add(opt12);

        panel.add(new JLabel("Выберите тип анализа:"));
        panel.add(opt1);
        panel.add(opt2);
        panel.add(opt3);
        panel.add(opt4);
        panel.add(opt5);
        panel.add(opt6);
        panel.add(opt7);
        panel.add(opt8);
        panel.add(opt9);
        panel.add(opt10);
        panel.add(opt11);
        panel.add(opt12);

        int result = JOptionPane.showConfirmDialog(this, panel, "Аналитика", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String output;
        if (opt1.isSelected()) {
            output = "Старше 20 лет: " + analytics.olderThan(20);
        } else if (opt2.isSelected()) {
            output = "Младше 20 лет: " + analytics.youngerThan(20);
        } else if (opt3.isSelected()) {
            output = "От 20 до 40 лет: " + analytics.ageInRange(20, 40);
        } else if (opt4.isSelected()) {
            output = "Вне 20-40: " + analytics.ageOutOfRange(20, 40);
        } else if (opt5.isSelected()) {
            output = "Рыжих с длинной бородой: " + analytics.countByBeardAndHair(BeardStyle.LONG, HairColor.Red);
        } else if (opt6.isSelected()) {
            output = "С 1 топором: " + analytics.countWithExactAxes(1);
        } else if (opt7.isSelected()) {
            output = "С 2 топорами: " + analytics.countWithExactAxes(2);
        } else if (opt8.isSelected()) {
            output = analytics.randomTallerThan(180)
                    .map(v -> "Случайный великан: " + v.name() + " (рост " + v.heightCm() + " см)")
                    .orElse("Великанов не найдено");
        } else if (opt9.isSelected()) {
            List<Viking> list = analytics.legendaryArmed();
            output = "Викинги с легендарным снаряжением: " +
                    list.stream().map(Viking::name).collect(Collectors.joining(", "));
        } else if (opt10.isSelected()) {
            List<Viking> redList = analytics.redHairedSortedByAge();
            output = "Рыжие по возрасту:\n" +
                    redList.stream().map(v -> v.name() + " (" + v.age() + ")").collect(Collectors.joining("\n"));
        } else if (opt11.isSelected()) {
            output = "Максимальный ID: " + analytics.findMaxId().orElse(0);
        } else {
            output = "Чётные ID: " + analytics.collectEvenIds();
        }

        JOptionPane.showMessageDialog(this, output, "Результат анализа", JOptionPane.INFORMATION_MESSAGE);
    }
}
