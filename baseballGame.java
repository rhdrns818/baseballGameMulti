package Assignment;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

public class baseballGame extends Player{
	//멤버 변수(전역)
	JLabel scoreJLabel; //판수,점수
	JTextArea myTextArea; //내 화면
	JTextArea oppTextArea; //상대 화면
	JTextArea resultTextArea; //결과 화면
	JTextField msgInput; //입력창
	JFrame readyFrame; //준비화면
	JFrame gameFrame; //게임화면
	JFrame resultFrame; //결과화면
	JButton recordRemoveButton; //아이템버튼1
	JButton mixNumButton; //아이템버튼2
	JButton addOppCountButton; //아이템버튼3
	int gameCount = 1; //게임 판수
	int myCount; //내가 보낸 횟수
	int oppCount; //상대방이 보낸 횟수
	int myScore; //내 점수
	int oppScore; //상대 점수
	int inputDisable; //input이 비활성화된 개수
	int randomNum; //addOppCountButton에서 나온 랜덤 숫자
	int restart; //재시작
	
	public void readyView() {
		readyFrame = new JFrame();
		readyFrame.setSize(420,180); //화면 크기
		readyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //x누르면 종료
		readyFrame.setLocationRelativeTo(null); //가운데에 화면
		
		myTextArea = new JTextArea(); //내 화면창
		readyFrame.add(myTextArea); //화면창 추가
		readyFrame.setVisible(true); //화면 출력
		readyFrame.setTitle("플레이어가 들어올 때까지 기다리세요!"); //제목창에 글씨 출력
	}
	
	//게임시작 전 카운트 세기
	public void readyCount(JFrame Frame,JTextArea TextArea,int gameCount){ //제목창,화면창,판수 받기
		Frame.setTitle("게임이 시작됩니다!"); //제목창 출력
		TextArea.setFont(new Font("",Font.BOLD,40)); //폰트 설정
		for(int i=5;i>0;i--) { //5회 반복
			TextArea.setText("       "+gameCount+"번째 게임이\n       시작됩니다! :  "+i);
			try {
				Thread.sleep(1000); //1초 지연
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		readyFrame.setVisible(false); //대기화면 보이지 않게 하기
	}
	
	public void buttonDisable() { //모든 버튼 비활성화
		recordRemoveButton.setEnabled(false);
		mixNumButton.setEnabled(false);
		addOppCountButton.setEnabled(false);
	}
	
	public void buttonEnable() { //모든 버튼 활성화
		recordRemoveButton.setEnabled(true);
		mixNumButton.setEnabled(true);
		addOppCountButton.setEnabled(true);
	}
	
	public void startView() {
		gameFrame = new JFrame(); //게임 화면
		gameFrame.setSize(460,500); //화면 크기
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //x누르면 종료
		gameFrame.setLocationRelativeTo(null); //가운데에 화면
		
		scoreJLabel = new JLabel(); //판수와 점수
		myTextArea = new JTextArea(0,20); //내 화면창
		oppTextArea = new JTextArea(0,20); //상대 화면창
		msgInput = new JTextField(); //입력창
		recordRemoveButton = new JButton("상대 기록 지우기"); //상대방 기록 지우기 버튼
		mixNumButton = new JButton("내 숫자 바꾸기"); //내 숫자 바꾸기 버튼
		addOppCountButton = new JButton("상대 시도횟수 늘리기"); //3~6회 상대 시도횟수 늘리기 버튼
		JPanel bottomPanel = new JPanel(); //하단 패널
		JPanel buttonPanel = new JPanel(); //버튼 패널
		//하단 패널에 넣기
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,0)); //FlowLayout으로 가운데 정렬, 가로Gap 10px
		buttonPanel.add(recordRemoveButton);
		buttonPanel.add(mixNumButton);
		buttonPanel.add(addOppCountButton);
		
		bottomPanel.setLayout(new GridLayout(2, 1)); //2행1열 Grid로 정렬
		bottomPanel.add(buttonPanel); //버튼 패널을 하단 패널에 넣기
		bottomPanel.add(msgInput); 
		
		Font gameFont = new Font("Serif",Font.BOLD,20); //폰트 설정
		myTextArea.setLineWrap(true); //자동 줄바꿈
		oppTextArea.setLineWrap(true); //자동 줄바꿈
		msgInput.setFont(gameFont); //폰트 적용
		msgInput.setHorizontalAlignment(JTextField.CENTER); //가운데 정렬
		scoreJLabel.setText(gameCount+"번째 게임!!   "+myScore+"  :  "+oppScore); //판수와 점수 출력
		scoreJLabel.setHorizontalAlignment(JLabel.CENTER); //가운데 정렬
		scoreJLabel.setFont(gameFont); //폰트 적용
		gameFrame.add(scoreJLabel,BorderLayout.NORTH); //판수,점수창 위쪽 배치
		gameFrame.add(new JScrollPane(myTextArea), BorderLayout.WEST); //스크롤 있는 내 화면창 좌측 배치
		gameFrame.add(new JScrollPane(oppTextArea), BorderLayout.EAST); //스크롤 있는 상대 화면창 우측 배치
		gameFrame.add(bottomPanel, BorderLayout.SOUTH); //하단 패널 아래쪽 배치
		gameFrame.setResizable(false); //화면 크기 조정 막기
		gameFrame.setVisible(true); //화면 출력
		generateNumber(); // 랜덤 숫자 생성시키기
		gameFrame.setTitle("내 숫자: "+getNumber()); //제목창에 랜덤 숫자 출력
		myTextArea.append("         ♠♠♠나의 게임 진행 상황♠♠♠\n\n"); //내 화면창에 출력
		oppTextArea.append("        ♤♤♤상대방의 게임 진행 상황♤♤♤\n\n"); //상대 화면창에 출력
		
		//msgInput 이벤트
		msgInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //enter 입력시 이벤트 발생
				String input = msgInput.getText(); //입력창에 적은 내용을 변수 input에 대입
				char [] n= new char[3]; //크기가 3인 배열 생성
				for(int i=0;i<3;i++) { 
					 n[i] = input.charAt(i); //각 자리수 변수에 대입
				}
				if(!isNumber(input) || input.length() != 3) { //문자거나 3글자가 아니면
					myTextArea.append("잘못입력하셨습니다. 숫자 3자리를 입력해주세요!\n"); //화면에 출력
				}else if((n[0] == n[1])||(n[0] == n[2])||(n[1] == n[2])){ //각 자리수를 비교하여 숫자가 같을 경우
					myTextArea.append("같은 숫자를 입력했습니다. 서로 다른 숫자 3자리를 입력해주세요!\n"); //화면에 출력
				}else { //숫자 3자리인 경우
					myCount++; //내가 보낸 횟수 +1
					msgInput.setText(""); //엔터 누른 후 input창 글씨 삭제
					myTextArea.append(input+"\n"); //화면창에 입력된 내용 추가
					sendToPeer(input); //onNumberReceived로 정보 보내기
				}
			}
		});

		//recodeRemoveButton 이벤트
		recordRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //버튼 클릭시 이벤트 발생
				//내 화면창에 출력
				myTextArea.append("♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣\n");
				myTextArea.append("상대방의 기록을 지우는 아이템을 사용했습니다!!!\n");
				myTextArea.append("♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣\n");
				sendToPeer("recordRemove"); //onMiscReceived로 이동
				buttonDisable(); //3개 아이템 버튼 비활성
			}
		});
		
		//mixNumButton 이벤트
		mixNumButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //버튼 클릭시 이벤트 발생
				//내 화면창에 출력
				myTextArea.append("♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣\n");
				myTextArea.append("자신의 숫자를 바꾸는 아이템을 사용했습니다!!!\n");
				myTextArea.append("♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣\n");
				sendToPeer("mixNumButton"); //onMiscReceived로 이동
				generateNumber(); //랜덤 숫자 만들기
				gameFrame.setTitle("바뀐 내 숫자: "+getNumber()); //바뀐 숫자 제목창 출력
				buttonDisable(); //3개 아이템 버튼 비활성
			}
		});
		
		//addOppCountButton 이벤트
		addOppCountButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //버튼 클릭시 이벤트 발생
				//내 화면창에 출력
				myTextArea.append("♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣\n");
				myTextArea.append("상대 시도횟수를 늘리는 아이템을 사용했습니다!!!\n");
				myTextArea.append("♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣♣\n");
				while(true) { //무한루프
					randomNum = (int)(Math.random()*10); //랜덤숫자
					if(randomNum>=3 && randomNum<=6) //3이상 6이하 일경우
						break;//무한루프 탈출
				}
				oppCount+=randomNum; //상대시도횟수에 랜덤숫자 추가
				oppTextArea.append(randomNum+"회가 추가되어 ("+oppCount+"회 시도)\n");
				sendToPeer(String.valueOf(randomNum)); //(int형->string형)onNumberReceived으로 이동
				buttonDisable(); //3개 아이템 버튼 비활성
			}
		});
	}
	
	public baseballGame() {
		readyView();// 대기화면 만들기
		start(); //통신 연결 요청
	}
	
	public static void main(String[] args) {
		new baseballGame(); //생성자 실행
	}
	
	public void onGameReady() { //연결성공 시 실행
		readyCount(readyFrame,myTextArea,gameCount);// 카운트 다운
		startView();// 게임화면 만들기
	}
	
	public void printMessage(String msg) {
		myTextArea.append(msg+"\n");// 화면에 출력
	}
	
	public void onNumberReceived(String msg) { //숫자를 수신한 경우
		//addOppCountButton으로 랜덤숫자를 받아온 경우
		if(msg.equals("3")||msg.equals("4")||msg.equals("5")||msg.equals("6")) {
			randomNum = Integer.parseInt(msg); //int형으로 변경 후 randomNum에 대입
			myCount+=randomNum; //내 시도횟수에 랜덤숫자 추가
			//내 화면창에 출력
			myTextArea.append("♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧\n");
			myTextArea.append("상대방이 시도횟수를 늘리는 아이템을 사용했습니다!!!\n");
			myTextArea.append(randomNum+"회가 추가되어 ("+myCount+")회 시도\n");
			myTextArea.append("♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧\n");
		}else {	//3자리 일반적인 숫자를 받은경우
			oppCount++; //상대방이 보낸 횟수 +1
			String result = checkNumber(msg); //받은 숫자를 0s0b형태로 result에 대입
			oppTextArea.append("Anonymous: "+msg+" "+result+" ("+oppCount+" 회 시도)\n"); //상대가 입력한 숫자, 상대 화면에 출력
			sendToPeer(result); //onResultReceived로 결과 보내주기
			if(result.equals("3s0b")) { //3s0b을 보내주면
				// 상대 화면에 출력
				oppTextArea.append("▶▶▶상대방이 정답을 맞혔습니다◀◀◀\n"); //상대 화면창에 출력
				oppTextArea.append("Anonymous 시도횟수: '"+oppCount+"' 회\n"); //상대 화면창에 출력
				inputDisable++; //input이 비활성화된 개수 +1
				result(); //상대와 내가 input이 비활성화된지 확인
			}
		}
	}
	
	public void onResultReceived(String msg) { //0s0b형태를 수신한 경우
		myTextArea.append("결과: "+msg+" ("+myCount+" 회 시도)\n"); //내 화면창에 출력
		if(msg.equals("3s0b")) { // 3s0b을 받으면
			myTextArea.append("▶▶▶정답!◀◀◀\n"); //내 화면창에 출력
			myTextArea.append("시도횟수: '"+myCount+"' 회\n"); //내 화면창에 출력
			if(inputDisable != 1) //마지막 사람이 맞힐때는 아래 글씨가 나오지 않게 설정
				myTextArea.append("상대방이 맞힐 때까지 기다려 주세요....\n"); //내 화면창에 출력
			msgInput.setEnabled(false); // input 비활성화
			inputDisable++; //input이 막힌 횟수 +1
			result(); //상대와 내가 input이 비활성화된지 확인
			buttonDisable(); //아이템 버튼 비활성화
		}
	}
	
	public void onMiscReceived(String msg) {
		if(msg.equals("recordRemove")) { //상대방이 recordRemove버튼을 눌렀을 경우
			//setText로 내 화면 기록 삭제
			myTextArea.setText("         ♠♠♠나의 게임 진행 상황♠♠♠\n\n");
			myTextArea.append("♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧\n");
			myTextArea.append("상대방이 기록을 지우는 아이템을 사용했습니다!!!\n");
			myTextArea.append("♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧\n");
		}else if(msg.equals("restart")) { //게임이 끝나고 재시작 버튼을 누렀을 경우
			restart++; //restart+1
			if(restart == 2) { //restart가 2이면
				sendToPeer("restart"); //상대방도 적용하기 위해
				resultTextArea.setFont(new Font("",Font.BOLD,40));
				readyCount(resultFrame,resultTextArea,gameCount); //카운트 다운
				resultFrame.setVisible(false); //결과화면 숨기기
				gameFrame.setVisible(true); //게임 화면 보이기
				myTextArea.setText("         ♠♠♠나의 게임 진행 상황♠♠♠\n\n"); //내 화면창에 출력
				oppTextArea.setText("        ♤♤♤상대방의 게임 진행 상황♤♤♤\n\n"); //상대 화면창에 출력
				myTextArea.append("게임 재시작!!!\n"); //내 화면창에 출력
				//모든 설정 초기화
				scoreJLabel.setText(gameCount+"번째 게임!!   "+myScore+"  :  "+oppScore); //바뀐 판수와 점수 출력
				generateNumber(); // 랜덤 숫자 생성시키기
				gameFrame.setTitle("내 숫자: "+getNumber()); //바뀐 숫자 제목창 출력
				buttonEnable(); //모든 버튼 활성화
				msgInput.setEnabled(true); //input 활성화
				myCount = 0;
				oppCount = 0;
				inputDisable = 0;
				restart = 0;
			}
			restart = 0; //2번째 이상의 게임을 시도하기 위해 초기화
		}else if(msg.equals("mixNumButton")) {//상대방이 mixNumButton버튼을 눌렀을 경우
			//내 화면에 출력
			myTextArea.append("♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧\n");
			myTextArea.append("상대방이 자신의 숫자를 바꾸는 아이템을 사용했습니다!!!\n");
			myTextArea.append("♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧♧\n");
		}
	}
	
	public void resultFrame() { //승패 결과 or 다시시작 창 보여주는 함수
		gameFrame.setVisible(false); //게임 화면 숨기기
		gameCount++; //판수+1
		resultFrame = new JFrame();
		resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //x누를면 종료
		resultFrame.setSize(420, 220); //결과 화면
		resultFrame.setTitle("게임 결과!!"); //타이틀
		Font fieldFont = new Font("",Font.BOLD,50); //폰트 설정
		resultTextArea = new JTextArea();
		resultTextArea.setFont(fieldFont); //폰트 적용
		JButton restartButton = new JButton("재시작!!"); //재시작 버튼
		restartButton.setPreferredSize(new Dimension(0,50)); //버튼 높이 50
		resultFrame.add(resultTextArea,BorderLayout.CENTER); //중간에 배치
		resultFrame.add(restartButton,BorderLayout.SOUTH); //하단에 배치
		resultFrame.setVisible(true); //화면 출력
		resultFrame.setLocationRelativeTo(null); //가운데에 화면
		
		//restartButton 이벤트
		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //버튼 클릭시 이벤트 발생
				//새로운 게임을 위한 초기화
				restartButton.setEnabled(false); //버튼 비활성화
				restart++; //restart+1
				sendToPeer("restart"); //onMiscReceived로 이동
			}
		});
	}
	
	public void result() { //상대와 내가 input이 비활성화된지 확인
		//input이 2개 비활성되면 상대가 입력한 개수와 내가 입력한 개수를 비교
		if(inputDisable == 2) {
			if(myCount < oppCount) { //내 시도횟수가 더 작을 경우
				resultFrame(); //결과 화면
				myScore++; //내 점수+1
				resultTextArea.append("★★YOU WIN★★\n         "+myScore+"  :  "+oppScore); //결과 화면에 출력
			}else if(myCount > oppCount) { //내 시도횟수가 더 클 경우
				resultFrame(); //결과 화면
				oppScore++; //상대 점수 +1
				resultTextArea.append("  ☆YOU LOSE☆\n         "+myScore+"  :  "+oppScore); //결과 화면에 출력
			}else { //무승부일경우
				resultFrame(); //결과 화면
				//내 점수와 상대 점수 +1
				myScore++; 
				oppScore++;
				resultTextArea.append(" ※YOU DRAW※\n         "+myScore+"  :  "+oppScore); //결과 화면에 출력
			}
		}
	}
}