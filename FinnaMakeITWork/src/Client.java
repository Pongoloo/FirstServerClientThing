import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Client implements Runnable{


    String host;
    int port;
    String id;

    public Client(String id,String host, int port) {
        this.id = id;
        this.host=host;
        this.port=port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host,port);
             PrintWriter out= new PrintWriter(socket.getOutputStream(),true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));){

            for (int i=0; i<5; i++){
                out.println("SIEMANO kurdebele x"+i +" FROM YOUR HOMIE THREAD NR " + id);

                System.out.println("wrote "+i + " iteration");
                System.out.println(in.readLine());
                System.out.println("i");
            }
            out.println("nara");
          //  String collect = in.lines().collect(Collectors.joining());
         //   System.out.println(collect);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
