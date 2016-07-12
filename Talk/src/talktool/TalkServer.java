package talktool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.ObjectInputStream.GetField;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TalkServer {
	public static int i = 0;
	private static final int PORT = 9620; // 端口号
	private static List<Socket> user_list = new ArrayList<Socket>(); // 保存连接对象
	private ExecutorService exec;//线程池
	private ServerSocket server;//用来监听端口
	
	public static void main(String[] args) {
		// 启动服务器程序
		new TalkServer();
	}
	public TalkServer() {
		try {
			server = new ServerSocket(PORT);
			// 创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们
			exec = Executors.newCachedThreadPool();
			System.out.println("服务器已启动！");

			Socket client = null;
			while (true) {
				client = server.accept(); // 接收客户连接
				user_list.add(client);// 将用户添加进列表
				System.out.println("端口号为： " + user_list.get(i).getPort());// 输出用户端口号
				i++;// 列表下标加一
				exec.execute(new s_talk(client));//运行s_talk方法
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static class s_talk implements Runnable {
		private Socket socket;
		private InputStream is = null;//输入流
		private OutputStream os = null;//输出流
		private String msg;

		public s_talk(Socket socket) throws IOException {
			this.socket = socket;
			is = this.socket.getInputStream();
			msg = "【" + this.socket.getPort() + "】进入聊天室！当前聊天室有【"+ user_list.size() + "】人"+"(";
			for(Socket st:user_list){
				msg+=st.getPort()+",";
			}
			msg+=")";
			System.out.println(msg);
			for (Socket client : user_list) {
				os = client.getOutputStream();
				os.write(msg.getBytes());//把在线人数信息输出到每个客户端
			}
		}

		public void run() {

			try {
				int len;
				byte[] b = new byte[1024];//字节
				while ((len = is.read(b)) != -1) {
					msg = new String(b, 0, len);//将数据存储到msg
					//私聊判定
					if (msg.indexOf("@") != -1) {
						int n = 0;
						String stt = null;
						for (n = 0; n < user_list.size() - 1; n++) {
							stt = "" + user_list.get(n).getPort();//将端口号转换为字符串类型
							if (msg.indexOf(stt) != -1) {
								break;//没找到此端口号，跳出循环
							}
						}
						String Str = null;
						int duan = socket.getPort();
						String st = "" + duan;
						int num1 = st.length();//端口号长度
						int num = msg.length();
						Str = msg.substring(num1 + 1, num);
						os = user_list.get(n).getOutputStream();//将信息存入输出流
						os.write((socket.getPort() + "对我说:" + Str).getBytes());//打印出私聊信息
						//System.out.println("私聊接收端口"+user_list.get(n).getPort());//在服务器显示私聊端口号及其数据
						System.out.println(socket.getPort() +"对"+user_list.get(n).getPort()+ "发送的私聊消息："+ "发送的数据-------" + Str);
					} else
					{  
						//判定退出，trim（）用于去掉开头和结尾的空格
						if (msg.trim().equals("bye")) {
							user_list.remove(socket);// 删除一个元素
							is.close();//关闭输入输出流
							os.close();
							msg = "【" + socket.getPort()+ "】离开聊天室！当前聊天室有【" + user_list.size()+ "】人";
							socket.close();
							System.out.println(msg);
							for (Socket client : user_list) {
								os = client.getOutputStream();
								os.write(msg.getBytes());
							}
							break;
						} else
							
						{   //群聊
							msg = "【" + socket.getPort() + "】说：" + msg;
							System.out.println(msg);
							for (Socket client : user_list) {
								os = client.getOutputStream();
								os.write(msg.getBytes());

							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
