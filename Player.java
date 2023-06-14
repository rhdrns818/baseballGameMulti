package Assignment;
/****************************************
 * 야구게임 기본 기능을 구현한 추상 클래스 Player
 * 통신연결 기능, 메시지송수신기능, 랜덤숫자생성기능, 스트라이크/볼 판정기능이 구현됨
 * 추상 메소드 5개는 상속받는 클래스에서 직접 구현해야 함
 * 추상 메소드들은 메시지 송수신 과정에서 자동으로 호출됨
 * 2023.5.23 version1.0 응용프로그래밍수업(설순욱) 
 ***************************************/
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

abstract class Player {
	private static final int PORT = 8000;
	private static final int SERVER = 0;
	private static final int CLIENT = 1;
	private Socket socket;
	private PrintWriter out;
	private Scanner in;
	
	private int n1, n2, n3;
	
	public abstract void onGameReady(); 
	public abstract void printMessage(String msg);
	public abstract void onNumberReceived(String msg); 
	public abstract void onResultReceived(String msg); 
	public abstract void onMiscReceived(String msg); 
	
	public void start() {
		String[] options = {"서버", "클라이언트", "취소(종료)"};
		int mode = JOptionPane.showOptionDialog(null, "시작 형태를 선택해주세요.\n클라이언트 시작시 서버의 주소를 알아야 합니다", "서버/클라이언트 선택", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		//0: 서버, 1:클라이언트, 2:취소(종료)
		if( mode==2 ) return; //취소 선택시 종료
		
		switch( mode ) {
		case SERVER: 
			startServer(); 
			break;
		case CLIENT:
			String ip = JOptionPane.showInputDialog("접속할 컴퓨터의 IP를 입력하세요\n"
					+ "취소하면 종료합니다.", "169.254.126.15");
			if( ip==null ) System.exit(0); 
			connectToServer(ip);
		}
	}
    public void generateNumber() { 
        // 겹치지 않는 n1, n2, n3 숫자를 랜덤으로 생성
        do {
            n1 = (int)(Math.random()*10);
            n2 = (int)(Math.random()*10);
            n3 = (int)(Math.random()*10);
        } while( n1==n2 || n1==n3 || n2==n3 );
    }
    public String getNumber() {
    	return ""+n1+n2+n3;
    }
    public String checkNumber(String number) {
    	// 문자열을 숫자로 바꾸고 아래의 checkNuber() 호출
    	return checkNumber(Integer.parseInt(number));
    }
    public String checkNumber(int number) { 
        // 주어진 숫자 number가 몇 스트라이크 몇 볼인지 구해서 문자열로 리턴
        int scount = 0;
        int bcount = 0;
        
        int num1 = number / 100;
        int num2 = number / 10 % 10;
        int num3 = number % 10;
        
        if( num1 == n1 ) scount++; 
        if( num2 == n2) scount++; 
        if( num3 == n3 ) scount++; 
        if( num1 == n2 || num1 == n3 ) bcount++;
        if( num2 == n1 || num2 == n3 ) bcount++;
        if( num3 == n1 || num3 == n2 ) bcount++;
       
        return scount + "s"+ bcount + "b";
    }

	private void startServer() {
		// 내가 다른 친구의 접속를 대기
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			printMessage("서버로 시작되었습니다.\n 친구의 접속을 기다립니다.\nListening on..." +
					Inet4Address.getLocalHost().getHostAddress() + ":" + PORT);
			
			socket = serverSocket.accept(); // 다른 컴이 접속할 때까지 대기
			printMessage("친구가 접속하였습니다");
			String remoteUser = socket.getRemoteSocketAddress().toString();
			printMessage("접속자의 IP: " + remoteUser);
			
			//양방향 통신을 위한 채널 생성 IO개념
			out = new PrintWriter( socket.getOutputStream(), true );
			in = new Scanner( socket.getInputStream() );
			printMessage("양방향 통신 준비 완료..");
			
			// 거의 준비 완료
			ReceiveThread th = new ReceiveThread(this);
			th.start();
			
			serverSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void connectToServer(String ip) {
		// 내가 다른 컴퓨터 ip로 접속하려고 함
		printMessage("친구(서버)에게 연결중...\nConnecting to..." + ip + ":" + PORT);
		try {
			socket = new Socket(ip, PORT);
			printMessage("연결 성공!");
			//양방향 통신을 위한 채널 생성 IO개념
			out = new PrintWriter( socket.getOutputStream(), true );
			in = new Scanner( socket.getInputStream() );
			printMessage("양방향 통신 준비 완료..");
			// 거의 준비 완료
			ReceiveThread th = new ReceiveThread(this);
			th.start();
			
		} catch (Exception e) {
			System.err.println("연결 실패");
			System.exit(0);
		}
	}
	
	public String receiveFromPeer() {
		return in.nextLine().trim();
	}
	public void sendToPeer(String msg) {
		if( socket==null) {
			System.err.println("접속된 친구가 없습니다.");
			System.exit(0);
		}
		out.println(msg);
	}
	public static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);					
		} catch (NumberFormatException e) {
			return false; // 숫자가 아니어서 오류가 나면 false를 리턴
		}
		return true; //정상적으로 통과하면 true 리턴 (숫자임)
	}
}
class ReceiveThread extends Thread{
	Player player;
	public ReceiveThread( Player p ) {
		player = p;
		p.sendToPeer("START");
	}
	@Override
	public void run() {
		while(true) {
			try {
				String str = player.receiveFromPeer(); //뭐라도 수신하면
				if( str.equals("START") ) { 
					player.printMessage("게임 시작 명령어(START) 수신");
					player.onGameReady();
				} else if( str.length()>3 && str.charAt(1)=='s' ) {
					player.onResultReceived(str); //볼 판정 결과를 수신한 경우 호출
				} else if( Player.isNumber(str) ) {
					player.onNumberReceived(str); //숫자를 수신한 경우 호출
				} else {
					player.onMiscReceived(str); //그 외의 메시지 수신한 경우 호출
				}				
			} catch (Exception e) {
				System.err.println("연결이 끊어졌습니다");
				e.printStackTrace();
				return;
			}
		}
	}
}