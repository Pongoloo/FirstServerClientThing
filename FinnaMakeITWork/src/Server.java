import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server extends Thread {

    String host;
    int port;

    ServerSocketChannel channel;
    Selector selector;

    Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() throws IOException {
        try (
                ServerSocketChannel socketChannel = ServerSocketChannel.open();
                Selector selector1 = Selector.open();
        ) {
            this.channel = socketChannel;
            this.selector = selector1;

            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(host, port));

            channel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println(selector.isOpen() + " czy selector jest open przed this.start()");

            handleConnections();
            System.out.println(selector.isOpen() + " czy selector jest open po this.start()");

        }
    }

    @Override
    public void run() {
        System.out.println(selector.isOpen() + " czy selector jest open w run");
        try {
            handleConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleConnections() throws IOException {
        System.out.println(selector.isOpen() + " czy selector jest open w handleConnections");

        int iteration=0;
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                System.out.println(key.isReadable() +" is readable w przejsciu:"+iteration);
                System.out.println(key.isAcceptable()+ " is acceptable w przejsciu " + iteration);
                iteration++;

                if (key.isAcceptable()) {
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ);
                    System.out.println("we registered the channel in read");
                    continue;
                }

                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    System.out.println("we're in read");
                    socketChannel.configureBlocking(false);
                    handleRequset(socketChannel);

                }
            }
        }
    }

    public void handleRequset(SocketChannel socketChannel) throws IOException {
        if (!socketChannel.isOpen()) {
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        int bytesRead = socketChannel.read(buffer);
        System.out.println("bytes read:"+bytesRead);
        if(bytesRead==-1){
            System.out.println("connection done and dusted bytesRead:-1");
            socketChannel.close();
        }
        if(bytesRead==0){
            System.out.println("everything that needed to be said was said...");
        }
        if(bytesRead>0){
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String message = new String(bytes);
            System.out.print("odczytana wiadomosc: " + message);

            writeResponse(socketChannel);
        }

    }
    public void writeResponse(SocketChannel socketChannel) throws IOException {
        String message = "SIEMA KLIENT\n";
        ByteBuffer encode = Charset.defaultCharset().encode(message);
        socketChannel.write(encode);

        /*StringBuffer response = new StringBuffer();
        response.setLength(0);
        response.append("SIEMANO KURWA KLIENT");
        response.append("\r\n");
        ByteBuffer byteBuffer =
                Charset.defaultCharset().
                        encode(CharBuffer.wrap(response));
        socketChannel.write(byteBuffer);*/
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server("localhost", 7777);
        server.startServer();
    }
}
