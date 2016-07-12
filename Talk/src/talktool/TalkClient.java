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
	private static ExecutorService exec = Executors.newCachedThreadPool(); // 线程池

	public static void main(String[] args) throws Exception {
		// 启动客户端函数
		new TalkClient();
	}

	public TalkClient() {
		try {
			Socket socket = new Socket(ip, PORT); // 连接端口
			exec.execute(new c_send(socket));// 启动客户端线程
			System.out.println("【" + socket.getLocalAddress() + "】您好，欢迎来到聊天室！");
			InputStream is = null;
			is = socket.getInputStream();// 创建输入流
			String msg;
			int len;
			byte[] b = new byte[1024];
			while ((len = is.read(b)) != -1) {
				msg = new String(b, 0, len);// 将消息存入字节数组并赋给msg
				System.out.println(msg);
			}
		} catch (Exception e) {
		}
	}

	class c_send implements Runnable {
		private Socket socket;// 定义套接字

		public c_send(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				InputStream is = null;
				is = socket.getInputStream();// 获取输入输出流
				OutputStream os = null;
				os = socket.getOutputStream();
				String msg;
				while (true) {
					String stt = null;
					Scanner sc = new Scanner(System.in);// 输入欲发送的信息
					msg = sc.next();// 把值返回给msg并打印
					os.write(msg.getBytes());
					if (msg.trim().equals("bye")) {
						is.close();
						os.close();
						exec.shutdownNow();//关闭线程池
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}