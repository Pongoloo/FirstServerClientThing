import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

     //   for (int i=0; i<5; i++){
            Client client = new Client(0+"", "localhost", 7777);
            new Thread(client).start();
     //   }

    }
}