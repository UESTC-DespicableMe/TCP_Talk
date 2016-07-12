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
	private static final int PORT = 9620; // �˿ں�
	private static List<Socket> user_list = new ArrayList<Socket>(); // �������Ӷ���
	private ExecutorService exec;//�̳߳�
	private ServerSocket server;//���������˿�
	
	public static void main(String[] args) {
		// ��������������
		new TalkServer();
	}
	public TalkServer() {
		try {
			server = new ServerSocket(PORT);
			// ����һ���ɸ�����Ҫ�������̵߳��̳߳أ���������ǰ������߳̿���ʱ����������
			exec = Executors.newCachedThreadPool();
			System.out.println("��������������");

			Socket client = null;
			while (true) {
				client = server.accept(); // ���տͻ�����
				user_list.add(client);// ���û���ӽ��б�
				System.out.println("�˿ں�Ϊ�� " + user_list.get(i).getPort());// ����û��˿ں�
				i++;// �б��±��һ
				exec.execute(new s_talk(client));//����s_talk����
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static class s_talk implements Runnable {
		private Socket socket;
		private InputStream is = null;//������
		private OutputStream os = null;//�����
		private String msg;

		public s_talk(Socket socket) throws IOException {
			this.socket = socket;
			is = this.socket.getInputStream();
			msg = "��" + this.socket.getPort() + "�����������ң���ǰ�������С�"+ user_list.size() + "����"+"(";
			for(Socket st:user_list){
				msg+=st.getPort()+",";
			}
			msg+=")";
			System.out.println(msg);
			for (Socket client : user_list) {
				os = client.getOutputStream();
				os.write(msg.getBytes());//������������Ϣ�����ÿ���ͻ���
			}
		}

		public void run() {

			try {
				int len;
				byte[] b = new byte[1024];//�ֽ�
				while ((len = is.read(b)) != -1) {
					msg = new String(b, 0, len);//�����ݴ洢��msg
					//˽���ж�
					if (msg.indexOf("@") != -1) {
						int n = 0;
						String stt = null;
						for (n = 0; n < user_list.size() - 1; n++) {
							stt = "" + user_list.get(n).getPort();//���˿ں�ת��Ϊ�ַ�������
							if (msg.indexOf(stt) != -1) {
								break;//û�ҵ��˶˿ںţ�����ѭ��
							}
						}
						String Str = null;
						int duan = socket.getPort();
						String st = "" + duan;
						int num1 = st.length();//�˿ںų���
						int num = msg.length();
						Str = msg.substring(num1 + 1, num);
						os = user_list.get(n).getOutputStream();//����Ϣ���������
						os.write((socket.getPort() + "����˵:" + Str).getBytes());//��ӡ��˽����Ϣ
						//System.out.println("˽�Ľ��ն˿�"+user_list.get(n).getPort());//�ڷ�������ʾ˽�Ķ˿ںż�������
						System.out.println(socket.getPort() +"��"+user_list.get(n).getPort()+ "���͵�˽����Ϣ��"+ "���͵�����-------" + Str);
					} else
					{  
						//�ж��˳���trim��������ȥ����ͷ�ͽ�β�Ŀո�
						if (msg.trim().equals("bye")) {
							user_list.remove(socket);// ɾ��һ��Ԫ��
							is.close();//�ر����������
							os.close();
							msg = "��" + socket.getPort()+ "���뿪�����ң���ǰ�������С�" + user_list.size()+ "����";
							socket.close();
							System.out.println(msg);
							for (Socket client : user_list) {
								os = client.getOutputStream();
								os.write(msg.getBytes());
							}
							break;
						} else
							
						{   //Ⱥ��
							msg = "��" + socket.getPort() + "��˵��" + msg;
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
