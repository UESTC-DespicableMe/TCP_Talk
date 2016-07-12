package talktool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TalkClient {

	public static String ip = "127.0.0.1";
	private static final int PORT = 9620;
	private static ExecutorService exec = Executors.newCachedThreadPool(); // �̳߳�

	public static void main(String[] args) throws Exception {
		// �����ͻ��˺���
		new TalkClient();
	}

	public TalkClient() {
		try {
			Socket socket = new Socket(ip, PORT); // ���Ӷ˿�
			exec.execute(new c_send(socket));// �����ͻ����߳�
			System.out.println("��" + socket.getLocalAddress() + "�����ã���ӭ���������ң�");
			InputStream is = null;
			is = socket.getInputStream();// ����������
			String msg;
			int len;
			byte[] b = new byte[1024];
			while ((len = is.read(b)) != -1) {
				msg = new String(b, 0, len);// ����Ϣ�����ֽ����鲢����msg
				System.out.println(msg);
			}
		} catch (Exception e) {
		}
	}

	class c_send implements Runnable {
		private Socket socket;// �����׽���

		public c_send(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				InputStream is = null;
				is = socket.getInputStream();// ��ȡ���������
				OutputStream os = null;
				os = socket.getOutputStream();
				String msg;
				while (true) {
					String stt = null;
					Scanner sc = new Scanner(System.in);// ���������͵���Ϣ
					msg = sc.next();// ��ֵ���ظ�msg����ӡ
					os.write(msg.getBytes());
					if (msg.trim().equals("bye")) {
						is.close();
						os.close();
						exec.shutdownNow();//�ر��̳߳�
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}