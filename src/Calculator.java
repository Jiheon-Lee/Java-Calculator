import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Calculator extends JFrame {
	
	private JTextField inputSpace;
	private String num = "";
	private String prev_operation = "";
	private ArrayList<String> equation = new ArrayList<String>();
	
	public Calculator() {
		setLayout(null);	// 레이아웃 관리자 제거
		
		// 화면 구현
		inputSpace = new JTextField();
		inputSpace.setEditable(false);	// 편집 가능 여부
		inputSpace.setBackground(Color.WHITE);
		inputSpace.setHorizontalAlignment(JTextField.RIGHT);	// 수평 우측 정
		inputSpace.setFont(new Font("Arial", Font.BOLD, 50));
		inputSpace.setBounds(8, 10, 270, 70);	// x:8, y:10의 위치에 270*70의 크기
		
		// 버튼을 담을 패널 구현
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 4, 10, 10));	// 격자 배치 (가로 칸, 세로 칸, 좌우 간격, 상하 간격)
		buttonPanel.setBounds(8, 90, 270, 235);
		
		// 버튼 글자
		String button_names[] = { "C", "/", "*", "=", "7", "8", "9", "+", "4", "5", "6", "-", "1", "2", "3", "0" };
		JButton buttons[] = new JButton[button_names.length];
		
		for (int i = 0; i < button_names.length; i++) {
			// 버튼 생성
			buttons[i] = new JButton(button_names[i]);
			buttons[i].setFont(new Font("Arial", Font.BOLD, 20));
			if (button_names[i] == "C") buttons[i].setBackground(Color.RED);
			else if ((i >= 4 && i <= 6) || (i >= 8 && i <= 10) || (i >= 12 && i <= 14)) buttons[i].setBackground(Color.BLACK);	// 숫자 버튼은 검정색
			else buttons[i].setBackground(Color.GRAY);
			buttons[i].setForeground(Color.WHITE);		// 글자색은 흰색
			buttons[i].setOpaque(true);		// 버튼 색 적용이 안될 때 적용
			buttons[i].setBorderPainted(false);		// 테두리 제거
			buttons[i].addActionListener(new PadActionListener());
			buttonPanel.add(buttons[i]);
		}
		
		add(inputSpace);
		add(buttonPanel);
		
		setTitle("계산기");	// 창의 제목
		setVisible(true);	// 창을 화면에 나타낼 것인지 설정
		setSize(300, 370);	// 창의 가로, 세로 길이
		setLocationRelativeTo(null);	// 창을 화면 가운데 배치
		setResizable(false);	// 창의 크기를 조절할 수 없도록 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// 창 종료 시 프로세스까지 종료
	}
	
	class PadActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String operation = e.getActionCommand();
			
			if (operation.equals("C")) {
				inputSpace.setText("");
			} else if (operation.equals("=")) {
				String result = Double.toString(calculate(inputSpace.getText()));
				inputSpace.setText("" + result);
				num = "";
			} else if (operation.equals("+") || operation.equals("-") || operation.equals("*") || operation.equals("/")) {
				if (inputSpace.getText().equals("") && operation.equals("-")) {
					// 첫 값을 음수로 입력할 수 있도록
					inputSpace.setText(inputSpace.getText() + e.getActionCommand());
				} else if (!inputSpace.getText().equals("") && !prev_operation.equals("+") && !prev_operation.equals("-") && !prev_operation.equals("*") && !prev_operation.equals("/")) {
					// 위의 계산식이 비어있지 않고 연산자를 중복으로 입력하지 않을 시 입력 가능
					inputSpace.setText(inputSpace.getText() + e.getActionCommand());
				}
			} else {
				inputSpace.setText(inputSpace.getText() + e.getActionCommand());
			}
			prev_operation = e.getActionCommand();
		}
	}
	private void fullTextParsing(String inputText) {
		equation.clear();
		
		for (int i = 0; i < inputText.length(); i++) {
			char ch = inputText.charAt(i);
			
			// ex) 69 + 3
			// num = "" => num = "6" => num = "69"
			// 연산 기호가 나올 시 ArrayList에 추가되고 초기화
			if (ch == '-' || ch == '+' || ch == '*' || ch == '/') {
				equation.add(num);
				num = "";
				equation.add(ch + "");
			} else {
				num = num + ch;
			}
		}
		equation.add(num);
		equation.remove("");
	}
	
	// 계산 기능 구현
	public double calculate(String inputText) {
		fullTextParsing(inputText);
		
		double prev = 0;
		double current = 0;
		String mode = "";
		
		// 곱셈, 나눗셈 연산을 먼저 진행
		for (int i = 0; i < equation.size(); i++) {
			String s = equation.get(i);
			
			if (s.equals("+")) {
				mode = "add";
			} else if (s.equals("-")) {
				mode = "sub";
			} else if (s.equals("*")) {
				mode = "mul";
			} else if (s.equals("/")) {
				mode = "div";
			} else {
				// 전에 있던 연산자가 곱셈 또는 나눗셈이고 현재 인덱스의 값이 숫자일 때 연산
				// ex) 3 + 5 * 6
				// 마지막 6에 왔을 때 계산
				// one = 5, two = 6
				// 3 + 30
				if (mode.equals("mul") || mode.equals("div") && !s.equals("+") && !s.equals("-") && !s.equals("*") && !s.equals("/")) {
					Double one = Double.parseDouble(equation.get(i - 2));
					Double two = Double.parseDouble(equation.get(i));
					Double result = 0.0;
					
					if (mode.equals("mul")) {
						result = one * two;
					} else if (mode.equals("div")) {
						result = one / two;
					}
					
					equation.add(i + 1, Double.toString(result));
					
					for (int j = 0; j < 3; j++) {
						// 계산이 끝난 계산식은 제거 (*,/)
						equation.remove(i - 2);
					}
					
					i -= 2;	// 결과값이 생긴 인덱스로 이동
				}
			}
		}	// 곱셈 나눗셈을 먼저 계산
		
		for (String s : equation) {
			if (s.equals("+")) {
				mode = "add";
			} else if (s.equals("-")) {
				mode = "sub";
			} else {
				current = Double.parseDouble(s);
				if (mode.equals("add")) {
					prev += current;
				} else if (mode.equals("sub")) {
					prev -= current;
				} else {
					prev = current;
				}
			}
			prev = Math.round(prev * 100000) / 100000.0;
		}
		
		return prev;
	}
	
	public static void main(String[] args) {
		new Calculator();
	}

}
