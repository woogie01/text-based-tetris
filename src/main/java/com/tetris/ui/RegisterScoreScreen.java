package com.tetris.ui;

import com.tetris.logic.Score;
import com.tetris.logic.RankScoreController;
import com.tetris.logic.SettingController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static com.tetris.component.Button.createBtn;
import static com.tetris.component.ScreenSize.setWidthHeight;

public class RegisterScoreScreen extends JFrame implements ActionListener {

    RankScoreController rankScoreController = new RankScoreController();
    SettingController settingController = new SettingController();

    JTextField nameField;
    JButton submitButton;
    private int playerScore;
    private boolean isItem;

    private int fontSizeTitle;
    private int fontSizeScore;

    public RegisterScoreScreen(int curr_score, boolean isItem) {

        // 노말 모드 vs 아이템 모드
        this.isItem = isItem;

        setTitle("Tetris - GameOver"); // 창의 제목 설정
        String screenSize = settingController.getScreenSize("screenSize", "small");
        switch (screenSize) {
            case "small":
                setWidthHeight(400, 400, this);
                fontSizeTitle = 30;
                fontSizeScore = 20;
                break;
            case "big":
                setWidthHeight(600, 600, this);
                fontSizeTitle = 50;
                fontSizeScore = 30;
                break;
            default:
                setWidthHeight(500, 500, this);
                fontSizeTitle = 40;
                fontSizeScore = 25;
                break;
        }
        setLocationRelativeTo(null); // 창을 화면 가운데에 위치시킴
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창을 닫으면 프로그램 종료

        // 메인 패널 : 상단(ScoreBoard) + 중앙(입력 폼) + 하단(종료 버튼)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // 수직으로 정렬

        // 상단 : ScoreBoard
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(5, 1)); // 제목 행(1칸) + 10개의 스코어(10칸)

        JLabel title = new JLabel("New Top10 Score!!", SwingConstants.CENTER); // 가운데 정렬
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, fontSizeTitle)); // 제목의 폰트 설정
        scorePanel.add(title);

        playerScore = curr_score;
        JLabel score = new JLabel("Your score : " + curr_score, SwingConstants.CENTER);
        score.setFont(new Font(title.getFont().getName(), Font.BOLD, fontSizeScore)); // 제목의 폰트 설정
        scorePanel.add(score);

        mainPanel.add(scorePanel);

        // 상단 패널과 중앙 패널 사이의 간격 추가
        mainPanel.add(Box.createVerticalStrut(10));

        // 중앙 : 이름 입력 칸 + 제출 버튼
        JPanel inputPanel = new JPanel();

        nameField = new JTextField(30);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setForeground(Color.GRAY);
        inputPanel.add(nameField);

        // 이름 필드에 대한 포커스 리스너 추가
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("Please register playerName")) {
                    nameField.setText("");
                    nameField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setForeground(Color.GRAY);
                }
            }
        });
        mainPanel.add(inputPanel);

        // 등록 버튼
        JPanel submitPanel = new JPanel();
        submitButton = createBtn("Submit", "submit", this);

        submitPanel.add(submitButton);
        mainPanel.add(submitPanel);

        // 설정이 끝난 패널을 JFrame에 추가
        add(mainPanel);

        // 키 바인딩 설정
        setKeyBindings();

        setLocationRelativeTo(null); // Centered window
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        setVisible(false);
        if (command.equals("submit")) {
            String name = !nameField.getText().isEmpty() ? nameField.getText() : "익명";
            String difficulty = setDifficultyFromCode(settingController.getDifficulty());


            Score currScore = new Score(name, playerScore, difficulty); // 이번 게임에 얻은 점수
            rankScoreController.addScore(currScore, isItem);
            setVisible(false);

            // TODO : 등록한 점수에 대해서 강조 표시하는 스코어보드 출력
            new ScoreboardScreen(currScore, isItem);
        }
    }

    private String setDifficultyFromCode(int difficultyCode) {
        return switch (difficultyCode) {
            case 0 -> "Easy";
            case 2 -> "Hard";
            default -> "Normal";
        };
    }

    private void setKeyBindings() {
        // 이름 필드에서 아래 키를 누를 때 버튼에 포커스 이동
        nameField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        nameField.getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitButton.requestFocus();
            }
        });

        // 버튼에서 위 키를 누를 때 텍스트 필드에 포커스 이동
        submitButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("UP"), "moveUp");
        submitButton.getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.requestFocus();
            }
        });

        // 이름 필드에서 엔터 키를 누를 때 버튼에 포커스 이동
        nameField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "submitFocus");
        nameField.getActionMap().put("submitFocus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitButton.requestFocus();
            }
        });

        // 버튼에서 엔터 키를 누를 때 프로그램 종료
        submitButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "submit");
        submitButton.getActionMap().put("submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = !nameField.getText().isEmpty() ? nameField.getText() : "익명";
                String difficulty = setDifficultyFromCode(settingController.getDifficulty());
                Score currScore = new Score(name, playerScore, difficulty); // 이번 게임에 얻은 점수
                rankScoreController.addScore(currScore, isItem);
                setVisible(false);
                new ScoreboardScreen(currScore, isItem);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterScoreScreen(100, false));
    }
}